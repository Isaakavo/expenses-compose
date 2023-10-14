package com.avocado.expensescompose.presentation.incomes.incomewithexpense

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun IncomeExpensesScreen(
  viewModel: IncomeWithExpenseViewModel = hiltViewModel(),
  incomeId: String
) {

  viewModel.getIncomesWithExpenses(incomeId)
}