package com.avocado.expensescompose.presentation.expenses.allexpenses.viewmodel

import com.avocado.expensescompose.presentation.charts.ChartType

sealed interface AllExpensesListEvents {
  data object DeleteExpense : AllExpensesListEvents
  data object ApplyFilter : AllExpensesListEvents
  data object UpdateDeleteExpenseId : AllExpensesListEvents
  data object UpdateSuccessDelete : AllExpensesListEvents
  data object RestoreLists : AllExpensesListEvents
  data class SelectChartType(val chartType: ChartType) : AllExpensesListEvents
}
