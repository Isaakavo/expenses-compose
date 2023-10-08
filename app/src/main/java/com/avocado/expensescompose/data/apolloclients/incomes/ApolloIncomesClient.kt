package com.avocado.expensescompose.data.apolloclients.incomes

import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.ApolloResponse
import com.apollographql.apollo3.api.Optional
import com.avocado.AllIncomesQuery
import com.avocado.CreateIncomeMutation
import com.avocado.IncomesByMonthQuery
import com.avocado.expensescompose.data.adapters.graphql.scalar.Date
import java.time.LocalDateTime

class ApolloIncomesClient(private val apolloClient: ApolloClient) : IncomesClient {
  override suspend fun getIncomesByMonth(date: String): ApolloResponse<IncomesByMonthQuery.Data> =
    apolloClient.query(IncomesByMonthQuery(Date(LocalDateTime.now()))).execute()

  override suspend fun getAllIncomes(): ApolloResponse<AllIncomesQuery.Data> =
    apolloClient.query(AllIncomesQuery()).execute()

  override suspend fun insertIncome(
    total: Double,
    paymentDate: LocalDateTime,
    comment: String,
  ): ApolloResponse<CreateIncomeMutation.Data> =
    apolloClient.mutation(
      CreateIncomeMutation(
        total = total,
        paymentDate = Date(paymentDate),
        comment = Optional.present(comment)
      )
    ).execute()
}