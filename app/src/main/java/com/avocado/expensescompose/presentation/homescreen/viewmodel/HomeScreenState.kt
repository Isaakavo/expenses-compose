package com.avocado.expensescompose.presentation.homescreen.viewmodel

import com.avocado.expensescompose.R

enum class HomeScreens {
  INCOME,
  CARDS,
  EXPENSES
}

data class HomeScreenState(
  val screen: HomeScreens = HomeScreens.INCOME,
  val screenTitle: Int = R.string.homescreen_generic_title,
  val showAddButtons: Boolean = false,
  val backPressState: BackPress = BackPress.Idle,
  val showToast: Boolean = false,
  val isLoading: Boolean = false,
  val uiError: Int = 0,
  val errorMessage: Int? = 0
)
