package com.avocado.expensescompose.data.adapters

import com.avocado.expensescompose.domain.income.models.Fortnight

fun Fortnight.adapt(): String {
  return when (this) {
    Fortnight.FIRST -> "Primera"
    Fortnight.SECOND -> "Segunda"
  }
}