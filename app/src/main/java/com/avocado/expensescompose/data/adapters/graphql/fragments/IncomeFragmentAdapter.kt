package com.avocado.expensescompose.data.adapters.graphql.fragments

import com.avocado.expensescompose.domain.income.models.Fortnight
import com.avocado.expensescompose.domain.income.models.Income
import com.avocado.expensescompose.domain.income.models.PaymentDate
import com.avocado.fragment.IncomeFragment

fun IncomeFragment.toIncome(): Income = Income(
  id = id,
  userId = userId,
  total = total,
  createdAt = createdAt?.date,
  paymentDate = PaymentDate(
    date = paymentDate.date.date,
    fortnight = Fortnight.valueOf(paymentDate.fortnight.name)
  )
)
