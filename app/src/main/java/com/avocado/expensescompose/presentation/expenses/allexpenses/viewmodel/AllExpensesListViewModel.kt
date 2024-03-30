package com.avocado.expensescompose.presentation.expenses.allexpenses.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.avocado.AllExpensesQuery
import com.avocado.ExpensesByFortnightQuery
import com.avocado.expensescompose.R
import com.avocado.expensescompose.data.adapters.graphql.fragments.toExpense
import com.avocado.expensescompose.data.adapters.graphql.scalar.Date
import com.avocado.expensescompose.data.adapters.graphql.utils.validateData
import com.avocado.expensescompose.data.apolloclients.GraphQlClientImpl
import com.avocado.expensescompose.data.model.card.Card
import com.avocado.expensescompose.data.model.expense.Expense
import com.avocado.expensescompose.data.model.successOrError
import com.avocado.expensescompose.presentation.util.formatDateForRequest
import com.avocado.type.PayBeforeInput
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AllExpensesListState(
  val expenses: List<Expense> = emptyList(),
  val filteredExpenses: List<Expense> = emptyList(),
  val totalExpenses: Double = 0.0,
  val cards: Set<Card> = emptySet(),
  val payBeforeInput: String = "",
  val isLoading: Boolean = false,
  val uiError: Int? = null
)

@HiltViewModel
class AllExpensesListViewModel @Inject constructor(
  private val graphQlClientImpl: GraphQlClientImpl
) : ViewModel() {
  private val _state = MutableStateFlow(AllExpensesListState())
  val state = _state.asStateFlow()

  fun onEvent(event: AllExpensesListEvents, expenseId: String?, filterType: String?, filterName: String?) {
    when (event) {
      AllExpensesListEvents.DeleteExpense -> {
      }

      AllExpensesListEvents.FilterExpenses -> {
      }

      AllExpensesListEvents.ApplyFilter -> {
        filterList(filterType, filterName)
      }
    }
  }

  private fun setLoadingState(state: Boolean) = _state.update {
    it.copy(isLoading = state)
  }

  private fun updateFilteredList(filtered: List<Expense>, total: Double) {
    _state.update {
      it.copy(
        filteredExpenses = filtered,
        totalExpenses = total
      )
    }
  }

  private fun reduceExpenses(filteredList: List<Expense>): Double = filteredList.fold(0.0) { acc, item -> acc + item.total }

  private fun filterList(type: String?, name: String?) {
    val expenses = _state.value.expenses
    val filteredList = when (type) {
      "CATEGORY" -> expenses.filter { it.category.name == name }
      "CARDS" -> expenses.filter { it.card?.bank == name }
      "CASH" -> expenses.filter { it.card == null }
      "RESET" -> expenses
      else -> return
    }
    updateFilteredList(filteredList, reduceExpenses(filteredList))
  }

  // TODO refactor those two functions to avoid repeat code
  fun getAllExpenses() {
    viewModelScope.launch {
      setLoadingState(true)

      graphQlClientImpl.query(
        AllExpensesQuery(),
        onError = {}
      )
        .map { apolloResponse -> validateData(apolloResponse) }
        .collect { result ->
          result.successOrError(
            onSuccess = { successResult ->
              val allExpenses = successResult.data?.allExpenses
              val expensesList = allExpenses?.mapNotNull { expense ->
                expense?.expenseFragment?.toExpense()
              } ?: emptyList()
              val cards = expensesList.mapNotNull { card -> card.card }.toSet()

              this.launch {
                _state.emit(
                  AllExpensesListState(
                    expenses = expensesList,
                    filteredExpenses = expensesList,
                    totalExpenses = reduceExpenses(expensesList),
                    cards = cards,
                    isLoading = false,
                    uiError = null
                  )
                )
              }
            },
            onError = {}
          )
        }
    }
  }

  fun getExpensesByFortnight(payBefore: String?) {
    viewModelScope.launch {
      setLoadingState(true)

      val formattedDate = payBefore?.formatDateForRequest()?.let { Date(it) } ?: run {
        _state.update { it.copy(isLoading = false, uiError = R.string.general_error) }
        return@launch
      }

      val input = PayBeforeInput(
        payBefore = formattedDate
      )

      graphQlClientImpl.query(
        ExpensesByFortnightQuery(input = input),
        onError = { _state.emit(AllExpensesListState(uiError = R.string.general_error)) }
      )
        .map { apolloResponse -> validateData(apolloResponse) }
        .collect { result ->
          result.successOrError(
            onSuccess = {
              val expensesListByFortnight = it.data?.expensesByFortnight
              val expensesList = expensesListByFortnight?.expenses?.mapNotNull { expense ->
                expense?.expenseFragment?.toExpense()
              } ?: emptyList()
              val cards = expensesList.mapNotNull { card -> card.card }.toSet()

              this.launch {
                _state.emit(
                  AllExpensesListState(
                    expenses = expensesList,
                    filteredExpenses = expensesList,
                    totalExpenses = reduceExpenses(expensesList),
                    cards = cards,
                    isLoading = false,
                    uiError = null
                  )
                )
              }
            },
            onError = {}
          )
        }
    }
  }
}
