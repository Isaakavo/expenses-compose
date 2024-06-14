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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
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
  displayNativeLegends: Boolean = false,
  displayChipsLegends: Boolean = false,
  listState: LazyListState = rememberLazyListState(),
  onSlideClick: (slice: PieChartData.Slice) -> Unit = {}
) {
  val selectedItem = remember {
    mutableIntStateOf(0)
  }

  LaunchedEffect(key1 = selectedItem.intValue) {
    listState.animateScrollToItem(selectedItem.intValue)
  }

  when (data) {
    is PieChartData -> {
      DonutPieChart(
        modifier = Modifier
          .fillMaxWidth(),
        data,
        donutChartConfig ?: PieChartConfig()
      ) {
        // TODO improve logic to handle when custom lambda is there
        onSlideClick(it)
        selectedItem.intValue = data.slices.indexOf(it)
      }
      if (displayNativeLegends && !displayChipsLegends) {
        Legends(
          legendsConfig = DataUtils.getLegendsConfigFromPieChartData(pieChartData = data, gridSize = data.slices.size / 3)
        )
      } else {
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
