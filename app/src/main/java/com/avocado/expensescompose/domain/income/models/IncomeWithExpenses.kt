package com.avocado.expensescompose.domain.income.models

data class IncomeWithExpenses(
  val income: List<Income>,
  val expensesList: List<Expense>?,
  val expensesTotal: Double,
  val remaining: Double
)
