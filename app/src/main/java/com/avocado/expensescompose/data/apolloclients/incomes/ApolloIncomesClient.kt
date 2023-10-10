package com.avocado.expensescompose.data.apolloclients.incomes

import com.apollographql.apollo3.ApolloClient
import com.avocado.AllIncomesQuery
import com.avocado.expensescompose.domain.income.IncomesClient
import com.avocado.expensescompose.domain.income.models.Income
import com.avocado.expensescompose.domain.income.models.Incomes
import com.avocado.expensescompose.domain.income.models.PaymentDate
import java.time.LocalDateTime

class ApolloIncomesClient(private val apolloClient: ApolloClient) : IncomesClient {
  override suspend fun getIncomesByMonth(date: String): List<Income> =
    listOf(Income(paymentDate = PaymentDate(date = null)))


  override suspend fun getAllIncomes(): Incomes {
    val responseIncome = apolloClient.query(AllIncomesQuery()).execute().data
    val incomesList = responseIncome?.incomesList?.incomes?.map { item ->
      item.toIncome()
    }
    val totalByMonth = responseIncome?.incomesList?.totalByMonth?.map {
      it.toTotalByMonth()
    }

    return Incomes(
      incomesList = incomesList ?: emptyList(),
      totalByMonth = totalByMonth ?: emptyList(),
      total = responseIncome?.incomesList?.total ?: 0.0
    )
  }


  override suspend fun insertIncome(
    total: Double,
    paymentDate: LocalDateTime,
    comment: String,
  ) {

  }
}