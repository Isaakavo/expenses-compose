package com.avocado.expensescompose.presentation.incomes.addscreen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.avocado.expensescompose.data.adapters.formatDateToISO
import com.avocado.expensescompose.data.adapters.formatDateWithYear
import com.avocado.expensescompose.data.model.MyResult
import com.avocado.expensescompose.domain.income.models.Income
import com.avocado.expensescompose.domain.income.usecase.CreateIncomeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import javax.inject.Inject

sealed class AddIncomeEvent {
  object InsertIncome : AddIncomeEvent()
}

data class AddIncomeState(
  val insertedIncome: Income? = null,
  val isInserted: Boolean = false,
  val userMessage: String = ""
)

@HiltViewModel
class AddIncomeViewModel @Inject constructor(
  private val createIncomeUseCase: CreateIncomeUseCase
) : ViewModel() {

  private val _state = MutableStateFlow(AddIncomeState())
  val state = _state.asStateFlow()

  var initialDate by mutableLongStateOf(
    LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
  )
  var paymentDate by mutableStateOf(LocalDateTime.now().formatDateWithYear())
    private set

  var total by mutableStateOf("0.0")
    private set

  var comments by mutableStateOf("")
    private set

  //TODO validate dates, when selecting a date is taking the day before
  fun setFormattedDate(dateInMilliSeconds: Long) {
    val convertedDate =
      Instant.ofEpochMilli(dateInMilliSeconds).atZone(ZoneId.systemDefault()).toLocalDateTime()
    paymentDate = convertedDate.formatDateWithYear()
    initialDate = dateInMilliSeconds
  }

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

  fun onEvent(addIncomeEvent: AddIncomeEvent) {
    when (addIncomeEvent) {
      is AddIncomeEvent.InsertIncome -> {
        viewModelScope.launch {
          val insertedIncome = createIncomeUseCase(
            total = total.toDouble(),
            paymentDate = paymentDate.formatDateToISO() ?: LocalDateTime.now(),
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
    }
  }
}