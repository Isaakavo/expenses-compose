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
  val expensesDataForChart = generateExpensesMap(filteredList)
  Charts(
    data = expensesDataForChart,
    chartType = chartType,
    listState = listState,
    displayChipsLegends = true
  )
}

fun generateExpensesMap(expensesListState: List<Expense>): Map<Category, Float> {
  val expensesMap = mutableMapOf<Category, Float>()
  for (expense in expensesListState) {
    val previousExpense = expensesMap[expense.category]
    expensesMap[expense.category] = if (previousExpense != null) (expense.total + previousExpense).toFloat() else expense.total.toFloat()
  }

  return expensesMap
}
