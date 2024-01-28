package com.avocado.expensescompose.data.repositories

import com.avocado.expensescompose.R
import com.avocado.expensescompose.data.model.MyResult
import com.avocado.expensescompose.data.model.SimpleResource
import com.avocado.expensescompose.data.model.auth.Auth
import com.avocado.expensescompose.data.model.auth.AuthParameters
import com.avocado.expensescompose.data.model.auth.AuthenticationResultException
import com.avocado.expensescompose.data.model.auth.CognitoResponse
import com.avocado.expensescompose.data.network.LoginJwtClient
import com.avocado.expensescompose.presentation.util.Constants
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okio.IOException
import retrofit2.HttpException
import timber.log.Timber
import javax.inject.Inject

class AuthRepository @Inject constructor(
  private val awsApi: LoginJwtClient, private val tokenManagerRepository: TokenManagerRepository
) {

  suspend fun saveUsername(username: String) = tokenManagerRepository.saveUsername(username)

  suspend fun getUsername() = tokenManagerRepository.getUsername()


  private suspend fun saveAccessToken(value: String): MyResult<Boolean> =
    tokenManagerRepository.saveAccessToken(
      value
    )

  private suspend fun saveRefreshToken(value: String): MyResult<Boolean> =
    tokenManagerRepository.saveRefreshToken(value)

  private suspend fun getAccessToken(): MyResult<String?> = tokenManagerRepository.getAccessToken()
  suspend fun getRefreshToken(): MyResult<String?> =
    tokenManagerRepository.getRefreshToken()

  suspend fun resetTokens(): MyResult<Boolean> {
    val refresh = tokenManagerRepository.deleteRefreshToken()
    val access = tokenManagerRepository.deleteAccessToken()
    val email = tokenManagerRepository.deleteUsername()

    if (refresh is MyResult.Success && access is MyResult.Success && email is MyResult.Success) {
      return MyResult.Success(true)
    }

    return MyResult.Error(false)
  }

  private suspend fun getTokenFromApi(email: String, password: String): SimpleResource =
    try {
      val response = awsApi.getJwtToken(
        base = Constants.AWS_PROVIDER, auth = Auth(
          authParameters = AuthParameters(
            password = password, username = email
          )
        )
      )
      val accessToken = response.authenticationResult.accessToken
      val refreshToken = response.authenticationResult.refreshToken
      run {
        saveAccessToken(accessToken)
        saveRefreshToken(refreshToken)
        MyResult.Success(Unit)
      }
    } catch (e: IOException) {
      Timber.e("Error getting token from AWS ${e.message}")
      MyResult.Error(uiText = R.string.general_error)
    } catch (e: HttpException) {
      when (e.code()) {
        400 -> {
          val gson = Gson()
          val errorResponse = gson.fromJson<AuthenticationResultException>(
            e.response()?.errorBody()?.charStream(),
            object : TypeToken<AuthenticationResultException>() {}.type
          )

          Timber.e("AWS error $errorResponse")
          when (errorResponse.type) {
            "NotAuthorizedException" -> MyResult.Error(uiText = R.string.login_incorrect_email_password)
            else -> MyResult.Error(uiText = R.string.general_error)
          }
        }

        else -> {
          Timber.e("Error getting token from AWS ${e.message}")
          MyResult.Error(uiText = R.string.general_error)
        }
      }
    }

  suspend fun getAccessToken(email: String, password: String): SimpleResource {
//    return getTokenFromApi(email, password)
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
                Timber.d(savedRefreshToken.uiText.toString())
              }
            }
          }
        }

        is MyResult.Error -> {
          Timber.d("Access Token not found, requesting a new one")
          return getTokenFromApi(email, password)
        }
      }
      //Timber.e("Login error $")
      MyResult.Error(uiText = R.string.credentials_error)
    } catch (e: Exception) {
      Timber.e("Error retrieving credentials ${e.message}")
      MyResult.Error(null, R.string.credentials_error)
    }
  }

  suspend fun refreshToken(auth: Auth): MyResult<CognitoResponse> = try {
    val result = awsApi.refreshToken(base = Constants.AWS_PROVIDER, auth)
    MyResult.Success(result)
  } catch (e: Exception) {
    Timber.e("Error refreshing the token ${e.message}")
    MyResult.Error(null, R.string.credentials_error)
  }
}