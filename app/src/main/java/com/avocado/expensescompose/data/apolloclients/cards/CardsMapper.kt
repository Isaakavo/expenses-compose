package com.avocado.expensescompose.data.apolloclients.cards

import com.avocado.AllCardsQuery
import com.avocado.expensescompose.data.model.card.Card

fun AllCardsQuery.CardList.toCard(): Card = Card(
  id = id,
  alias = alias ?: "",
  bank = bank
)