package com.avocado.expensescompose.presentation.shared

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.avocado.expensescompose.R
import com.avocado.expensescompose.presentation.util.formatDateFromMillis

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateRangeDialog(
  modifier: Modifier = Modifier,
  iconResource: Int? = null,
  onConfirm: (LongRange) -> Unit
) {
  var dateRange by remember { mutableStateOf(LongRange.EMPTY) }
  var dateToDisplay by remember { mutableIntStateOf(R.string.homescreen_expenses_all) }
  var openDateDialog by remember { mutableStateOf(false) }
  val dateState = rememberDateRangePickerState()

  Row(
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically,
    modifier = Modifier
      .fillMaxWidth()
      .padding(top = 8.dp)
  ) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
      if (iconResource != null) {
        Icon(
          painter = painterResource(id = iconResource),
          contentDescription = stringResource(id = R.string.date_dialog_date)
        )
      }
      ClickableText(
        text = stringResource(dateToDisplay, dateRange.first.formatDateFromMillis(), dateRange.last.formatDateFromMillis()),
        modifier = modifier
      ) {
        openDateDialog = true
      }
    }

    if (dateRange != LongRange.EMPTY) {
      TextButton(
        onClick = {
          dateRange = LongRange.EMPTY
          dateToDisplay = R.string.homescreen_expenses_all
          onConfirm(LongRange.EMPTY)
        }
      ) {
        Text(text = stringResource(id = R.string.expenses_list_filter_reset))
      }
    }
  }

  if (openDateDialog) {
    DatePickerDialog(
      onDismissRequest = { openDateDialog = false },
      confirmButton = {
        TextButton(
          onClick = {
            val initialSelectedDate = dateState.selectedStartDateMillis
            val endSelectedDate = dateState.selectedEndDateMillis

            if (initialSelectedDate == null || endSelectedDate == null) {
              openDateDialog = false
              return@TextButton
            }

            val range = initialSelectedDate.rangeTo(endSelectedDate)
            dateRange = range
            dateToDisplay = R.string.homescreen_expenses_all_dates
            onConfirm(range)
            openDateDialog = false
          }
        ) {
          Text(text = stringResource(id = R.string.dialog_accept))
        }
      }
    ) {
      DateRangePicker(state = dateState, modifier = Modifier.weight(1f))
    }
  }
}
