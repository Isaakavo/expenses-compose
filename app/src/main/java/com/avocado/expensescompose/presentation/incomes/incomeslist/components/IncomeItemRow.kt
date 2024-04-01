package com.avocado.expensescompose.presentation.incomes.incomeslist.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.avocado.expensescompose.data.adapters.formatMoney
import com.avocado.expensescompose.domain.income.models.Income
import com.avocado.expensescompose.presentation.util.formatDateDaysWithMonth

@Composable
fun IncomeItemRow(item: Income, renderIcon: Boolean = true) {
  Row(
    modifier = Modifier
      .padding(top = 6.dp, bottom = 6.dp)
      .fillMaxWidth(),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.SpaceBetween
  ) {
    Text(
      text = item.paymentDate.date?.formatDateDaysWithMonth() ?: "",
      style = MaterialTheme.typography.titleMedium,
      textAlign = TextAlign.End
    )
    Text(text = item.total.formatMoney(), style = MaterialTheme.typography.titleMedium)
    if (renderIcon) {
      Icon(Icons.Filled.KeyboardArrowRight, "")
    }
  }
}
