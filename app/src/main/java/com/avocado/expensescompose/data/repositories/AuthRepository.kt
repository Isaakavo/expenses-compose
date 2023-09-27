package com.avocado.expensescompose.data.repositories

import com.avocado.expensescompose.data.LoginJwtTokenClient
import com.avocado.expensescompose.data.model.auth.Auth
import com.avocado.expensescompose.data.model.auth.Constants
import com.avocado.expensescompose.data.model.auth.Jwt
import javax.inject.Inject
import com.avocado.expensescompose.data.model.Result

class AuthRepository @Inject constructor(
    private val awsApi: LoginJwtTokenClient
) {
    suspend fun getJwtToken(auth: Auth): Result<Jwt> = try {
        val result = awsApi.getJwtToken(base = Constants.AWS_PROVIDER, auth = auth)
        Result.Success(result)
    } catch (e: Exception) {
        Result.Error(e.message)
    }
}
