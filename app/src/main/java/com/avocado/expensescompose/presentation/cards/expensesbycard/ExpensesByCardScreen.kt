package com.avocado.expensescompose.presentation.cards.expensesbycard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.avocado.expensescompose.data.model.expense.Expense
import com.avocado.expensescompose.presentation.incomes.incomewithexpense.ExpensesList
import com.avocado.expensescompose.presentation.topbar.AppBar

@Composable
fun ExpensesByCardScreen(
  viewModel: ExpensesByCardViewModel = hiltViewModel(),
  cardId: String,
  payBefore: String,
  onPopBackStack: () -> Unit = {},
) {

  val state by viewModel.state.collectAsStateWithLifecycle()

  LaunchedEffect(key1 = Unit) {
    viewModel.getExpensesByFortnight(payBefore = payBefore, cardId = cardId)
  }

  ExpensesByCardContent(expensesList = state.expensesList, onPopBackStack = onPopBackStack)
}

@Composable
fun ExpensesByCardContent(expensesList: List<Expense>, onPopBackStack: () -> Unit) {
  Scaffold(
    topBar = {
      AppBar(title = "Gastos tarjeta {Tarjeta id}", onNavigationIconClick = { onPopBackStack() })
    }
  ) { paddingValues ->
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
        ExpensesList(expenseList = expensesList)
      }
    }
  }
}