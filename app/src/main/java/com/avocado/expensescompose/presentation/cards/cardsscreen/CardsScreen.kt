package com.avocado.expensescompose.presentation.cards.cardsscreen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.avocado.expensescompose.presentation.cards.cardsscreen.components.CardsScreenContent
import com.avocado.expensescompose.presentation.navigation.NavigateEvent

@Composable
fun CardsScreen(
  viewModel: CardsScreenViewModel = hiltViewModel(),
  onNavigate: (navigateEvent: NavigateEvent, cardId: String) -> Unit = { one, two -> }
) {
  val state by viewModel.state.collectAsStateWithLifecycle()

  CardsScreenContent(
    cardsList = state.cardsList,
    uiError = state.uiError,
    isAdded = state.isAdded,
    isLoading = state.isLoading,
    onEvent = viewModel::onEvent,
    onNavigate = onNavigate
  )
}
