package com.avocado.expensescompose.data.model.total

import com.avocado.type.Fortnight

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
  val fortnight: Fortnight? = null,
  val year: String? = null
)
