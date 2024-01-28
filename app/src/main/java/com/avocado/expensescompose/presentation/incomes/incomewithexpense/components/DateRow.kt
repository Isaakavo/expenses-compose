package com.avocado.expensescompose.presentation.incomes.incomewithexpense.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun DateRow(date: String) {
  Row(
    modifier = Modifier
      .padding(bottom = 4.dp)
      .fillMaxWidth(),
    horizontalArrangement = Arrangement.Start
  ) {
    Text(
      text = date,
      fontSize = 20.sp
    )
  }
}
