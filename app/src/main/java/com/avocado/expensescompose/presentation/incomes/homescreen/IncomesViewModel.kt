package com.avocado.expensescompose.presentation.incomes.homescreen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apollographql.apollo3.exception.ApolloHttpException
import com.avocado.expensescompose.data.model.MyResult
import com.avocado.expensescompose.domain.income.models.Income
import com.avocado.expensescompose.domain.income.models.IncomeTotalByMonth
import com.avocado.expensescompose.domain.income.usecase.GetAllIncomesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class IncomeState(
  val incomesList: List<Income> = emptyList(),
  val totalByMonth: List<IncomeTotalByMonth?> = emptyList(),
  val showAddButtons: Boolean = false,
  val backPressState: Boolean = false,
  val showToast: Boolean = false,
  val isLoading: Boolean = false,
  val isInvalidSession: Boolean = false,
  val errorMessage: String = ""
)

sealed class IncomeEvent {

  object FetchQuery : IncomeEvent()
}

@HiltViewModel
class IncomesViewModel @Inject constructor(
  private val getAllIncomesUseCase: GetAllIncomesUseCase
) : ViewModel() {
  private val _state = MutableStateFlow(IncomeState())
  val state = _state.asStateFlow()

  init {
    fetchQuery()
  }

  fun updateToast(show: Boolean) = _state.update {
    it.copy(showToast = show)
  }

  fun onEvent(incomeEvent: IncomeEvent) {
    when (incomeEvent) {
      is IncomeEvent.FetchQuery -> {
        fetchQuery()
      }
    }
  }

  private fun fetchQuery() {
    viewModelScope.launch {
      callQuery()
    }
  }

  private suspend fun callQuery() {
    _state.update { it.copy(isLoading = true) }

    try {
      when (val incomes = getAllIncomesUseCase()) {
        is MyResult.Success -> {
          _state.update {
            it.copy(
              incomesList = incomes.data.incomesList,
              totalByMonth = incomes.data.totalByMonth,
              isLoading = false
            )
          }
        }

        is MyResult.Error -> {
          _state.update {
            it.copy(
              errorMessage = incomes.uiText ?: ""
            )
          }
          Log.d("Incomes List", "Something went wrong ${incomes.uiText}")
        }
      }

    } catch (exception: ApolloHttpException) {
      Log.d("JWT", exception.statusCode.toString())
//      if (exception.statusCode == 401) {
//
//      }
    }

  }
}