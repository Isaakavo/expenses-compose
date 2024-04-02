package com.avocado.expensescompose.presentation.cards.cardsinputtext.viewmodel

import com.avocado.expensescompose.data.model.card.Card

data class CardInputTextState(
  val openCardMenu: Boolean = false,
  val cards: List<Card> = emptyList(),
  val uiError: Int = 0,
  val isLoadingCards: Boolean = false
)
