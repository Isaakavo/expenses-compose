package com.avocado.expensescompose.presentation.shared

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.avocado.expensescompose.R
import com.avocado.expensescompose.presentation.util.convertDateToMillis
import com.avocado.expensescompose.presentation.util.formatDateFromMillis
import com.avocado.expensescompose.presentation.util.formatDateWithYear
import java.time.LocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateDialog(
  modifier: Modifier = Modifier,
  iconResource: Int? = null,
  initialSelectedDate: Long? = null,
  onConfirm: (String) -> Unit
) {
  var date by remember { mutableStateOf(LocalDateTime.now().formatDateWithYear()) }
  var openDateDialog by remember { mutableStateOf(false) }
  val dateToDisplay = date.ifEmpty { LocalDateTime.now().formatDateWithYear() }

  val datePickerState = rememberDatePickerState(
    initialSelectedDateMillis = if (initialSelectedDate == 0L) {
      LocalDateTime.now()
        .convertDateToMillis()
    } else {
      initialSelectedDate
    }
  )
  Row(
    horizontalArrangement = Arrangement.Center,
    modifier = Modifier
      .fillMaxWidth()
      .padding(top = 8.dp)
  ) {
    if (iconResource != null) {
      Icon(
        painter = painterResource(id = iconResource),
        contentDescription = stringResource(id = R.string.date_dialog_date)
      )
    }
    ClickableText(text = dateToDisplay, modifier = modifier) {
      openDateDialog = true
    }
  }

  if (openDateDialog) {
    DatePickerDialog(
      onDismissRequest = { openDateDialog = false },
      confirmButton = {
        TextButton(
          onClick = {
            datePickerState.selectedDateMillis?.let { millis ->
              val formattedDate = millis.formatDateFromMillis()
              date = formattedDate
              onConfirm(formattedDate)
            }
            openDateDialog = false
          }
        ) {
          Text(text = stringResource(id = R.string.dialog_accept))
        }
      },
      dismissButton = {
        TextButton(
          onClick = { openDateDialog = false }
        ) {
          Text(text = stringResource(id = R.string.dialog_cancel))
        }
      }
    ) {
      DatePicker(state = datePickerState)
    }
  }
}
