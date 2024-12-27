package com.avocado.expensescompose.presentation.charts

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import co.yml.charts.ui.piechart.models.PieChartData

enum class ChartType {
  BAR,
  PIE
}

@Composable
fun Charts(
  data: Map<String, Float>,
  chartType: ChartType = ChartType.BAR,
  displayChipsLegends: Boolean = false,
  listState: LazyListState = rememberLazyListState(),
  modifier: Modifier = Modifier,
  onSlideClick: (slice: PieChartData.Slice) -> Unit = {}
) {
  when (chartType) {
    ChartType.PIE -> PieChart(data = data, listState = listState, displayChipsLegends = displayChipsLegends, onSlideClick)
    ChartType.BAR -> LineChart(data, modifier)
  }
}
