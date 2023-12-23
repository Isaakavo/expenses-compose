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
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.avocado.expensescompose.data.adapters.formatMoney
import com.avocado.expensescompose.data.model.expense.Expense
import com.avocado.expensescompose.presentation.navigation.NavigateEvent
import com.avocado.expensescompose.presentation.shared.ExpensesList
import com.avocado.expensescompose.presentation.topbar.AppBar
import com.avocado.expensescompose.presentation.topbar.MenuItems
import com.avocado.expensescompose.presentation.util.formatDateMonthWithYear

@Composable
fun IncomeExpensesScreen(
  viewModel: IncomeWithExpenseViewModel = hiltViewModel(),
  paymentDate: String,
  onNavigateBack: () -> Unit = {},
  onNavigate: (navigateEvent: NavigateEvent) -> Unit = {},
  onEditIncome: (navigateEvent: NavigateEvent, incomeId: String) -> Unit = { one, two -> }
) {
  val state by viewModel.state.collectAsStateWithLifecycle()

  LaunchedEffect(key1 = paymentDate) {
    viewModel.getIncomesWithExpenses(paymentDate)
  }

  IncomeWithExpensesContent(
    incomeId = state.incomes?.get(0)?.id ?: "",
    incomesTotal = state.incomesTotal,
    fortnight = state.incomes?.get(0)?.paymentDate?.fortnight?.translate() ?: "",
    month = state.incomes?.get(0)?.paymentDate?.date?.formatDateMonthWithYear() ?: "",
    remaining = state.remaining,
    expended = state.expensesTotal,
    expenseList = state.expensesList,
    isLoading = state.isLoading,
    onNavigate = onNavigate,
    onNavigateBack = onNavigateBack,
    onEditIncome = onEditIncome
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
  onNavigateBack: () -> Unit = {},
  onNavigate: (navigateEvent: NavigateEvent) -> Unit,
  onEditIncome: (navigateEvent: NavigateEvent, incomeId: String) -> Unit = { one, two -> }
) {
  Scaffold(
    topBar = {
      AppBar(
        title = "$fortnight Quincena",
        onNavigationIconClick = { onNavigateBack() },
        dropDownMenuItems = listOf(
          MenuItems(
            text = "Editar",
            icon = Icons.Rounded.Edit,
            action = {
              // Hardcoding the first id for the income
              Log.d("IncomeExpensesScreen", "Clicking on edit")
              onEditIncome(NavigateEvent.NavigationEditIncomeScreen, incomeId)
            }
          ),
          MenuItems(
            text = "Borrar",
            icon = Icons.Rounded.Delete,
            action = {}
          )
        )
      )
    },
    floatingActionButton = {
      FABAddExpense(onNavigate)
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
        Column(
          modifier = Modifier
            .fillMaxSize()
            .padding(top = 8.dp, start = 16.dp, end = 16.dp, bottom = 12.dp),
          verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          DateRow(date = month)
          IncomeDetails(
            incomesTotal = incomesTotal,
            remaining = remaining,
            expended = expended
          )
          ExpensesList(expenseList)
        }
      }
    }
  }
}

@Composable
fun DateRow(date: String) {
  Row(
    modifier = Modifier
      .padding(bottom = 8.dp)
      .fillMaxWidth(),
    horizontalArrangement = Arrangement.End
  ) {
    Text(
      text = date,
      fontSize = 20.sp
    )
  }
}

@Composable
fun IncomeDetails(incomesTotal: Double, remaining: Double, expended: Double) {
  Card(
    modifier = Modifier.fillMaxWidth(),
    elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
  ) {
    Column(
      modifier = Modifier.padding(16.dp),
      verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween
      ) {
        //TODO make text color of remaining
        // if remaining is more than income make it green if not red
        Text(
          text = "Ingreso",
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
          text = "Gastos",
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
          text = "Restante",
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

@Composable
fun FABAddExpense(onNavigate: (navigateEvent: NavigateEvent) -> Unit) {
  FloatingActionButton(onClick = { onNavigate(NavigateEvent.NavigateAddExpenseScreen) }) {
    Icon(Icons.Rounded.Add, contentDescription = "")
  }
}

@Preview
@Composable
fun IncomeDetailsPreview() {
  Column(modifier = Modifier.padding(22.dp)) {
    IncomeDetails(
      incomesTotal = 18750.23,
      remaining = 5000.0,
      expended = 8000.0
    )
  }
}
