package com.avocado.expensescompose.presentation.shared

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.avocado.expensescompose.R

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
          Text(text = stringResource(id = R.string.dialog_accept))
        }
      },
      dismissButton =
      {
        Button(onClick = { onDismissRequest() }) {
          Text(text = stringResource(R.string.dialog_cancel))
        }
      },
      icon = { Icon(imageVector = Icons.Rounded.Delete, contentDescription = "") },
      title = { Text(text = deleteMessage) },
      text = {
        Text(
          text = stringResource(id = R.string.delete_dialog_warning)
        )
      }
    )
  }
}