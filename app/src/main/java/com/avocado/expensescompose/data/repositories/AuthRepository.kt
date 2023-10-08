package com.avocado.expensescompose.data.repositories

import com.avocado.expensescompose.data.model.MyResult
import com.avocado.expensescompose.data.model.auth.Auth
import com.avocado.expensescompose.data.model.auth.Constants
import com.avocado.expensescompose.data.model.auth.Jwt
import com.avocado.expensescompose.data.network.LoginJwtClient
import javax.inject.Inject

class AuthRepository @Inject constructor(
  private val awsApi: LoginJwtClient
) {
  suspend fun getAccessToken(auth: Auth): MyResult<Jwt> = try {
    val result = awsApi.getJwtToken(base = Constants.AWS_PROVIDER, auth = auth)
    MyResult.Success(result)
  } catch (e: Exception) {
    MyResult.Error(null, e.message)
  }

  suspend fun refreshToken(auth: Auth): MyResult<Jwt> = try {
    val result = awsApi.refreshToken(base = Constants.AWS_PROVIDER, auth)
    MyResult.Success(result)
  } catch (e: Exception) {
    MyResult.Error(null, e.message)
  }
}