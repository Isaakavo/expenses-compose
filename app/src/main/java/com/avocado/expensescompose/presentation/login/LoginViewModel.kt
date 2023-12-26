package com.avocado.expensescompose.presentation.login

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.avocado.expensescompose.data.model.MyResult
import com.avocado.expensescompose.domain.login.usecase.LoginUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class LoginEvent {
  object UpdateUsername : LoginEvent()
  object UpdatePassword : LoginEvent()
  object ToggleViewPassword : LoginEvent()
  object Login : LoginEvent()
  object SetIsSuccess : LoginEvent()
}

// TODO add remember password logic
// Add a internal database so I can store the password there, and every time the user clicks in
// login, the code retrieve the hashed password and use it to send the request to aws
data class LoginUiState(
  val username: String = "",
  val password: String = "",
  val isLoading: Boolean = false,
  val shouldShowPassword: Boolean = false,
  var userMessage: String? = null,
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
          it.copy(userMessage = "Email incorrecto")
        }
      }
      if (loginResult.passwordError != null) {
        _uiState.update {
          it.copy(userMessage = "password incorrecto")
        }
      }

      when (loginResult.result) {
        is MyResult.Success -> {
          Log.d("LoginViewModel", "Setting success")
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
          Log.e(
            "LoginViewModel",
            "Error trying to save the username in data storage ${isSaved.exception?.stackTraceToString()}"
          )
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
          _uiState.update { it.copy(userMessage = "${username.uiText}") }
        }

        is MyResult.Success -> {
          _uiState.update { it.copy(username = username.data.orEmpty()) }
        }
      }
    }
  }
}