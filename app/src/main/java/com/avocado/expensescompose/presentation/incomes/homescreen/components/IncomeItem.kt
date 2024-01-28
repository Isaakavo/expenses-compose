package com.avocado.expensescompose.presentation.incomes.homescreen.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.avocado.expensescompose.R
import com.avocado.expensescompose.domain.income.models.Income
import com.avocado.expensescompose.presentation.incomes.homescreen.NavigationIncomeDetails
import com.avocado.expensescompose.presentation.navigation.NavigateEvent

@Composable
fun IncomeItem(
  items: List<Income>,
  fortnight: String,
  onNavigate: (navigateEvent: NavigateEvent, income: NavigationIncomeDetails) -> Unit
) {
  Column(
    modifier = Modifier.clickable {
      onNavigate(
        NavigateEvent.NavigateIncomeExpensesList,
        NavigationIncomeDetails(
          paymentDate = items[0].paymentDate.date
        )
      )
    }
  ) {
    Text(text = stringResource(id = R.string.income_fortnight, fortnight))
    if (items.size == 1) {
      IncomeItemRow(item = items[0], false)
    } else {
      Column {
        items.map { item ->
          IncomeItemRow(item = item, renderIcon = false)
        }
      }
    }
  }
}
