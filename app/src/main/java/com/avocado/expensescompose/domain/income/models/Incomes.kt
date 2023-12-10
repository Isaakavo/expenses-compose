package com.avocado.expensescompose.domain.income.models

import com.avocado.expensescompose.data.model.total.Total

data class Incomes(
  val incomesList: List<Income>,
  val totalByMonth: List<Total>,
  val total: Double
)
