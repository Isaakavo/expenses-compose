package com.avocado.expensescompose.presentation.expenses.allexpenses.components

import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.avocado.expensescompose.data.adapters.adapt
import com.avocado.expensescompose.data.model.expense.Expense
import com.avocado.expensescompose.data.repositories.ChartDataProcessor
import com.avocado.expensescompose.presentation.charts.ChartType
import com.avocado.expensescompose.presentation.charts.Charts

enum class ChartDataEntityType {
  MONTH,
  CATEGORY,
  CONCEPT
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
  val context = LocalContext.current
  val expensesDataForChart = when (entityType) {
    ChartDataEntityType.MONTH -> ChartDataProcessor(filteredList)
      .groupExpenseBy(
        groupBy = { it.payBefore?.month?.name ?: "Unknown" },
        sumBy = { it.total.toFloat() }
      )

    ChartDataEntityType.CATEGORY -> ChartDataProcessor(filteredList.sortedBy { it.category })
      .groupExpenseBy(
        groupBy = { context.resources.getString(it.category.adapt()) },
        sumBy = { it.total.toFloat() }
      )

    ChartDataEntityType.CONCEPT -> ChartDataProcessor(filteredList)
      .groupExpenseBy(
        groupBy = { it.concept },
        sumBy = { it.total.toFloat() }
      )
  }

  Charts(
    data = expensesDataForChart,
    chartType = chartType,
    listState = listState,
    displayChipsLegends = true
  )
}
