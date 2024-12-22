package com.avocado.expensescompose.presentation.charts.configs

import androidx.compose.ui.graphics.Color

fun color(index: Int): Color = listOf(
  Color(0xFFFFA726),
  Color(0xFFE57373),
  Color(0xFFAED581),
  Color(0xFF4FC3F7),
  Color(0xFF4DD0E1),
  Color(0xFF66BB6A),
  Color(0xFF9575CD),
  Color(0xFF7986CB),
  Color(0xFF4DB6AC),
  Color(0xFF4FC3F7),
  Color(0xFFF06292),
  Color(0xFF8D6E63),
  Color(0xFFFFB74D),
  Color(0xFFCFD8DC),
  Color(0xFF7986CB),
  Color(0xFF64B5F6),
  Color(0xFFE57373),
  Color(0xFFBA8B67),
  Color(0xFF4DB6AC),
  Color(0xFFA1887F)
).getOrNull(index) ?: Color(0xFF64B5F6)
