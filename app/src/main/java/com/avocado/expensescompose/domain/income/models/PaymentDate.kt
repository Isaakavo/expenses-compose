package com.avocado.expensescompose.domain.income.models

import java.time.LocalDateTime

data class PaymentDate(
  val date: LocalDateTime ? = null,
  val fortnight: Fortnight? = null
)
