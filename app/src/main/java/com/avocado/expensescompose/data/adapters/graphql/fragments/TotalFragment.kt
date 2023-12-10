package com.avocado.expensescompose.data.adapters.graphql.fragments

import com.avocado.expensescompose.domain.income.models.IncomeTotalByMonth
import com.avocado.fragment.TotalFragment

fun TotalFragment.toTotal(): IncomeTotalByMonth = IncomeTotalByMonth(
  date = date,
  total = total
)