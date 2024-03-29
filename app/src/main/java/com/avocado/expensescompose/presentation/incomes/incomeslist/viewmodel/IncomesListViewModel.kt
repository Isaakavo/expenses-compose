package com.avocado.expensescompose.presentation.incomes.incomeslist.viewmodel

import androidx.lifecycle.ViewModel
import com.avocado.HomeScreenAllIncomesQuery
import com.avocado.expensescompose.R
import com.avocado.expensescompose.data.adapters.graphql.fragments.toIncome
import com.avocado.expensescompose.data.adapters.graphql.fragments.toTotal
import com.avocado.expensescompose.data.apolloclients.GraphQlClientImpl
import com.avocado.expensescompose.data.model.MyResult
import com.avocado.expensescompose.data.model.successOrError
import com.avocado.expensescompose.domain.income.models.Income
import com.avocado.expensescompose.domain.income.models.Incomes
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

@HiltViewModel
class IncomesListViewModel @Inject constructor(
  private val graphQlClientImpl: GraphQlClientImpl
) : ViewModel() {
  private val _state = MutableStateFlow(IncomesViewModelState())
  val state = _state.asStateFlow()

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
            incomeFortnight,
            mutableListOf(income)
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

  suspend fun getAllIncomes() {
    _state.update { it.copy(isLoading = true) }
    graphQlClientImpl.query(
      HomeScreenAllIncomesQuery(),
      onError = {
        _state.emit(
          IncomesViewModelState(isLoading = false, uiError = R.string.general_error)
        )
      }
    )
      .map {
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
          MyResult.Error(uiText = R.string.general_error, data = null)
        }
      }.collect { incomes ->
        incomes.successOrError(
          onSuccess = { success ->
            val incomesList = success.data.incomesList
            _state.update {
              it.copy(
                incomesMap = getIncomesMap(incomesList),
                totalByMonth = success.data.totalByMonth,
                isLoading = false
              )
            }
          },
          onError = { error ->
            _state.update {
              it.copy(
                errorMessage = error.uiText
              )
            }
          }
        )
      }
  }
}
