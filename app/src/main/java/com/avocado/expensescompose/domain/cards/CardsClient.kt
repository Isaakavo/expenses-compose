package com.avocado.expensescompose.domain.cards

import com.avocado.expensescompose.data.model.MyResult
import com.avocado.expensescompose.domain.cards.models.Card

interface CardsClient {
  suspend fun getAllCards(): MyResult<List<Card>>
}