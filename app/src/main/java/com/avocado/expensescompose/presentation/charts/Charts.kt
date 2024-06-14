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
import co.yml.charts.common.model.PlotData
import co.yml.charts.common.model.PlotType
import co.yml.charts.ui.piechart.charts.PieChart
import co.yml.charts.ui.piechart.models.PieChartConfig
import co.yml.charts.ui.piechart.models.PieChartData
import com.avocado.type.Category

@Composable
fun Charts(
  data: PlotData,
  chartConfig: PieChartConfig?,
  displayChipsLegends: Boolean = false,
  listState: LazyListState = rememberLazyListState(),
  onSlideClick: (slice: PieChartData.Slice) -> Unit = {}
) {
  val selectedItem = remember {
    mutableIntStateOf(0)
  }

  val chartType = data.plotType

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

    else -> {}
  }
}

fun Category.color(): Color = when (this) {
  Category.SAVINGS -> Color(0xFFFFA726) // Deep Orange 300
  Category.COMMUNICATION -> Color(0xFFE57373) // Red 300
  Category.BILLS -> Color(0xFFAED581) // Light Green 300
  Category.FOOD -> Color(0xFF4FC3F7) // Light Blue 300
  Category.CAR -> Color(0xFF4DD0E1) // Cyan 300
  Category.CLOTHES -> Color(0xFF66BB6A) // Green 400
  Category.EATING_OUT -> Color(0xFF9575CD) // Deep Purple 300
  Category.ENTERTAINMENT -> Color(0xFF7986CB) // Indigo 300
  Category.GIFTS -> Color(0xFFFFA726) // Deep Orange 300
  Category.HANG_OUT -> Color(0xFF4FC3F7) // Light Blue 300
  Category.HEALTH -> Color(0xFFF06292) // Pink 300
  Category.HOUSE -> Color(0xFF8D6E63) // Brown 300
  Category.INSURANCE -> Color(0xFFF06292) // Pink 300
  Category.PETS -> Color(0xFFCFD8DC) // Blue Grey 200
  Category.SPORTS -> Color(0xFF7986CB) // Indigo 300
  Category.SUPER_MARKET -> Color(0xFF64B5F6) // Blue 300
  Category.TRANSPORT -> Color(0xFFE57373) // Red 300
  Category.SUBSCRIPTION -> Color(0xFFBA8B67) // Brown 400
  Category.FIXED_EXPENSE -> Color(0xFF4DB6AC) // Teal 300
  Category.MONTHS_WITHOUT_INTEREST -> Color(0xFFA1887F)
  else -> Color.Black
}
