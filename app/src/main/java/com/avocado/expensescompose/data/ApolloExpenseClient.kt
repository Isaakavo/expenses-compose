package com.avocado.expensescompose.data

import com.apollographql.apollo3.ApolloClient
import com.avocado.ExpensesByFortnightQuery
import com.avocado.expensescompose.data.adapters.graphql.scalar.Date
import com.avocado.expensescompose.domain.Expense
import com.avocado.expensescompose.domain.adapt
import java.time.LocalDateTime

class ApolloExpenseClient(
  private val apolloClient: ApolloClient
) : ExpensesClient {
  override suspend fun getExpensesByFortnight(): List<Expense> {
    return apolloClient
      //TODO make the request dynamic
      .query(ExpensesByFortnightQuery(Date(LocalDateTime.now())))
      .execute().data?.expensesByFortnight?.map {
        it?.adapt() ?: Expense()
      } ?: emptyList()
  }
}