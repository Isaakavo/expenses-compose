package com.avocado.expensescompose.presentation.login.viewmodel

// TODO add remember password logic
// Add a internal database so I can store the password there, and every time the user clicks in
// login, the code retrieve the hashed password and use it to send the request to aws
data class LoginViewModelState(
  val username: String = "",
  val password: String = "",
  val isLoading: Boolean = false,
  val shouldShowPassword: Boolean = false,
  val isQuickLogin: Boolean = false,
  var userMessage: Int? = null,
  var isSuccess: Boolean = false
)
