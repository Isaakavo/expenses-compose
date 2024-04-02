package com.avocado.expensescompose.presentation.cards.addcards.viewmodel

data class AddCardState(
  val bank: String = "",
  val alias: String = "",
  val uiError: Int = 0,
  val isDebit: Boolean = true,
  val isCredit: Boolean = false,
  val isPhysical: Boolean = true,
  val isDigital: Boolean = false,
  val isAdded: Boolean = false,
  val isLoading: Boolean = false
)
