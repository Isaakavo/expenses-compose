package com.avocado.expensescompose.presentation.login

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.avocado.expensescompose.data.model.MyResult
import com.avocado.expensescompose.data.model.auth.Auth
import com.avocado.expensescompose.data.model.auth.AuthParameters
import com.avocado.expensescompose.data.repositories.AuthRepository
import com.avocado.expensescompose.data.repositories.TokenManagerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LoginUiState(
  val username: String = "isaakhaas96@gmail.com",
  val password: String = "Weisses9622!",
  val isLoading: Boolean = false,
  val shouldShowPassword: Boolean = false,
  var userMessage: String? = null,
  var isSuccess: Boolean = false,
  val retryLogin: Boolean = false
)

@HiltViewModel
class LoginViewModel @Inject constructor(
  private val authRepository: AuthRepository,
  private val tokenManagerRepository: TokenManagerRepository
) : ViewModel() {

  private val _uiState = MutableStateFlow(LoginUiState())
  val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

  fun updateUsername(newValue: String) {
    _uiState.update {
      it.copy(username = newValue)
    }
  }

  fun updatePassword(newValue: String) {
    _uiState.update {
      it.copy(password = newValue)
    }
  }

  fun onToggleViewPassword() {
    _uiState.update {
      it.copy(shouldShowPassword = !it.shouldShowPassword)
    }
  }

  private suspend fun saveAccessToken(value: String): MyResult<Boolean> =
    tokenManagerRepository.saveAccessToken(
      value
    )

  private suspend fun saveRefreshToken(value: String): MyResult<Boolean> =
    tokenManagerRepository.saveRefreshToken(value)

  private suspend fun getAccessToken(): MyResult<String?> = tokenManagerRepository.getAccessToken()
  private suspend fun getRefreshToken(): MyResult<String?> =
    tokenManagerRepository.getRefreshToken()

  fun login() {
    val auth = Auth(
      authParameters = AuthParameters(
        password = uiState.value.password, username = uiState.value.username
      )
    )

    viewModelScope.launch(Dispatchers.IO) {
      _uiState.update {
        it.copy(isLoading = true)
      }

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
                  _uiState.update {
                    it.copy(isLoading = false, isSuccess = true)
                  }
                  return@launch
                }
              }

              is MyResult.Error -> {
                Log.d("JWT", savedRefreshToken.exception.toString())
              }
            }
          }
        }

        is MyResult.Error -> {

        }
      }

      // If there is no access nor refresh token, make the call to the API
      // and store the tokens
      when (val response = authRepository.getAccessToken(auth)) {
        is MyResult.Success -> {
          val accessToken = response.data.authenticationResult.accessToken
          val refreshToken = response.data.authenticationResult.refreshToken
          saveAccessToken(accessToken)
          saveRefreshToken(refreshToken)
          Log.d("JWT", "Access Token saved $accessToken")
          Log.d("JWT", "Refresh Token saved $refreshToken")
          _uiState.update {
            it.copy(isLoading = false, isSuccess = true)
          }
        }

        is MyResult.Error -> {
          _uiState.update {
            it.copy(userMessage = response.exception, isLoading = false)
          }
        }
      }
    }
  }
}