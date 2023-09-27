package com.avocado.expensescompose.presentation.login

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.avocado.expensescompose.data.model.Result
import com.avocado.expensescompose.data.model.auth.Auth
import com.avocado.expensescompose.data.model.auth.AuthParameters
import com.avocado.expensescompose.data.repositories.AuthRepository
import com.avocado.expensescompose.data.repositories.DataStoreRepository
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
    var userMessage: String? = null
)

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository, private val dataStoreRepository: DataStoreRepository
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

    private suspend fun saveToken(value: String) = dataStoreRepository.putString("JWT", value)

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
            when (val response = authRepository.getJwtToken(auth)) {
                is Result.Success -> {
                    val token = response.data.authenticationResult.accessToken
                    saveToken(token)
                    Log.d("JWT", "Token saved $token")
                    _uiState.update {
                        it.copy(isLoading = false)
                    }
                }

                is Result.Error -> {
                    _uiState.update {
                        it.copy(userMessage = response.exception, isLoading = false)
                    }
                }
            }
        }
    }
}