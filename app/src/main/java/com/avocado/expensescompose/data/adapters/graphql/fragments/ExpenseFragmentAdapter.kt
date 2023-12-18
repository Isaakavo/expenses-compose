package com.avocado.expensescompose.data.adapters.graphql.fragments

import com.avocado.expensescompose.data.model.expense.Expense
import com.avocado.expensescompose.data.model.card.Card
import com.avocado.expensescompose.domain.income.models.ExpenseTag
import com.avocado.fragment.ExpenseFragment

fun ExpenseFragment.toExpense(): Expense = Expense(
  id = id,
  concept = concept,
  comment = comment,
  total = total,
  payBefore = payBefore.date,
  createdAt = createdAt?.date,
  updatedAt = updatedAt?.date,
  card = Card(id = card?.id ?: "", bank = card?.bank ?: "", alias = card?.alias ?: "")
)
