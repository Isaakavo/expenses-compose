package com.avocado.expensescompose.domain.income.models

import java.time.LocalDateTime

data class ExpenseTag(
  val id: String,
  val name: String,
  val createdAt: LocalDateTime? = null,
  val updatedAt: LocalDateTime? = null
)
