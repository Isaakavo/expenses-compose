package com.avocado.expensescompose.presentation.expenses.allexpenses.viewmodel

import com.avocado.expensescompose.data.model.card.Card
import com.avocado.expensescompose.data.model.expense.Expense

data class AllExpensesListState(
  val expenses: List<Expense> = emptyList(),
  val filteredExpenses: List<Expense> = emptyList(),
  val totalExpenses: Double = 0.0,
  val cards: Set<Card> = emptySet(),
  val payBeforeInput: String = "",
  val expenseToDelete: String = "",
  val successDelete: Boolean = false,
  val isLoading: Boolean = false,
  val uiError: Int? = null
)
