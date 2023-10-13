package com.avocado.expensescompose.domain.income.models

data class Incomes(
  val incomesList: List<Income>,
  val totalByMonth: List<IncomeTotalByMonth>,
  val total: Double
)
