package com.avocado.expensescompose.presentation.shared

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.avocado.expensescompose.presentation.util.formatDateFromMillis

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateDialog(
  modifier: Modifier = Modifier,
  date: String,
  iconResource: Int? = null,
  openDateDialog: Boolean,
  datePickerState: DatePickerState,
  onConfirm: (String) -> Unit,
  onDismiss: () -> Unit,
  onSelectTextField: () -> Unit,
) {
  Row(
    horizontalArrangement = Arrangement.Center,
    modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
  ) {
    if (iconResource != null) {
      Icon(
        painter = painterResource(id = iconResource),
        contentDescription = "Fecha"
      )
    }
    ClickableText(text = date, modifier = modifier) {
      onSelectTextField()
      // onEvent(AddExpenseEvent.DateDialogOpen, null)
    }
  }

  if (openDateDialog) {
    DatePickerDialog(
      onDismissRequest = { onDismiss() },
      confirmButton = {
        TextButton(
          onClick = {
            datePickerState.selectedDateMillis?.let { millis ->
              val formattedDate = millis.formatDateFromMillis()
              onConfirm(formattedDate)
              // onEvent(AddExpenseEvent.UpdateDate, formattedDate)
            }
            onDismiss()
            // onEvent(AddExpenseEvent.DateDialogClose, null)
          }) {
          Text(text = "Aceptar")
        }
      },
      dismissButton = {
        TextButton(onClick = { onDismiss() }) {
          Text(text = "Cancelar")
        }
      }) {
      DatePicker(state = datePickerState)
    }
  }
}