package com.avocado.expensescompose.domain.login.usecase

import com.avocado.expensescompose.R
import com.avocado.expensescompose.data.model.MyResult
import com.avocado.expensescompose.data.model.flatMapSuccess
import com.avocado.expensescompose.data.repositories.TokenManagerService
import com.avocado.expensescompose.domain.login.models.UserInfoResult
import javax.inject.Inject

// TODO maybe this can be another user case to handle username and refresh token
class UserInfoUseCase @Inject constructor(
  private val tokenManagerService: TokenManagerService
) {

  suspend operator fun invoke(): MyResult<UserInfoResult> =
    tokenManagerService
      .getRefreshToken()
      .flatMapSuccess { refreshToken ->
        if (refreshToken.isNullOrBlank()) {
          return MyResult.Error(data = null, uiText = R.string.general_error)
        }
        tokenManagerService
          .getUsername()
          .flatMapSuccess { username ->
            if (username.isNullOrBlank()) {
              return MyResult.Error(data = null, uiText = R.string.general_error)
            }

            MyResult.Success(UserInfoResult(username, true))
          }
      }
}
