package com.avocado.expensescompose.presentation.expenses.allexpenses.viewmodel

import com.avocado.expensescompose.presentation.charts.ChartType
import com.avocado.expensescompose.presentation.expenses.allexpenses.components.Filters

sealed interface AllExpensesListEvents {
  data class DeleteExpense(val expenseId: String) : AllExpensesListEvents
  data class ApplyFilter(val filters: Map<Filters, List<String>>) : AllExpensesListEvents
  data class UpdateDeleteExpenseId(val expenseId: String) : AllExpensesListEvents
  data class SelectChartType(val chartType: ChartType) : AllExpensesListEvents
  data object UpdateSuccessDelete : AllExpensesListEvents
  data object RestoreLists : AllExpensesListEvents
}
