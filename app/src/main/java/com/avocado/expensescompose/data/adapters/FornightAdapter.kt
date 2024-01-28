package com.avocado.expensescompose.data.adapters

import com.avocado.expensescompose.R
import com.avocado.type.Fortnight

fun Fortnight.adapt(): Int {
  return when (this) {
    Fortnight.FIRST -> R.string.first_fortnight
    Fortnight.SECOND -> R.string.second_fortnight
    else -> 0
  }
}
