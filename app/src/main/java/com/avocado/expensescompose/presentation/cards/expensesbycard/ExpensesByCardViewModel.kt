package com.avocado.expensescompose.presentation.cards.expensesbycard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apollographql.apollo3.api.Optional
import com.avocado.ExpensesByFortnightQuery
import com.avocado.ExpensesByMonthQuery
import com.avocado.expensescompose.R
import com.avocado.expensescompose.data.adapters.graphql.fragments.toExpense
import com.avocado.expensescompose.data.adapters.graphql.scalar.Date
import com.avocado.expensescompose.data.adapters.graphql.utils.validateData
import com.avocado.expensescompose.data.apolloclients.GraphQlClientImpl
import com.avocado.expensescompose.data.model.card.Card
import com.avocado.expensescompose.data.model.expense.Expense
import com.avocado.expensescompose.data.model.successOrError
import com.avocado.expensescompose.presentation.util.formatDateForRequestPayBefore
import com.avocado.type.PayBeforeInput
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ExpensesByCardState(
  val expensesList: List<Expense> = emptyList(),
  val expenseTotal: Double = 0.0,
  val isLoading: Boolean = false,
  val card: Card? = null,
  val uiError: Int = 0
)

@HiltViewModel
class ExpensesByCardViewModel @Inject constructor(private val graphQlClientImpl: GraphQlClientImpl) :
  ViewModel() {
  private val _state = MutableStateFlow(ExpensesByCardState())
  val state = _state.asStateFlow()

  fun getExpensesByFortnight(payBefore: String, cardId: String) {
    _state.update { it.copy(isLoading = true) }
    val formattedDate = payBefore.formatDateForRequestPayBefore()
    if (formattedDate != null) {
      val input = PayBeforeInput(payBefore = Date(formattedDate), cardId = Optional.present(cardId))
      viewModelScope.launch {
        graphQlClientImpl.query(
          ExpensesByFortnightQuery(input = input),
          onError = { }
        ).map { apolloResponse ->
          validateData(apolloResponse.data?.expensesByFortnight)
        }.collect { collectResult ->
          collectResult.successOrError(onSuccess = { successResult ->
            val data = successResult.data
            val expensesList =
              data?.expenses?.mapNotNull { it?.expenseFragment?.toExpense() }.orEmpty()
            val expenseTotal = data?.expensesTotal ?: 0.0
            val card = expensesList.getOrNull(0)
            this.launch {
              _state.emit(
                ExpensesByCardState(
                  expensesList = expensesList,
                  expenseTotal = expenseTotal,
                  card = card?.card,
                  isLoading = false
                )
              )
            }
          }, onError = { error -> ExpensesByCardState() })
        }
      }
    }
  }

  fun getExpensesByMonth(payBefore: String, cardId: String) {
    _state.update { it.copy(isLoading = true) }
    val formattedDate = payBefore.formatDateForRequestPayBefore()
    if (formattedDate != null) {
      val input = PayBeforeInput(payBefore = Date(formattedDate), cardId = Optional.present(cardId))
      viewModelScope.launch {
        graphQlClientImpl.query(
          ExpensesByMonthQuery(input),
          onError = { _state.emit(ExpensesByCardState(uiError = R.string.general_error)) }
        ).map { apolloResponse ->
          validateData(apolloResponse.data?.expensesByMonth)
        }.collect { collectResult ->
          collectResult.successOrError(
            onSuccess = { successResult ->
              val data = successResult.data
              val expensesList =
                data?.expenses?.mapNotNull { it?.expenseFragment?.toExpense() }.orEmpty()
              val expenseTotal = data?.expensesTotal ?: 0.0
              val card = expensesList.getOrNull(0)
              this.launch {
                _state.emit(
                  ExpensesByCardState(
                    expensesList = expensesList,
                    expenseTotal = expenseTotal,
                    card = card?.card,
                    isLoading = false
                  )
                )
              }
            }, onError = {}
          )
        }
      }
    }
  }
}