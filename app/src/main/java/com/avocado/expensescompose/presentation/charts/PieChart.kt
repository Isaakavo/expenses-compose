package com.avocado.expensescompose.presentation.charts

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import co.yml.charts.common.model.PlotType
import co.yml.charts.ui.piechart.models.PieChartData
import com.avocado.expensescompose.presentation.charts.configs.color
import com.avocado.expensescompose.presentation.charts.configs.defaultDonutChartConfig
import com.avocado.type.Category

@Composable
fun PieChart(
  data: Map<String, Float>,
  listState: LazyListState = rememberLazyListState(),
  displayChipsLegends: Boolean = false,
  onSlideClick: (
    slice: PieChartData.Slice
  ) -> Unit = {}
) {
  val selectedItem = remember {
    mutableIntStateOf(0)
  }
  LaunchedEffect(key1 = selectedItem.intValue) {
    listState.animateScrollToItem(selectedItem.intValue)
  }
  val pieChartData = PieChartData(
    plotType = PlotType.Pie,
    slices = data.map {
      val color = Category.safeValueOf(it.key).color()
      PieChartData.Slice(
        label = it.key,
        value = it.value,
        color = color
      )
    }
  )
  co.yml.charts.ui.piechart.charts.PieChart(
    modifier = Modifier
      .fillMaxWidth(),
    pieChartData = pieChartData,
    pieChartConfig = defaultDonutChartConfig(
      backgroundColor = MaterialTheme.colorScheme.background,
      textColor = Color.White
    )
  ) {
    // TODO improve logic to handle when custom lambda is there
    onSlideClick(it)
    selectedItem.intValue = pieChartData.slices.indexOf(it)
  }

  if (displayChipsLegends) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(4.dp), state = listState) {
      itemsIndexed(pieChartData.slices) { index, slice ->
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
