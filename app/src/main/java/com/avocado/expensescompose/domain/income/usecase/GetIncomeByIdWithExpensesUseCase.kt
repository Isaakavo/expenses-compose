package com.avocado.expensescompose.domain.income.usecase

import com.avocado.expensescompose.data.model.MyResult
import com.avocado.expensescompose.domain.income.IncomesClient
import com.avocado.expensescompose.domain.income.models.IncomeWithExpenses

class GetIncomeByIdWithExpensesUseCase(
  private val incomesClient: IncomesClient
) {
  suspend operator fun invoke(paymentDate: String): MyResult<IncomeWithExpenses> {
    return incomesClient.getIncomeByIdWithExpenses(paymentDate)
  }
}