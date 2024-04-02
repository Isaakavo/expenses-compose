package com.avocado.expensescompose.presentation.cards.cardsinputtext.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.avocado.AllCardsQuery
import com.avocado.expensescompose.R
import com.avocado.expensescompose.data.adapters.graphql.types.toCard
import com.avocado.expensescompose.data.adapters.graphql.utils.validateData
import com.avocado.expensescompose.data.apolloclients.GraphQlClientImpl
import com.avocado.expensescompose.data.model.successOrError
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class CardsInputTextViewModel @Inject constructor(
  private val graphQlClientImpl: GraphQlClientImpl
) : ViewModel() {
  private val _state = MutableStateFlow(CardInputTextState())
  val state = _state.asStateFlow()

  fun onEvent(events: CardsInputTextEvents) {
    when (events) {
      CardsInputTextEvents.OpenCardMenu -> _state.update { it.copy(openCardMenu = true) }
      CardsInputTextEvents.CloseCardMenu -> _state.update { it.copy(openCardMenu = false) }
    }
  }

  fun getAllCards() {
    viewModelScope.launch {
      _state.update { it.copy(isLoadingCards = true) }
      graphQlClientImpl.query(
        AllCardsQuery(),
        onError = { _state.emit(CardInputTextState(uiError = R.string.cards_add_card_retrieve_card_error)) }
      )
        .map { apolloResponse ->
          validateData(apolloResponse)
        }
        .collect { result ->
          result.successOrError(
            onSuccess = { successResult ->
              val cards = successResult.data?.cardList?.mapNotNull { it?.toCard() }
              _state.update {
                it.copy(
                  cards = cards ?: emptyList(),
                  isLoadingCards = false
                )
              }
            },
            onError = {}
          )
        }
    }
  }
}
