package com.avocado.expensescompose.presentation.incomes.incomeslist.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.avocado.expensescompose.R
import com.avocado.expensescompose.data.model.total.Total
import com.avocado.expensescompose.domain.income.models.Income
import com.avocado.expensescompose.presentation.homescreen.components.IncomeItem
import com.avocado.expensescompose.presentation.homescreen.components.MonthRow
import com.avocado.expensescompose.presentation.homescreen.components.YearRow
import com.avocado.expensescompose.presentation.util.getMonthTotal
import java.time.LocalDateTime

@Composable
fun IncomesListContent(
  isLoading: Boolean,
  uiError: Int,
  incomesMap: Map<String, MutableMap<String, MutableMap<String, MutableList<Income>?>>>?,
  totalByMonth: List<Total?>,
  onNavigate: (incomeId: LocalDateTime?) -> Unit
) {
  when {
    incomesMap?.isNotEmpty() == true -> {
      LazyColumn(
        contentPadding = PaddingValues(start = 24.dp, end = 24.dp)
      ) {
        items(incomesMap.toList()) { year ->
          YearRow(year = year.first)
          year.second.map { month ->
            Card(
              shape = RoundedCornerShape(16.dp),
              modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(bottom = 22.dp, start = 12.dp, end = 12.dp),
              elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
            ) {
              Column(
                modifier = Modifier.padding(
                  start = 12.dp,
                  end = 12.dp,
                  top = 12.dp,
                  bottom = 6.dp
                )
              ) {
                MonthRow(
                  monthTotal = getMonthTotal(totalByMonth, month.key, year.first),
                  incomeMonth = month.key
                )
                month.value.map { income ->
                  IncomeItem(
                    items = income.value ?: emptyList(),
                    fortnight = income.key,
                    onNavigate = onNavigate
                  )
                }
              }
            }
          }
        }
      }
    }

    else -> {
      Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.padding(start = 12.dp, end = 12.dp)
      ) {
        Text(
          text = stringResource(id = R.string.income_empty_list),
          style = MaterialTheme.typography.headlineLarge
        )
      }
    }
  }
}
