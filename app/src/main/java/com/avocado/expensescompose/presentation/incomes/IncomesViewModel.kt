package com.avocado.expensescompose.presentation.incomes

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.avocado.expensescompose.data.adapters.adapt
import com.avocado.expensescompose.data.model.incomes.Income
import com.avocado.expensescompose.domain.income.GetIncomeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

data class IncomeState(
    val incomes: List<Income> = emptyList(), val isLoading: Boolean = false
)

@HiltViewModel
class IncomesViewModel @Inject constructor(
    private val getIncomeUseCase: GetIncomeUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(IncomeState())
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val incomes =
                getIncomeUseCase.execute(LocalDateTime.now().toString()).data?.incomesByMonth?.map {
                    it?.adapt()
                }
            Log.d("INCOMES", incomes.toString())
            _state.update {
                it.copy(
                    isLoading = false
                )
            }
        }
    }

    fun callQuery() =
        viewModelScope.launch { getIncomeUseCase.execute(LocalDateTime.now().toString()) }
}