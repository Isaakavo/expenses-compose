package com.avocado.expensescompose.domain


class GetExpensesUseCase(
    private val expenseClient: ExpensesClient
) {
    suspend fun execute(): List<Expense> {
        return expenseClient.getExpensesByFortnight().sortedBy { it.concept }
    }
}