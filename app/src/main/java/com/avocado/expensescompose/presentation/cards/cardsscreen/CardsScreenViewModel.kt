package com.avocado.expensescompose.presentation.cards.cardsscreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apollographql.apollo3.api.Optional
import com.avocado.AllCardsQuery
import com.avocado.CreateCardMutation
import com.avocado.expensescompose.R
import com.avocado.expensescompose.data.adapters.graphql.types.toCard
import com.avocado.expensescompose.data.adapters.graphql.utils.validateDataWithoutErrors
import com.avocado.expensescompose.data.apolloclients.GraphQlClientImpl
import com.avocado.expensescompose.data.model.MyResult
import com.avocado.expensescompose.data.model.card.Card
import com.avocado.expensescompose.data.model.successOrError
import com.avocado.type.CreateCardInput
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class CardsScreenEvents {
  object OpenAddCardDialog : CardsScreenEvents()
  object CloseAddCardDialog : CardsScreenEvents()
  object UpdateBank : CardsScreenEvents()
  object UpdateAlias : CardsScreenEvents()
  object HandleCardType : CardsScreenEvents()
  object CreateCard : CardsScreenEvents()
}

data class CardsScreenState(
  val cardsList: List<Card> = emptyList(),
  val bank: String = "",
  val alias: String = "",
  val uiError: Int = 0,
  val openAddCardDialog: Boolean = false,
  val isDebit: Boolean = true,
  val isCredit: Boolean = false,
  val isPhysical: Boolean = true,
  val isDigital: Boolean = false,
  val isAdded: Boolean = false
)

@HiltViewModel
class CardsScreenViewModel @Inject constructor(private val graphQlClient: GraphQlClientImpl) :
  ViewModel() {

  private val _state = MutableStateFlow(CardsScreenState())
  val state = _state.asStateFlow()

  init {
    getAllCards()
  }

  fun onEvent(events: CardsScreenEvents, inputValue: String = "") {
    when (events) {

      CardsScreenEvents.CloseAddCardDialog -> {
        _state.update { it.copy(openAddCardDialog = false) }
      }

      CardsScreenEvents.OpenAddCardDialog -> {
        _state.update { it.copy(openAddCardDialog = true) }
      }

      CardsScreenEvents.HandleCardType -> {
        when (inputValue) {
          "credit" -> {
            _state.update { it.copy(isCredit = true, isDebit = false) }
          }

          "debit" -> {
            _state.update { it.copy(isCredit = false, isDebit = true) }
          }

          "physical" -> {
            _state.update { it.copy(isPhysical = true, isDigital = false) }
          }

          "digital" -> {
            _state.update { it.copy(isPhysical = false, isDigital = true) }
          }
        }
      }

      CardsScreenEvents.UpdateAlias -> {
        _state.update { it.copy(alias = inputValue) }
      }

      CardsScreenEvents.UpdateBank -> {
        _state.update { it.copy(bank = inputValue) }
      }

      CardsScreenEvents.CreateCard -> {
        viewModelScope.launch {
          graphQlClient.mutate(
            CreateCardMutation(
              Optional.present(
                CreateCardInput(
                  bank = _state.value.bank,
                  alias = Optional.present(_state.value.alias),
                  isDebit = Optional.present(_state.value.isDebit),
                  isDigital = Optional.present(_state.value.isDigital)
                )
              )

            )
          ).map { apolloResponse ->
            val response = apolloResponse.data?.createCard
            if (response != null) {
              val card = Card(
                id = response.id,
                bank = response.bank,
                alias = response.alias ?: "",
                isDigital = response.isDigital,
                isDebit = response.isDebit,
                userId = response.userId
              )
              MyResult.Success(data = card)
            } else {
              MyResult.Error(data = null, uiText = R.string.cards_add_card_error)
            }
          }.collect { myResult ->
            when (myResult) {
              is MyResult.Success -> {
                val cardList = state.value.cardsList.toMutableList()
                cardList.add(myResult.data)
                _state.update {
                  it.copy(
                    cardsList = cardList.toList(),
                    isAdded = true,
                    openAddCardDialog = false
                  )
                }
              }

              is MyResult.Error -> {
                _state.update {
                  it.copy(
                    openAddCardDialog = false
                  )
                }
              }
            }
          }
        }
      }
    }
  }

  private fun getAllCards() {
    viewModelScope.launch {
      graphQlClient.query(
        AllCardsQuery(),
        onError = { _state.emit(CardsScreenState(uiError = R.string.cards_add_card_retrieve_card_error)) }
      ).map { apolloResponse ->
        validateDataWithoutErrors(apolloResponse)
      }
        .collect { collectResult ->
          collectResult.successOrError(
            onSuccess = {
              this.launch {
                _state.emit(
                  CardsScreenState(
                    cardsList = it.data.cardList?.mapNotNull { it?.toCard() }
                      ?: emptyList()
                  )
                )
              }
            },
            onError = {
              this.launch {
                _state.emit(
                  CardsScreenState(
                    uiError = R.string.cards_add_card_retrieve_card_error
                  )
                )
              }
            }
          )
        }
    }
  }
}