package com.avocado.expensescompose.presentation.cards.cardsscreen.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.avocado.expensescompose.presentation.navigation.NavigateEvent

@Composable
fun CardItem(
  id: String,
  alias: String,
  bank: String,
  cardType: String,
  onNavigate: (navigateEvent: NavigateEvent, cardId: String) -> Unit
) {
  Card(
    shape = RoundedCornerShape(16.dp),
    modifier = Modifier
      .fillMaxWidth()
      .clickable { onNavigate(NavigateEvent.NavigateCardsWithExpenseScreen, id) }
  ) {
    Column(
      modifier = Modifier.padding(16.dp),
      verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Text(text = alias, style = MaterialTheme.typography.headlineSmall)
        Text(text = bank, style = MaterialTheme.typography.headlineSmall)
      }
      Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
        Text(text = cardType, style = MaterialTheme.typography.bodyMedium)
      }
    }
  }
}
