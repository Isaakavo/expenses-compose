package com.avocado.expensescompose.presentation.login

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.avocado.expenses.shared.Greeting
import com.avocado.expensescompose.presentation.login.components.LoginScreenContent
import com.avocado.expensescompose.presentation.login.viewmodel.LoginViewModel
import com.avocado.expensescompose.presentation.navigation.NavigateEvent
import com.avocado.expensescompose.presentation.util.Operations
import timber.log.Timber

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
  Timber.i("Login Activity", "Hello from shared module: " + (Greeting().greet()))

  LoginScreenContent(
    username = state.username,
    password = state.password,
    userMessage = state.userMessage,
    shouldShowPassword = state.shouldShowPassword,
    isQuickLogin = state.isQuickLogin,
    isLoading = state.isLoading,
    onEvent = viewModel::onEvent
  )
}
