package com.avocado.expensescompose.data.network

import com.avocado.expensescompose.data.model.auth.Auth
import com.avocado.expensescompose.data.model.auth.CognitoResponse
import retrofit2.http.Body
import retrofit2.http.Headers
import javax.inject.Singleton
import retrofit2.http.POST
import retrofit2.http.Url

@Singleton
interface LoginJwtClient {

  @Headers(
    "X-Amz-Target: AWSCognitoIdentityProviderService.InitiateAuth",
    "Content-Type: application/x-amz-json-1.1"
  )
  @POST
  suspend fun getJwtToken(
    @Url base: String,
    @Body auth: Auth
  ): CognitoResponse

  @Headers(
    "X-Amz-Target: AWSCognitoIdentityProviderService.InitiateAuth",
    "Content-Type: application/x-amz-json-1.1"
  )
  @POST
  suspend fun refreshToken(
    @Url base: String,
    @Body auth: Auth
  ): CognitoResponse
}