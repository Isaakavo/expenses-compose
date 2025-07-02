package com.avocado.expensescompose.data.source

import com.avocado.expensescompose.R
import com.avocado.expensescompose.data.model.MyResult
import com.avocado.expensescompose.data.model.auth.Auth
import com.avocado.expensescompose.data.model.auth.AuthParameters
import com.avocado.expensescompose.data.model.auth.AuthenticationResultException
import com.avocado.expensescompose.data.model.auth.CognitoResponse
import com.avocado.expensescompose.data.network.CognitoRetrofitWebClient
import com.avocado.expensescompose.presentation.util.Constants
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import java.io.IOException
import javax.inject.Inject
import retrofit2.HttpException
import timber.log.Timber

class CognitoAuthService @Inject constructor(
  private val cognitoRetrofitWebClient: CognitoRetrofitWebClient,
  private val gson: Gson
) {

  suspend fun signIn(email: String, password: String): MyResult<CognitoResponse> =
    try {
      val apiRequest = Auth( // Constructing the specific request for Cognito
        authParameters = AuthParameters( // Assuming AuthParameters is nested
          username = email,
          password = password
        )
      )
      // Actual API call using the Retrofit interface
      val response: CognitoResponse = cognitoRetrofitWebClient.getJwtToken(
        base = Constants.AWS_PROVIDER, // This might also be part of cognitoApi's setup
        auth = apiRequest
      )
      MyResult.Success(response)
    } catch (e: IOException) {
      Timber.e(e, "Cognito SignIn: Network error")
      MyResult.Error(data = null, uiText = R.string.network_error, exception = e)
    } catch (e: HttpException) {
      Timber.e(e, "Cognito SignIn: HTTP error ${e.code()} - ${e.message()}")
      parseSpecificCognitoHttpError(e) // Delegate to Cognito-specific HTTP error parsing
    } catch (e: Exception) {
      Timber.e(e, "Cognito SignIn: Unexpected error")
      MyResult.Error(data = null, uiText = R.string.general_error, exception = e)
    }

  suspend fun refreshToken(auth: Auth): MyResult<CognitoResponse> =
    try {
      val response = cognitoRetrofitWebClient.refreshToken(base = Constants.AWS_PROVIDER, auth)
      MyResult.Success(response)
    } catch (e: IOException) {
      Timber.e(e, "Cognito RefreshToken: Network error")
      MyResult.Error(data = null, uiText = R.string.credentials_error, exception = e)
    } catch (e: HttpException) {
      Timber.e(e, "Cognito RefreshToken: HTTP error ${e.code()} - ${e.message()}")
      parseSpecificCognitoHttpError(e)
    } catch (e: Exception) {
      Timber.e(e, "Cognito RefreshToken: Unexpected error")
      MyResult.Error(data = null, uiText = R.string.general_error, exception = e)
    }

  // This method is now part of the Cognito service, as it knows about Cognito error formats
  private fun <T> parseSpecificCognitoHttpError(e: HttpException): MyResult.Error<T> {
    val errorBody = e.response()?.errorBody()?.charStream()
    if (errorBody == null) {
      Timber.w("Cognito HTTP error ${e.code()}: Error body was null.")
      return MyResult.Error(data = null, uiText = R.string.general_error_body, exception = e)
    }

    return try {
      // Assuming AuthenticationResultException is the structure of Cognito's JSON error
      val errorResponse = gson.fromJson<AuthenticationResultException>(
        errorBody,
        object : TypeToken<AuthenticationResultException>() {}.type
      )
      Timber.w("Parsed Cognito error response: $errorResponse from code ${e.code()}")

      when (errorResponse?.type) { // Using the 'type' field from your AuthenticationResultException
        "NotAuthorizedException" -> MyResult.Error(
          data = null,
          uiText = R.string.login_incorrect_email_password,
          exception = e
        )

        "UserNotFoundException" -> MyResult.Error(
          data = null,
          uiText = R.string.general_error_body,
          exception = e
        )
        // Add more specific Cognito errors based on the 'type' field
        // e.g., "PasswordResetRequiredException", "UserNotConfirmedException", etc.
        else -> {
          Timber.w("Unknown Cognito error type: ${errorResponse?.type} from code ${e.code()}")
          MyResult.Error(data = null, uiText = R.string.general_error_body, exception = e)
        }
      }
    } catch (jsonEx: JsonSyntaxException) {
      Timber.e(jsonEx, "Cognito HTTP error ${e.code()}: Failed to parse error JSON.")
      MyResult.Error(data = null, uiText = R.string.general_error_body, exception = jsonEx)
    } catch (ex: Exception) {
      Timber.e(ex, "Cognito HTTP error ${e.code()}: Unexpected error during error parsing.")
      MyResult.Error(data = null, uiText = R.string.general_error_body, exception = ex)
    }
  }
}
