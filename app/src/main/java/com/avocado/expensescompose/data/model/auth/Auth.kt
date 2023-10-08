package com.avocado.expensescompose.data.model.auth

import com.avocado.expensescompose.presentation.util.Constants
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
    val password: String = "",
    @SerializedName("USERNAME")
    val username: String = "",
    @SerializedName("REFRESH_TOKEN")
    val refreshToken: String = ""
)