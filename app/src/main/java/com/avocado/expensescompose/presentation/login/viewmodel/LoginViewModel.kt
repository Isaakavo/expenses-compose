package com.avocado.expensescompose.presentation.login.viewmodel

import android.util.Patterns.EMAIL_ADDRESS
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.avocado.LoginQuery
import com.avocado.expensescompose.R
import com.avocado.expensescompose.data.adapters.graphql.utils.awaitResult
import com.avocado.expensescompose.data.apolloclients.GraphQlClientImpl
import com.avocado.expensescompose.data.model.MyResult
import com.avocado.expensescompose.data.model.onError
import com.avocado.expensescompose.data.model.onSuccess
import com.avocado.expensescompose.data.model.successOrError
import com.avocado.expensescompose.domain.login.usecase.LoginUseCase
import com.avocado.type.AUTH_STATUS
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber

// TODO add remember password logic
// Add a internal database so I can store the password there, and every time the user clicks in
// login, the code retrieve the hashed password and use it to send the request to aws
data class LoginViewModelState(
  val username: String = "",
  val password: String = "",
  val isLoading: Boolean = false,
  val shouldShowPassword: Boolean = false,
  val isQuickLogin: Boolean = false,
  val userMessage: Int? = null,
  val isSuccess: Boolean = false,
  val emailHasError: Boolean = false,
  val isButtonEnabled: Boolean = true,
  val passwordHasError: Boolean = false
)

@HiltViewModel
class LoginViewModel @Inject constructor(
  private val loginUseCase: LoginUseCase,
  private val graphQlClientImpl: GraphQlClientImpl
) : ViewModel() {

  private val _uiState = MutableStateFlow(LoginViewModelState())
  val uiState: StateFlow<LoginViewModelState> = _uiState.asStateFlow()

  init {
    getUsername()
    refreshTokenExist()
  }

  private fun refreshTokenExist() {
    viewModelScope.launch {
      val result = loginUseCase.getRefreshToken()
      if (result is MyResult.Success && result.data != null) {
        _uiState.update {
          it.copy(isQuickLogin = true, password = "Fake value for now")
        }
      }
    }
  }

  fun onEvent(event: LoginViewModelEvents, value: String) {
    when (event) {
      LoginViewModelEvents.ToggleViewPassword -> _uiState.update {
        it.copy(shouldShowPassword = !it.shouldShowPassword)
      }

      LoginViewModelEvents.UpdatePassword -> _uiState.update {
        val hasError = validatePasswordInput(value)
        it.copy(password = value, passwordHasError = hasError, isButtonEnabled = !hasError)
      }

      LoginViewModelEvents.UpdateUsername -> _uiState.update {
        val hasError = validateEmailInput(value)
        it.copy(username = value, emailHasError = hasError, isButtonEnabled = !hasError)
      }

      LoginViewModelEvents.Login -> login()

      LoginViewModelEvents.SetIsSuccess -> _uiState.update {
        it.copy(isSuccess = false)
      }

      LoginViewModelEvents.ChangeUser -> {
        viewModelScope.launch {
          val result = loginUseCase.resetLoginCredentials()
          if (result is MyResult.Success) {
            _uiState.update { it.copy(password = "", isQuickLogin = false, username = "") }
          }
        }
      }
    }
  }

  private fun validateEmailInput(username: String): Boolean =
    username.isNotBlank() && !EMAIL_ADDRESS.matcher(username).matches()

  private fun validatePasswordInput(password: String): Boolean = password.isBlank()

  fun login() =
    viewModelScope
      .launch {
        _uiState.update {
          it.copy(isLoading = true, isButtonEnabled = false)
        }

        if (saveUsername()) {
          validateLogin()
        }

        _uiState.update {
          it.copy(isLoading = false, isButtonEnabled = true)
        }
      }

  private suspend fun validateLogin() =
    graphQlClientImpl
      .query(LoginQuery()) { throwable ->
        Timber.e("Error validating login: ${throwable.message}")
        _uiState.update { it.copy(userMessage = R.string.general_error) }
      }
      .awaitResult()
      .onSuccess { response ->
        response?.login?.status
          .let { status ->
            val loginResult = loginUseCase(
              email = uiState.value.username.trim(),
              password = uiState.value.password.trim()
            )

            if (loginResult.emailError != null) {
              _uiState.update {
                it.copy(userMessage = R.string.login_incorrect_email)
              }
            }
            if (loginResult.passwordError != null) {
              _uiState.update {
                it.copy(userMessage = R.string.login_incorrect_password)
              }
            }

            loginResult
              .result
              ?.onSuccess {
                Timber.d("Setting success")
                _uiState.update {
                  it.copy(isSuccess = status == AUTH_STATUS.AUTHENTICATED)
                }
              }
          }
      }
      .onError { throwable ->
        Timber.e("Error validating login: ${throwable.message}")
        _uiState.update { it.copy(userMessage = R.string.general_error) }
      }

  private suspend fun saveUsername() =
    loginUseCase
      .saveUsername(_uiState.value.username)
      .successOrError(
        onSuccess = {
          Timber.d("Username saved successfully")
          true
        },
        onError = { error ->
          Timber.e("Error saving username: ${error.uiText}")
          _uiState.update { it.copy(userMessage = error.uiText) }
          false
        }
      )

  private fun getUsername() {
    viewModelScope.launch {
      when (val username = loginUseCase.getUsernameFromStorage()) {
        is MyResult.Error -> {
          _uiState.update { it.copy(userMessage = username.uiText) }
        }

        is MyResult.Success -> {
          _uiState.update { it.copy(username = username.data.orEmpty()) }
        }
      }
    }
  }
}
