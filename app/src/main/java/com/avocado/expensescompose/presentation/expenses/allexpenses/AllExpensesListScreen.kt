package com.avocado.expensescompose.presentation.expenses.allexpenses

import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.avocado.expensescompose.R
import com.avocado.expensescompose.data.model.expense.Expense
import com.avocado.expensescompose.presentation.charts.ChartType
import com.avocado.expensescompose.presentation.expenses.allexpenses.components.AllExpensesListContent
import com.avocado.expensescompose.presentation.expenses.allexpenses.components.ChartDataEntityType
import com.avocado.expensescompose.presentation.expenses.allexpenses.viewmodel.AllExpensesListEvents
import com.avocado.expensescompose.presentation.expenses.allexpenses.viewmodel.AllExpensesListViewModel
import com.avocado.expensescompose.presentation.navigation.NavigateEvent
import com.avocado.expensescompose.presentation.shared.CustomScaffold
import com.avocado.expensescompose.ui.theme.LocalSnackBarHostState
import kotlinx.coroutines.launch

@Composable
fun AllExpensesListScreen(
  viewModel: AllExpensesListViewModel = hiltViewModel(),
  payBeforeInput: String? = null,
  dateRange: LongRange? = null,
  isChartScreen: Boolean = false,
  isSingleScreen: Boolean = false,
  onNavigate: (navigateEvent: NavigateEvent, operation: String) -> Unit = { one, two -> },
  onSetData: (expenseList: List<Expense>) -> Unit = {}
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
          duration = SnackbarDuration.Short
        )

        when (snackBarResult) {
          SnackbarResult.ActionPerformed -> {
            viewModel.onEvent(AllExpensesListEvents.UpdateSuccessDelete, "", null)
            viewModel.onEvent(AllExpensesListEvents.UpdateDeleteExpenseId, "", null)
            viewModel.onEvent(AllExpensesListEvents.RestoreLists, "", null)
          }

          SnackbarResult.Dismissed -> {
            viewModel.deleteExpense()
          }
        }

        viewModel.onEvent(AllExpensesListEvents.UpdateSuccessDelete, "", null)
      }
    }
  }

  LaunchedEffect(key1 = state.uiError) {
    if (state.uiError != null && state.uiError != 0) {
      snackBarHostState.showSnackbar(
        context.resources.getString(state.uiError!!),
        duration = SnackbarDuration.Short
      )
    }
  }

  LaunchedEffect(key1 = state.filteredExpenses) {
    onSetData(state.filteredExpenses)
  }

  // TODO handle in a better way this logic
  if (isSingleScreen) {
    CustomScaffold {
      AllExpensesListContent(
        filteredList = state.filteredExpenses,
        totalExpenses = state.totalExpenses,
        cards = state.cards,
        isLoading = state.isLoading,
        isChartScreen = isChartScreen,
        chartType = state.chartType,
        onEdit = { onNavigate(NavigateEvent.NavigateEditExpenseScreen, it) },
        onEvent = viewModel::onEvent
      )
    }
  } else {
    AllExpensesListContent(
      filteredList = state.filteredExpenses,
      totalExpenses = state.totalExpenses,
      cards = state.cards,
      isLoading = state.isLoading,
      isChartScreen = isChartScreen,
      onEdit = { onNavigate(NavigateEvent.NavigateEditExpenseScreen, it) },
      onEvent = viewModel::onEvent
    )
  }
}

@Composable
fun ChartDatFilterMenu(onFilterSelect: (String) -> Unit) {
  val context = LocalContext.current
  var expanded by remember { mutableStateOf(false) }
  var buttonText by remember {
    mutableStateOf(context.resources.getString(R.string.expenses_list_filter_data))
  }

  OutlinedButton(onClick = { expanded = !expanded }) {
    Text(text = buttonText, modifier = Modifier)
    Icon(imageVector = Icons.Rounded.ArrowDropDown, contentDescription = "")
  }

  Box {
    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = !expanded }) {
      DropdownMenuItem(
        text = {
          Text(text = "Category")
        },
        onClick = {
          expanded = !expanded
          buttonText = "Category"
          onFilterSelect(ChartDataEntityType.CATEGORY.name)
        }
      )

      DropdownMenuItem(
        text = {
          Text(text = "Months")
        },
        onClick = {
          expanded = !expanded
          buttonText = "Months"
          onFilterSelect(ChartDataEntityType.MONTH.name)
        }
      )

      DropdownMenuItem(
        text = {
          Text(text = "Concept")
        },
        onClick = {
          expanded = !expanded
          buttonText = "Concept"
          onFilterSelect(ChartDataEntityType.CONCEPT.name)
        }
      )

      DropdownMenuItem(
        text = {
          Text(text = "Concept quantity")
        },
        onClick = {
          expanded = !expanded
          buttonText = "Concept quantity"
          onFilterSelect(ChartDataEntityType.CONCEPT_QUANTITY.name)
        }
      )
    }
  }
}

@Composable
fun ChartTypeMenu(onChartSelected: (chartType: ChartType) -> Unit) {
  val context = LocalContext.current
  var expanded by remember { mutableStateOf(false) }
  var buttonText by remember {
    mutableStateOf(context.resources.getString(R.string.expenses_list_chart_type))
  }

  OutlinedButton(onClick = { expanded = !expanded }) {
    Text(text = buttonText, modifier = Modifier)
    Icon(imageVector = Icons.Rounded.ArrowDropDown, contentDescription = "")
  }

  Box {
    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = !expanded }) {
      DropdownMenuItem(
        text = {
          Text(text = "Bar")
        },
        onClick = {
          expanded = !expanded
          buttonText = "Bar"
          onChartSelected(ChartType.BAR)
        }
      )

      DropdownMenuItem(
        text = {
          Text(text = "Pie")
        },
        onClick = {
          expanded = !expanded
          buttonText = "Pie"
          onChartSelected(ChartType.PIE)
        }
      )
    }
  }
}
