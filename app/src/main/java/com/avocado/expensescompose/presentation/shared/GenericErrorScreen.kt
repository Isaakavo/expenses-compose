package com.avocado.expensescompose.presentation.shared

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.avocado.expensescompose.R
import com.avocado.expensescompose.presentation.cards.cardsscreen.CardsScreenEvents

//TODO improve code to make more reusable and with parameters to pass the ui error from other sources
@Composable
fun GenericErrorScreen() {
  Card(
    modifier = Modifier
      .padding(16.dp)
      .wrapContentHeight(),
    shape = RoundedCornerShape(16.dp),
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(22.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
      Text(
        text = stringResource(id = R.string.general_error_title),
        style = MaterialTheme.typography.headlineLarge,
        modifier = Modifier.padding(bottom = 16.dp)
      )
      Text(
        text = stringResource(id = R.string.general_error_body),
        style = MaterialTheme.typography.headlineMedium,
        modifier = Modifier.padding(bottom = 8.dp),
        textAlign = TextAlign.Center
      )
    }
  }
}