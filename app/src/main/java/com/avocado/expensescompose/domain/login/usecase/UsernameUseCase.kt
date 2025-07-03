package com.avocado.expensescompose.domain.login.usecase

import com.avocado.expensescompose.R
import com.avocado.expensescompose.data.model.MyResult
import com.avocado.expensescompose.data.model.flatMapSuccess
import com.avocado.expensescompose.data.repositories.TokenManagerService
import javax.inject.Inject

class UsernameUseCase @Inject constructor(
  private val tokenManagerService: TokenManagerService
) {

  suspend operator fun invoke(): MyResult<String?> {
    Result
    return tokenManagerService
      .getUsername()
      .flatMapSuccess { username ->
        if (username.isNullOrBlank()) {
          return MyResult.Error(uiText = R.string.general_error)
        }
        MyResult.Success(username)
      }
  }
}
