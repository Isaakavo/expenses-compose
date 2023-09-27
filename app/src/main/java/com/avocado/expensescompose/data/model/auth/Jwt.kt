package com.avocado.expensescompose.data.model.auth

import retrofit2.http.Field

data class Jwt(
    @Field("AuthenticationResult")
    val authenticationResult: AuthenticationResult,
    @Field("ChallengeParameters")
    val challengeParameters: ChallengeParameters
)

data class AuthenticationResult(
    @Field("AccessToken")
    val accessToken: String,
    @Field("ExpiresIn")
    val expiresIn: Int,
    @Field("IdToken")
    val idToken: String,
    @Field("RefreshToken")
    val refreshToken: String,
    @Field("TokenType")
    val tokenType: String
)

class ChallengeParameters