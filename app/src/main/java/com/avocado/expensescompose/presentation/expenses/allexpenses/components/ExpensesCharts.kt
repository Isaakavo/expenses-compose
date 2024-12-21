package com.avocado.expensescompose.presentation.expenses.allexpenses.components

import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import com.avocado.expensescompose.data.model.expense.Expense
import com.avocado.expensescompose.presentation.charts.ChartType
import com.avocado.expensescompose.presentation.charts.Charts
import com.avocado.type.Category

@Composable
fun ExpensesCharts(
  filteredList: List<Expense>,
  chartType: ChartType = ChartType.BAR
) {
  if (filteredList.isEmpty()) {
    return
  }

  val listState = rememberLazyListState()
  val expensesDataForChart = generateExpensesMapByCategory(filteredList)
  Charts(
    data = expensesDataForChart,
    chartType = chartType,
    listState = listState,
    displayChipsLegends = true
  )
}

fun generateExpensesMapByCategory(expensesListState: List<Expense>): Map<Category, Float> {
  return mutableMapOf<Category, Float>().apply {
    for (expense in expensesListState) {
      val previousExpense = this[expense.category]
      this[expense.category] = if (previousExpense != null) (expense.total + previousExpense).toFloat() else expense.total.toFloat()
    }
  }
}
