package com.avocado.expensescompose.domain.income.models

import java.time.LocalDateTime

data class Expense(
  val id: String,
  val incomeId: String = "",
  val userId: String? = null,
  val concept: String,
  val total: Double,
  val payBefore: LocalDateTime? = null,
  val createdAt: LocalDateTime? = null,
  val updatedAt: LocalDateTime? = null,
  val tags: List<ExpenseTag>,
  val comment: String? = null
)