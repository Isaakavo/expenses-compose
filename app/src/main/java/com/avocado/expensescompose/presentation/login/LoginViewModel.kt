package com.avocado.expensescompose.presentation.login

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apollographql.apollo3.exception.ApolloHttpException
import com.avocado.expensescompose.data.model.MyResult
import com.avocado.expensescompose.data.model.auth.Auth
import com.avocado.expensescompose.data.model.auth.AuthParameters
import com.avocado.expensescompose.data.repositories.DataStoreRepository
import com.avocado.expensescompose.domain.login.GetLoginUseCase
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
  private val dataStoreRepository: DataStoreRepository, private val getLoginUseCase: GetLoginUseCase
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

  private suspend fun saveToken(value: String): MyResult<Boolean> = dataStoreRepository.putString(
    "JWT", value
  )

  private suspend fun getToken(): MyResult<String?> = dataStoreRepository.getString("JWT")

  private suspend fun validateSavedToken(tokenResponse: String?) {
    when (val isSaved = saveToken(tokenResponse ?: "")) {
      is MyResult.Success -> {
        Log.d("JWT", "Token saved $tokenResponse")
        _uiState.update {
          it.copy(isLoading = false, isSuccess = true)
        }
      }

      is MyResult.Error -> {
        _uiState.update {
          it.copy(isLoading = false, userMessage = isSaved.exception)
        }
      }
    }
  }

  private suspend fun requestToken(auth: Auth, token: String) {
    when (token.isBlank()) {
      true -> {
        requestNewTokenMutation(auth)
      }

      false -> {
        validateExistingToken(token)
      }
    }
  }

  private suspend fun validateExistingToken(token: String) {
    try {
      when (val response = getLoginUseCase.executeValidateToken(token)) {
        is MyResult.Success -> {
          val isExpired = response.data.data?.validateToken?.isExpired
          if (isExpired == true) {
            dataStoreRepository.putString("JWT", "")
            _uiState.update {
              it.copy(
                retryLogin = true
              )
            }
            return
          }
          validateSavedToken(response.data.data?.validateToken?.accessToken)
        }

        is MyResult.Error -> {

        }
      }
    } catch (exception: ApolloHttpException) {

    }
  }

  private suspend fun requestNewTokenMutation(auth: Auth) {
    try {
      when (val response = getLoginUseCase.executeGetToken(auth.authParameters)) {
        is MyResult.Success -> {
          val tokenResponse = response.data.data?.login?.accessToken
          val error = response.data.errors
          Log.d("JWT", error.toString())
          validateSavedToken(tokenResponse)
        }
        //TODO implement logic to handle errors and display correct message
        is MyResult.Error -> {
          _uiState.update {
            it.copy(userMessage = response.exception, isLoading = false)
          }
        }
      }
    } catch (exception: ApolloHttpException) {
      Log.d("JWT", exception.message ?: "")
      dataStoreRepository.putString("JWT", "")
      _uiState.update {
        it.copy(userMessage = exception.message, isLoading = false)
      }
    }
  }


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

      //TODO improve login process to refresh tokens
      when (val tokenFromDataStore = getToken()) {
        is MyResult.Success -> {
          Log.d("JWT", "The value extracted from data store is $tokenFromDataStore")
          requestToken(
            auth,
            tokenFromDataStore.data ?: ""
          )
          if (_uiState.value.retryLogin) {
            _uiState.update {
              it.copy(retryLogin = false)
            }
          }
        }

        is MyResult.Error -> {
          Log.d("JWT", "Error retrieving the JWT")
          _uiState.update {
            it.copy(isLoading = false, userMessage = "Error at login")
          }
        }
      }
    }
  }
}