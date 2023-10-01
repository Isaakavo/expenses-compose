package com.avocado.expensescompose.presentation.incomes.homescreen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apollographql.apollo3.exception.ApolloHttpException
import com.avocado.expensescompose.data.adapters.adapt
import com.avocado.expensescompose.data.model.incomes.Income
import com.avocado.expensescompose.data.model.incomes.TotalByMonth
import com.avocado.expensescompose.domain.income.GetIncomeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class IncomeState(
  val incomes: List<Income?> = emptyList(),
  val totalByMonth: List<TotalByMonth?> = emptyList(),
  val showAddButtons: Boolean = false,
  val backPressState: Boolean = false,
  val showToast: Boolean = false,
  val isLoading: Boolean = false
)

sealed class IncomeEvent {
  object AddIncomeFabClick : IncomeEvent()

  object CloseAddIncomeFabClick : IncomeEvent()

  object FetchQuery : IncomeEvent()
}

@HiltViewModel
class IncomesViewModel @Inject constructor(
  private val getIncomeUseCase: GetIncomeUseCase
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
      is IncomeEvent.AddIncomeFabClick -> {
        onClickAddFab(true)
      }

      is IncomeEvent.CloseAddIncomeFabClick -> {
        onClickAddFab(false)
      }

      is IncomeEvent.FetchQuery -> {
        fetchQuery()
      }
    }
  }

  private fun onClickAddFab(state: Boolean) {
    _state.update {
      it.copy(showAddButtons = state)
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
      val data = getIncomeUseCase.executeAllIncomes().data?.incomes
      val incomes = data?.incomes?.map {
        it?.adapt()
      } ?: emptyList()
      val totalByMonth = data?.totalByMonth?.map {
        it?.adapt()
      } ?: emptyList()

      _state.update {
        it.copy(
          incomes = incomes, totalByMonth = totalByMonth, isLoading = false
        )
      }
    } catch (exception: ApolloHttpException) {
      Log.d("JWT", exception.statusCode.toString())
//      if (exception.statusCode == 401) {
//
//      }
    }

  }
}