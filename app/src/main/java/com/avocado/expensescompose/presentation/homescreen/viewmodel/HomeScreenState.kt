package com.avocado.expensescompose.presentation.homescreen.viewmodel

data class HomeScreenState(
  val showAddButtons: Boolean = false,
  val backPressState: BackPress = BackPress.Idle,
  val showToast: Boolean = false,
  val isLoading: Boolean = false,
  val uiError: Int = 0,
  val errorMessage: Int? = 0
)
