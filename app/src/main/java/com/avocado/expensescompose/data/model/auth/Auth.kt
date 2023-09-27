package com.avocado.expensescompose.data.model.auth

import retrofit2.http.Field

data class Auth(
    @Field("AuthFlow")
    val authFlow: String = "USER_PASSWORD_AUTH",
    @Field("AuthParameters")
    val authParameters: AuthParameters,
    @Field("ClientId")
    val clientId: String = Constants.CLIENT_ID
)

data class AuthParameters(
    @Field("PASSWORD")
    val password: String,
    @Field("USERNAME")
    val username: String
)