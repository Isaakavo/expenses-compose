package com.avocado.expensescompose.presentation.charts

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import co.yml.charts.common.components.Legends
import co.yml.charts.common.model.PlotData
import co.yml.charts.common.utils.DataUtils
import co.yml.charts.ui.piechart.charts.DonutPieChart
import co.yml.charts.ui.piechart.models.PieChartConfig
import co.yml.charts.ui.piechart.models.PieChartData
import com.avocado.type.Category

@Composable
fun Charts(
  data: PlotData,
  donutChartConfig: PieChartConfig?,
  displayLegends: Boolean = false,
  onSlideClick: (slice: PieChartData.Slice) -> Unit = {}
) {
  when (data) {
    is PieChartData -> {
      DonutPieChart(
        modifier = Modifier
          .fillMaxWidth(),
        data,
        donutChartConfig ?: PieChartConfig()
      ) {
        onSlideClick(it)
      }
      if (displayLegends) {
        Legends(
          legendsConfig = DataUtils.getLegendsConfigFromPieChartData(pieChartData = data, gridSize = data.slices.size / 3)
        )
      }
    }
  }
}

fun Category.color(): Color = when (this) {
  Category.SAVINGS -> Color(0xFFFF9800)
  Category.COMMUNICATION -> Color(0xFFF44336)
  Category.BILLS -> Color(0xFF8BC34A)
  Category.FOOD -> Color(0xFF03A9F4)
  Category.CAR -> Color(0xFF00BCD4)
  Category.CLOTHES -> Color(0xFF22E059)
  Category.EATING_OUT -> Color(0xFF673AB7)
  Category.ENTERTAINMENT -> Color(0xFF3F51B5)
  Category.GIFTS -> Color(0xFFFF9800)
  Category.HANG_OUT -> Color(0xFF03A9F4)
  Category.HEALTH -> Color(0xFFE91E63)
  Category.HOUSE -> Color(0xFF795548)
  Category.INSURANCE -> Color(0xFFE91E63)
  Category.PETS -> Color(0xFFD8E1E6)
  Category.SPORTS -> Color(0xFF3F51B5)
  Category.SUPER_MARKET -> Color(0xFF2196F3)
  Category.TRANSPORT -> Color(0xFFF44336)
  Category.SUBSCRIPTION -> Color(0xFFC26A4A)
  Category.FIXED_EXPENSE -> Color(0xFF009688)
  Category.MONTHS_WITHOUT_INTEREST -> Color(0xFF815E2B)
  else -> Color.Black
}
