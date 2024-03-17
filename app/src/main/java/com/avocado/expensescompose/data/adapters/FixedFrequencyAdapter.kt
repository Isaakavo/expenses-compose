package com.avocado.expensescompose.data.adapters

import com.avocado.expensescompose.R
import com.avocado.type.FixedExpenseFrequency

fun FixedExpenseFrequency.adapt(): Int = when (this) {
  FixedExpenseFrequency.Monthly -> R.string.fixed_expense_frequency_month
  FixedExpenseFrequency.Biweekly -> R.string.fixed_expense_frequency_biweekly
  else -> 0
}
