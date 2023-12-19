package com.avocado.expensescompose.presentation.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.avocado.expensescompose.R
import com.avocado.expensescompose.presentation.navigation.NavigateEvent


@Composable
fun LoginScreen(
  viewModel: LoginViewModel = hiltViewModel(),
  onNavigate: (navigateEvent: NavigateEvent, shouldRefresh: Boolean, isSuccessLogin: Boolean) -> Unit,
) {
  val uiState by viewModel.uiState.collectAsStateWithLifecycle()

  LaunchedEffect(key1 = uiState.isSuccess) {
    onNavigate(NavigateEvent.NavigateIncomeOverview, false, uiState.isSuccess)
  }

  LoginScreenContent(
    username = uiState.username,
    password = uiState.password,
    userMessage = uiState.userMessage ?: "",
    shouldShowPassword = uiState.shouldShowPassword,
    isLoading = uiState.isLoading,
    onEvent = viewModel::onEvent,
  )
}

@Composable
fun LoginScreenContent(
  username: String,
  password: String,
  userMessage: String,
  shouldShowPassword: Boolean,
  isLoading: Boolean,
  onEvent: (event: LoginEvent, value: String) -> Unit,
) {
  Box(modifier = Modifier.fillMaxSize()) {
    Column(
      modifier = Modifier.fillMaxSize(),
      verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Text(text = "Iniciar Sesión", modifier = Modifier)
      OutlinedTextField(
        value = username,
        onValueChange = { onEvent(LoginEvent.UpdateUsername, it) },
        placeholder = { Text(text = "User name") },
      )
      OutlinedTextField(
        value = password,
        onValueChange = { onEvent(LoginEvent.UpdatePassword, it) },
        placeholder = { Text(text = "Password") },
        trailingIcon = {
          IconButton(onClick = { onEvent(LoginEvent.ToggleViewPassword, "") }) {
            if (shouldShowPassword) Icon(
              painter = painterResource(id = R.drawable.baseline_visibility_24),
              contentDescription = "Mostrar contraseña"
            )
            else Icon(
              painter = painterResource(id = R.drawable.baseline_visibility_off_24),
              contentDescription = "Ocultar contraseña"
            )
          }
        },
        visualTransformation = if (shouldShowPassword) VisualTransformation.None else PasswordVisualTransformation()
      )

      Button(onClick = { onEvent(LoginEvent.Login, "") }) {
        Row(horizontalArrangement = Arrangement.SpaceBetween) {
          if (isLoading) {
            CircularProgressIndicator(
              color = Color.White,
              strokeWidth = 2.dp,
              modifier = Modifier
                .size(18.dp)
                .padding(top = 1.dp, end = 4.dp)
            )
          }
          Text(text = "Iniciar Sesión")
        }
      }

      if (userMessage.isNotBlank()) {
        Text(text = userMessage)
      }
    }
  }
}