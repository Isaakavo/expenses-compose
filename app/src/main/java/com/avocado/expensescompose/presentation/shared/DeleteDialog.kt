package com.avocado.expensescompose.presentation.shared

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun DeleteAlertDialog(
  shouldDisplay: Boolean,
  deleteMessage: String,
  onConfirmRequest: () -> Unit,
  onDismissRequest: () -> Unit
) {
  if (shouldDisplay) {
    AlertDialog(
      onDismissRequest = { onDismissRequest() },
      confirmButton =
      {
        Button(onClick = { onConfirmRequest() }) {
          Text(text = "Aceptar")
        }
      },
      dismissButton =
      {
        Button(onClick = { onDismissRequest() }) {
          Text(text = "Cancelar")
        }
      },
      icon = { Icon(imageVector = Icons.Rounded.Delete, contentDescription = "") },
      title = { Text(text = deleteMessage) },
      text = {
        Text(
          text = "Esta accion no se puede deshacer. Los gastos asociados a este ingreso no seran eliminados"
        )
      }
    )
  }
}