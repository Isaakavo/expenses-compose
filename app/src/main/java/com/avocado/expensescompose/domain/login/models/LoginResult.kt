package com.avocado.expensescompose.domain.login.models

import com.avocado.expensescompose.data.model.SimpleResource
import com.avocado.expensescompose.presentation.util.AuthError

data class LoginResult(
  val emailError: AuthError? = null,
  val passwordError: AuthError? = null,
  val result: SimpleResource? = null
)
