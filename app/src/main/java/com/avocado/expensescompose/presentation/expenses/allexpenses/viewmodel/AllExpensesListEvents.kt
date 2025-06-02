package com.avocado.expensescompose.presentation.expenses.allexpenses.viewmodel

sealed class AllExpensesListEvents {
  data object DeleteExpense : AllExpensesListEvents()
  data object ApplyFilter : AllExpensesListEvents()
  data object UpdateDeleteExpenseId : AllExpensesListEvents()
  data object UpdateSuccessDelete : AllExpensesListEvents()
  data object RestoreLists : AllExpensesListEvents()
}
