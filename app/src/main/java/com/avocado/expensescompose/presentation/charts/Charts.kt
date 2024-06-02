package com.avocado.expensescompose.presentation.charts

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.yml.charts.common.model.PlotType
import co.yml.charts.ui.piechart.charts.DonutPieChart
import co.yml.charts.ui.piechart.models.PieChartConfig
import co.yml.charts.ui.piechart.models.PieChartData
import com.avocado.expensescompose.presentation.shared.CustomScaffold
import com.avocado.expensescompose.ui.theme.ExpensesComposeTheme

// TODO validate what fields to ask to retrieve data from server
@Composable
fun Charts() {
  val donutChartData = PieChartData(
    plotType = PlotType.Donut,
    slices = listOf(
      PieChartData.Slice("HP", 15f, Color(0xFF5F0A87)),
      PieChartData.Slice("Dell", 30f, Color(0xFF20BF55)),
      PieChartData.Slice("Lenovo", 40f, Color(0xFFEC9F05)),
      PieChartData.Slice("Asus", 10f, Color(0xFFF53844))
    )
  )

  val donutChartConfig = PieChartConfig(
    strokeWidth = 120f,
    activeSliceAlpha = .9f,
    isAnimationEnable = true,
    labelColor = Color.Red,
    sliceLabelTextColor = Color.Red,
    sliceLabelTextSize = 28.sp,
    showSliceLabels = true
  )

  DonutPieChart(
    modifier = Modifier
      .fillMaxWidth()
      .height(500.dp),
    donutChartData,
    donutChartConfig
  )
}

@Preview
@Composable
fun ChartsPreview() {
  ExpensesComposeTheme {
    CustomScaffold {
      Column(modifier = Modifier.padding(it)) {
        Charts()
      }
    }
  }
}
