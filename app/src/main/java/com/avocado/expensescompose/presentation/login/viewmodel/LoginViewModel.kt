package com.avocado.expensescompose.presentation.login.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.avocado.expensescompose.R
import com.avocado.expensescompose.data.model.MyResult
import com.avocado.expensescompose.domain.login.usecase.LoginUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber

@HiltViewModel
class LoginViewModel @Inject constructor(
  private val loginUseCase: LoginUseCase
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
        it.copy(password = value)
      }

      LoginViewModelEvents.UpdateUsername -> _uiState.update {
        it.copy(username = value)
      }

      LoginViewModelEvents.Login -> saveUsername()

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

  // TODO refactor this
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
            .e(
              "Error trying to save the username in data storage: " + isSaved.exception?.stackTraceToString()
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
          _uiState.update { it.copy(userMessage = username.uiText) }
        }

        is MyResult.Success -> {
          _uiState.update { it.copy(username = username.data.orEmpty()) }
        }
      }
    }
  }
}
