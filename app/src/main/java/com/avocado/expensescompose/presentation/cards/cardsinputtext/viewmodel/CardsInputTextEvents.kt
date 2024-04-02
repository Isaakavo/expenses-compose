package com.avocado.expensescompose.presentation.cards.cardsinputtext.viewmodel

sealed class CardsInputTextEvents {
  object OpenCardMenu : CardsInputTextEvents()
  object CloseCardMenu : CardsInputTextEvents()
}
