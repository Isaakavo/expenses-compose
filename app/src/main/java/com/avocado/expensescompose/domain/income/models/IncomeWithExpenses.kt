package com.avocado.expensescompose.domain.income.models

data class IncomeWithExpenses(
  val incomes: List<Income>,
  val expensesList: List<Expense>?,
  val incomesTotal: Double,
  val expensesTotal: Double,
  val remaining: Double
)
