package com.avocado.expensescompose.data

import com.apollographql.apollo3.api.ApolloResponse
import com.avocado.AllIncomesQuery
import com.avocado.IncomesByMonthQuery
import com.avocado.LoginMutation
import com.avocado.expensescompose.data.model.auth.AuthParameters
import com.avocado.expensescompose.domain.Expense


interface ExpensesClient {
  suspend fun getLoginToken(auth: AuthParameters, token: String): ApolloResponse<LoginMutation.Data>
  suspend fun getIncomesByMonth(date: String): ApolloResponse<IncomesByMonthQuery.Data>
  suspend fun getAllIncomes(): ApolloResponse<AllIncomesQuery.Data>
  suspend fun getExpensesByFortnight(): List<Expense>
}