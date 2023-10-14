package com.avocado.expensescompose.presentation.incomes.incomewithexpense

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.avocado.expensescompose.data.model.MyResult
import com.avocado.expensescompose.domain.income.models.Expense
import com.avocado.expensescompose.domain.income.models.Income
import com.avocado.expensescompose.domain.income.usecase.GetIncomeByIdWithExpensesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class IncomeWithExpenseState(
  val income: Income? = null,
  val expense: List<Expense> = emptyList(),
  val expensesTotal: Double = 0.0
)

@HiltViewModel
class IncomeWithExpenseViewModel @Inject constructor(
  private val getIncomeByIdWithExpensesUseCase: GetIncomeByIdWithExpensesUseCase
) : ViewModel() {
  private val _state = MutableStateFlow(IncomeWithExpenseState())
  val state = _state.asStateFlow()

  fun getIncomesWithExpenses(incomeId: String) {
    viewModelScope.launch {

      when (val response = getIncomeByIdWithExpensesUseCase(incomeId, "2023-10-15")) {
        is MyResult.Success -> {
          Log.d("IncomeWithExpense", response.data.income.comment)
        }

        is MyResult.Error -> {

        }
      }

    }
  }
}