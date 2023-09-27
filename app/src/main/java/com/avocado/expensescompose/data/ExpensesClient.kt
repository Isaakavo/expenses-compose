package com.avocado.expensescompose.data

import com.avocado.expensescompose.domain.Expense


interface ExpensesClient {
    suspend fun getExpensesByFortnight(): List<Expense>
}