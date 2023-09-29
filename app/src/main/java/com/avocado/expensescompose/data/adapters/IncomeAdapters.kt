package com.avocado.expensescompose.data.adapters

import com.avocado.AllIncomesQuery
import com.avocado.IncomesByMonthQuery
import com.avocado.expensescompose.data.model.incomes.Fortnight
import com.avocado.expensescompose.data.model.incomes.Income
import com.avocado.expensescompose.data.model.incomes.PaymentDate
import com.avocado.expensescompose.data.model.incomes.TotalByMonth

fun IncomesByMonthQuery.IncomesByMonth.adapt(): Income = Income(
    userId = userId,
    total = total,
    paymentDate = PaymentDate(
        date = paymentDate.date.date,
        fortnight = Fortnight.valueOf(paymentDate.forthnight.name)
    ),
    createdAt = createdAt?.date?.formatDateWithYear()
)

fun AllIncomesQuery.Income.adapt(): Income = Income(
    userId = userId,
    total = total,
    paymentDate = PaymentDate(
        date = paymentDate.date.date,
        fortnight = Fortnight.valueOf(paymentDate.forthnight.name)
    ),
    createdAt = createdAt?.date?.formatDateWithYear()
)

fun AllIncomesQuery.TotalByMonth.adapt() = TotalByMonth(
    date = date,
    total = total
)
