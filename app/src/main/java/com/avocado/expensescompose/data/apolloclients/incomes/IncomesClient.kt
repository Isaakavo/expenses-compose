package com.avocado.expensescompose.data.apolloclients.incomes

import com.apollographql.apollo3.api.ApolloResponse
import com.avocado.AllIncomesQuery
import com.avocado.IncomesByMonthQuery

interface IncomesClient {
  suspend fun getIncomesByMonth(date: String): ApolloResponse<IncomesByMonthQuery.Data>
  suspend fun getAllIncomes(): ApolloResponse<AllIncomesQuery.Data>
}