package com.avocado.expensescompose.presentation.incomes.incomewithexpense

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.MoreVert
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.avocado.expensescompose.presentation.util.formatDateOnlyMonth
import com.avocado.expensescompose.data.adapters.formatMoney
import com.avocado.expensescompose.data.model.expense.Expense
import com.avocado.expensescompose.presentation.navigation.NavigateEvent
import com.avocado.expensescompose.presentation.shared.ExpensesList
import com.avocado.expensescompose.presentation.topbar.AppBar
import com.avocado.expensescompose.presentation.topbar.IconsActions

@Composable
fun IncomeExpensesScreen(
  viewModel: IncomeWithExpenseViewModel = hiltViewModel(),
  paymentDate: String,
  onNavigateBack: () -> Unit = {},
  onNavigate: (navigateEvent: NavigateEvent) -> Unit = {}
) {
  val state by viewModel.state.collectAsStateWithLifecycle()

  LaunchedEffect(key1 = paymentDate) {
    viewModel.getIncomesWithExpenses(paymentDate)
  }

  IncomeWithExpensesContent(
    incomesTotal = state.incomesTotal,
    fortnight = state.income?.get(0)?.paymentDate?.fortnight?.translate() ?: "",
    month = state.income?.get(0)?.paymentDate?.date?.formatDateOnlyMonth() ?: "",
    remaining = state.remaining,
    expenseList = state.expensesList,
    isLoading = state.isLoading,
    onNavigate = onNavigate,
    onNavigateBack = onNavigateBack
  )
}

@Composable
fun IncomeWithExpensesContent(
  incomesTotal: Double,
  fortnight: String,
  month: String,
  remaining: Double,
  expenseList: List<Expense>,
  isLoading: Boolean = false,
  onNavigateBack: () -> Unit = {},
  onNavigate: (navigateEvent: NavigateEvent) -> Unit
) {
  //TODO add logic to edit income
  // and more menu
  Scaffold(topBar = {
    AppBar(
      title = "$fortnight Quincena",
      onNavigationIconClick = { onNavigateBack() },
      actionsList = listOf(
        IconsActions(icon = Icons.Rounded.Edit, action = {}),
        IconsActions(icon = Icons.Rounded.MoreVert, action = {})
      )
    )
  }, floatingActionButton = {
    FABAddExpense(onNavigate)
  }) { paddingValues ->
    Surface(
      modifier = Modifier
        .padding(paddingValues)
        .fillMaxSize()
    ) {
      Column(
        modifier = Modifier
          .fillMaxSize()
          .padding(top = 8.dp, start = 16.dp, end = 16.dp, bottom = 12.dp),
        verticalArrangement = Arrangement.spacedBy(22.dp)
      ) {
        if (isLoading) {
          CircularProgressIndicator(strokeWidth = 6.dp)
        } else {
          IncomeDetails(incomesTotal, month, remaining)
          ExpensesList(expenseList)
        }
      }
    }

  }
}

@Composable
fun IncomeDetails(incomesTotal: Double, month: String, remaining: Double) {
  Card(
    modifier = Modifier.fillMaxWidth(),
    elevation = CardDefaults.cardElevation(defaultElevation = 22.dp)
  ) {
    Column(
      modifier = Modifier.padding(16.dp)
    ) {
      Row(
        modifier = Modifier
          .padding(bottom = 8.dp)
          .fillMaxWidth(),
        horizontalArrangement = Arrangement.End
      ) {
        Text(
          text = month,
          style = MaterialTheme.typography.headlineSmall
        )
      }

      Row(
        modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween
      ) {
        //TODO make text color of remaining
        // if remaining is more than income make it green if not red
        Text(
          text = incomesTotal.formatMoney(), style = MaterialTheme.typography.headlineMedium
        )
        Text(text = remaining.formatMoney(), style = MaterialTheme.typography.headlineMedium)
      }

      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(top = 4.dp, start = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Text(text = "Ingreso", color = MaterialTheme.colorScheme.secondary)
        Text(text = "Restante", color = MaterialTheme.colorScheme.secondary)
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
