package com.avocado.expensescompose.presentation.expenses.allexpenses.components

import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.avocado.expensescompose.data.adapters.adapt
import com.avocado.expensescompose.data.model.expense.Expense
import com.avocado.expensescompose.presentation.charts.ChartType
import com.avocado.expensescompose.presentation.charts.Charts
import java.time.LocalDateTime

enum class ChartDataEntityType {
  MONTH,
  CATEGORY
}

@Composable
fun ExpensesCharts(
  filteredList: List<Expense>,
  chartType: ChartType = ChartType.BAR,
  entityType: ChartDataEntityType = ChartDataEntityType.CATEGORY
) {
  if (filteredList.isEmpty()) {
    return
  }

  val listState = rememberLazyListState()
  val expensesDataForChart = when (entityType) {
    ChartDataEntityType.MONTH -> generateExpensesMapByMonth(expensesListState = filteredList)
    ChartDataEntityType.CATEGORY -> generateExpensesMapByCategory(expensesListState = filteredList)
  }

  Charts(
    data = expensesDataForChart,
    chartType = chartType,
    listState = listState,
    displayChipsLegends = true
  )
}

@Composable
internal fun generateExpensesMapByCategory(expensesListState: List<Expense>): Map<String, Float> {
  return mutableMapOf<String, Float>().apply {
    val sortedList = expensesListState.sortedBy { it.category }
    for (expense in sortedList) {
      val categoryName = stringResource(expense.category.adapt())
      val previousExpense = this[categoryName]
      this[categoryName] = if (previousExpense != null) (expense.total + previousExpense).toFloat() else expense.total.toFloat()
    }
  }
}

internal fun generateExpensesMapByMonth(expensesListState: List<Expense>, yearToFilter: Int = LocalDateTime.now().year): Map<String, Float> =
  mutableMapOf<String, Float>().apply {
    val sortedList = expensesListState.filter { it.payBefore?.year == yearToFilter }.sortedBy { it.payBefore }
    for (expense in sortedList) {
      val date = expense.payBefore?.month?.name ?: continue
      val previousExpense = this[date]
      this[date] = if (previousExpense != null) (expense.total + previousExpense).toFloat() else expense.total.toFloat()
    }
  }
