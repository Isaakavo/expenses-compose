package com.avocado.expensescompose.data.repositories

import com.avocado.expensescompose.R
import com.avocado.expensescompose.data.auth.AuthProvider
import com.avocado.expensescompose.data.auth.RefreshTokenData
import com.avocado.expensescompose.data.auth.UserCredentials
import com.avocado.expensescompose.data.model.MyResult
import com.avocado.expensescompose.data.model.auth.Auth
import com.avocado.expensescompose.data.model.auth.CognitoResponse
import com.avocado.expensescompose.data.model.flatMapSuccess
import com.avocado.expensescompose.data.model.fold
import com.avocado.expensescompose.data.model.onSuccess
import com.avocado.expensescompose.data.network.CognitoRetrofitWebClient
import com.avocado.expensescompose.presentation.util.Constants
import javax.inject.Inject
import timber.log.Timber

// TODO check the correct way of handle errors in the viewmodel
class AuthRepository @Inject constructor(
  private val authProvider: AuthProvider,
  private val awsApi: CognitoRetrofitWebClient,
  private val tokenManagerRepository: TokenManagerService
) {

  suspend fun signIn(email: String, password: String): MyResult<Unit> =
    if (tokenManagerRepository.validateTokens()) {
      MyResult.Success(Unit)
    } else {
      authProvider
        .singIn(
          credentials = UserCredentials(
            username = email,
            password = password
          )
        )
        .flatMapSuccess { authResult ->
          // Save tokens and username in the repository
          tokenManagerRepository
            .saveAccessToken(authResult.accessToken)
            .onSuccess {
              tokenManagerRepository.saveRefreshToken(authResult.refreshToken.orEmpty())
              tokenManagerRepository.saveUsername(email)
            }
        }
    }

  suspend fun refreshToken(): MyResult<Unit> {
    return tokenManagerRepository.getRefreshToken()
      .fold(
        onSuccess = { refreshToken ->
          if (refreshToken != null) {
            val result = authProvider.refreshToken(RefreshTokenData(refreshToken))
            result.flatMapSuccess { authResult ->
              // Save the new access token and refresh token
              tokenManagerRepository
                .saveAccessToken(authResult.accessToken)
                .onSuccess {
                  tokenManagerRepository.saveRefreshToken(authResult.refreshToken.orEmpty())
                }
            }
          }
          MyResult.Error(
            uiText = R.string.credentials_error,
            data = Unit
          )
        },
        onError = { exception ->
          MyResult.Error(
            exception = exception,
            uiText = R.string.credentials_error,
            data = Unit
          )
        }
      )
  }

  // TODO Remove all functions that are not needed after refactoring

  suspend fun saveUsername(username: String) = tokenManagerRepository.saveUsername(username)

  suspend fun getUsername() = tokenManagerRepository.getUsername()

  suspend fun saveAccessToken(value: String): MyResult<Unit> =
    tokenManagerRepository.saveAccessToken(value)

  suspend fun getAccessToken(): MyResult<String?> = tokenManagerRepository.getAccessToken()

  suspend fun getRefreshToken(): MyResult<String?> =
    tokenManagerRepository.getRefreshToken()

  suspend fun resetTokens(): MyResult<Boolean> {
    val refresh = tokenManagerRepository.deleteRefreshToken()
    val access = tokenManagerRepository.deleteAccessToken()
    val email = tokenManagerRepository.deleteUsername()

    if (refresh is MyResult.Success && access is MyResult.Success && email is MyResult.Success) {
      return MyResult.Success(true)
    }

    return MyResult.Error(false)
  }

  suspend fun refreshToken(auth: Auth): MyResult<CognitoResponse> = try {
    val result = awsApi.refreshToken(base = Constants.AWS_PROVIDER, auth)
    MyResult.Success(result)
  } catch (e: Exception) {
    Timber.e("Error refreshing the token ${e.message}")
    MyResult.Error(null, R.string.credentials_error)
  }
}
