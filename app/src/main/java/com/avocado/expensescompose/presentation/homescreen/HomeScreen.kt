package com.avocado.expensescompose.presentation.homescreen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.avocado.expensescompose.presentation.homescreen.components.HomeScreenContent
import com.avocado.expensescompose.presentation.homescreen.viewmodel.HomeScreenViewModel
import com.avocado.expensescompose.presentation.navigation.NavigateEvent
import java.time.LocalDateTime

@Composable
fun HomeScreen(
  operation: String = "",
  viewModel: HomeScreenViewModel = hiltViewModel(),
  onNavigate: (navigateEvent: NavigateEvent, income: LocalDateTime?) -> Unit,
  onNavigateCardsScreen: (navigateEvent: NavigateEvent, operation: String) -> Unit
) {
  val state by viewModel.state.collectAsStateWithLifecycle()

  HomeScreenContent(
    screens = state.screen,
    backPressState = state.backPressState,
    isLoading = state.isLoading,
    uiError = state.uiError,
    operation = operation,
    showToast = state.showToast,
    onNavigate = onNavigate,
    onEvent = viewModel::onEvent,
    onNavigateCardsScreen = onNavigateCardsScreen
  )
}
