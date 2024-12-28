package com.avocado.expensescompose.presentation.expenses.allexpenses

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.avocado.expensescompose.R
import com.avocado.expensescompose.data.model.expense.Expense
import com.avocado.expensescompose.presentation.charts.ChartType
import com.avocado.expensescompose.presentation.expenses.allexpenses.components.AllExpensesListContent
import com.avocado.expensescompose.presentation.expenses.allexpenses.components.ChartDataEntityType
import com.avocado.expensescompose.presentation.expenses.allexpenses.components.ExpensesCharts
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
  isChartScreen: Boolean = false,
  chartType: ChartType = ChartType.BAR,
  onNavigateBack: () -> Unit = {},
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

  if (isChartScreen) {
    var entityType by remember {
      mutableStateOf(ChartDataEntityType.CATEGORY)
    }
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(top = 8.dp, start = 16.dp, end = 16.dp, bottom = 12.dp),
      verticalArrangement = Arrangement.spacedBy(15.dp)
    ) {
      Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
        ChartDatFilterMenu { filter -> entityType = ChartDataEntityType.valueOf(filter) }
      }
      ExpensesCharts(
        filteredList = state.filteredExpenses,
        chartType = chartType,
        entityType = entityType,
        modifier = Modifier
          .fillMaxHeight()
          .weight(1f)
      )
      AllExpensesListContent(
        filteredList = state.filteredExpenses,
        totalExpenses = state.totalExpenses,
        cards = state.cards,
        isLoading = state.isLoading,
        modifier = Modifier.weight(1f),
        onEdit = { onNavigate(NavigateEvent.NavigateEditExpenseScreen, it) },
        onEvent = viewModel::onEvent
      )
    }
    return
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

// TODO move this to own component and use events to handle data selection
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
