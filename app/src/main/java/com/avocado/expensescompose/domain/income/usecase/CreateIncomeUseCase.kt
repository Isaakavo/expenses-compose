package com.avocado.expensescompose.domain.income.usecase

import com.avocado.expensescompose.data.model.MyResult
import com.avocado.expensescompose.domain.income.IncomesClient
import com.avocado.expensescompose.domain.income.models.Income
import java.time.LocalDateTime

class CreateIncomeUseCase(
  private val incomesClient: IncomesClient
) {
  suspend operator fun invoke(
    total: Double,
    paymentDate: LocalDateTime,
    comment: String
  ): MyResult<Income?> {
    return incomesClient.insertIncome(total, paymentDate, comment.trim())
  }
}
