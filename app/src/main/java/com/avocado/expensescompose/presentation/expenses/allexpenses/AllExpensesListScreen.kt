package com.avocado.expensescompose.presentation.expenses.allexpenses

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.avocado.expensescompose.presentation.expenses.allexpenses.components.AllExpensesListContent
import com.avocado.expensescompose.presentation.expenses.allexpenses.viewmodel.AllExpensesListViewModel
import com.avocado.expensescompose.presentation.navigation.NavigateEvent

@Composable
fun AllExpensesListScreen(
  viewModel: AllExpensesListViewModel = hiltViewModel(),
  payBeforeInput: String? = null,
  onNavigate: (navigateEvent: NavigateEvent, operation: String) -> Unit = { one, two -> }
) {
  val state by viewModel.state.collectAsStateWithLifecycle()

  LaunchedEffect(key1 = payBeforeInput) {
    if (payBeforeInput != null) {
      viewModel.getExpensesByFortnight(payBeforeInput)
    } else {
      viewModel.getAllExpenses()
    }
  }

  AllExpensesListContent(
    filteredList = state.filteredExpenses,
    totalExpenses = state.totalExpenses,
    cards = state.cards,
    isLoading = state.isLoading,
    onEdit = { onNavigate(NavigateEvent.NavigateEditExpenseScreen, it) },
    onEvent = viewModel::onEvent
  )
}
