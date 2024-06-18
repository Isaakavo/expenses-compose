package com.avocado.expensescompose.presentation.expenses.allexpenses.components

import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import co.yml.charts.common.model.PlotType
import co.yml.charts.ui.piechart.models.PieChartData
import com.avocado.expensescompose.data.adapters.adapt
import com.avocado.expensescompose.data.model.expense.Expense
import com.avocado.expensescompose.presentation.charts.Charts
import com.avocado.expensescompose.presentation.charts.configs.color
import com.avocado.expensescompose.presentation.charts.configs.defaultDonutChartConfig
import com.avocado.type.Category

@Composable
fun ExpensesCharts(
  filteredList: List<Expense>
) {
  if (filteredList.isNotEmpty()) {
    val listState = rememberLazyListState()
    val expensesListForChart = generateExpensesMap(filteredList)
    val expensesCategoryData = PieChartData(
      plotType = PlotType.Pie,
      slices = expensesListForChart.map {
        PieChartData.Slice(
          label = stringResource(it.key.adapt()),
          value = it.value,
          color = it.key.color()
        )
      }
    )

    Charts(
      data = expensesCategoryData,
      listState = listState,
      displayChipsLegends = true,
      chartConfig = defaultDonutChartConfig(
        backgroundColor = MaterialTheme.colorScheme.background,
        textColor = Color.White
      )
    )
  }
}

fun generateExpensesMap(expensesListState: List<Expense>): Map<Category, Float> {
  val expensesMap = mutableMapOf<Category, Float>()
  for (expense in expensesListState) {
    val previousExpense = expensesMap[expense.category]
    expensesMap[expense.category] = if (previousExpense != null) (expense.total + previousExpense).toFloat() else expense.total.toFloat()
  }

  return expensesMap
}
