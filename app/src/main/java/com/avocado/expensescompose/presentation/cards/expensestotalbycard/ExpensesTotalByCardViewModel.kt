package com.avocado.expensescompose.presentation.cards.expensestotalbycard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apollographql.apollo3.exception.ApolloException
import com.avocado.CardByIdQuery
import com.avocado.ExpensesTotalByCardIdQuery
import com.avocado.expensescompose.data.adapters.adapt
import com.avocado.expensescompose.data.adapters.graphql.fragments.toTotal
import com.avocado.expensescompose.data.adapters.graphql.fragments.toTotalFortnight
import com.avocado.expensescompose.data.apolloclients.GraphQlClientImpl
import com.avocado.expensescompose.data.model.MyResult
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
  object OpenDropDownMenu : ExpensesTotalByCardEvent()
  object CloseDropDownMenu : ExpensesTotalByCardEvent()
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
  val openDropDownMenu: Boolean = false,
)

@HiltViewModel
class ExpensesTotalByCardViewModel @Inject constructor(private val graphQlClientImpl: GraphQlClientImpl) :
  ViewModel() {

  private val _state = MutableStateFlow(CardsWithExpensesState())
  val state = _state.asStateFlow()

  fun onEvent(event: ExpensesTotalByCardEvent) {
    when (event) {
      ExpensesTotalByCardEvent.OpenDropDownMenu -> {
        _state.update { it.copy(openDropDownMenu = true) }
      }

      ExpensesTotalByCardEvent.CloseDropDownMenu -> {
        _state.update { it.copy(openDropDownMenu = false) }
      }

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
        when (expensesCard) {
          is MyResult.Success -> {
            val data = expensesCard.data
            val totalByMonth =
              data.totalByMonth?.mapNotNull { it?.totalFragment?.toTotal() }.orEmpty()
            val totalByyFortnight =
              data.totalByFortnight?.mapNotNull { it?.totalFragment?.toTotalFortnight(it.fortnight.adapt()) }
                .orEmpty()
            when (card) {
              is MyResult.Success -> {
                CardsWithExpensesState(
                  totalByMonthList = totalByMonth,
                  totalByFortnight = totalByyFortnight,
                  cardBank = card.data.bank,
                  cardAlias = card.data.alias.orEmpty(),
                  openDropDownMenu = _state.value.openDropDownMenu,
                  dataSelector = _state.value.dataSelector
                )
              }

              is MyResult.Error -> {
                CardsWithExpensesState()
              }
            }
          }

          else -> {
            CardsWithExpensesState()
          }
        }
      }
        .catch { e -> MyResult.Error(uiText = "Error from network ${e.localizedMessage}", data = null) }
        .collect {
        _state.emit(it)
      }
    }

  }

  private suspend fun getCardById(cardId: String): Flow<MyResult<CardByIdQuery.CardById>> {
    return graphQlClientImpl.query(CardByIdQuery(cardId)).map { apolloResponse ->
      try {
        val data = apolloResponse.data?.cardById
        if (data != null) {
          MyResult.Success(data)
        } else {
          MyResult.Error(data = null, uiText = "")
        }
      } catch (e: ApolloException) {
        MyResult.Error(data = null, uiText = "Something went wrong from the server")
      }
    }
  }

  private suspend fun getExpensesByCardId(cardId: String):
      Flow<MyResult<ExpensesTotalByCardIdQuery.ExpensesTotalByCardId>> {
    return graphQlClientImpl.query(ExpensesTotalByCardIdQuery(cardId)).map { apolloResponse ->
      try {
        val data = apolloResponse.data?.expensesTotalByCardId
        if (data != null) {
          MyResult.Success(data)
        } else {
          MyResult.Error(data = null, uiText = "")
        }
      } catch (e: ApolloException) {
        MyResult.Error(data = null, uiText = "Something went wrong from the server")
      }
    }
  }
}