package com.avocado.expensescompose.presentation.cards

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.avocado.expensescompose.presentation.topbar.AppBar

@Composable
fun CardsScreen() {
  CardsScreenContent()
}

@Composable
fun CardsScreenContent() {
  Scaffold(
    topBar = {
      AppBar(title = "Tarjetas") {

      }
    }
  ) { paddingValues ->
    Surface(
      modifier = Modifier
        .fillMaxSize()
        .padding(paddingValues)
    ) {
      Column(
        modifier = Modifier.padding(start = 12.dp, end = 12.dp, top = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        CardItem()
      }
    }
  }
}

@Composable
fun CardItem() {
  Card(
    shape = RoundedCornerShape(16.dp),
    modifier = Modifier
      .fillMaxWidth()
      .clickable { }) {
    Column(
      modifier = Modifier.padding(16.dp),
      verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Text(text = "Tarjeta Naranja", style = MaterialTheme.typography.headlineSmall)
        Text(text = "Banregio", style = MaterialTheme.typography.headlineSmall)
      }
      Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
        Text(text = "Tarjeta de credito", style = MaterialTheme.typography.bodyMedium)
      }
    }
  }
}

@Preview
@Composable
fun CardScreenPreview() {
  CardsScreenContent()
}