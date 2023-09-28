package com.avocado.expensescompose.data.model.incomes

import java.time.LocalDateTime

data class Income(
    val userId: String,
    val total: Double,
    val paymentDate: PaymentDate,
    val createdAt: LocalDateTime
)
