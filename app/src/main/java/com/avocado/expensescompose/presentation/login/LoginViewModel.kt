package com.avocado.expensescompose.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.avocado.expensescompose.R
import com.avocado.expensescompose.data.model.MyResult
import com.avocado.expensescompose.domain.login.usecase.LoginUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

sealed class LoginEvent {
  object UpdateUsername : LoginEvent()
  object UpdatePassword : LoginEvent()
  object ToggleViewPassword : LoginEvent()
  object Login : LoginEvent()
  object SetIsSuccess : LoginEvent()
  object ChangeUser : LoginEvent()
}

// TODO add remember password logic
// Add a internal database so I can store the password there, and every time the user clicks in
// login, the code retrieve the hashed password and use it to send the request to aws
data class LoginUiState(
  val username: String = "",
  val password: String = "",
  val isLoading: Boolean = false,
  val shouldShowPassword: Boolean = false,
  val isQuickLogin: Boolean = false,
  var userMessage: Int? = null,
  var isSuccess: Boolean = false
)

@HiltViewModel
class LoginViewModel @Inject constructor(
  private val loginUseCase: LoginUseCase
) : ViewModel() {

  private val _uiState = MutableStateFlow(LoginUiState())
  val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

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

  fun onEvent(event: LoginEvent, value: String) {
    when (event) {
      LoginEvent.ToggleViewPassword -> _uiState.update {
        it.copy(shouldShowPassword = !it.shouldShowPassword)
      }

      LoginEvent.UpdatePassword -> _uiState.update {
        it.copy(password = value)
      }

      LoginEvent.UpdateUsername -> _uiState.update {
        it.copy(username = value)
      }

      LoginEvent.Login -> saveUsername()

      LoginEvent.SetIsSuccess -> _uiState.update {
        it.copy(isSuccess = false)
      }

      LoginEvent.ChangeUser -> {
        viewModelScope.launch {
          val result = loginUseCase.resetLoginCredentials()
          if (result is MyResult.Success) {
            _uiState.update { it.copy(password = "", isQuickLogin = false, username = "") }
          }
        }
      }
    }
  }

  //TODO refactor this
  fun login() {
    viewModelScope.launch {
      _uiState.update {
        it.copy(isLoading = true)
      }
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

      when (loginResult.result) {
        is MyResult.Success -> {
          Timber.d("Setting success")
          _uiState.update {
            it.copy(isSuccess = true, isLoading = false)
          }
        }

        is MyResult.Error -> {
          _uiState.update {
            it.copy(userMessage = loginResult.result.uiText, isLoading = false)
          }
        }

        else -> {}
      }

    }
  }

  private fun saveUsername() {
    viewModelScope.launch {
      when (val isSaved = loginUseCase.saveUsername(_uiState.value.username)) {
        is MyResult.Error -> {
          Timber
            .e("Error trying to save the username in data storage: " + isSaved.exception?.stackTraceToString())
        }

        is MyResult.Success -> {
          login()
        }
      }
    }
  }

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