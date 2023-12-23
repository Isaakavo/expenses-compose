package com.avocado.expensescompose.presentation.incomes.incomewithexpense

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.avocado.DeleteIncomeByIdMutation
import com.avocado.IncomeByIdWithExpensesListQuery
import com.avocado.expensescompose.data.adapters.graphql.fragments.toExpense
import com.avocado.expensescompose.data.adapters.graphql.fragments.toIncome
import com.avocado.expensescompose.data.adapters.graphql.scalar.Date
import com.avocado.expensescompose.data.adapters.graphql.utils.validateData
import com.avocado.expensescompose.data.adapters.graphql.utils.validateDataWithoutErrors
import com.avocado.expensescompose.data.apolloclients.GraphQlClientImpl
import com.avocado.expensescompose.data.model.expense.Expense
import com.avocado.expensescompose.data.model.successOrError
import com.avocado.expensescompose.domain.income.models.Income
import com.avocado.expensescompose.presentation.util.formatDateForRequest
import com.avocado.type.PayBeforeInput
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

sealed class IncomeWithExpenseEvent {
  object DeleteIncome : IncomeWithExpenseEvent()
  object CancelDeleteIncome : IncomeWithExpenseEvent()
  object ConfirmDeleteIncome : IncomeWithExpenseEvent()
}

data class IncomeWithExpenseState(
  val incomes: List<Income>? = null,
  val expensesList: List<Expense> = emptyList(),
  val incomesTotal: Double = 0.0,
  val expensesTotal: Double = 0.0,
  val remaining: Double = 0.0,
  val isLoading: Boolean = false,
  val isDeleted: Boolean = false,
  val shouldDisplayAlertDialog: Boolean = false
)

@HiltViewModel
class IncomeWithExpenseViewModel @Inject constructor(
  private val graphQlClientImpl: GraphQlClientImpl
) : ViewModel() {
  private val _state = MutableStateFlow(IncomeWithExpenseState())
  val state = _state.asStateFlow()

  fun onEvent(event: IncomeWithExpenseEvent, param: String = "") {
    when (event) {
      IncomeWithExpenseEvent.DeleteIncome -> {
        _state.update { it.copy(shouldDisplayAlertDialog = true) }
      }

      IncomeWithExpenseEvent.CancelDeleteIncome -> {
        _state.update { it.copy(shouldDisplayAlertDialog = false) }
      }

      IncomeWithExpenseEvent.ConfirmDeleteIncome -> {
        deleteIncome(param)
      }
    }
  }

  fun getIncomesWithExpenses(payBefore: String) {
    viewModelScope.launch {
      _state.update {
        it.copy(isLoading = true)
      }

      val input = PayBeforeInput(
        payBefore = payBefore.formatDateForRequest()?.let { Date(it) }
          ?: Date(
            LocalDateTime.now()
          )
      )

      graphQlClientImpl.query(IncomeByIdWithExpensesListQuery(input = input))
        .map { apolloResponse ->
          validateData(apolloResponse)
        }.collect { result ->
          result.successOrError(
            onSuccess = { success ->
              val incomesAndExpensesByFortnight = success.data.data?.incomesAndExpensesByFortnight
              val incomes = incomesAndExpensesByFortnight?.incomes?.map { income ->
                income.incomeFragment.toIncome()
              }.orEmpty()
              val expensesList = incomesAndExpensesByFortnight?.expenses?.map { expense ->
                expense.expenseFragment.toExpense()
              }.orEmpty()
              this.launch {
                _state.emit(
                  IncomeWithExpenseState(
                    isLoading = false,
                    incomes = incomes,
                    expensesList = expensesList,
                    incomesTotal = incomesAndExpensesByFortnight?.incomesTotal ?: 0.0,
                    expensesTotal = incomesAndExpensesByFortnight?.expensesTotal ?: 0.0,
                    remaining = incomesAndExpensesByFortnight?.remaining ?: 0.0
                  )
                )
              }
            },
            onError = {}
          )
        }
    }
  }

  private fun deleteIncome(incomeId: String) {
    viewModelScope.launch {
      graphQlClientImpl.mutate(DeleteIncomeByIdMutation(deleteIncomeByIdId = incomeId)).map {
        validateDataWithoutErrors(it)
      }.collect { collectResult ->
        collectResult.successOrError(
          onSuccess = { success ->
            if (success.data.deleteIncomeById == true) {
              this.launch {
                _state.emit(
                  IncomeWithExpenseState(isDeleted = true)
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