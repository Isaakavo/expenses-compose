package com.avocado.expensescompose.domain.login.usecase

import com.avocado.expensescompose.data.repositories.AuthRepository
import com.avocado.expensescompose.domain.login.models.LoginResult
import com.avocado.expensescompose.presentation.util.AuthError
import javax.inject.Inject

class LoginUseCase @Inject constructor(
  private val authRepository: AuthRepository,

  ) {

  suspend operator fun invoke(email: String, password: String): LoginResult {
    val emailError = if (email.isBlank()) AuthError.FieldEmpty else null
    val passwordError = if (password.isBlank()) AuthError.FieldEmpty else null

    if (emailError != null || passwordError != null) {
      return LoginResult(emailError, passwordError)
    }

    return LoginResult(result = authRepository.getAccessToken(email, password))
  }
}