package com.avocado.expensescompose.domain.income.models

enum class Fortnight {
  FIRST,
  SECOND;

  fun translate(): String = when(this) {
    FIRST -> "Primera"
    SECOND -> "Segunda"
  }
}