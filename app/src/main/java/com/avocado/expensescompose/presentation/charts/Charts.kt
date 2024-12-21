package com.avocado.expensescompose.presentation.charts

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import co.yml.charts.ui.piechart.models.PieChartData
import com.avocado.type.Category

enum class ChartType {
  BAR,
  PIE
}

@Composable
fun Charts(
  data: Map<Category, Float>,
  chartType: ChartType = ChartType.BAR,
  displayChipsLegends: Boolean = false,
  listState: LazyListState = rememberLazyListState(),
  onSlideClick: (slice: PieChartData.Slice) -> Unit = {}
) {
  when (chartType) {
    ChartType.PIE -> PieChart(data = data, listState = listState, displayChipsLegends = displayChipsLegends, onSlideClick)
    ChartType.BAR -> LineChart(data)
  }
}
