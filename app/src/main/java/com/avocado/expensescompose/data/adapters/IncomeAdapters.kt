package com.avocado.expensescompose.data.adapters

import com.avocado.IncomesByMonthQuery
import com.avocado.expensescompose.data.model.incomes.Fortnight
import com.avocado.expensescompose.data.model.incomes.Income
import com.avocado.expensescompose.data.model.incomes.PaymentDate

import java.time.LocalDateTime

fun IncomesByMonthQuery.IncomesByMonth.adapt(): Income = Income(
    userId = userId,
    total = total,
    paymentDate = PaymentDate(
        date = LocalDateTime.now(),
        fortnight = Fortnight.valueOf(paymentDate.forthnight.name)
    ),
    createdAt = LocalDateTime.now()
)
