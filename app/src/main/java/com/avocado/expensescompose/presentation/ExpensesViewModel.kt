package com.avocado.expensescompose.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.avocado.expensescompose.domain.Expense
import com.avocado.expensescompose.domain.GetExpensesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExpensesViewModel @Inject constructor(
    private val getExpensesUseCase: GetExpensesUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(ExpensesState())
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            _state.update {
                it.copy(
                    expenses = getExpensesUseCase.execute(),
                    isLoading = false
                )
            }
        }
    }

    data class ExpensesState(
        val expenses: List<Expense> = emptyList(),
        val isLoading: Boolean = false
    )
}