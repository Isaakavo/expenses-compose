package com.avocado.expensescompose.data

import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.ApolloResponse
import com.avocado.AllIncomesQuery
import com.avocado.ExpensesByFortnightQuery
import com.avocado.IncomesByMonthQuery
import com.avocado.LoginMutation
import com.avocado.ValidateTokenMutation
import com.avocado.expensescompose.data.adapters.graphql.scalar.Date
import com.avocado.expensescompose.data.model.auth.AuthParameters
import com.avocado.expensescompose.domain.Expense
import com.avocado.expensescompose.domain.adapt
import java.time.LocalDateTime

class ApolloExpenseClient(
  private val apolloClient: ApolloClient
) : ExpensesClient {
  override suspend fun getLoginToken(auth: AuthParameters): ApolloResponse<LoginMutation.Data> =
    apolloClient.mutation(LoginMutation(auth.username, auth.password)).execute()

  override suspend fun validateToken(token: String): ApolloResponse<ValidateTokenMutation.Data> =
    apolloClient.mutation(ValidateTokenMutation(token)).execute()


  override suspend fun getIncomesByMonth(date: String): ApolloResponse<IncomesByMonthQuery.Data> =
    apolloClient
      .query(IncomesByMonthQuery(Date(LocalDateTime.now())))
      .execute()

  override suspend fun getAllIncomes(): ApolloResponse<AllIncomesQuery.Data> =
    apolloClient.query(AllIncomesQuery()).execute()

  override suspend fun getExpensesByFortnight(): List<Expense> {
    return apolloClient
      //TODO make the request dynamic
      .query(ExpensesByFortnightQuery(Date(LocalDateTime.now())))
      .execute()
      .data
      ?.expensesByFortnight
      ?.map {
        it?.adapt() ?: Expense()
      } ?: emptyList()
  }
}