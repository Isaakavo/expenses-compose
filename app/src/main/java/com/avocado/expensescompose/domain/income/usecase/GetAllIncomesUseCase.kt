package com.avocado.expensescompose.domain.income.usecase

import com.avocado.expensescompose.data.model.MyResult
import com.avocado.expensescompose.domain.income.IncomesClient
import com.avocado.expensescompose.domain.income.models.Incomes

class GetAllIncomesUseCase(
  private val incomesClient: IncomesClient
) {
  suspend operator fun invoke(): MyResult<Incomes> {
    return incomesClient.getAllIncomes()
  }
}