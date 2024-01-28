package com.avocado.expensescompose.data.adapters.graphql.fragments

import com.avocado.expensescompose.data.model.card.Card
import com.avocado.expensescompose.data.model.expense.Expense
import com.avocado.fragment.ExpenseFragment

fun ExpenseFragment.toExpense(): Expense = Expense(
  id = id,
  category = category,
  concept = concept,
  comment = comment,
  total = total,
  payBefore = payBefore.date,
  createdAt = createdAt?.date,
  updatedAt = updatedAt?.date,
  card = if (card != null) {
    Card(
      id = card.id,
      bank = card.bank,
      alias = card.alias
    )
  } else {
    null
  }
)
