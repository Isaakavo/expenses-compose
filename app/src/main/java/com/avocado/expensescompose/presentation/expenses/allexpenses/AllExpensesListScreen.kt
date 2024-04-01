package com.avocado.expensescompose.presentation.expenses.allexpenses

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.avocado.expensescompose.R
import com.avocado.expensescompose.presentation.expenses.allexpenses.components.AllExpensesListContent
import com.avocado.expensescompose.presentation.expenses.allexpenses.viewmodel.AllExpensesListEvents
import com.avocado.expensescompose.presentation.expenses.allexpenses.viewmodel.AllExpensesListViewModel
import com.avocado.expensescompose.presentation.navigation.NavigateEvent
import com.avocado.expensescompose.ui.theme.LocalSnackBarHostState
import kotlinx.coroutines.launch

@Composable
fun AllExpensesListScreen(
  viewModel: AllExpensesListViewModel = hiltViewModel(),
  payBeforeInput: String? = null,
  dateRange: LongRange? = null,
  onNavigate: (navigateEvent: NavigateEvent, operation: String) -> Unit = { one, two -> }
) {
  val state by viewModel.state.collectAsStateWithLifecycle()
  val scope = rememberCoroutineScope()
  val snackBarHostState = LocalSnackBarHostState.current
  val context = LocalContext.current

  LaunchedEffect(key1 = payBeforeInput, key2 = dateRange) {
    if (!payBeforeInput.isNullOrEmpty()) {
      viewModel.getExpensesByFortnight(payBeforeInput)
    } else {
      if (dateRange?.isEmpty() == true) {
        viewModel.getAllExpenses()
      } else {
        viewModel.getAllExpensesByDateRange(dateRange)
      }
    }
  }

  LaunchedEffect(key1 = state.successDelete) {
    if (state.successDelete) {
      scope.launch {
        val snackBarResult = snackBarHostState.showSnackbar(
          context.resources.getString(R.string.expenses_list_delete_successful),
          actionLabel = context.resources.getString(R.string.snackbar_undo),
          duration = SnackbarDuration.Long
        )

        when (snackBarResult) {
          SnackbarResult.ActionPerformed -> {
            viewModel.onEvent(AllExpensesListEvents.UpdateSuccessDelete, "", null, null)
            viewModel.onEvent(AllExpensesListEvents.UpdateDeleteExpenseId, "", null, null)
            viewModel.onEvent(AllExpensesListEvents.RestoreLists, "", null, null)
          }

          SnackbarResult.Dismissed -> {
            viewModel.deleteExpense()
          }
        }

        viewModel.onEvent(AllExpensesListEvents.UpdateSuccessDelete, "", null, null)
      }
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
