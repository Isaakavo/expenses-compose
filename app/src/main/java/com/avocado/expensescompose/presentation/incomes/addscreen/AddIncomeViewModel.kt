package com.avocado.expensescompose.presentation.incomes.addscreen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.avocado.expensescompose.data.adapters.formatDateWithYear
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import javax.inject.Inject

@HiltViewModel
class AddIncomeViewModel @Inject constructor() : ViewModel() {

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
}