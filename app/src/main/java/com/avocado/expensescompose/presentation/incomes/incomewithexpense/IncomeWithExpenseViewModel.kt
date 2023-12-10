package com.avocado.expensescompose.presentation.incomes.incomewithexpense

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.avocado.expensescompose.data.model.MyResult
import com.avocado.expensescompose.data.model.expense.Expense
import com.avocado.expensescompose.domain.income.models.Income
import com.avocado.expensescompose.domain.income.usecase.GetIncomeByIdWithExpensesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class IncomeWithExpenseState(
  val income: List<Income>? = null,
  val expensesList: List<Expense> = emptyList(),
  val incomesTotal: Double = 0.0,
  val expensesTotal: Double = 0.0,
  val remaining: Double = 0.0,
  val isLoading: Boolean = false
)

@HiltViewModel
class IncomeWithExpenseViewModel @Inject constructor(
  private val getIncomeByIdWithExpensesUseCase: GetIncomeByIdWithExpensesUseCase
) : ViewModel() {
  private val _state = MutableStateFlow(IncomeWithExpenseState())
  val state = _state.asStateFlow()

  fun getIncomesWithExpenses(paymentDate: String) {
    viewModelScope.launch {
      _state.update {
        it.copy(isLoading = true)
      }

      when (val response = getIncomeByIdWithExpensesUseCase(paymentDate = paymentDate)) {
        is MyResult.Success -> {
          val data = response.data
          _state.update {
            it.copy(
              income = data.incomes,
              incomesTotal = data.incomesTotal,
              expensesList = data.expensesList ?: emptyList(),
              expensesTotal = data.expensesTotal,
              remaining = data.remaining,
              isLoading = false
            )
          }
        }

        is MyResult.Error -> {
          _state.update {
            it.copy(isLoading = false)
          }
        }
      }
    }
  }
}