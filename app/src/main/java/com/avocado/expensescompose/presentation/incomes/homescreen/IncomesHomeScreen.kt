package com.avocado.expensescompose.presentation.incomes.homescreen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.avocado.expensescompose.presentation.incomes.homescreen.components.IncomeScreenContent
import com.avocado.expensescompose.presentation.incomes.homescreen.viewmodel.IncomesViewModel
import com.avocado.expensescompose.presentation.navigation.NavigateEvent
import java.time.LocalDateTime

data class NavigationIncomeDetails(
  val paymentDate: LocalDateTime?
)

@Composable
fun IncomesScreen(
  operation: String = "",
  viewModel: IncomesViewModel = hiltViewModel(),
  onNavigate: (navigateEvent: NavigateEvent, income: NavigationIncomeDetails?) -> Unit,
  onNavigateCardsScreen: (navigateEvent: NavigateEvent, operation: String) -> Unit
) {
  val state by viewModel.state.collectAsStateWithLifecycle()

  IncomeScreenContent(
    backPressState = state.backPressState,
    isLoading = state.isLoading,
    uiError = state.uiError,
    operation = operation,
    incomesMap = state.incomesMap,
    totalByMonth = state.totalByMonth,
    showToast = state.showToast,
    onNavigate = onNavigate,
    onEvent = viewModel::onEvent,
    onNavigateCardsScreen = onNavigateCardsScreen
  )
}
