package com.avocado.expensescompose.data.repositories

import android.util.Log
import com.avocado.expensescompose.data.model.MyResult
import com.avocado.expensescompose.data.model.SimpleResource
import com.avocado.expensescompose.data.model.auth.Auth
import com.avocado.expensescompose.data.model.auth.AuthParameters
import com.avocado.expensescompose.presentation.util.Constants
import com.avocado.expensescompose.data.model.auth.CognitoResponse
import com.avocado.expensescompose.data.network.LoginJwtClient
import okio.IOException
import retrofit2.HttpException
import javax.inject.Inject

class AuthRepository @Inject constructor(
  private val awsApi: LoginJwtClient,
  private val tokenManagerRepository: TokenManagerRepository
) {
  private suspend fun saveAccessToken(value: String): MyResult<Boolean> =
    tokenManagerRepository.saveAccessToken(
      value
    )

  private suspend fun saveRefreshToken(value: String): MyResult<Boolean> =
    tokenManagerRepository.saveRefreshToken(value)

  private suspend fun getAccessToken(): MyResult<String?> = tokenManagerRepository.getAccessToken()
  private suspend fun getRefreshToken(): MyResult<String?> =
    tokenManagerRepository.getRefreshToken()

  private suspend fun getTokenFromApi(email: String, password: String): SimpleResource {
    val auth = Auth(
      authParameters = AuthParameters(
        password = password, username = email
      )
    )
    return try {
      val response = awsApi.getJwtToken(base = Constants.AWS_PROVIDER, auth = auth)
      val accessToken = response.authenticationResult.accessToken
      val refreshToken = response.authenticationResult.refreshToken
      run {
        saveAccessToken(accessToken)
        saveRefreshToken(refreshToken)
        MyResult.Success(Unit)
      }
    } catch (e: IOException) {
      MyResult.Error(uiText = "Couldn't reach the server")
    } catch (e: HttpException) {
      if (e.code() == 400) {
        //TODO convert this to object and handle the error to return
        // "Contraseña o email incorrectos"
        val errorResponse = e.response()?.errorBody()?.string()
        return MyResult.Error(uiText = errorResponse)
      }
      MyResult.Error(uiText = "Something went wrong")
    }

  }

  suspend fun getAccessToken(email: String, password: String): SimpleResource {
    return try {
      // Validate the existence of a previous Access Token
      // If exists, continue and use it
       when (val savedAccessToken = getAccessToken()) {
        is MyResult.Success -> {
          if (savedAccessToken.data != null) {
            // Validate also that there is a refresh token available
            // If exists, we are safe to make the request
            // Interceptor will use it to ask for a new access token
            when (val savedRefreshToken = getRefreshToken()) {
              is MyResult.Success -> {
                if (savedRefreshToken.data != null) {
                  return MyResult.Success(Unit)
                }
              }

              is MyResult.Error -> {
                Log.d("JWT", savedRefreshToken.uiText.toString())
              }
            }
          }
        }

        is MyResult.Error -> {
          Log.d("JWT", "Access Token not found, requesting a new one")
          return getTokenFromApi(email, password)
        }
      }
      MyResult.Error(uiText = "Something went wrong retrieving your credentials")
    } catch (e: Exception) {
      MyResult.Error(null, e.message)
    }
  }

  suspend fun refreshToken(auth: Auth): MyResult<CognitoResponse> = try {
    val result = awsApi.refreshToken(base = Constants.AWS_PROVIDER, auth)
    MyResult.Success(result)
  } catch (e: Exception) {
    MyResult.Error(null, e.message)
  }
}