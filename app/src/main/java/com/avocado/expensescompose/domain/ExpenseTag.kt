package com.avocado.expensescompose.domain

import java.util.Date

data class ExpenseTag(
    val id: Int,
    val name: String,
    val createdAt: Date,
    val updatedAt: Date
    )
