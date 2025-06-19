package com.avocado.expensescompose.presentation.util

sealed class AuthError : Error() {
  data object FieldEmpty : AuthError()
  data object InputTooShort : AuthError()
  data object InvalidEmail : AuthError()
  data object InvalidPassword : AuthError()
}
