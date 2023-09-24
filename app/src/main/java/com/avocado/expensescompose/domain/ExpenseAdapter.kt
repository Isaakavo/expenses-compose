package com.avocado.expensescompose.domain

import com.avocado.ExpensesByFortnightQuery

fun ExpensesByFortnightQuery.ExpensesByFortnight.adapt(): Expense = Expense(
    id = id.toInt(),
    userId = userId,
    concept = concept,
    total = total
)