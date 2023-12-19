package com.avocado.expensescompose.data.adapters.graphql.fragments

import com.avocado.expensescompose.data.model.total.Total
import com.avocado.expensescompose.data.model.total.TotalFortnight
import com.avocado.fragment.TotalFragment

fun TotalFragment.toTotal(): Total = Total(
  date = date,
  month = month,
  total = total,
  year = year
)

fun TotalFragment.toTotalFortnight(fortnight: String): TotalFortnight = TotalFortnight(
  date = date,
  month = month,
  total = total,
  fortnight = fortnight,
  year = year
)