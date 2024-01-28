package com.avocado.expensescompose.data.interceptor

import com.apollographql.apollo3.api.http.HttpRequest
import com.apollographql.apollo3.api.http.HttpResponse
import com.apollographql.apollo3.network.http.HttpInterceptor
import com.apollographql.apollo3.network.http.HttpInterceptorChain
import com.avocado.expensescompose.data.model.MyResult
import com.avocado.expensescompose.data.model.auth.Auth
import com.avocado.expensescompose.data.model.auth.AuthParameters
import com.avocado.expensescompose.data.repositories.AuthRepository
import com.avocado.expensescompose.data.repositories.TokenManagerRepository
import javax.inject.Inject
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import timber.log.Timber

class AuthorizationInterceptor @Inject constructor(
  private val authClient: AuthRepository,
  private val tokenManagerRepository: TokenManagerRepository
) : HttpInterceptor {
  private val mutex = Mutex()

  private suspend fun extractJwt(): String? = mutex.withLock {
    when (val value = tokenManagerRepository.getAccessToken()) {
      is MyResult.Success -> {
        return value.data
      }

      is MyResult.Error -> {
        return ""
      }
    }
  }

  private suspend fun validateJwtIsNotNullOrEmpty(
    jwt: String?,
    request: HttpRequest,
    chain: HttpInterceptorChain
  ): HttpResponse {
    if (jwt.isNullOrBlank()) {
      Timber.d("Error jwt empty")
      return chain.proceed(request)
    }

    return chain.proceed(request.newBuilder().addHeader("X-Session-Key", jwt).build())
  }

  override suspend fun intercept(
    request: HttpRequest,
    chain: HttpInterceptorChain
  ): HttpResponse {
    try {
      // Extract the current access token
      val jwt = extractJwt()
      val response = validateJwtIsNotNullOrEmpty(jwt, request, chain)
      // If the token is not valid, extract the current token and call refresh api
      return if (response.statusCode == 401) {
        // Extract the refresh token from the data store
        val refreshToken = mutex.withLock {
          when (val value = tokenManagerRepository.getRefreshToken()) {
            is MyResult.Success -> {
              value.data
            }

            is MyResult.Error -> {
              ""
            }
          }
        }

        val refreshTokenResponse = authClient.refreshToken(
          Auth(
            authFlow = "REFRESH_TOKEN_AUTH",
            authParameters = AuthParameters(refreshToken = refreshToken ?: "")
          )
        )
        // If cognito returns the new access token, save it to replace the old one and proceed with
        // the request
        if (refreshTokenResponse is MyResult.Success) {
          val authResults = refreshTokenResponse.data.authenticationResult
          val accessToken = authResults.accessToken
          tokenManagerRepository.saveAccessToken(accessToken)
          return chain.proceed(
            request.newBuilder()
              .addHeader("X-Session-Key", accessToken).build()
          )
        }
        chain.proceed(request.newBuilder().build())
      } else {
        response
      }
    } catch (e: Exception) {
      Timber.e(e)
    }

    return chain.proceed(request)
  }
}
