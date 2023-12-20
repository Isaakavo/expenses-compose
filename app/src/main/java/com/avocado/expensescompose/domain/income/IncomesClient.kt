package com.avocado.expensescompose.domain.income

import com.avocado.expensescompose.data.model.MyResult
import com.avocado.expensescompose.domain.income.models.Income
import com.avocado.expensescompose.domain.income.models.IncomeWithExpenses
import com.avocado.expensescompose.domain.income.models.Incomes
import java.time.LocalDateTime

interface IncomesClient {
  suspend fun getIncomesByMonth(date: String): List<Income>
  suspend fun getAllIncomes(): MyResult<Incomes>
  suspend fun insertIncome(
    total: Double,
    paymentDate: LocalDateTime,
    comment: String
  ): MyResult<Income?>
}