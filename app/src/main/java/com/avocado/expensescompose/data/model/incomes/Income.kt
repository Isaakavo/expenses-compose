package com.avocado.expensescompose.data.model.incomes

data class Income(
    val userId: String,
    val total: Double,
    val paymentDate: PaymentDate,
    val createdAt: String? = null
)
