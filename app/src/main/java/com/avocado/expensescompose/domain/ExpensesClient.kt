package com.avocado.expensescompose.domain


interface ExpensesClient {
    suspend fun getExpensesByFortnight(): List<Expense>
}