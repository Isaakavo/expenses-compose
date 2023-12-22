package com.avocado.expensescompose.presentation.incomes.homescreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.avocado.HomeScreenAllIncomesQuery
import com.avocado.expensescompose.data.adapters.graphql.fragments.toIncome
import com.avocado.expensescompose.data.adapters.graphql.fragments.toTotal
import com.avocado.expensescompose.data.apolloclients.GraphQlClientImpl
import com.avocado.expensescompose.data.model.MyResult
import com.avocado.expensescompose.data.model.successOrError
import com.avocado.expensescompose.data.model.total.Total
import com.avocado.expensescompose.domain.income.models.Income
import com.avocado.expensescompose.domain.income.models.Incomes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class BackPress {
  object Idle : BackPress()
  object InitialTouch : BackPress()
}

data class IncomeState(
  val incomesMap: Map<String, MutableMap<String, MutableMap<String, MutableList<Income>?>>>? = null,
  val totalByMonth: List<Total?> = emptyList(),
  val showAddButtons: Boolean = false,
  val backPressState: BackPress = BackPress.Idle,
  val showToast: Boolean = false,
  val isLoading: Boolean = false,
  val errorMessage: String = ""
)

sealed class IncomeEvent {
  //  object FetchQuery : IncomeEvent()
  object BackPressInitialTouch : IncomeEvent()
  object BackPressIdle : IncomeEvent()
  object CloseToast : IncomeEvent()
  object OpenToast : IncomeEvent()
  object FetchIncomes : IncomeEvent()
}

@HiltViewModel
class IncomesViewModel @Inject constructor(
  private val graphQlClientImpl: GraphQlClientImpl
) : ViewModel() {
  private val _state = MutableStateFlow(IncomeState())
  val state = _state.asStateFlow()

  fun onEvent(incomeEvent: IncomeEvent) {
    when (incomeEvent) {

      is IncomeEvent.BackPressInitialTouch -> {
        _state.update {
          it.copy(backPressState = BackPress.InitialTouch, showToast = true)
        }
      }

      is IncomeEvent.BackPressIdle -> {
        _state.update {
          it.copy(backPressState = BackPress.Idle)
        }
      }

      IncomeEvent.CloseToast -> {
        _state.update {
          it.copy(showToast = false)
        }
      }

      IncomeEvent.OpenToast -> {
        _state.update {
          it.copy(showToast = true)
        }
      }

      IncomeEvent.FetchIncomes -> {
        viewModelScope.launch {
          getAllIncomes()
        }
      }
    }
  }

  private fun getIncomesMap(incomesList: List<Income>): Map<String, MutableMap<String, MutableMap<String, MutableList<Income>?>>>? {
    val yearMap =
      mutableMapOf<String, MutableMap<String, MutableMap<String, MutableList<Income>?>>>()
    var monthMap = mutableMapOf<String, MutableMap<String, MutableList<Income>?>>()
    var prevYear = ""

    incomesList.map { income ->
      val year = income.paymentDate.date?.year.toString()
      val month = income.paymentDate.date?.month?.name ?: return null
      val fortnight = income.paymentDate.fortnight ?: return null
      val incomeYear = yearMap[year]
      val incomeMonths = incomeYear?.get(month)

      if (incomeYear != null) {
        if (incomeMonths != null) {
          val incomeFortnight = income.paymentDate.fortnight.translate()
          val incomeArr = incomeMonths[incomeFortnight]
          incomeArr?.add(income) ?: incomeMonths.put(
            incomeFortnight, mutableListOf(income)
          )
          monthMap[month] = incomeMonths
          yearMap[year] = monthMap
        } else {
          monthMap[month] = mutableMapOf(
            fortnight.translate() to mutableListOf(income)
          )
          yearMap[year] = monthMap
        }
      } else {
        if (prevYear != year) {
          monthMap = mutableMapOf()
        }
        monthMap[month] = mutableMapOf(
          fortnight.translate() to mutableListOf(income)
        )
        yearMap[year] = monthMap
        prevYear = year
      }
    }

    return yearMap.toMap()
  }

  private suspend fun getAllIncomes() {
    _state.update { it.copy(isLoading = true) }
    graphQlClientImpl.query(HomeScreenAllIncomesQuery()).map {
      val responseIncome = it.data
      if (responseIncome != null) {
        val incomesList = responseIncome.incomesList?.incomes?.map { item ->
          item.incomeFragment.toIncome()
        }
        val totalByMonth = responseIncome.incomesList?.totalByMonth?.map { totalByMonth ->
          totalByMonth.totalFragment.toTotal()
        }

        MyResult.Success(
          Incomes(
            incomesList = incomesList ?: emptyList(),
            totalByMonth = totalByMonth ?: emptyList(),
            total = responseIncome.incomesList?.total ?: 0.0
          )
        )
      } else {
        MyResult.Error(uiText = "error", data = null)
      }
    }.catch {
      // TODO find a way to detect the type of apollo error and create an error screen
      _state.emit(IncomeState(isLoading = false))
    }.collect { incomes ->
      incomes.successOrError(onSuccess = { success ->
        val incomesList = success.data.incomesList
        _state.update {
          it.copy(
            incomesMap = getIncomesMap(incomesList),
            totalByMonth = success.data.totalByMonth,
            isLoading = false
          )
        }
      }, onError = { error ->
        _state.update {
          it.copy(
            errorMessage = error.uiText ?: ""
          )
        }
      })
    }
  }
}