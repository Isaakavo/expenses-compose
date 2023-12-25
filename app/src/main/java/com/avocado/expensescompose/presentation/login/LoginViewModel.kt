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

      LoginEvent.Login -> login()

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
        email = uiState.value.username,
        password = uiState.value.password
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
            it.copy(userMessage = loginResult.result.uiText)
          }
        }

        else -> {}
      }

    }
  }
}