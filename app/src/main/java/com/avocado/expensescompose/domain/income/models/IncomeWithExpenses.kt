package com.avocado.expensescompose.domain.income.models

data class IncomeWithExpenses(
  val income: Income,
  val expensesList: List<Expense>?,
  val expensesTotal: Double,
  val remaining: Double
)
