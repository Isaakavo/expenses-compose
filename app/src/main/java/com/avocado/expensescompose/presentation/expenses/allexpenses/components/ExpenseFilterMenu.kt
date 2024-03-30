package com.avocado.expensescompose.presentation.expenses.allexpenses.components

import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowDropDown
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.avocado.expensescompose.R
import com.avocado.expensescompose.data.model.card.Card

@Composable
fun ExpenseFilterMenu(
  cards: Set<Card>,
  onFilterSelect: (String, String) -> Unit
) {
  var expanded by remember { mutableStateOf(false) }
  var categoryExpanded by remember { mutableStateOf(false) }
  var cardsExpanded by remember { mutableStateOf(false) }

  OutlinedButton(onClick = { expanded = !expanded }) {
    Text(text = stringResource(id = R.string.expenses_list_filter), modifier = Modifier)
    Icon(imageVector = Icons.Rounded.ArrowDropDown, contentDescription = "")
  }

  Box {
    DropdownMenu(
      expanded = expanded,
      onDismissRequest = { expanded = !expanded }
    ) {
      // Category list
      DropdownMenuItem(
        text = { Text(text = stringResource(id = R.string.expenses_list_filter_category)) },
        onClick = {
          expanded = !expanded
          categoryExpanded = true
        }
      )

      // Cards List
      cards.takeIf { it.isNotEmpty() }?.let {
        DropdownMenuItem(
          text = { Text(text = stringResource(id = R.string.expenses_list_filter_cards)) },
          onClick = {
            expanded = !expanded
            cardsExpanded = true
          }
        )
      }

      // Cash expenses
      DropdownMenuItem(
        text = { Text(text = stringResource(id = R.string.expenses_list_filter_cash)) },
        onClick = {
          expanded = false
          onFilterSelect("CASH", "ALL")
        }
      )

      Divider()
      // Reset
      DropdownMenuItem(
        text = { Text(text = stringResource(R.string.expenses_list_filter_reset)) },
        onClick = {
          expanded = false
          onFilterSelect("RESET", "ALL")
        }
      )
    }
    ExpenseCategoryList(categoryExpanded = categoryExpanded, onCategoryExpandedChange = { categoryExpanded = it }) { field, name ->
      onFilterSelect(field, name)
    }
    ExpenseCardsList(
      cards = cards,
      cardsExpanded = cardsExpanded,
      onCardExpandedChange = { cardsExpanded = it }
    ) { field, bankName ->
      onFilterSelect(field, bankName)
    }
  }
}
