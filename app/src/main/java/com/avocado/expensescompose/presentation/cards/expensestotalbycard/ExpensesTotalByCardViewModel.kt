package com.avocado.expensescompose.presentation.cards.expensestotalbycard

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.avocado.CardByIdQuery
import com.avocado.ExpensesTotalByCardIdQuery
import com.avocado.expensescompose.data.adapters.graphql.fragments.toTotal
import com.avocado.expensescompose.data.adapters.graphql.fragments.toTotalFortnight
import com.avocado.expensescompose.data.adapters.graphql.utils.validateDataWithoutErrors
import com.avocado.expensescompose.data.apolloclients.GraphQlClientImpl
import com.avocado.expensescompose.data.model.MyResult
import com.avocado.expensescompose.data.model.successOrError
import com.avocado.expensescompose.data.model.total.Total
import com.avocado.expensescompose.data.model.total.TotalFortnight
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.zip
import kotlinx.coroutines.launch
import javax.inject.Inject


sealed class ExpensesTotalByCardEvent {
  object MonthData : ExpensesTotalByCardEvent()
  object FortnightData : ExpensesTotalByCardEvent()
}

enum class DataSelector {
  FORTNIGHT,
  MONTH
}

data class CardsWithExpensesState(
  val totalByMonthList: List<Total> = emptyList(),
  val totalByFortnight: List<TotalFortnight> = emptyList(),
  val cardBank: String = "",
  val cardAlias: String = "",
  val dataSelector: DataSelector = DataSelector.FORTNIGHT,
  val uiError: String = ""
)

@HiltViewModel
class ExpensesTotalByCardViewModel @Inject constructor(private val graphQlClientImpl: GraphQlClientImpl) :
  ViewModel() {

  private val _state = MutableStateFlow(CardsWithExpensesState())
  val state = _state.asStateFlow()

  fun onEvent(event: ExpensesTotalByCardEvent) {
    when (event) {

      ExpensesTotalByCardEvent.FortnightData -> {
        _state.update { it.copy(dataSelector = DataSelector.FORTNIGHT) }
      }

      ExpensesTotalByCardEvent.MonthData -> {
        _state.update { it.copy(dataSelector = DataSelector.MONTH) }
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
                CardsWithExpensesState(
                  totalByMonthList = totalByMonth,
                  totalByFortnight = totalByyFortnight,
                  cardBank = cardById.data.cardById?.bank.orEmpty(),
                  cardAlias = cardById.data.cardById?.alias.orEmpty(),
                  dataSelector = _state.value.dataSelector
                )
              },
              onError = { cardByIdError ->
                Log.e(
                  "ExpensesTotalByCardViewModel",
                  cardByIdError.exception?.localizedMessage.orEmpty()
                )
                CardsWithExpensesState(uiError = cardByIdError.uiText.orEmpty())
              }
            )
          },
          onError = {
            CardsWithExpensesState(uiError = it.uiText.orEmpty())
          }
        )
      }
        .catch { e ->
          Log.e("ExpensesTotalByCardViewModel", "Error retrieving data ${e.localizedMessage}")
          MyResult.Error(
            uiText = "Error from network ${e.localizedMessage}",
            exception = e,
            data = CardsWithExpensesState()
          )
        }
        .collect {
          _state.emit(it)
        }
    }

  }

  private suspend fun getCardById(cardId: String): Flow<MyResult<CardByIdQuery.Data>> {
    return graphQlClientImpl.query(CardByIdQuery(cardId)).map { apolloResponse ->
      validateDataWithoutErrors(apolloResponse)
    }
  }

  private suspend fun getExpensesByCardId(cardId: String):
      Flow<MyResult<ExpensesTotalByCardIdQuery.Data>> {
    return graphQlClientImpl.query(ExpensesTotalByCardIdQuery(cardId)).map { apolloResponse ->
      validateDataWithoutErrors(apolloResponse)
    }
  }
}