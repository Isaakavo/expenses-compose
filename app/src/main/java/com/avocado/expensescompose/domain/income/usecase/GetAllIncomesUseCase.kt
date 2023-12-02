package com.avocado.expensescompose.domain.income.usecase

import com.avocado.HomeScreenAllIncomesQuery
import com.avocado.expensescompose.data.apolloclients.GraphQlClient
import com.avocado.expensescompose.data.apolloclients.incomes.toIncome
import com.avocado.expensescompose.data.apolloclients.incomes.toTotalByMonth
import com.avocado.expensescompose.data.model.MyResult
import com.avocado.expensescompose.domain.income.models.Incomes
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.Flow

class GetAllIncomesUseCase(
  private val graphQlClient: GraphQlClient
) {
  suspend operator fun invoke(): Flow<MyResult<Incomes>> =
    graphQlClient.query(HomeScreenAllIncomesQuery()).map {
      val responseIncome = it.data
      if (responseIncome != null) {
        val incomesList = responseIncome.incomesList?.incomes?.map { item ->
          item.toIncome()
        }
        val totalByMonth = responseIncome.incomesList?.totalByMonth?.map { totalByMonth ->
          totalByMonth.toTotalByMonth()
        }

        MyResult.Success(
          Incomes(
            incomesList = incomesList ?: emptyList(),
            totalByMonth = totalByMonth ?: emptyList(),
            total = responseIncome.incomesList?.total ?: 0.0
          )
        )
      } else {
        MyResult.Error(uiText = "error", data = null)
      }
    }

}