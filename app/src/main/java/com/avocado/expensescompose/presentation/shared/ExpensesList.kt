package com.avocado.expensescompose.presentation.shared

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.avocado.expensescompose.data.adapters.formatMoney
import com.avocado.expensescompose.data.model.expense.Expense
import com.avocado.expensescompose.presentation.util.formatDateDaysWithMonth

@Composable
fun ExpensesList(expenseList: List<Expense>) {
  Text(
    text = "Transacciones", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.End
  )
  LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
    items(expenseList) { expense ->
      ExpenseItem(expense = expense)
    }
  }
}

@Composable
fun ExpenseItem(expense: Expense) {
  Card(
    modifier = Modifier.fillMaxWidth()
  ) {
    Column(modifier = Modifier.padding(12.dp)) {

      Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(text = expense.concept, style = MaterialTheme.typography.bodySmall)
        Text(
          text = expense.payBefore?.formatDateDaysWithMonth() ?: "",
          style = MaterialTheme.typography.bodyMedium
        )
      }

      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(top = 4.dp, end = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        if (expense.card.alias.isNotEmpty()) {
          SuggestionChip(
            onClick = { /*TODO*/ },
            label = { Text(text = expense.card.alias, style = MaterialTheme.typography.bodySmall) })
        }

        //TODO make this text to always be at the right
        Text(
          text = expense.total.formatMoney(),
          style = MaterialTheme.typography.bodyLarge,
          textAlign = TextAlign.End,
          fontWeight = FontWeight.SemiBold
        )

      }

      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(start = 4.dp, top = 6.dp),
        horizontalArrangement = Arrangement.End
      ) {
        LazyRow(modifier = Modifier.wrapContentWidth()) {
          items(expense.tags) { tag ->
            // ExpenseTags(tags = tag)
            Card(
              modifier = Modifier.padding(end = 4.dp),
              border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary)
            ) {
              Row {
                Text(
                  text = tag.name,
                  modifier = Modifier.padding(start = 6.dp, end = 6.dp, top = 2.dp, bottom = 2.dp)
                )
              }
            }
          }
        }
      }

      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(end = 6.dp),
        horizontalArrangement = Arrangement.End
      ) {
        Text(text = "${expense.tags.size} tags", style = MaterialTheme.typography.bodySmall)
      }
    }
  }
}