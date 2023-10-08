package com.avocado.expensescompose.data.apolloclients.incomes

import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.ApolloResponse
import com.avocado.AllIncomesQuery
import com.avocado.IncomesByMonthQuery
import com.avocado.expensescompose.data.adapters.graphql.scalar.Date
import java.time.LocalDateTime

class ApolloIncomesClient(private val apolloClient: ApolloClient): IncomesClient {
  override suspend fun getIncomesByMonth(date: String): ApolloResponse<IncomesByMonthQuery.Data> =
    apolloClient.query(IncomesByMonthQuery(Date(LocalDateTime.now()))).execute()

  override suspend fun getAllIncomes(): ApolloResponse<AllIncomesQuery.Data> =
    apolloClient.query(AllIncomesQuery()).execute()
}