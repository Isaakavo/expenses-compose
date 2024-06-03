package com.avocado.expensescompose.presentation.charts

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import co.yml.charts.common.components.Legends
import co.yml.charts.common.model.PlotData
import co.yml.charts.common.utils.DataUtils
import co.yml.charts.ui.piechart.charts.DonutPieChart
import co.yml.charts.ui.piechart.models.PieChartConfig
import co.yml.charts.ui.piechart.models.PieChartData

@Composable
fun Charts(
  data: PlotData,
  donutChartConfig: PieChartConfig?
) {
  when (data) {
    is PieChartData -> {
      DonutPieChart(
        modifier = Modifier
          .fillMaxWidth(),
        data,
        donutChartConfig ?: PieChartConfig()
      )
      Legends(
        legendsConfig = DataUtils.getLegendsConfigFromPieChartData(pieChartData = data, 3)
      )
    }
  }
}

// @Preview
// @Composable
// fun ChartsPreview() {
//  ExpensesComposeTheme {
//    CustomScaffold {
//      Column(modifier = Modifier.padding(it)) {
//        Charts()
//      }
//    }
//  }
// }
