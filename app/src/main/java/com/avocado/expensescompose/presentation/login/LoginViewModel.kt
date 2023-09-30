package com.avocado.expensescompose.presentation.login

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.avocado.expensescompose.data.model.MyResult
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
    var userMessage: String? = null,
    var isSuccess: Boolean = false
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

    fun onToggleViewPassword() {
        _uiState.update {
            it.copy(shouldShowPassword = !it.shouldShowPassword)
        }
    }

    private suspend fun saveToken(value: String): MyResult<Boolean> =
        dataStoreRepository.putString("JWT", value)

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
            //TODO validate if the token already exists and if it exists, prevent the call to api
            // also validate in server if the token already expired
            when (val response = authRepository.getJwtToken(auth)) {
                is MyResult.Success -> {
                    val token = response.data.authenticationResult.accessToken
                    when (val isSaved = saveToken(token)) {
                        is MyResult.Success -> {
                            Log.d("JWT", "Token saved $token")
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

                //TODO implement logic to handle errors and display correct message
                is MyResult.Error -> {
                    _uiState.update {
                        it.copy(userMessage = response.exception, isLoading = false)
                    }
                }
            }
        }
    }
}