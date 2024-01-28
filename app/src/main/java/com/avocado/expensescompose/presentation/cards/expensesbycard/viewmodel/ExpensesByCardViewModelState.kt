package com.avocado.expensescompose.presentation.cards.expensesbycard.viewmodel

import com.avocado.expensescompose.data.model.card.Card
import com.avocado.expensescompose.data.model.expense.Expense

data class ExpensesByCardViewModelState(
  val expensesList: List<Expense> = emptyList(),
  val expenseTotal: Double = 0.0,
  val isLoading: Boolean = false,
  val card: Card? = null,
  val uiError: Int = 0
)
