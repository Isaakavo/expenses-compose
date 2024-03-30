package com.avocado.expensescompose.presentation.expenses.allexpenses.components

import androidx.compose.foundation.layout.height
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.avocado.expensescompose.data.adapters.adapt
import com.avocado.type.Category

@Composable
fun ExpenseCategoryList(
  categoryExpanded: Boolean,
  onCategoryExpandedChange: (Boolean) -> Unit,
  onFilterSelect: (String, String) -> Unit
) {
  DropdownMenu(
    expanded = categoryExpanded,
    onDismissRequest = {
      onCategoryExpandedChange(!categoryExpanded)
    },
    modifier = Modifier.height(250.dp)
  ) {
    Category.values().filter { it != Category.UNKNOWN__ }.forEach { category ->
      DropdownMenuItem(
        text = {
          category.adapt().takeIf { it != 0 }?.let {
            Text(text = stringResource(it))
          }
        },
        onClick = {
          onCategoryExpandedChange(false)
          onFilterSelect("CATEGORY", category.name)
        }
      )
    }
  }
}
