package com.avocado.expensescompose.domain.login.usecase

import com.avocado.LoginQuery
import com.avocado.expensescompose.data.repositories.AuthRepository
import com.avocado.expensescompose.data.repositories.login.loginvalidation.LoginValidationRepository
import com.avocado.expensescompose.domain.login.models.LoginResult
import com.avocado.expensescompose.presentation.util.AuthError
import javax.inject.Inject

class LoginUseCase @Inject constructor(
  private val authRepository: AuthRepository,
  private val loginValidationRepository: LoginValidationRepository<LoginQuery.Data?>
) {

  // Use cases
  // validate JWT in the graphql client
  // validate and extract refresh token
  // validate and save username
  suspend operator fun invoke(
    email: String,
    password: String
  ): LoginResult {
    // TODO add more validation logic here
    val emailError = if (email.isBlank()) AuthError.FieldEmpty else null
    val passwordError = if (password.isBlank()) AuthError.FieldEmpty else null

    if (emailError != null || passwordError != null) {
      return LoginResult(emailError, passwordError)
    }

//    authRepository.getAccessToken()
//      .fold(
//        onSuccess = {
//          authRepository.getRefreshToken()
//            .onSuccess {
//              Timber.d("Refresh token found, proceeding with login")
//            }
//        },
//        onError = {
//          authRepository.getTokenFromApi(email, password)
//        }
//      )
//      .map { result ->
//
//      }
//      .map { accessToken ->
//        if (accessToken != null) {
//          return@map getRefreshToken()
//        }
//        null
//      }
//      .onError {
//        authRepository.getTokenFromApi(email, password)
//        Result
//      }
//      .map { refreshToken ->
//        if (refreshToken != null) {
//
//        }
//        Timber.e("No refresh token found, proceeding with login")
//      }

    return LoginResult(result = authRepository.signIn(email, password))
  }

  suspend fun getRefreshToken() = authRepository.getRefreshToken()

  suspend fun saveUsername(username: String) = authRepository.saveUsername(username)

  suspend fun getUsernameFromStorage() = authRepository.getUsername()

  suspend fun resetLoginCredentials() = authRepository.resetTokens()
}
