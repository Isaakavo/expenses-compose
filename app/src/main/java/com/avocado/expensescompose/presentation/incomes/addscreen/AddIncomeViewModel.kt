package com.avocado.expensescompose.presentation.incomes.addscreen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.avocado.expensescompose.presentation.util.formatDateToISO
import com.avocado.expensescompose.data.model.MyResult
import com.avocado.expensescompose.domain.income.models.Income
import com.avocado.expensescompose.domain.income.usecase.CreateIncomeUseCase
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
}

data class AddIncomeState(
  val insertedIncome: Income? = null,
  val isInserted: Boolean = false,
  val userMessage: String = "",
  val date: String = "",
  val openDateDialog: Boolean = false,
)

// TODO refactor this code to use events to update the values
@HiltViewModel
class AddIncomeViewModel @Inject constructor(
  private val createIncomeUseCase: CreateIncomeUseCase
) : ViewModel() {

  private val _state = MutableStateFlow(AddIncomeState())
  val state = _state.asStateFlow()

  var initialDate by mutableLongStateOf(
    LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
  )

  var total by mutableStateOf("0.0")
    private set

  var comments by mutableStateOf("")
    private set

  fun setComment(input: String) {
    comments = input
  }

  fun setTotalIncome(input: String) {
    total = input
  }

  fun resetInputs() {
    total = ""
    comments = ""
  }

  fun closeDialog() {
    _state.update {
      it.copy(isInserted = false)
    }
  }

  fun onEvent(addIncomeEvent: AddIncomeEvent, param: String? = null) {
    when (addIncomeEvent) {
      is AddIncomeEvent.InsertIncome -> {
        viewModelScope.launch {
          val insertedIncome = createIncomeUseCase(
            total = total.toDouble(),
            paymentDate = _state.value.date.formatDateToISO() ?: LocalDateTime.now(),
            comment = comments
          )

          when (insertedIncome) {
            is MyResult.Success -> {
              _state.update {
                it.copy(isInserted = true, insertedIncome = insertedIncome.data)
              }
            }

            //TODO create toast for when the mutation fails
            is MyResult.Error -> {

            }
          }
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
    }
  }
}