package com.avocado.expensescompose.presentation.shared

import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import com.avocado.expensescompose.presentation.util.formatDateFromMillis

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateDialog(
  date: String,
  textFieldText: String,
  iconResource: Int? = null,
  openDateDialog: Boolean,
  datePickerState: DatePickerState,
  onConfirm: (String) -> Unit,
  onDismiss: ()  -> Unit,
  onSelectTextField: () -> Unit,
) {
  if (iconResource != null) {
    Icon(
      painter = painterResource(id = iconResource),
      contentDescription = "Fecha"
    )
  }
  ClickableTextField(value = date, label = textFieldText) {
    onSelectTextField()
    // onEvent(AddExpenseEvent.DateDialogOpen, null)
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