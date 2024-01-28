package com.avocado.expensescompose.presentation.cards.cardsscreen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.avocado.expensescompose.presentation.cards.cardsscreen.components.CardsScreenContent
import com.avocado.expensescompose.presentation.navigation.NavigateEvent

@Composable
fun CardsScreen(
  operation: String = "",
  viewModel: CardsScreenViewModel = hiltViewModel(),
  onPopBackStack: () -> Unit = {},
  onNavigate: (navigateEvent: NavigateEvent, cardId: String) -> Unit
) {
  val state by viewModel.state.collectAsStateWithLifecycle()

  CardsScreenContent(
    operation = operation,
    cardsList = state.cardsList,
    bank = state.bank,
    alias = state.alias,
    uiError = state.uiError,
    openAddCardDialog = state.openAddCardDialog,
    isDebitCard = state.isDebit,
    isCreditCard = state.isCredit,
    isPhysical = state.isPhysical,
    isDigital = state.isDigital,
    isAdded = state.isAdded,
    onEvent = viewModel::onEvent,
    onPopBackStack = onPopBackStack,
    onNavigate = onNavigate
  )
}
