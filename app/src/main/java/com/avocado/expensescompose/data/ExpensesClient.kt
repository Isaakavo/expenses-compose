package com.avocado.expensescompose.data

import com.apollographql.apollo3.api.ApolloResponse
import com.avocado.IncomesByMonthQuery
import com.avocado.expensescompose.domain.Expense


interface ExpensesClient {
    suspend fun getIncomesByMonth(date: String): ApolloResponse<IncomesByMonthQuery.Data>
    suspend fun getExpensesByFortnight(): List<Expense>
}