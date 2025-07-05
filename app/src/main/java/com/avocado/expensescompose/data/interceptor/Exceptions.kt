package com.avocado.expensescompose.data.interceptor

class RefreshTokenNotFoundException(
  message: String? = null,
  cause: Throwable? = null
) : Exception(message, cause)

class RefreshTokenExpiredException(
  message: String? = null,
  cause: Throwable? = null
) : Exception(message, cause)

class RefreshTokenSavedException(
  message: String? = null,
  cause: Throwable? = null
) : Exception(message, cause)
