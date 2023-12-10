package com.avocado.expensescompose.presentation.cards.expensesbycard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apollographql.apollo3.api.Optional
import com.avocado.ExpensesByFortnightQuery
import com.avocado.ExpensesByMonthQuery
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
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ExpensesByCardState(
  val expensesList: List<Expense> = emptyList(),
  val card: Card? = null
)

@HiltViewModel
class ExpensesByCardViewModel @Inject constructor(private val graphQlClientImpl: GraphQlClientImpl) :
  ViewModel() {
  private val _state = MutableStateFlow(ExpensesByCardState())
  val state = _state.asStateFlow()

  fun getExpensesByFortnight(payBefore: String, cardId: String) {
    val formattedDate = payBefore.formatDateForRequestPayBefore()
    if (formattedDate != null) {
      val input = PayBeforeInput(payBefore = Date(formattedDate), cardId = Optional.present(cardId))
      viewModelScope.launch {
        graphQlClientImpl.query(ExpensesByFortnightQuery(input = input)).map { apolloResponse ->
          validateData(apolloResponse.data?.expensesByFortnight)
        }.collect { collectResult ->
          collectResult.successOrError(onSuccess = { successResult ->
            val data = successResult.data
            val expensesList = data?.mapNotNull { it?.expenseFragment?.toExpense() }.orEmpty()
            this.launch {
              _state.emit(ExpensesByCardState(expensesList = expensesList))
            }
          }, onError = { error -> ExpensesByCardState() })
        }
      }
    }
  }

  fun getExpensesByMonth(payBefore: String, cardId: String) {
    val formattedDate = payBefore.formatDateForRequestPayBefore()
    if (formattedDate != null) {
      val input = PayBeforeInput(payBefore = Date(formattedDate), cardId = Optional.present(cardId))
      viewModelScope.launch {
        graphQlClientImpl.query(ExpensesByMonthQuery(input)).map { apolloResponse ->
          validateData(apolloResponse.data?.expensesByMonth)
        }.collect { collectResult ->
          collectResult.successOrError(
            onSuccess = { successResult ->
              val data = successResult.data
              val expensesList = data?.mapNotNull { it?.expenseFragment?.toExpense() }.orEmpty()
              this.launch {
                _state.emit(ExpensesByCardState(expensesList = expensesList))
              }
            }, onError = {}
          )
        }
      }
    }
  }
}