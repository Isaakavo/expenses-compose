package com.avocado.expensescompose.presentation.cards.cardswithexpenses

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apollographql.apollo3.exception.ApolloException
import com.avocado.CardsWithListExpensesQuery
import com.avocado.expensescompose.data.adapters.graphql.fragments.toExpense
import com.avocado.expensescompose.data.adapters.graphql.fragments.toTotal
import com.avocado.expensescompose.data.adapters.graphql.fragments.toTotalFortnight
import com.avocado.expensescompose.data.apolloclients.GraphQlClientImpl
import com.avocado.expensescompose.data.model.MyResult
import com.avocado.expensescompose.data.model.expense.Expense
import com.avocado.expensescompose.data.model.total.Total
import com.avocado.expensescompose.data.model.total.TotalFortnight
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject


data class CardsWithExpensesState(
  val expensesList: List<Expense> = emptyList(),
  val totalByMonthList: List<Total> = emptyList(),
  val totalByFortnight: List<TotalFortnight> = emptyList()
)

@HiltViewModel
class CardsWithExpensesViewModel @Inject constructor(private val graphQlClientImpl: GraphQlClientImpl) :
  ViewModel() {

  private val _state = MutableStateFlow(CardsWithExpensesState())
  val state = _state.asStateFlow()

  fun getExpensesByCardId(cardId: String) {
    viewModelScope.launch {
      graphQlClientImpl.query(CardsWithListExpensesQuery(cardId)).map { apolloResponse ->
        try {
          val data = apolloResponse.data?.cardWithListExpenses
          if (data != null) {
            MyResult.Success(data)
          } else {
            MyResult.Error(data = null, uiText = "")
          }
        } catch (e: ApolloException) {
          MyResult.Error(data = null, uiText = "Something went wrong from the server")
        }
      }.collect { result ->
        when (result) {
          is MyResult.Success -> {
            val data = result.data
            val expensesList =
              data.expenses?.mapNotNull { it?.expenseFragment?.toExpense() }.orEmpty()
            val totalByMonth =
              data.totalByMonth?.mapNotNull { it?.totalFragment?.toTotal() }.orEmpty()
            val totalByyFortnight =
              data.totalByFortnight?.mapNotNull { it?.totalFragment?.toTotalFortnight(it.fortnight) }
                .orEmpty()

            _state.emit(
              CardsWithExpensesState(
                expensesList = expensesList,
                totalByMonthList = totalByMonth,
                totalByFortnight = totalByyFortnight
              )
            )
          }

          else -> {}
        }
      }
    }
  }
}