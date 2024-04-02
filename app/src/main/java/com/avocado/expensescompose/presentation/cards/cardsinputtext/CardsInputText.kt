package com.avocado.expensescompose.presentation.cards.cardsinputtext

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.avocado.expensescompose.data.model.card.Card
import com.avocado.expensescompose.presentation.cards.cardsinputtext.components.CardsInputTextContent
import com.avocado.expensescompose.presentation.cards.cardsinputtext.viewmodel.CardsInputTextViewModel

@Composable
fun CardsInputText(
  viewModel: CardsInputTextViewModel = hiltViewModel(),
  selectedCard: Card? = null,
  onSelectCard: (Card?) -> Unit
) {
  val state by viewModel.state.collectAsStateWithLifecycle()

  LaunchedEffect(key1 = Unit) {
    viewModel.getAllCards()
  }

  CardsInputTextContent(
    openCardMenu = state.openCardMenu,
    selectedCard = selectedCard,
    cards = state.cards,
    isLoadingCards = state.isLoadingCards,
    onEvent = viewModel::onEvent,
    onSelectCard = onSelectCard
  )
}
