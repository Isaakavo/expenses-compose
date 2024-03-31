package com.avocado.expensescompose.presentation.shared

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CalendarLocale
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
  initialSelectedDate: Long = LocalDateTime.now().convertDateToMillis(),
  onConfirm: (String) -> Unit
) {
  var date by remember { mutableStateOf(initialSelectedDate.formatDateFromMillis()) }
  var openDateDialog by remember { mutableStateOf(false) }
  val dateToDisplay = date.ifEmpty { LocalDateTime.now().formatDateWithYear() }
  var datePickerState: DatePickerState? by remember {
    mutableStateOf(null)
  }

  LaunchedEffect(key1 = initialSelectedDate) {
    datePickerState = DatePickerState(
      locale = CalendarLocale("en"),
      initialSelectedDateMillis = initialSelectedDate
    )
  }

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
            datePickerState?.selectedDateMillis?.let { millis ->
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
      datePickerState?.let { DatePicker(state = it) }
    }
  }
}
