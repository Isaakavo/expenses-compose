package com.avocado.expensescompose.presentation.cards.addcards.viewmodel

sealed class AddCardEvents {
  object CreateCard : AddCardEvents()
  object UpdateAlias : AddCardEvents()
  object UpdateBank : AddCardEvents()
  object HandleCardType : AddCardEvents()
}
