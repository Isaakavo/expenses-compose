package com.avocado.expensescompose.data.adapters

import java.text.NumberFormat
import java.util.Locale

fun Double.formatMoney(): String {
  val moneyFormat = NumberFormat.getCurrencyInstance(Locale.US)
  return moneyFormat.format(this)
}
