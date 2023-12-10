package com.avocado.expensescompose.domain.income.models


import java.time.LocalDateTime

data class Income(
  val id: String = "",
  val userId: String = "",
  val comment: String = "",
  val total: Double = 0.0,
  val createdAt: LocalDateTime? = null,
  val paymentDate: PaymentDate
)
