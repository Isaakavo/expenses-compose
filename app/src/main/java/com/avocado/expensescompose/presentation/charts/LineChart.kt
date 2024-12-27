package com.avocado.expensescompose.presentation.charts

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottom
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStart
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberColumnCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.common.component.rememberLineComponent
import com.patrykandpatrick.vico.compose.common.component.rememberTextComponent
import com.patrykandpatrick.vico.compose.common.component.shapeComponent
import com.patrykandpatrick.vico.compose.common.dimensions
import com.patrykandpatrick.vico.compose.common.fill
import com.patrykandpatrick.vico.core.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.columnSeries
import com.patrykandpatrick.vico.core.cartesian.decoration.HorizontalLine
import com.patrykandpatrick.vico.core.cartesian.layer.ColumnCartesianLayer
import com.patrykandpatrick.vico.core.common.Fill
import com.patrykandpatrick.vico.core.common.shape.CorneredShape

@Composable
fun LineChart(data: Map<String, Float>, modifier: Modifier = Modifier) {
  val modelProducer = remember { CartesianChartModelProducer() }
  var average by remember {
    mutableDoubleStateOf(0.0)
  }

  LaunchedEffect(data) {
    modelProducer.runTransaction { columnSeries { series(data.values) } }
    average = (data.values.sum() / data.values.size).toDouble()
  }

  CartesianChartHost(
    chart = rememberCartesianChart(
      rememberColumnCartesianLayer(
        ColumnCartesianLayer.ColumnProvider.series(
          rememberLineComponent(
            fill = fill(Color(0xffff5500)),
            thickness = 16.dp,
            shape = CorneredShape.rounded(allPercent = 40)
          )
        )
      ),
      startAxis = VerticalAxis.rememberStart(),
      bottomAxis = HorizontalAxis.rememberBottom(
        valueFormatter = { _, x, _ -> "${data.keys.toList().getOrNull(x.toInt())}" },
        itemPlacer = remember {
          HorizontalAxis.ItemPlacer.aligned(addExtremeLabelPadding = false, shiftExtremeLines = false)
        }
      ),
      marker = rememberMarker(),
      decorations = listOf(rememberComposeHorizontalLine(average))
    ),
    modelProducer = modelProducer,
    modifier = modifier
  )
}

@Composable
private fun rememberComposeHorizontalLine(average: Double): HorizontalLine {
  val fill = Fill(HORIZONTAL_LINE_COLOR)
  val line = rememberLineComponent(fill, HORIZONTAL_LINE_THICKNESS_DP.dp)
  val labelComponent =
    rememberTextComponent(
      margins = dimensions(HORIZONTAL_LINE_LABEL_MARGIN_DP.dp),
      padding =
      dimensions(
        HORIZONTAL_LINE_LABEL_HORIZONTAL_PADDING_DP.dp,
        HORIZONTAL_LINE_LABEL_VERTICAL_PADDING_DP.dp
      ),
      background = shapeComponent(fill, CorneredShape.Pill)
    )
  return HorizontalLine({ average }, line, labelComponent)
}

private const val HORIZONTAL_LINE_COLOR = -2893786
private const val HORIZONTAL_LINE_THICKNESS_DP = 2f
private const val HORIZONTAL_LINE_LABEL_HORIZONTAL_PADDING_DP = 8f
private const val HORIZONTAL_LINE_LABEL_VERTICAL_PADDING_DP = 2f
private const val HORIZONTAL_LINE_LABEL_MARGIN_DP = 4f
