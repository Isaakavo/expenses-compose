package com.avocado.expensescompose.presentation.shared

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropDownMenu(
  expanded: Boolean,
  textFieldLabel: String,
  textFieldValue: String,
  textFieldEnabled: Boolean = true,
  dropDownMenuEnabled: Boolean = false,
  onOpenEvent: () -> Unit,
  onCloseEvent: () -> Unit,
  menuItems: @Composable () -> Unit
) {
  ExposedDropdownMenuBox(
    expanded = expanded,
    onExpandedChange = {
      if (expanded) {
        onCloseEvent()
      } else {
        onOpenEvent()
      }
    }
  ) {
    OutlinedTextField(
      readOnly = true,
      value = textFieldValue,
      onValueChange = { },
      label = { Text(textFieldLabel) },
      trailingIcon = {
        ExposedDropdownMenuDefaults.TrailingIcon(
          expanded = expanded
        )
      },
      enabled = textFieldEnabled,
      modifier = Modifier
        .menuAnchor()
        .fillMaxWidth()
    )
    ExposedDropdownMenu(
      expanded = if (dropDownMenuEnabled) false else expanded,
      onDismissRequest = {
        onCloseEvent()
      }
    ) {
      menuItems()
    }
  }
}
