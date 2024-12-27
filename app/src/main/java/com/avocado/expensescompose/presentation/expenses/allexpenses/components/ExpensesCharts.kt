package com.avocado.expensescompose.presentation.expenses.allexpenses.components

import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.avocado.expensescompose.data.adapters.adapt
import com.avocado.expensescompose.data.model.expense.Expense
import com.avocado.expensescompose.data.repositories.ChartDataProcessor
import com.avocado.expensescompose.presentation.charts.ChartType
import com.avocado.expensescompose.presentation.charts.Charts
import java.time.LocalDateTime

enum class ChartDataEntityType {
  MONTH,
  CATEGORY,
  CONCEPT,
  CONCEPT_QUANTITY
}

@Composable
fun ExpensesCharts(
  filteredList: List<Expense>,
  chartType: ChartType = ChartType.BAR,
  entityType: ChartDataEntityType = ChartDataEntityType.CATEGORY,
  modifier: Modifier = Modifier
) {
  if (filteredList.isEmpty()) {
    return
  }

  val listState = rememberLazyListState()
  val context = LocalContext.current
  val expensesDataForChart = when (entityType) {
    // TODO move this logic to viewmodel to handle the filters and sorts
    ChartDataEntityType.MONTH -> ChartDataProcessor(
      filteredList
        .filter { it.payBefore?.year == LocalDateTime.now().year }
        .sortedBy { it.payBefore?.month }
    )
      .groupExpenseBy(
        groupBy = { it.payBefore?.month?.name ?: "Unknown" },
        sumBy = { it.total.toFloat() }
      )

    ChartDataEntityType.CATEGORY -> ChartDataProcessor(filteredList.sortedBy { it.category })
      .groupExpenseBy(
        groupBy = { context.resources.getString(it.category.adapt()) },
        sumBy = { it.total.toFloat() }
      )

    ChartDataEntityType.CONCEPT -> ChartDataProcessor(filteredList.sortedByDescending { it.total }.take(50))
      .groupExpenseBy(
        groupBy = { it.concept.lowercase() },
        sumBy = { it.total.toFloat() }
      )

    ChartDataEntityType.CONCEPT_QUANTITY -> ChartDataProcessor(filteredList.take(10))
      .groupExpenseListBy(
        groupBy = { it.concept.lowercase() },
        sumListBy = { it.size }
      )
  }

  Charts(
    data = expensesDataForChart,
    chartType = chartType,
    listState = listState,
    modifier = modifier,
    displayChipsLegends = true
  )
}
