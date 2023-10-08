package com.avocado.expensescompose.data.model.auth

import com.google.gson.annotations.SerializedName


data class Jwt(
  @SerializedName("AuthenticationResult")
  val authenticationResult: AuthenticationResult,
  @SerializedName("ChallengeParameters")
  val challengeParameters: ChallengeParameters
)

data class AuthenticationResult(
  @SerializedName("AccessToken")
  val accessToken: String,
  @SerializedName("ExpiresIn")
  val expiresIn: Int,
  @SerializedName("IdToken")
  val idToken: String,
  @SerializedName("RefreshToken")
  val refreshToken: String,
  @SerializedName("TokenType")
  val tokenType: String
)

class ChallengeParameters