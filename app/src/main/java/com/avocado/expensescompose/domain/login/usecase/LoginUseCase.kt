package com.avocado.expensescompose.domain.login.usecase

import com.avocado.LoginQuery
import com.avocado.expensescompose.data.model.MyResult
import com.avocado.expensescompose.data.model.flatMapSuccess
import com.avocado.expensescompose.data.model.map
import com.avocado.expensescompose.data.model.onError
import com.avocado.expensescompose.data.repositories.AuthRepository
import com.avocado.expensescompose.data.repositories.login.loginvalidation.LoginValidationRepository
import com.avocado.expensescompose.domain.login.models.LoginResult
import com.avocado.expensescompose.presentation.util.AuthError
import com.avocado.type.AUTH_STATUS
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
    password: String,
    isQuickLogin: Boolean = false
  ): MyResult<LoginResult> {
    // TODO add more validation logic here
    val emailError = if (email.isBlank()) AuthError.FieldEmpty else null
    val passwordError = if (password.isBlank()) AuthError.FieldEmpty else null

    // TODO improve this logic
//    if ((emailError != null || passwordError != null) && !isQuickLogin) {
//    if (!isQuickLogin) {
//      return MyResult.Error(LoginResult(emailError, passwordError))
//    }

    return loginValidationRepository
      .validate()
      .map { data ->
        val status = data?.login?.status
        if (status != AUTH_STATUS.AUTHENTICATED) {
          LoginResult()
        }

        LoginResult(isSuccess = true)
      }
      .onError { data, throwable, uiText ->
        return authRepository
          .signIn(email, password)
          .flatMapSuccess {
            MyResult.Success(LoginResult(isSuccess = true))
          }
      }
  }

  suspend fun saveUsername(username: String) = authRepository.saveUsername(username)

  suspend fun resetLoginCredentials() = authRepository.resetTokens()
}
