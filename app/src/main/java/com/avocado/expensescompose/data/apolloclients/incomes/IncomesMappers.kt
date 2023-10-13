package com.avocado.expensescompose.data.apolloclients.incomes

import com.avocado.HomeScreenAllIncomesQuery
import com.avocado.expensescompose.domain.income.models.Fortnight
import com.avocado.expensescompose.domain.income.models.Income
import com.avocado.expensescompose.domain.income.models.IncomeTotalByMonth
import com.avocado.expensescompose.domain.income.models.PaymentDate

fun HomeScreenAllIncomesQuery.Income.toIncome(): Income {
  return Income(
    id = id,
    userId = userId,
    total = total,
    createdAt = createdAt?.date,
    paymentDate = PaymentDate(
      date = paymentDate.date.date,
      fortnight = Fortnight.valueOf(paymentDate.fortnight.name)
    )
  )
}

fun HomeScreenAllIncomesQuery.TotalByMonth.toTotalByMonth(): IncomeTotalByMonth {
  return IncomeTotalByMonth(
    date = date,
    total = total
  )
}
