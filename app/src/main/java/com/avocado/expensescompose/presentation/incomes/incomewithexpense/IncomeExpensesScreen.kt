package com.avocado.expensescompose.presentation.incomes.incomewithexpense

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.avocado.expensescompose.presentation.incomes.incomewithexpense.components.IncomeWithExpensesContent
import com.avocado.expensescompose.presentation.navigation.NavigateEvent
import com.avocado.expensescompose.presentation.util.Operations
import com.avocado.expensescompose.presentation.util.formatDateMonthWithYear

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
    cards = state.cards,
    isLoading = state.isLoading,
    onNavigate = onNavigate,
    onNavigateBack = onNavigateBack,
    onEditIncome = onEditIncome,
    onEvent = viewModel::onEvent
  )
}
