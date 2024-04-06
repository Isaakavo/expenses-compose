package com.avocado.expensescompose.presentation.homescreen.viewmodel

import com.avocado.expensescompose.R

enum class HomeScreens {
  INCOME,
  CARDS,
  EXPENSES
}

fun HomeScreens.toText() = when (this) {
  HomeScreens.EXPENSES -> R.string.homescreen_expenses_all
  HomeScreens.CARDS -> R.string.homescreen_cards_option
  HomeScreens.INCOME -> R.string.homescreen_incomes_option
}

data class HomeScreenState(
  val screen: HomeScreens = HomeScreens.INCOME,
  val screenTitle: Int = R.string.homescreen_generic_title,
  val tabs: List<HomeScreens> = HomeScreens.values().toList(),
  val selectedTabIndex: Int = 0,
  val showAddButtons: Boolean = false,
  val backPressState: BackPress = BackPress.Idle,
  val showToast: Boolean = false,
  val isLoading: Boolean = false,
  val uiError: Int = 0,
  val errorMessage: Int? = 0
)
