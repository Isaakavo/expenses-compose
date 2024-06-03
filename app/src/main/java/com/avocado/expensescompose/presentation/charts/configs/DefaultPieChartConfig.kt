package com.avocado.expensescompose.presentation.charts.configs

import android.graphics.Typeface
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import co.yml.charts.ui.piechart.models.PieChartConfig

fun defaultDonutChartConfig(backgroundColor: Color, textColor: Color) = PieChartConfig(
  strokeWidth = 80f,
  activeSliceAlpha = .9f,
  isAnimationEnable = true,
  labelColor = textColor,
  sliceLabelTextColor = textColor,
  sliceLabelTextSize = 28.sp,
  labelFontSize = 24.sp,
  labelVisible = true,
  backgroundColor = backgroundColor,
  labelType = PieChartConfig.LabelType.PERCENTAGE,
  sliceLabelTypeface = Typeface.DEFAULT_BOLD,
  isSumVisible = true
)
