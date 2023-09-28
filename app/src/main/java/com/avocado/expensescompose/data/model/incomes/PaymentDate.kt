package com.avocado.expensescompose.data.model.incomes

import java.time.LocalDateTime

data class PaymentDate(
    val date: LocalDateTime, val fortnight: Fortnight
)
