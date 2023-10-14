package com.avocado.expensescompose.presentation.incomes.incomewithexpense

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.avocado.expensescompose.data.adapters.adapt
import com.avocado.expensescompose.data.adapters.formatDateDaysWithMonth
import com.avocado.expensescompose.data.adapters.formatMoney
import com.avocado.expensescompose.domain.income.models.Fortnight
import com.avocado.expensescompose.domain.income.models.Income
import com.avocado.expensescompose.domain.income.models.PaymentDate
import com.avocado.expensescompose.presentation.topbar.AppBar
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

  IncomeWithExpensesContent(income = state.income, remaining = state.remaining, onNavigateBack)
}

@Composable
fun IncomeWithExpensesContent(income: Income?, remaining: Double, onNavigateBack: () -> Unit = {}) {
  Scaffold(
    topBar = {
      AppBar(
        title = income?.paymentDate?.date?.formatDateDaysWithMonth() ?: "",
        onNavigationIconClick = { onNavigateBack() })
    }
  ) {
    Surface(
      modifier = Modifier
        .padding(it)
        .fillMaxSize()
    ) {
      Column {
        IncomeDetails(income, remaining)
      }
    }

  }
}

@Composable
fun IncomeDetails(income: Income?, remaining: Double) {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .padding(6.dp)
  ) {
    Column(
      modifier = Modifier
        .padding(16.dp)
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
        modifier = Modifier
          .fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Text(
          text = "${income?.total?.formatMoney()}",
          style = MaterialTheme.typography.headlineMedium
        )

        Text(text = remaining.formatMoney(), style = MaterialTheme.typography.headlineMedium)
      }
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(top = 4.dp, start = 8.dp), horizontalArrangement = Arrangement.SpaceBetween
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

@Preview
@Composable
fun IncomeWithExpenseContent() {
  IncomeWithExpensesContent(
    Income(
      comment = "Aumento salarial",
      total = 15712.22,
      createdAt = LocalDateTime.now(),
      paymentDate = PaymentDate(date = LocalDateTime.now(), fortnight = Fortnight.FIRST)
    ),
    remaining = 10000.0
  )
}