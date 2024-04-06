package com.avocado.expensescompose.presentation.homescreen.viewmodel

sealed class BackPress {
  object Idle : BackPress()
  object InitialTouch : BackPress()
}

sealed class HomeScreenEvents {
  object ScreenToDisplay : HomeScreenEvents()
  object BackPressInitialTouch : HomeScreenEvents()
  object BackPressIdle : HomeScreenEvents()
  object CloseToast : HomeScreenEvents()
  object OpenToast : HomeScreenEvents()
  object UpdateTopBarTitle : HomeScreenEvents()
}
