package com.avocado.expensescompose.presentation.charts

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.SnackbarDuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import co.yml.charts.common.components.Legends
import co.yml.charts.common.model.PlotData
import co.yml.charts.common.utils.DataUtils
import co.yml.charts.ui.piechart.charts.DonutPieChart
import co.yml.charts.ui.piechart.models.PieChartConfig
import co.yml.charts.ui.piechart.models.PieChartData
import com.avocado.expensescompose.ui.theme.LocalSnackBarHostState

@Composable
fun Charts(
  data: PlotData,
  donutChartConfig: PieChartConfig?,
  displayLegends: Boolean = false
) {
  val snackBarHostState = LocalSnackBarHostState.current
  var snackBarData by remember {
    mutableStateOf("")
  }
  when (data) {
    is PieChartData -> {
      DonutPieChart(
        modifier = Modifier
          .fillMaxWidth(),
        data,
        donutChartConfig ?: PieChartConfig()
      ) {
        snackBarData = "${it.label}, $${it.value}"
      }
      if (displayLegends) {
        Legends(
          legendsConfig = DataUtils.getLegendsConfigFromPieChartData(pieChartData = data, gridSize = data.slices.size / 3)
        )
      }
    }
  }

  LaunchedEffect(key1 = snackBarData) {
    if (snackBarData.isNotEmpty()) {
      snackBarHostState.showSnackbar(
        snackBarData,
        duration = SnackbarDuration.Short
      )
    }
  }
}

fun generateColorMaterialDesign(): Color = listOf(
  Color(0xFFF44336), Color(0xFFE91E63), Color(0xFF9C27B0), Color(0xFF673AB7), Color(0xFF3F51B5),
  Color(0xFF2196F3), Color(0xFF03A9F4), Color(0xFF00BCD4), Color(0xFF009688), Color(0xFF4CAF50),
  Color(0xFF8BC34A), Color(0xFFCDDC39), Color(0xFFFFEB3B), Color(0xFFFFC107), Color(0xFFFF9800),
  Color(0xFFFF5722), Color(0xFF795548), Color(0xFF9E9E9E), Color(0xFF607D8B)
).random()
