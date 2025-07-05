package com.avocado.expensescompose.data.auth

import com.avocado.expensescompose.data.model.MyResult

interface AuthProvider {

  suspend fun singIn(credentials: UserCredentials): MyResult<AuthResult>

  suspend fun refreshToken(refreshTokenData: RefreshTokenData): MyResult<AuthResult>
}
