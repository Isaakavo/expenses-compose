package com.avocado.expensescompose.domain

import java.util.Date

//TODO make the required values not null
data class Expense(
    val id: Int? = null,
    val userId: String? = null,
    val concept: String? = null,
    val total: Double? = null,
    val payBefore: Date? = null,
    val createdAt: Date? = null,
    val updatedAt: Date? = null,
    val tags: ExpenseTag? = null,
    val comment: String? = null
)
