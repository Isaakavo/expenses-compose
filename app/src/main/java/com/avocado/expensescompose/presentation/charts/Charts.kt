package com.avocado.expensescompose.presentation.charts

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import co.yml.charts.axis.AxisData
import co.yml.charts.common.model.PlotType
import co.yml.charts.ui.barchart.BarChart
import co.yml.charts.ui.barchart.models.BarChartData
import co.yml.charts.ui.barchart.models.BarData
import co.yml.charts.ui.piechart.charts.PieChart
import co.yml.charts.ui.piechart.models.PieChartConfig
import co.yml.charts.ui.piechart.models.PieChartData
import timber.log.Timber

@Composable
fun Charts(
  data: Any,
  chartType: PlotType = PlotType.Bar,
  chartConfig: PieChartConfig?,
  displayChipsLegends: Boolean = false,
  listState: LazyListState = rememberLazyListState(),
  onSlideClick: (slice: PieChartData.Slice) -> Unit = {}
) {
  val selectedItem = remember {
    mutableIntStateOf(0)
  }

//  val chartType = data.plotType

  LaunchedEffect(key1 = selectedItem.intValue) {
    listState.animateScrollToItem(selectedItem.intValue)
  }

  when (chartType) {
    is PlotType.Pie -> {
      PieChart(
        modifier = Modifier
          .fillMaxWidth(),
        pieChartData = data as PieChartData,
        pieChartConfig = chartConfig ?: PieChartConfig()
      ) {
        // TODO improve logic to handle when custom lambda is there
        onSlideClick(it)
        selectedItem.intValue = data.slices.indexOf(it)
      }

      if (displayChipsLegends) {
        LazyRow(horizontalArrangement = Arrangement.spacedBy(4.dp), state = listState) {
          itemsIndexed(data.slices) { index, slice ->
            Row {
              AssistChip(
                onClick = { /*TODO*/ },
                label = {
                  Text(text = "${slice.label}, $${slice.value}", color = Color.White)
                },
                colors = AssistChipDefaults.assistChipColors(containerColor = slice.color),
                elevation = if (index == selectedItem.intValue) AssistChipDefaults.assistChipElevation(elevation = 8.dp) else AssistChipDefaults.assistChipElevation(elevation = 2.dp)
              )
            }
          }
        }
      }
    }
    is PlotType.Bar -> {
      val castedData = data as? List<BarData> ?: return

      Timber.d(castedData.toString())

//      val barChartDataVal = DataUtils.getBarChartData(10, 20, BarChartType.VERTICAL, DataCategoryOptions())
      val xAxisData = AxisData
        .Builder()
        .axisLabelAngle(45f)
        .bottomPadding(100.dp)
        .labelData { index -> castedData.getOrNull(index)?.label ?: "Not Available" }
        .build()

      val yAxisData = AxisData.Builder()
        .steps(10)
        .labelAndAxisLinePadding(5.dp)
        .axisOffset(5.dp)
        .labelData { index -> "$${castedData.getOrNull(index)?.point?.y}" ?: "0" }
        .build()

      val barChartData = BarChartData(
        chartData = castedData,
        xAxisData = xAxisData,
        yAxisData = yAxisData
      )

      BarChart(modifier = Modifier.fillMaxHeight(), barChartData = barChartData)
    }
    else -> {}
  }
}
