package com.avocado.expensescompose.data.model.total

data class Total(
  val date: String? = null,
  val total: Double? = null,
  val month: String? = null,
  val year: String? = null
)

data class TotalFortnight(
  val date: String? = null,
  val month: String? = null,
  val total: Double? = null,
  val fortnight: String? = null,
  val year: String? = null
)