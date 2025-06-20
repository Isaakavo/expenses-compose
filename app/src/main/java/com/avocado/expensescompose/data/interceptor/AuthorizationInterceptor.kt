package com.avocado.expensescompose.data.interceptor

import com.apollographql.apollo3.api.http.HttpRequest
import com.apollographql.apollo3.api.http.HttpResponse
import com.apollographql.apollo3.network.http.HttpInterceptor
import com.apollographql.apollo3.network.http.HttpInterceptorChain
import com.avocado.expensescompose.data.model.auth.Auth
import com.avocado.expensescompose.data.model.auth.AuthParameters
import com.avocado.expensescompose.data.model.successOrError
import com.avocado.expensescompose.data.repositories.AuthRepository
import javax.inject.Inject
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import timber.log.Timber

class AuthorizationInterceptor @Inject constructor(
  private val authRepository: AuthRepository
) : HttpInterceptor {
  private val mutex = Mutex()

  companion object {
    private const val HEADER_SESSION_KEY = "X-Session-Key"
  }

  /**
   * Intercepts the HTTP request to add the JWT token in the header.
   * If the token is expired, it will refresh the token and retry the request.
   */
  override suspend fun intercept(
    request: HttpRequest,
    chain: HttpInterceptorChain
  ): HttpResponse {
    val jwt = extractJwt()
    val httpResponse = validateJwtIsNotNullOrEmpty(jwt, request, chain)
    return handle401(httpResponse, request, chain)
  }

  private suspend fun extractJwt(): String? = mutex.withLock {
    authRepository
      .getAccessToken()
      .successOrError(
        onSuccess = { it.data },
        onError = {
          Timber.e("Error getting access token: ${it.uiErrorText ?: "Unknown error"}")
          null
        }
      )
  }

  private suspend fun validateJwtIsNotNullOrEmpty(
    jwt: String?,
    request: HttpRequest,
    chain: HttpInterceptorChain
  ): HttpResponse = if (!jwt.isNullOrBlank()) {
    chain.proceed(request.newBuilder().addHeader(HEADER_SESSION_KEY, jwt).build())
  } else {
    Timber.d("Error jwt empty")
    chain.proceed(request)
  }

  private suspend fun handle401(
    response: HttpResponse,
    request: HttpRequest,
    chain: HttpInterceptorChain
  ): HttpResponse = if (response.statusCode == 401) {
    val refreshToken = mutex.withLock {
      authRepository
        .getRefreshToken()
        .successOrError(
          onSuccess = { it.data },
          onError = { throw RefreshTokenNotFoundException() }
        )
    }
    val auth = Auth(
      authFlow = "REFRESH_TOKEN_AUTH",
      authParameters = AuthParameters(refreshToken = refreshToken ?: "")
    )
    authRepository
      .refreshToken(auth)
      .successOrError(
        onSuccess = { success ->
          val accessToken = success.data.authenticationResult.accessToken
          authRepository
            .saveAccessToken(accessToken)
            .successOrError(
              onSuccess = {
                chain.proceed(
                  request.newBuilder()
                    .addHeader(HEADER_SESSION_KEY, accessToken).build()
                )
              },
              onError = { throw RefreshTokenSavedException() }
            )
        },
        onError = { throw RefreshTokenExpiredException() }
      )
  } else {
    response
  }
}
