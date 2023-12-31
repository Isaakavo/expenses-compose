package com.avocado.expensescompose.presentation.cards.expensesbycard

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.avocado.expensescompose.data.adapters.formatMoney
import com.avocado.expensescompose.data.model.expense.Expense
import com.avocado.expensescompose.presentation.cards.expensestotalbycard.DataSelector
import com.avocado.expensescompose.presentation.navigation.NavigateEvent
import com.avocado.expensescompose.presentation.shared.ExpensesList
import com.avocado.expensescompose.presentation.topbar.AppBar
import com.avocado.expensescompose.presentation.util.formatDateOnlyMonth

@Composable
fun ExpensesByCardScreen(
  viewModel: ExpensesByCardViewModel = hiltViewModel(),
  cardId: String,
  dataSelector: DataSelector,
  payBefore: String,
  onPopBackStack: () -> Unit = {},
  onNavigate: (navigateEvent: NavigateEvent, param: String) -> Unit = {one, two -> }
) {

  val state by viewModel.state.collectAsStateWithLifecycle()

  LaunchedEffect(key1 = Unit) {
    when (dataSelector) {
      DataSelector.FORTNIGHT -> {
        Log.d("ExpensesByCardScreen", "Querying fortnight")
        viewModel.getExpensesByFortnight(payBefore = payBefore, cardId = cardId)
      }

      DataSelector.MONTH -> {
        Log.d("ExpensesByCardScreen", "Querying month")
        viewModel.getExpensesByMonth(payBefore = payBefore, cardId = cardId)
      }
    }

  }

  ExpensesByCardContent(
    expensesList = state.expensesList,
    expensesTotal = state.expenseTotal,
    payBefore = payBefore,
    onPopBackStack = onPopBackStack,
    onNavigate = onNavigate
  )
}

@Composable
fun ExpensesByCardContent(
  expensesList: List<Expense>,
  expensesTotal: Double,
  payBefore: String,
  onPopBackStack: () -> Unit,
  onNavigate: (navigateEvent: NavigateEvent, param: String) -> Unit = {one, two -> }
) {
  Scaffold(
    topBar = {
      AppBar(title = "Gastos tarjeta", onNavigationIconClick = { onPopBackStack() })
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
        CardExpensesDetails(expensesTotal = expensesTotal, payBefore = payBefore)
        ExpensesList(
          expenseList = expensesList,
          onEdit = { onNavigate(NavigateEvent.NavigateEditExpenseScreen, it) }
          )
      }
    }
  }
}

@Composable
fun CardExpensesDetails(expensesTotal: Double, payBefore: String) {
  Card(
    modifier = Modifier.fillMaxWidth(),
    elevation = CardDefaults.cardElevation(defaultElevation = 22.dp)
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
      ) {
        Text(
          text = payBefore.formatDateOnlyMonth(),
          fontSize = 18.sp,
          fontWeight = FontWeight.Normal
        )
      }
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(start = 8.dp), horizontalArrangement = Arrangement.Start
      ) {
        Text(text = expensesTotal.formatMoney(), fontWeight = FontWeight.Bold, fontSize = 22.sp)
      }
    }
  }
}