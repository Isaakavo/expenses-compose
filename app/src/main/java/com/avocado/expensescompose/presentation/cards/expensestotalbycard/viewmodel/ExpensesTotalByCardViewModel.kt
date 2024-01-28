package com.avocado.expensescompose.presentation.cards.expensestotalbycard.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.avocado.CardByIdQuery
import com.avocado.DeleteCardMutation
import com.avocado.ExpensesTotalByCardIdQuery
import com.avocado.expensescompose.R
import com.avocado.expensescompose.data.adapters.graphql.fragments.toTotal
import com.avocado.expensescompose.data.adapters.graphql.fragments.toTotalFortnight
import com.avocado.expensescompose.data.adapters.graphql.utils.validateDataWithoutErrors
import com.avocado.expensescompose.data.apolloclients.GraphQlClientImpl
import com.avocado.expensescompose.data.model.MyResult
import com.avocado.expensescompose.data.model.successOrError
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.zip
import kotlinx.coroutines.launch
import timber.log.Timber

@HiltViewModel
class ExpensesTotalByCardViewModel @Inject constructor(
  private val graphQlClientImpl: GraphQlClientImpl
) :
  ViewModel() {

  private val _state = MutableStateFlow(ExpensesTotalByCardViewModelState())
  val state = _state.asStateFlow()

  fun onEvent(event: ExpensesTotalByCardEvent, param: String = "") {
    when (event) {
      ExpensesTotalByCardEvent.FortnightData -> {
        _state.update { it.copy(dataSelector = DataSelector.FORTNIGHT) }
      }

      ExpensesTotalByCardEvent.MonthData -> {
        _state.update { it.copy(dataSelector = DataSelector.MONTH) }
      }

      ExpensesTotalByCardEvent.DeleteCard -> {
        deleteCard(param)
      }
    }
  }

  fun fetchData(cardId: String) {
    viewModelScope.launch {
      getCardById(cardId).zip(getExpensesByCardId(cardId)) { card, expensesCard ->
        expensesCard.successOrError(
          onSuccess = { expensesCardResult ->
            val data = expensesCardResult.data.expensesTotalByCardId
            val totalByMonth =
              data?.totalByMonth?.mapNotNull { it?.totalFragment?.toTotal() }.orEmpty()
            val totalByyFortnight =
              data?.totalByFortnight?.mapNotNull { it?.totalFragment?.toTotalFortnight(it.fortnight) }
                .orEmpty()
            card.successOrError(
              onSuccess = { cardById ->
                ExpensesTotalByCardViewModelState(
                  totalByMonthList = totalByMonth,
                  totalByFortnight = totalByyFortnight,
                  cardBank = cardById.data.cardById?.bank.orEmpty(),
                  cardAlias = cardById.data.cardById?.alias.orEmpty(),
                  dataSelector = _state.value.dataSelector
                )
              },
              onError = { cardByIdError ->
                Timber.e(cardByIdError.exception?.localizedMessage.orEmpty())
                ExpensesTotalByCardViewModelState(uiError = cardByIdError.uiText)
              }
            )
          },
          onError = {
            ExpensesTotalByCardViewModelState(uiError = it.uiText)
          }
        )
      }
        .catch { e ->
          Timber.e("Error retrieving data " + e.printStackTrace())
          MyResult.Error(
            uiText = R.string.general_network_error,
            exception = e,
            data = ExpensesTotalByCardViewModelState()
          )
        }
        .collect {
          _state.emit(it)
        }
    }
  }

  private suspend fun getCardById(cardId: String): Flow<MyResult<CardByIdQuery.Data>> {
    return graphQlClientImpl.query(
      CardByIdQuery(cardId),
      onError = { _state.emit(ExpensesTotalByCardViewModelState(uiError = R.string.general_error)) }
    ).map { apolloResponse ->
      validateDataWithoutErrors(apolloResponse)
    }
  }

  private suspend fun getExpensesByCardId(cardId: String):
    Flow<MyResult<ExpensesTotalByCardIdQuery.Data>> {
    return graphQlClientImpl.query(
      ExpensesTotalByCardIdQuery(cardId),
      onError = { _state.emit(ExpensesTotalByCardViewModelState(uiError = R.string.general_error)) }
    ).map { apolloResponse ->
      validateDataWithoutErrors(apolloResponse)
    }
  }

  private fun deleteCard(cardId: String) {
    viewModelScope.launch {
      graphQlClientImpl.mutate(DeleteCardMutation(deleteCardId = cardId)).map {
        validateDataWithoutErrors(it)
      }.collect { collectResult ->
        collectResult.successOrError(
          onSuccess = { data ->
            val isDeleted = data.data.deleteCard
            this.launch {
              if (isDeleted) {
                _state.emit(ExpensesTotalByCardViewModelState(isDeleted = true))
              } else {
                _state.emit(
                  ExpensesTotalByCardViewModelState(
                    isDeleted = false,
                    uiError = R.string.expenses_delete_card_error
                  )
                )
              }
            }
          },
          onError = { error ->
            this.launch {
              _state.emit(ExpensesTotalByCardViewModelState(uiError = error.uiText))
            }
          }
        )
      }
    }
  }
}
