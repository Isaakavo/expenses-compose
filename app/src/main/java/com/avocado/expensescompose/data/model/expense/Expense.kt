package com.avocado.expensescompose.data.model.expense

import com.avocado.expensescompose.data.model.card.Card
import com.avocado.expensescompose.domain.income.models.ExpenseTag
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
  val comment: String? = null,
  val card: Card
)