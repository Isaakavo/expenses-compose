package com.avocado.expensescompose.data.source

import com.avocado.expensescompose.data.auth.AuthProvider
import com.avocado.expensescompose.data.auth.AuthResult
import com.avocado.expensescompose.data.auth.RefreshTokenData
import com.avocado.expensescompose.data.auth.UserCredentials
import com.avocado.expensescompose.data.model.MyResult
import com.avocado.expensescompose.data.model.auth.Auth
import com.avocado.expensescompose.data.model.auth.AuthParameters
import com.avocado.expensescompose.data.model.auth.CognitoResponse
import com.avocado.expensescompose.data.model.fold
import javax.inject.Inject

class CognitoAuthProvider @Inject constructor(
  private val cognitoAuthProvider: CognitoAuthService
) : AuthProvider {

  override suspend fun singIn(credentials: UserCredentials): MyResult<AuthResult> {
    val (username, password) = credentials
    val response = cognitoAuthProvider.signIn(username, password)

    return response
      .fold(
        onSuccess = { cognitoResponse ->
          MyResult.Success(cognitoResponse.toAuthResult())
        },
        onError = { exception ->
          MyResult.Error(
            exception = exception
          )
        }
      )
  }

  override suspend fun refreshToken(refreshTokenData: RefreshTokenData): MyResult<AuthResult> {
    val (refreshToken) = refreshTokenData
    val auth = Auth(
      authParameters = AuthParameters(
        refreshToken = refreshToken
      )
    )
    val response = cognitoAuthProvider.refreshToken(auth)

    return response
      .fold(
        onSuccess = { cognitoResponse ->
          MyResult.Success(cognitoResponse.toAuthResult())
        },
        onError = { exception ->
          MyResult.Error(
            exception = exception
          )
        }
      )
  }

  private fun CognitoResponse.toAuthResult(): AuthResult = AuthResult(
    accessToken = this.authenticationResult.accessToken,
    refreshToken = this.authenticationResult.refreshToken,
    expiresIn = this.authenticationResult.expiresIn,
    tokenType = this.authenticationResult.tokenType
  )
}
