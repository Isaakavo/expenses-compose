package com.avocado.expensescompose.presentation.incomes.incomewithexpense.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.avocado.expensescompose.R
import com.avocado.expensescompose.data.model.card.Card
import com.avocado.expensescompose.data.model.expense.Expense
import com.avocado.expensescompose.presentation.incomes.incomewithexpense.IncomeWithExpenseEvent
import com.avocado.expensescompose.presentation.navigation.NavigateEvent
import com.avocado.expensescompose.presentation.shared.DeleteAlertDialog
import com.avocado.expensescompose.presentation.shared.ExpensesList
import com.avocado.expensescompose.presentation.topbar.AppBar
import com.avocado.expensescompose.presentation.topbar.MenuItems
import com.avocado.expensescompose.presentation.util.Operations
import timber.log.Timber

@Composable
fun IncomeWithExpensesContent(
  incomeId: String,
  incomesTotal: Double,
  fortnight: String,
  month: String,
  remaining: Double,
  expended: Double,
  expenseList: List<Expense>,
  cards: Set<Card>,
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
                Operations.FETCH.name
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
          deleteMessage = stringResource(
            id = if (shouldDeleteIncome) R.string.income_expense_delete_income else R.string.income_expense_delete_expense
          ),
          onConfirmRequest = {
            if (shouldDeleteIncome) {
              onEvent(
                IncomeWithExpenseEvent.ConfirmDeleteIncome,
                incomeId
              )
            } else {
              onEvent(IncomeWithExpenseEvent.ConfirmDeleteExpense, "")
            }
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
            cards = cards,
            onEdit = { onNavigate(NavigateEvent.NavigateEditExpenseScreen, it) },
            onDelete = { onEvent(IncomeWithExpenseEvent.DeleteExpense, it) }
          )
        }
      }
    }
  }
}
