package com.avocado.expensescompose.presentation.login.viewmodel

sealed class LoginViewModelEvents {
  object UpdateUsername : LoginViewModelEvents()
  object UpdatePassword : LoginViewModelEvents()
  object ToggleViewPassword : LoginViewModelEvents()
  object Login : LoginViewModelEvents()
  object SetIsSuccess : LoginViewModelEvents()
  object ChangeUser : LoginViewModelEvents()
}
