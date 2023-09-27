package com.avocado.expensescompose.data.model.auth

import com.google.gson.annotations.SerializedName

data class Auth(
    @SerializedName("AuthFlow")
    val authFlow: String = "USER_PASSWORD_AUTH",
    @SerializedName("AuthParameters")
    val authParameters: AuthParameters,
    @SerializedName("ClientId")
    val clientId: String = Constants.CLIENT_ID
)

data class AuthParameters(
    @SerializedName("PASSWORD")
    val password: String,
    @SerializedName("USERNAME")
    val username: String
)