package com.avocado.expensescompose.presentation.expenses.allexpenses.viewmodel

sealed class AllExpensesListEvents {
  object DeleteExpense : AllExpensesListEvents()
  object ApplyFilter : AllExpensesListEvents()
}
