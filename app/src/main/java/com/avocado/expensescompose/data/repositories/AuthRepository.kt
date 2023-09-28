package com.avocado.expensescompose.data.repositories

import com.avocado.expensescompose.data.LoginJwtTokenClient
import com.avocado.expensescompose.data.model.auth.Auth
import com.avocado.expensescompose.data.model.auth.Constants
import com.avocado.expensescompose.data.model.auth.Jwt
import javax.inject.Inject
import com.avocado.expensescompose.data.model.MyResult

class AuthRepository @Inject constructor(
    private val awsApi: LoginJwtTokenClient
) {
    suspend fun getJwtToken(auth: Auth): MyResult<Jwt> = try {
        val result = awsApi.getJwtToken(base = Constants.AWS_PROVIDER, auth = auth)
        MyResult.Success(result)
    } catch (e: Exception) {
        MyResult.Error(null, e.message)
    }
}
