package com.avocado.expensescompose.domain

import com.avocado.expensescompose.data.ExpensesClient


class GetExpensesUseCase(
    private val expenseClient: ExpensesClient
) {
    suspend fun execute(): List<Expense> {
        return expenseClient.getExpensesByFortnight().sortedBy { it.concept }
    }
}