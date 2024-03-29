package com.avocado.expensescompose.presentation.incomes.incomeslist

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.avocado.expensescompose.presentation.incomes.incomeslist.components.IncomesListContent
import com.avocado.expensescompose.presentation.incomes.incomeslist.viewmodel.IncomesListViewModel
import java.time.LocalDateTime

@Composable
fun IncomesList(
  viewModel: IncomesListViewModel = hiltViewModel(),
  onNavigate: (income: LocalDateTime?) -> Unit
) {
  val state by viewModel.state.collectAsStateWithLifecycle()

  LaunchedEffect(key1 = Unit) {
    viewModel.getAllIncomes()
  }

  IncomesListContent(
    isLoading = state.isLoading,
    uiError = state.uiError,
    incomesMap = state.incomesMap,
    totalByMonth = state.totalByMonth,
    onNavigate = onNavigate
  )
}
