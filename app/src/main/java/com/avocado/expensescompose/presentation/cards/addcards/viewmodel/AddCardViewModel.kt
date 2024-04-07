package com.avocado.expensescompose.presentation.cards.addcards.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apollographql.apollo3.api.Optional
import com.avocado.CreateCardMutation
import com.avocado.expensescompose.R
import com.avocado.expensescompose.data.adapters.graphql.utils.validateData
import com.avocado.expensescompose.data.apolloclients.GraphQlClientImpl
import com.avocado.expensescompose.data.model.successOrError
import com.avocado.type.CreateCardInput
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class AddCardViewModel @Inject constructor(
  private val graphQlClientImpl: GraphQlClientImpl
) : ViewModel() {
  private val _state = MutableStateFlow(AddCardState())
  val state = _state.asStateFlow()

  fun onEvent(event: AddCardEvents, inputValue: String = "") {
    when (event) {
      AddCardEvents.HandleCardType -> handleCardType(inputValue)
      AddCardEvents.UpdateAlias -> _state.update { it.copy(alias = inputValue) }
      AddCardEvents.UpdateBank -> _state.update { it.copy(bank = inputValue) }
      AddCardEvents.CreateCard -> createCard()
    }
  }

  private fun handleCardType(inputValue: String) {
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

  private fun createCard() {
    viewModelScope.launch {
      _state.update { it.copy(isLoading = true) }
      graphQlClientImpl.mutate(
        CreateCardMutation(
          Optional.present(
            CreateCardInput(
              bank = _state.value.bank,
              alias = Optional.present(_state.value.alias),
              isDebit = Optional.present(_state.value.isDebit),
              isDigital = Optional.present(_state.value.isDigital)
            )
          )

        ),
        onError = {}
      ).map { apolloResponse ->
        validateData(apolloResponse)
      }.collect { myResult ->
        myResult.successOrError(
          onSuccess = {
            _state.update {
              it.copy(
                isAdded = true,
                isLoading = false
              )
            }
          },
          onError = {
            _state.update { it.copy(uiError = R.string.general_error, isLoading = false) }
          }
        )
      }
    }
  }
}
