package com.avocado.expensescompose.data.apolloclients.incomes

import com.apollographql.apollo3.api.ApolloResponse
import com.avocado.AllIncomesQuery
import com.avocado.CreateIncomeMutation
import com.avocado.IncomesByMonthQuery
import java.time.LocalDateTime

// TODO refactor to no use apollo types in the return
interface IncomesClient {
  suspend fun getIncomesByMonth(date: String): ApolloResponse<IncomesByMonthQuery.Data>
  suspend fun getAllIncomes(): ApolloResponse<AllIncomesQuery.Data>
  suspend fun insertIncome(
    total: Double,
    paymentDate: LocalDateTime,
    comment: String
  ): ApolloResponse<CreateIncomeMutation.Data>
}