package com.avocado.expensescompose.data.adapters

import com.avocado.type.Fortnight


fun Fortnight.adapt(): String {
  return when (this) {
    Fortnight.FIRST -> "Primera Quincena"
    Fortnight.SECOND -> "Segunda Quincena"
    else -> ""
  }
}