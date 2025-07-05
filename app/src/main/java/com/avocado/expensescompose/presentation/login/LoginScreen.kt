package com.avocado.expensescompose.presentation.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.avocado.expensescompose.R
import com.avocado.expensescompose.presentation.login.viewmodel.LoginViewModel
import com.avocado.expensescompose.presentation.login.viewmodel.LoginViewModelEvents
import com.avocado.expensescompose.presentation.navigation.NavigateEvent
import com.avocado.expensescompose.presentation.util.Operations

@Composable
fun LoginScreen(
  viewModel: LoginViewModel = hiltViewModel(),
  onNavigate: (navigateEvent: NavigateEvent, operation: String) -> Unit
) {
  val state by viewModel.uiState.collectAsStateWithLifecycle()

  if (state.isSuccess) {
    LaunchedEffect(key1 = Unit) {
      onNavigate(NavigateEvent.NavigateHomeScreen, Operations.SUCCESS_LOGIN.name)
    }
  }

  LoginScreen(
    username = state.username,
    password = state.password,
    userMessage = state.userMessage,
    shouldShowPassword = state.shouldShowPassword,
    isQuickLogin = state.isQuickLogin,
    isLoading = state.isLoading,
    emailHasError = state.emailHasError,
    passwordHasError = state.passwordHasError,
    isButtonEnabled = state.isButtonEnabled,
    onEvent = viewModel::onEvent
  )
}

@Composable
fun LoginScreen(
  username: String,
  password: String,
  userMessage: Int?,
  shouldShowPassword: Boolean,
  isQuickLogin: Boolean,
  isLoading: Boolean,
  emailHasError: Boolean = false,
  passwordHasError: Boolean = false,
  isButtonEnabled: Boolean = true,
  onEvent: (event: LoginViewModelEvents, value: String) -> Unit
) {
  Surface {
    Box(modifier = Modifier.fillMaxSize()) {
      Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Text(text = stringResource(id = R.string.login_login), modifier = Modifier)
        OutlinedTextField(
          value = username,
          onValueChange = { onEvent(LoginViewModelEvents.UpdateUsername, it) },
          placeholder = { Text(text = stringResource(id = R.string.login_user)) },
          isError = emailHasError,
          supportingText = {
            if (emailHasError) {
              Text(text = stringResource(id = R.string.login_email_error), color = Color.Red)
            }
          }
        )
        OutlinedTextField(
          value = password,
          onValueChange = { onEvent(LoginViewModelEvents.UpdatePassword, it) },
          placeholder = { Text(text = stringResource(id = R.string.login_password)) },
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
          enabled = !isQuickLogin,
          isError = passwordHasError,
          supportingText = {
            if (passwordHasError) {
              Text(text = stringResource(id = R.string.login_password_error), color = Color.Red)
            }
          },
          trailingIcon = {
            IconButton(onClick = { onEvent(LoginViewModelEvents.ToggleViewPassword, "") }) {
              if (shouldShowPassword) {
                Icon(
                  painter = painterResource(id = R.drawable.baseline_visibility_24),
                  contentDescription = stringResource(id = R.string.login_show_password)
                )
              } else {
                Icon(
                  painter = painterResource(id = R.drawable.baseline_visibility_off_24),
                  contentDescription = stringResource(id = R.string.login_hide_password)
                )
              }
            }
          },
          visualTransformation = if (shouldShowPassword) VisualTransformation.None else PasswordVisualTransformation()
        )

        Button(
          enabled = isButtonEnabled,
          onClick = { onEvent(LoginViewModelEvents.Login, "") }
        ) {
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
            Text(text = stringResource(id = R.string.login_login))
          }
        }

        userMessage.takeIf { it != 0 }?.let {
          Text(text = stringResource(it))
        }

        if (isQuickLogin) {
          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            TextButton(onClick = { onEvent(LoginViewModelEvents.ChangeUser, "") }) {
              Text(text = stringResource(id = R.string.login_change_user), fontSize = 14.sp)
            }
          }
        }
      }
    }
  }
}
