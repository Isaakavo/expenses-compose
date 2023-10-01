package com.avocado.expensescompose.data.model.auth

data class Auth(
    val authFlow: String = "USER_PASSWORD_AUTH",
    val authParameters: AuthParameters,
    val clientId: String = Constants.CLIENT_ID
)

data class AuthParameters(
    val password: String,
    val username: String
)