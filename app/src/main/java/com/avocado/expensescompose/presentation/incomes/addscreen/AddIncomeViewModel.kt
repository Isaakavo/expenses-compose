package com.avocado.expensescompose.presentation.incomes.addscreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.avocado.expensescompose.presentation.util.formatDateToISO
import com.avocado.expensescompose.data.model.successOrError
import com.avocado.expensescompose.domain.income.models.Income
import com.avocado.expensescompose.domain.income.usecase.CreateIncomeUseCase
import com.avocado.expensescompose.presentation.util.formatDateWithYear
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.ZoneId
import javax.inject.Inject

sealed class AddIncomeEvent {
  object InsertIncome : AddIncomeEvent()
  object UpdateDate : AddIncomeEvent()
  object DateDialogOpen : AddIncomeEvent()
  object DateDialogClose : AddIncomeEvent()
  object UpdateTotal : AddIncomeEvent()
  object UpdateComment : AddIncomeEvent()
}

data class AddIncomeState(
  val insertedIncome: Income? = null,
  val isInserted: Boolean = false,
  val userMessage: String = "",
  val date: String = "",
  val openDateDialog: Boolean = false,
  val total: String = "",
  val comments: String = "",
  val initialDate: Long = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()
    .toEpochMilli()
)

// TODO refactor this code to use events to update the values
@HiltViewModel
class AddIncomeViewModel @Inject constructor(
  private val createIncomeUseCase: CreateIncomeUseCase
) : ViewModel() {

  private val _state = MutableStateFlow(AddIncomeState())
  val state = _state.asStateFlow()

  init {
    _state.update { it.copy(date = LocalDateTime.now().formatDateWithYear()) }
  }

  fun onEvent(addIncomeEvent: AddIncomeEvent, param: String? = null) {
    when (addIncomeEvent) {
      is AddIncomeEvent.InsertIncome -> {
        viewModelScope.launch {
          val insertedIncome = createIncomeUseCase(
            total = _state.value.total.toDouble(),
            paymentDate = _state.value.date.formatDateToISO() ?: LocalDateTime.now(),
            comment = _state.value.comments
          )

          insertedIncome.successOrError(
            onSuccess = {
              _state.update {
                it.copy(isInserted = true, insertedIncome = it.insertedIncome)
              }
            },
            onError = {}
          )
        }
      }

      AddIncomeEvent.UpdateDate -> {
        _state.update { it.copy(date = param.orEmpty()) }
      }

      AddIncomeEvent.DateDialogClose -> {
        _state.update { it.copy(openDateDialog = false) }
      }

      AddIncomeEvent.DateDialogOpen -> {
        _state.update { it.copy(openDateDialog = true) }
      }

      AddIncomeEvent.UpdateComment -> {
        _state.update { it.copy(comments = param as String) }
      }

      AddIncomeEvent.UpdateTotal -> {
        _state.update { it.copy(total = param as String) }
      }
    }
  }
}