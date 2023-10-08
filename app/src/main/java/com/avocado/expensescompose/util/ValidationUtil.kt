package com.avocado.expensescompose.util

import android.util.Patterns
import com.avocado.expensescompose.presentation.util.AuthError
import com.avocado.expensescompose.presentation.util.Constants

object ValidationUtil {

  fun validateEmail(email: String): AuthError? {
    val trimmedEmail = email.trim()

    if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
      return AuthError.InvalidEmail
    }
    if(trimmedEmail.isBlank()) {
      return AuthError.FieldEmpty
    }
    return null
  }

  fun validatePassword(password: String): AuthError? {
    val capitalLettersInPassword = password.any { it.isUpperCase() }
    val numberInPassword = password.any { it.isDigit() }
    if(!capitalLettersInPassword || !numberInPassword) {
      return AuthError.InvalidPassword
    }
    if(password.length < Constants.MIN_PASSWORD_LENGTH) {
      return AuthError.InputTooShort
    }
    if(password.isBlank()) {
      return AuthError.FieldEmpty
    }
    return null
  }
}