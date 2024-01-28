package com.avocado.expensescompose.presentation.incomes.incomewithexpense.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.avocado.expensescompose.R
import com.avocado.expensescompose.data.adapters.formatMoney

@Composable
fun IncomeDetails(incomesTotal: Double, remaining: Double, expended: Double, month: String) {
  Card(
    modifier = Modifier.fillMaxWidth(),
    elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
  ) {
    Column(
      modifier = Modifier.padding(16.dp),
      verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
      DateRow(date = month)
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        // TODO make text color of remaining
        // if remaining is more than income make it green if not red
        Text(
          text = stringResource(id = R.string.income_expense_income),
          color = MaterialTheme.colorScheme.secondary,
          style = MaterialTheme.typography.bodyLarge
        )
        Text(
          text = incomesTotal.formatMoney(),
          style = MaterialTheme.typography.bodyLarge,
          fontWeight = FontWeight.SemiBold
        )
      }

      Row(
        modifier = Modifier
          .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Text(
          text = stringResource(id = R.string.income_expense_expense),
          color = MaterialTheme.colorScheme.secondary,
          style = MaterialTheme.typography.bodyLarge
        )
        Text(
          text = expended.formatMoney(),
          style = MaterialTheme.typography.bodyLarge,
          fontWeight = FontWeight.SemiBold
        )
      }

      Row(
        modifier = Modifier
          .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Text(
          text = stringResource(id = R.string.income_expense_remaining),
          color = MaterialTheme.colorScheme.secondary,
          style = MaterialTheme.typography.bodyLarge
        )
        Text(
          text = remaining.formatMoney(),
          style = MaterialTheme.typography.bodyLarge,
          fontWeight = FontWeight.SemiBold
        )
      }
    }
  }
}
