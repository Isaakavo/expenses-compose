package com.avocado.expensescompose.presentation.charts

import android.graphics.Typeface
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import co.yml.charts.common.model.PlotData
import co.yml.charts.ui.piechart.charts.DonutPieChart
import co.yml.charts.ui.piechart.models.PieChartConfig
import co.yml.charts.ui.piechart.models.PieChartData

// TODO validate what fields to ask to retrieve data from server
@Composable
fun Charts(
  data: PlotData
) {
  when (data) {
    is PieChartData -> {
      val donutChartConfig = PieChartConfig(
        strokeWidth = 90f,
        activeSliceAlpha = .9f,
        isAnimationEnable = true,
        labelColor = Color.Black,
        sliceLabelTextColor = Color.Red,
        sliceLabelTextSize = 28.sp,
        labelFontSize = 24.sp,
        backgroundColor = MaterialTheme.colorScheme.background,
        sumUnit = "$",
        labelType = PieChartConfig.LabelType.PERCENTAGE,
        sliceLabelTypeface = Typeface.DEFAULT_BOLD
      )

      DonutPieChart(
        modifier = Modifier
          .fillMaxWidth(),
        data,
        donutChartConfig
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
