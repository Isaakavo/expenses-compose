package com.avocado.expensescompose.presentation.charts.configs

import androidx.compose.ui.graphics.Color
import com.avocado.type.Category

fun Category.color(): Color = when (this) {
  Category.SAVINGS -> Color(0xFFFFA726)
  Category.COMMUNICATION -> Color(0xFFE57373)
  Category.BILLS -> Color(0xFFAED581)
  Category.FOOD -> Color(0xFF4FC3F7)
  Category.CAR -> Color(0xFF4DD0E1)
  Category.CLOTHES -> Color(0xFF66BB6A)
  Category.EATING_OUT -> Color(0xFF9575CD)
  Category.ENTERTAINMENT -> Color(0xFF7986CB)
  Category.GIFTS -> Color(0xFF4DB6AC)
  Category.HANG_OUT -> Color(0xFF4FC3F7)
  Category.HEALTH -> Color(0xFFF06292)
  Category.HOUSE -> Color(0xFF8D6E63)
  Category.INSURANCE -> Color(0xFFFFB74D)
  Category.PETS -> Color(0xFFCFD8DC)
  Category.SPORTS -> Color(0xFF7986CB)
  Category.SUPER_MARKET -> Color(0xFF64B5F6)
  Category.TRANSPORT -> Color(0xFFE57373)
  Category.SUBSCRIPTION -> Color(0xFFBA8B67)
  Category.FIXED_EXPENSE -> Color(0xFF4DB6AC)
  Category.MONTHS_WITHOUT_INTEREST -> Color(0xFFA1887F)
  else -> Color.Black
}
