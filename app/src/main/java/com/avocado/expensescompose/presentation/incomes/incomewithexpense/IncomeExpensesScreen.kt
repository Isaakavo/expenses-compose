package com.avocado.expensescompose.presentation.incomes.incomewithexpense

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.avocado.expensescompose.R
import com.avocado.expensescompose.data.adapters.formatMoney
import com.avocado.expensescompose.data.model.expense.Expense
import com.avocado.expensescompose.presentation.navigation.NavigateEvent
import com.avocado.expensescompose.presentation.shared.DeleteAlertDialog
import com.avocado.expensescompose.presentation.shared.ExpensesList
import com.avocado.expensescompose.presentation.topbar.AppBar
import com.avocado.expensescompose.presentation.topbar.MenuItems
import com.avocado.expensescompose.presentation.util.Operations
import com.avocado.expensescompose.presentation.util.formatDateMonthWithYear
import timber.log.Timber

@Composable
fun IncomeExpensesScreen(
  viewModel: IncomeWithExpenseViewModel = hiltViewModel(),
  paymentDate: String,
  onNavigateBack: () -> Unit = {},
  onNavigate: (navigateEvent: NavigateEvent, operation: String) -> Unit = { one, two -> },
  onEditIncome: (navigateEvent: NavigateEvent, incomeId: String) -> Unit = { one, two -> }
) {
  val state by viewModel.state.collectAsStateWithLifecycle()

  LaunchedEffect(key1 = paymentDate) {
    viewModel.getIncomesWithExpenses(paymentDate)
  }


  if (state.isDeleted) {
    LaunchedEffect(key1 = Unit) {
      onNavigate(NavigateEvent.NavigateIncomeOverview, Operations.DELETE.name)
    }
  }

  IncomeWithExpensesContent(
    incomeId = state.incomes?.get(0)?.id ?: "",
    incomesTotal = state.incomesTotal,
    fortnight = state.incomes?.get(0)?.paymentDate?.fortnight?.translate() ?: "",
    month = state.incomes?.get(0)?.paymentDate?.date?.formatDateMonthWithYear() ?: "",
    shouldDeleteIncome = state.shouldDeleteIncome,
    shouldDeleteExpense = state.shouldDeleteExpense,
    remaining = state.remaining,
    expended = state.expensesTotal,
    expenseList = state.expensesList,
    isLoading = state.isLoading,
    onNavigate = onNavigate,
    onNavigateBack = onNavigateBack,
    onEditIncome = onEditIncome,
    onEvent = viewModel::onEvent
  )
}

@Composable
fun IncomeWithExpensesContent(
  incomeId: String,
  incomesTotal: Double,
  fortnight: String,
  month: String,
  remaining: Double,
  expended: Double,
  expenseList: List<Expense>,
  isLoading: Boolean = false,
  shouldDeleteIncome: Boolean = false,
  shouldDeleteExpense: Boolean = false,
  onNavigateBack: () -> Unit = {},
  onNavigate: (navigateEvent: NavigateEvent, operation: String) -> Unit = { one, two -> },
  onEditIncome: (navigateEvent: NavigateEvent, incomeId: String) -> Unit = { one, two -> },
  onEvent: (event: IncomeWithExpenseEvent, param: String) -> Unit = { one, two -> }
) {
  Scaffold(
    topBar = {
      AppBar(
        title = stringResource(id = R.string.income_fortnight, fortnight),
        onNavigationIconClick = { onNavigateBack() },
        dropDownMenuItems = listOf(
          MenuItems(
            text = stringResource(id = R.string.appbar_edit),
            icon = Icons.Rounded.Edit,
            action = {
              // Hardcoding the first id for the income
              Timber.d("Clicking on edit")
              onEditIncome(NavigateEvent.NavigationEditIncomeScreen, incomeId)
            }
          ),
          MenuItems(
            text = stringResource(id = R.string.appbar_delete),
            icon = Icons.Rounded.Delete,
            action = { onEvent(IncomeWithExpenseEvent.DeleteIncome, "") }
          ),
          MenuItems(
            text = stringResource(id = R.string.appbar_add_expense),
            icon = Icons.Rounded.Add,
            action = {
              onNavigate(
                NavigateEvent.NavigateAddExpenseScreen,
                Operations.FETCH.name,
              )
            }
          )
        )
      )
    }
  ) { paddingValues ->
    Surface(
      modifier = Modifier
        .padding(paddingValues)
        .fillMaxSize()
    ) {
      if (isLoading) {
        Column(
          modifier = Modifier
            .fillMaxSize()
            .padding(22.dp)
        ) {
          CircularProgressIndicator(strokeWidth = 6.dp)
        }
      } else {
        DeleteAlertDialog(
          shouldDisplay = shouldDeleteIncome || shouldDeleteExpense,
          deleteMessage = stringResource(id = if (shouldDeleteIncome) R.string.income_expense_delete_income else R.string.income_expense_delete_expense),
          onConfirmRequest = {
            if (shouldDeleteIncome) onEvent(
              IncomeWithExpenseEvent.ConfirmDeleteIncome,
              incomeId
            ) else onEvent(IncomeWithExpenseEvent.ConfirmDeleteExpense, "")
          },
          onDismissRequest = { onEvent(IncomeWithExpenseEvent.CancelDeleteIncome, "") }
        )
        Column(
          modifier = Modifier
            .fillMaxSize()
            .padding(top = 8.dp, start = 16.dp, end = 16.dp, bottom = 12.dp),
          verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          IncomeDetails(
            incomesTotal = incomesTotal,
            remaining = remaining,
            expended = expended,
            month = month
          )
          ExpensesList(
            expenseList = expenseList,
            onEdit = { onNavigate(NavigateEvent.NavigateEditExpenseScreen, it) },
            onDelete = { onEvent(IncomeWithExpenseEvent.DeleteExpense, it) }
          )
        }
      }
    }
  }
}

@Composable
fun DateRow(date: String) {
  Row(
    modifier = Modifier
      .padding(bottom = 4.dp)
      .fillMaxWidth(),
    horizontalArrangement = Arrangement.Start
  ) {
    Text(
      text = date,
      fontSize = 20.sp
    )
  }
}

@Composable
fun IncomeDetails(incomesTotal: Double, remaining: Double, expended: Double, month: String) {
  Card(
    modifier = Modifier.fillMaxWidth(),
    elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
  ) {
    Column(
      modifier = Modifier.padding(16.dp),
      verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
      DateRow(date = month)
      Row(
        modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween
      ) {
        //TODO make text color of remaining
        // if remaining is more than income make it green if not red
        Text(
          text = stringResource(id = R.string.income_expense_income),
          color = MaterialTheme.colorScheme.secondary,
          style = MaterialTheme.typography.bodyLarge
        )
        Text(
          text = incomesTotal.formatMoney(),
          style = MaterialTheme.typography.bodyLarge,
          fontWeight = FontWeight.SemiBold
        )
      }

      Row(
        modifier = Modifier
          .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Text(
          text = stringResource(id = R.string.income_expense_expense),
          color = MaterialTheme.colorScheme.secondary,
          style = MaterialTheme.typography.bodyLarge
        )
        Text(
          text = expended.formatMoney(),
          style = MaterialTheme.typography.bodyLarge,
          fontWeight = FontWeight.SemiBold
        )
      }

      Row(
        modifier = Modifier
          .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Text(
          text = stringResource(id = R.string.income_expense_remaining),
          color = MaterialTheme.colorScheme.secondary,
          style = MaterialTheme.typography.bodyLarge
        )
        Text(
          text = remaining.formatMoney(),
          style = MaterialTheme.typography.bodyLarge,
          fontWeight = FontWeight.SemiBold
        )
      }
    }
  }
}

@Preview
@Composable
fun IncomeDetailsPreview() {
  Column(modifier = Modifier.padding(22.dp)) {
    IncomeDetails(
      incomesTotal = 18750.23,
      remaining = 5000.0,
      expended = 8000.0,
      month = "DECEMBER"
    )
  }
}
