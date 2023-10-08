package com.avocado.expensescompose.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.avocado.expensescompose.data.model.MyResult
import com.avocado.expensescompose.domain.login.usecase.LoginUseCase
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
  private val loginUseCase: LoginUseCase
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

  //TODO if the token is available, remove the password field and add a
  // "is not you? change your account option
  // When a user changes the account, delete all saved tokens and make the call to the api
  // to validate the user
  fun login() {
    viewModelScope.launch(Dispatchers.IO) {
      _uiState.update {
        it.copy(isLoading = true)
      }
      val loginResult = loginUseCase(
        email = uiState.value.username,
        password = uiState.value.password
      )
      _uiState.update {
        it.copy(isLoading = false)
      }

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
          _uiState.update {
            it.copy(isSuccess = true)
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