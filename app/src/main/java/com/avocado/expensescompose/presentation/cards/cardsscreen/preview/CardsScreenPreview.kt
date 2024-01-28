package com.avocado.expensescompose.presentation.cards.cardsscreen.preview

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.avocado.expensescompose.R

@Preview
@Composable
fun NoCardsPRev() {
  Card(
    modifier = Modifier
      .padding(16.dp)
      .wrapContentHeight(),
    shape = RoundedCornerShape(16.dp)
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(22.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
      Text(
        text = "No hay tarjetas ",
        style = MaterialTheme.typography.headlineLarge,
        modifier = Modifier.padding(bottom = 16.dp)
      )
      Text(
        text = "Agregar tarjetas te permitira trackear " +
          "los gastos que se ha realizado en cada una por mes o quincena",
        style = MaterialTheme.typography.headlineMedium,
        modifier = Modifier.padding(bottom = 8.dp),
        textAlign = TextAlign.Center
      )
      Button(onClick = { /* TODO */ }, modifier = Modifier.align(Alignment.CenterHorizontally)) {
        Icon(
          painterResource(id = R.drawable.baseline_credit_card_24),
          contentDescription = "",
          modifier = Modifier.padding(end = 12.dp)
        )
        Text(text = "Agregar tarjeta")
      }
    }
  }
}
