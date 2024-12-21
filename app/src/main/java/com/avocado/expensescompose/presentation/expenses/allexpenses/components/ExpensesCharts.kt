package com.avocado.expensescompose.presentation.expenses.allexpenses.components

import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import co.yml.charts.axis.DataCategoryOptions
import co.yml.charts.common.model.PlotType
import co.yml.charts.common.model.Point
import co.yml.charts.ui.barchart.models.BarChartType
import co.yml.charts.ui.barchart.models.BarData
import com.avocado.expensescompose.data.model.expense.Expense
import com.avocado.expensescompose.presentation.charts.Charts
import com.avocado.expensescompose.presentation.charts.configs.color
import com.avocado.expensescompose.presentation.charts.configs.defaultDonutChartConfig
import com.avocado.type.Category
import kotlin.random.Random

@Composable
fun ExpensesCharts(
  filteredList: List<Expense>,
  plotType: PlotType = PlotType.Bar
) {
  if (filteredList.isEmpty()) {
    return
  }

  val listState = rememberLazyListState()
  val expensesDataForChart = generateExpensesMap(filteredList)
//  val expensesCategoryData = when (plotType) {
//    is PlotType.Pie -> PieChartData(
//      plotType = PlotType.Pie,
//      slices = expensesListForChart.map {
//        PieChartData.Slice(
//          label = stringResource(it.key.adapt()),
//          value = it.value,
//          color = it.key.color()
//        )
//      }
//    )
//
//    is PlotType.Bar -> {
// //      activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
//      generateBarChartData(expensesListForChart, BarChartType.VERTICAL)
//    }
//    PlotType.Donut -> {}
//    PlotType.Line -> {}
//    PlotType.Wave -> {}
//  }

  Charts(
    data = expensesDataForChart,
    listState = listState,
    displayChipsLegends = true,
    chartConfig = defaultDonutChartConfig(
      backgroundColor = MaterialTheme.colorScheme.background,
      textColor = Color.White
    )
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

fun generateBarChartData(expensesMap: Map<Category, Float>, barChartType: BarChartType): List<BarData> {
  return arrayListOf<BarData>().apply {
    expensesMap.entries.forEachIndexed { index, (key, value) ->
      val point = when (barChartType) {
        BarChartType.VERTICAL -> {
          Point(
            index.toFloat(),
            value
          )
        }

        BarChartType.HORIZONTAL -> {
          Point(
            index.toFloat(),
            value
          )
        }
      }
      add(
        BarData(
          point = point,
          color = Color(
            Random.nextInt(256),
            Random.nextInt(256),
            Random.nextInt(256)
          ),
          label = key.name,
          dataCategoryOptions = DataCategoryOptions()
        )
      )
    }
  }
}
