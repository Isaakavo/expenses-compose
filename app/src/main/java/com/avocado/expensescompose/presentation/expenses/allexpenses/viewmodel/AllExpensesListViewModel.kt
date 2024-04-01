package com.avocado.expensescompose.presentation.expenses.allexpenses.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apollographql.apollo3.api.Optional
import com.avocado.AllExpensesByDateRangeQuery
import com.avocado.AllExpensesQuery
import com.avocado.DeleteExpenseMutation
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
import com.avocado.expensescompose.presentation.util.formatDateFromMillis
import com.avocado.expensescompose.presentation.util.formatDateToISO
import com.avocado.type.AllExpensesByDateRangeInput
import com.avocado.type.EndDate
import com.avocado.type.InitialDate
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
  val expenseToDelete: String = "",
  val successDelete: Boolean = false,
  val isLoading: Boolean = false,
  val uiError: Int? = null
)

@HiltViewModel
class AllExpensesListViewModel @Inject constructor(
  private val graphQlClientImpl: GraphQlClientImpl
) : ViewModel() {
  private val _state = MutableStateFlow(AllExpensesListState())
  val state = _state.asStateFlow()

  fun onEvent(event: AllExpensesListEvents, expenseId: String, filterType: String?, filterName: String?) {
    when (event) {
      AllExpensesListEvents.DeleteExpense -> {
        _state.update { it.copy(successDelete = true, expenseToDelete = expenseId) }
        updateListsForDelete()
      }

      AllExpensesListEvents.ApplyFilter -> {
        filterList(filterType, filterName)
      }

      AllExpensesListEvents.UpdateDeleteExpenseId -> {
        _state.update { it.copy(expenseToDelete = expenseId) }
      }

      AllExpensesListEvents.UpdateSuccessDelete -> {
        _state.update { it.copy(successDelete = false) }
      }

      AllExpensesListEvents.RestoreLists -> {
        restoreLists()
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

  private fun updateListsForDelete() {
    val expenseId = _state.value.expenseToDelete
    val updatedFilteredList = _state.value.expenses.filter { it.id != expenseId }
    _state.update { it.copy(filteredExpenses = updatedFilteredList) }
  }

  private fun restoreLists() {
    val expenses = _state.value.expenses
    _state.update { it.copy(filteredExpenses = expenses) }
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

  fun getAllExpensesByDateRange(dateRange: LongRange?) {
    viewModelScope.launch {
      setLoadingState(true)

      val formattedInitialDate = dateRange?.start?.formatDateFromMillis()?.formatDateToISO()
      val formattedEndDate = dateRange?.last?.formatDateFromMillis()?.formatDateToISO()

      val input = Optional.present(
        AllExpensesByDateRangeInput(
          initialDate = Optional.present(
            InitialDate(
              year = Optional.present(formattedInitialDate?.year),
              month = Optional.present(formattedInitialDate?.monthValue?.minus(1)),
              day = Optional.present(
                formattedInitialDate?.dayOfMonth
              )
            )
          ),
          endDate = Optional.present(
            EndDate(
              year = Optional.present(formattedEndDate?.year),
              month = Optional.present(formattedEndDate?.monthValue?.minus(1)),
              day = Optional.present(formattedEndDate?.dayOfMonth)
            )
          )
        )
      )
      graphQlClientImpl.query(AllExpensesByDateRangeQuery(input), onError = {})
        .map { validateData(it) }
        .collect { result ->
          result.successOrError(
            onSuccess = {
              val allExpensesByDateRange = it.data?.allExpensesByDateRange
              val expensesList = allExpensesByDateRange?.mapNotNull { expense ->
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

  fun deleteExpense() {
    viewModelScope.launch {
      val expenseId = _state.value.expenseToDelete
      graphQlClientImpl.mutate(DeleteExpenseMutation(expenseId))
        .map { validateData(it) }
        .collect { result ->
          result.successOrError(
            onSuccess = { success ->
              val isDeleted = success.data?.deleteExpense
              if (isDeleted == true) {
                val updatedFExpensesList = _state.value.expenses.filter { it.id != expenseId }
                val updatedFilteredList = _state.value.filteredExpenses.filter { it.id != expenseId }
                _state.update {
                  it.copy(
                    successDelete = false,
                    expenses = updatedFExpensesList,
                    filteredExpenses = updatedFilteredList
                  )
                }
              }
            },
            onError = {}
          )
        }
    }
  }
}
