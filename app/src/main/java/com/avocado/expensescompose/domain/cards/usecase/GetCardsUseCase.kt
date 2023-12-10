package com.avocado.expensescompose.domain.cards.usecase

import com.avocado.expensescompose.data.model.MyResult
import com.avocado.expensescompose.domain.cards.CardsClient
import com.avocado.expensescompose.data.model.card.Card

class GetCardsUseCase(
  private val cardsClient: CardsClient
){

  suspend operator fun invoke(): MyResult<List<Card>> = cardsClient.getAllCards()
}