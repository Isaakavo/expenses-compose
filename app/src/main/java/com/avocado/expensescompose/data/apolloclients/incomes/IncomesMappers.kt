package com.avocado.expensescompose.data.apolloclients.incomes

import com.avocado.AllIncomesQuery
import com.avocado.expensescompose.domain.income.models.Fortnight
import com.avocado.expensescompose.domain.income.models.Income
import com.avocado.expensescompose.domain.income.models.IncomeTotalByMonth
import com.avocado.expensescompose.domain.income.models.PaymentDate

fun AllIncomesQuery.Income.toIncome(): Income {
  return Income(
    userId = userId,
    total = total,
    createdAt = createdAt?.date,
    paymentDate = PaymentDate(
      date = paymentDate.date.date,
      fortnight = Fortnight.valueOf(paymentDate.fortnight.name)
    )
  )
}

fun AllIncomesQuery.TotalByMonth.toTotalByMonth(): IncomeTotalByMonth {
  return IncomeTotalByMonth(
    date = date,
    total = total
  )
}
