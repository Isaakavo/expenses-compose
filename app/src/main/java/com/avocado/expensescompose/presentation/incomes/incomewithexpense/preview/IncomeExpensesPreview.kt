package com.avocado.expensescompose.presentation.incomes.incomewithexpense.preview

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.avocado.expensescompose.presentation.incomes.incomewithexpense.components.IncomeDetails

@Preview
@Composable
fun IncomeDetailsPreview() {
  Column(modifier = Modifier.padding(22.dp)) {
    IncomeDetails(
      incomesTotal = 18750.23,
      remaining = 5000.0,
      expended = 8000.0,
      month = "DECEMBER"
    )
  }
}
