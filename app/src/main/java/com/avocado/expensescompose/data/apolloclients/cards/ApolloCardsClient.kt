package com.avocado.expensescompose.data.apolloclients.cards

import com.apollographql.apollo3.ApolloClient
import com.avocado.AllCardsQuery
import com.avocado.expensescompose.data.model.MyResult
import com.avocado.expensescompose.domain.cards.CardsClient
import com.avocado.expensescompose.data.model.card.Card

class ApolloCardsClient(private val apolloClient: ApolloClient) : CardsClient {
  override suspend fun getAllCards(): MyResult<List<Card>> {
    val cards = apolloClient.query(AllCardsQuery()).execute().data?.cardList
      ?: return MyResult.Error(data = emptyList(), uiText = "No cards available")

    return MyResult.Success(
      cards.mapNotNull {
        it?.toCard()
      }
    )
  }
}