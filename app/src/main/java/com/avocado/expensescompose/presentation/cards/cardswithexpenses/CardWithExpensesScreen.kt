package com.avocado.expensescompose.presentation.cards.cardswithexpenses

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.avocado.expensescompose.data.model.expense.Expense
import com.avocado.expensescompose.data.model.total.Total
import com.avocado.expensescompose.data.model.total.TotalFortnight
import com.avocado.expensescompose.presentation.incomes.incomewithexpense.ExpensesList

@Composable
fun CardWithExpenseScreen(viewModel: CardsWithExpensesViewModel = hiltViewModel(), cardId: String) {
  val state by viewModel.state.collectAsStateWithLifecycle()
  viewModel.getExpensesByCardId(cardId)

  CardWithExpenseContent(
    expenseList = state.expensesList,
    totalByMonth = state.totalByMonthList,
    totalByFortnight = state.totalByFortnight
  )
}

@Composable
fun CardWithExpenseContent(
  expenseList: List<Expense>,
  totalByMonth: List<Total>,
  totalByFortnight: List<TotalFortnight>
) {

  Scaffold { paddingValues ->
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
        ExpensesList(expenseList = expenseList)
      }
    }
  }
}