package com.avocado.expensescompose.data.apolloclients.incomes

import android.util.Log
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.Optional
import com.apollographql.apollo3.exception.ApolloException
import com.avocado.AllIncomesQuery
import com.avocado.CreateIncomeMutation
import com.avocado.expensescompose.data.model.MyResult
import com.avocado.expensescompose.domain.income.IncomesClient
import com.avocado.expensescompose.domain.income.models.Fortnight
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
  ): MyResult<Income?> {
    try {
      val insertedIncome = apolloClient.mutation(
        CreateIncomeMutation(
          total = total,
          paymentDate = com.avocado.expensescompose.data.adapters.graphql.scalar.Date(paymentDate),
          comment = Optional.present(comment)
        )
      ).execute().data?.createIncome
        ?: return MyResult.Error(uiText = "Error extracting the response")

      return MyResult.Success(
        Income(
          userId = insertedIncome.userId,
          total = insertedIncome.total,
          paymentDate = PaymentDate(
            date = insertedIncome.paymentDate.date.date,
            fortnight = Fortnight.valueOf(insertedIncome.paymentDate.fortnight.name)
          )
        )
      )
    } catch (e: ApolloException) {
      Log.d("Apollo Incomes", e.message.toString())
      return MyResult.Error(uiText = e.message.toString())
    }
  }
}