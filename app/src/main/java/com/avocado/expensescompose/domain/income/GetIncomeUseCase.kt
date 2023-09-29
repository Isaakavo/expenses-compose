package com.avocado.expensescompose.domain.income

import com.apollographql.apollo3.api.ApolloResponse
import com.avocado.AllIncomesQuery
import com.avocado.IncomesByMonthQuery
import com.avocado.expensescompose.data.ExpensesClient

class GetIncomeUseCase(
    private val expenseClient: ExpensesClient
) {

    suspend fun executeAllIncomes(): ApolloResponse<AllIncomesQuery.Data> =
        expenseClient.getAllIncomes()

    suspend fun executeIncomesByMonth(date: String): ApolloResponse<IncomesByMonthQuery.Data> =
        expenseClient.getIncomesByMonth(date)

}