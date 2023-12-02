package com.avocado.expensescompose.domain.expense.models

data class Expense(
  val concept: String,
  val comment: String,
  val date: String,
  val total: Double
)
