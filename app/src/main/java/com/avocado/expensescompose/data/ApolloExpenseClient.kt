package com.avocado.expensescompose.data

import com.apollographql.apollo3.ApolloClient
import com.avocado.ExpensesByFortnightQuery
import com.avocado.expensescompose.domain.Expense
import com.avocado.expensescompose.domain.ExpensesClient
import com.avocado.expensescompose.domain.adapt

class ApolloExpenseClient(
    private val apolloClient: ApolloClient
): ExpensesClient {
    override suspend fun getExpensesByFortnight(): List<Expense> {
        return apolloClient
            //TODO make the request dynamic
            .query(ExpensesByFortnightQuery("2023-09-30"))
            .execute()
            .data
            ?.expensesByFortnight
            ?.map {
                it?.adapt() ?: Expense()
            } ?: emptyList()
    }
}