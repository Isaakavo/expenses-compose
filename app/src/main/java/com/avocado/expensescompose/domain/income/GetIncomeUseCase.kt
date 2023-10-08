package com.avocado.expensescompose.domain.income

import com.apollographql.apollo3.api.ApolloResponse
import com.avocado.AllIncomesQuery
import com.avocado.IncomesByMonthQuery
import com.avocado.expensescompose.data.apolloclients.incomes.IncomesClient

class GetIncomeUseCase(
    private val incomesClient: IncomesClient
) {

    suspend fun executeAllIncomes(): ApolloResponse<AllIncomesQuery.Data> =
        incomesClient.getAllIncomes()

    suspend fun executeIncomesByMonth(date: String): ApolloResponse<IncomesByMonthQuery.Data> =
        incomesClient.getIncomesByMonth(date)

}