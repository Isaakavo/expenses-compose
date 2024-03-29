package com.avocado.expensescompose.presentation.expenses.allexpenses

import androidx.lifecycle.ViewModel
import com.avocado.expensescompose.data.apolloclients.GraphQlClientImpl
import com.avocado.expensescompose.data.model.expense.Expense
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

data class AllExpensesListState(
  val expenses: List<Expense> = emptyList()
)

@HiltViewModel
class AllExpensesListViewModel @Inject constructor(
  private val graphQlClientImpl: GraphQlClientImpl
) : ViewModel() {
  private val _state = MutableStateFlow(AllExpensesListState())
  val state = _state.asStateFlow()
}
