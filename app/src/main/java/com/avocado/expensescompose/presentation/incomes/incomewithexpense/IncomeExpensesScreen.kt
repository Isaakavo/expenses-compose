package com.avocado.expensescompose.presentation.incomes.incomewithexpense

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.avocado.expensescompose.data.adapters.adapt
import com.avocado.expensescompose.data.adapters.formatDateDaysWithMonth
import com.avocado.expensescompose.data.adapters.formatMoney
import com.avocado.expensescompose.domain.income.models.Expense
import com.avocado.expensescompose.domain.income.models.ExpenseTag
import com.avocado.expensescompose.domain.income.models.Fortnight
import com.avocado.expensescompose.domain.income.models.Income
import com.avocado.expensescompose.domain.income.models.PaymentDate
import com.avocado.expensescompose.presentation.topbar.AppBar
import com.avocado.expensescompose.presentation.topbar.IconsActions
import java.time.LocalDateTime

@Composable
fun IncomeExpensesScreen(
  viewModel: IncomeWithExpenseViewModel = hiltViewModel(),
  incomeId: String,
  paymentDate: String,
  onNavigateBack: () -> Unit = {}
) {
  val state by viewModel.state.collectAsState()
  viewModel.getIncomesWithExpenses(incomeId, paymentDate)

  IncomeWithExpensesContent(
    income = state.income,
    remaining = state.remaining,
    expenseList = state.expensesList,
    isLoading = state.isLoading,
    onNavigateBack
  )
}

@Composable
fun IncomeWithExpensesContent(
  income: Income?,
  remaining: Double,
  expenseList: List<Expense>,
  isLoading: Boolean = false,
  onNavigateBack: () -> Unit = {}
) {
  //TODO add logic to edit income
  // and more menu
  Scaffold(topBar = {
    AppBar(
      title = income?.paymentDate?.date?.formatDateDaysWithMonth() ?: "",
      onNavigationIconClick = { onNavigateBack() },
      actionsList = listOf(
        IconsActions(icon = Icons.Rounded.Edit, action = {}),
        IconsActions(icon = Icons.Rounded.MoreVert, action = {})
      )
    )
  }, floatingActionButton = {
    FABAddExpense()
  }) {
    Surface(
      modifier = Modifier
        .padding(it)
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
          IncomeDetails(income, remaining)
          ExpensesList(expenseList)
        }
      }
    }

  }
}

@Composable
fun IncomeDetails(income: Income?, remaining: Double) {
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
          text = "${income?.paymentDate?.fortnight?.adapt()} Quincena",
          style = MaterialTheme.typography.bodyLarge
        )
      }

      Row(
        modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween
      ) {
        //TODO make text color of remaining
        // if remaining is more than income make it green if not red
        Text(
          text = "${income?.total?.formatMoney()}", style = MaterialTheme.typography.headlineMedium
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

      if (income?.comment != null && income.comment.isNotEmpty()) {
        Row(
          modifier = Modifier
            .padding(top = 8.dp)
            .fillMaxWidth(),
          horizontalArrangement = Arrangement.End
        ) {
          Text(text = income.comment, style = MaterialTheme.typography.bodyLarge)
        }
      }
    }

  }
}

@Composable
fun ExpenseItem(expense: Expense) {
  Card(
    modifier = Modifier.fillMaxWidth()
  ) {
    Column(modifier = Modifier.padding(12.dp)) {

      Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(text = expense.concept, style = MaterialTheme.typography.bodySmall)
        Text(
          text = expense.createdAt?.formatDateDaysWithMonth() ?: "",
          style = MaterialTheme.typography.bodyMedium
        )
      }

      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(top = 4.dp, end = 8.dp),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
      ) {

        Text(
          text = expense.total.formatMoney(),
          style = MaterialTheme.typography.bodyLarge,
          fontWeight = FontWeight.SemiBold
        )

      }

      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(start = 4.dp, top = 6.dp),
        horizontalArrangement = Arrangement.End
      ) {
        LazyRow(modifier = Modifier.wrapContentWidth()) {
          items(expense.tags) { tag ->
            ExpenseTags(tags = tag)
          }
        }
      }

      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(end = 6.dp),
        horizontalArrangement = Arrangement.End
      ) {
        Text(text = "${expense.tags.size} tags", style = MaterialTheme.typography.bodySmall)
      }
    }
  }
}

@Composable
fun ExpenseTags(tags: ExpenseTag) {
  SuggestionChip(
    onClick = {},
    label = { Text(text = tags.name, style = MaterialTheme.typography.bodySmall) },
    modifier = Modifier.padding(end = 4.dp),
  )
}

@Composable
fun ExpensesList(expenseList: List<Expense>) {
  Text(
    text = "Transacciones", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.End
  )
  LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
    items(expenseList) { expense ->
      ExpenseItem(expense = expense)
    }
  }
}

@Composable
fun FABAddExpense() {
  FloatingActionButton(onClick = { /*TODO*/ }) {
    Icon(Icons.Rounded.Add, contentDescription = "")
  }
}

@Preview
@Composable
fun IncomeWithExpenseContent() {
  IncomeWithExpensesContent(
    Income(
      comment = "Aumento salarial",
      total = 15712.22,
      createdAt = LocalDateTime.now(),
      paymentDate = PaymentDate(date = LocalDateTime.now(), fortnight = Fortnight.FIRST)
    ), remaining = 10000.0, expenseList = listOf(
      Expense(
        total = 5500.0,
        incomeId = "1",
        concept = "Gasto de prueba",
        createdAt = LocalDateTime.now(),
        comment = "",
        id = "1",
        tags = listOf(
          ExpenseTag(
            id = "1",
            name = "Tag de prueba",
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
          ), ExpenseTag(
            id = "1",
            name = "Tag de prueba 2",
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
          ), ExpenseTag(
            id = "1",
            name = "Tag de prueba",
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
          ), ExpenseTag(
            id = "1",
            name = "Tag de prueba 2",
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
          )
        )
      )
    )
  )
}