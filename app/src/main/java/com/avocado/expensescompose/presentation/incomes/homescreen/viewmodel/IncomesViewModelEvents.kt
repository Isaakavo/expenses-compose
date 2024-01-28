package com.avocado.expensescompose.presentation.incomes.homescreen.viewmodel

sealed class BackPress {
  object Idle : BackPress()
  object InitialTouch : BackPress()
}

sealed class IncomeEvent {
  //  object FetchQuery : IncomeEvent()
  object BackPressInitialTouch : IncomeEvent()
  object BackPressIdle : IncomeEvent()
  object CloseToast : IncomeEvent()
  object OpenToast : IncomeEvent()
  object FetchIncomes : IncomeEvent()
}
