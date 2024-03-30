package com.avocado.expensescompose.presentation.expenses.allexpenses.components

import androidx.compose.foundation.layout.height
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.avocado.expensescompose.data.model.card.Card

@Composable
fun ExpenseCardsList(
  cards: Set<Card>,
  cardsExpanded: Boolean,
  onCardExpandedChange: (Boolean) -> Unit,
  onFilterSelect: (String, String) -> Unit
) {
  val startHeight = 60.dp
  val maxHeight = 250.dp
  val listHeight = (startHeight * cards.size).takeUnless { it > maxHeight } ?: maxHeight
  DropdownMenu(
    expanded = cardsExpanded,
    onDismissRequest = {
      onCardExpandedChange(!cardsExpanded)
    },
    modifier = Modifier.height(listHeight)
  ) {
    cards.map {
      DropdownMenuItem(
        text = { Text(text = it.bank) },
        onClick = {
          onCardExpandedChange(false)
          onFilterSelect("CARDS", it.bank)
        }
      )
    }
  }
}
