package com.avocado.expensescompose.presentation.incomes.homescreen.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.avocado.expensescompose.data.adapters.formatMoney

@Composable
fun MonthRow(monthTotal: Double, incomeMonth: String) {
  Row(
    modifier = Modifier
      .padding(top = 6.dp, bottom = 6.dp)
      .fillMaxWidth(),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.SpaceBetween
  ) {
    Text(text = incomeMonth, style = MaterialTheme.typography.titleMedium)
    Text(text = monthTotal.formatMoney(), style = MaterialTheme.typography.titleMedium)
  }
}
