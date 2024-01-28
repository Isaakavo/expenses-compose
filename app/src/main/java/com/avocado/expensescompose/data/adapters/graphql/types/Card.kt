package com.avocado.expensescompose.data.adapters.graphql.types

import com.avocado.AllCardsQuery
import com.avocado.expensescompose.data.model.card.Card

fun AllCardsQuery.CardList.toCard(): Card = Card(
  id = id,
  alias = alias ?: "",
  bank = bank
)
