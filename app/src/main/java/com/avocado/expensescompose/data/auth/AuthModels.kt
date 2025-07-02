package com.avocado.expensescompose.data.auth

data class UserCredentials(
  val username: String,
  val password: String
)

data class RefreshTokenData(
  val refreshToken: String
)

data class AuthResult(
  val accessToken: String,
  val refreshToken: String?,
  val expiresIn: Int?,
  val tokenType: String?
)
