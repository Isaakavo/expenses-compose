package com.avocado.expensescompose.domain.income

import com.apollographql.apollo3.api.ApolloResponse
import com.avocado.IncomesByMonthQuery
import com.avocado.expensescompose.data.ExpensesClient

class GetIncomeUseCase(
    private val expenseClient: ExpensesClient
) {
    suspend fun execute(date: String): ApolloResponse<IncomesByMonthQuery.Data> {
        return expenseClient.getIncomesByMonth(date)
    }
}