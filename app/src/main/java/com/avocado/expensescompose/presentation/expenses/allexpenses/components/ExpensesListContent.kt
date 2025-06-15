package com.avocado.expensescompose.presentation.expenses.allexpenses.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowDropDown
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.avocado.expensescompose.R
import com.avocado.expensescompose.data.adapters.formatMoney
import com.avocado.expensescompose.data.model.card.Card
import com.avocado.expensescompose.data.model.expense.Expense
import com.avocado.expensescompose.presentation.charts.ChartType
import com.avocado.expensescompose.presentation.expenses.allexpenses.viewmodel.AllExpensesListEvents
import com.avocado.expensescompose.presentation.homescreen.components.FabNestedScrollConnection
import com.avocado.expensescompose.presentation.shared.shimmerBackground

@Composable
fun AllExpensesListContent(
  filteredList: List<Expense>,
  totalExpenses: Double,
  isLoading: Boolean,
  modifier: Modifier = Modifier,
  isChartScreen: Boolean = false,
  cards: Set<Card> = setOf(),
  chartType: ChartType = ChartType.BAR,
  onEdit: (expenseId: String) -> Unit = {},
  onEvent: (event: AllExpensesListEvents) -> Unit = { }
) {
  val fabNestedScrollConnection = remember {
    FabNestedScrollConnection
  }

  when {
    isLoading -> {
      Column(
        modifier
          .nestedScroll(fabNestedScrollConnection)
          .padding(top = 8.dp, bottom = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(end = 24.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Box(
            modifier = Modifier
              .size(18.dp)
              .padding(start = 8.dp, end = 12.dp)
              .shimmerBackground()
              .weight(0.5f)
          )

          Box(
            modifier = Modifier
              .size(18.dp)
              .padding(start = 8.dp, end = 12.dp)
              .shimmerBackground()
              .weight(0.5f)
          )
        }
        repeat(15) {
          Card(
            modifier = Modifier
              .fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
          ) {
            ExpenseItemSkeleton()
          }
        }
      }
    }

    else -> {
      // What will be required if i want to add more scroll connections
      LazyColumn(
        modifier = modifier
          .nestedScroll(fabNestedScrollConnection)
          .padding(top = 8.dp, bottom = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        item {
          MenuRow(
            filteredList = filteredList,
            totalExpenses = totalExpenses,
            cards = cards,
            onEvent = onEvent
          )
          if (isChartScreen) {
            var entityType by remember {
              mutableStateOf(ChartDataEntityType.CATEGORY)
            }

            Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
              ChartDatFilterMenu { filter -> entityType = ChartDataEntityType.valueOf(filter) }
            }
            ExpensesCharts(
              filteredList = filteredList,
              chartType = chartType,
              entityType = entityType,
              modifier = Modifier
                .fillMaxHeight()
            )
          }
        }
        itemsIndexed(filteredList, key = { _, item -> item.id }) { index, expense ->
          ExpenseDateRow(payBefore = expense.payBefore, index = index, expenseList = filteredList)
          Card(
            modifier = Modifier.animateItem(),
            shape = RoundedCornerShape(12.dp)
          ) {
            ExpenseItem(
              expense = expense,
              onEdit = onEdit,
              onDelete = { onEvent(AllExpensesListEvents.DeleteExpense(it)) }
            )
          }
        }
      }
    }
  }
}

@Composable
fun MenuRow(
  filteredList: List<Expense>,
  totalExpenses: Double,
  cards: Set<Card>,
  onEvent: (event: AllExpensesListEvents) -> Unit
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(end = 24.dp),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.SpaceBetween
  ) {
    Text(
      text = stringResource(
        R.string.expenses_list_transaction,
        filteredList.size,
        totalExpenses.formatMoney()
      ),
      modifier = Modifier
        .padding(start = 8.dp)
        .weight(0.5f),
      textAlign = TextAlign.Start,
      fontWeight = FontWeight.Bold,
      fontSize = 14.sp
    )
    FilterAndSortMenu(
      list = filteredList,
      cards = cards,
      onSelectedChart = { chartType -> onEvent(AllExpensesListEvents.SelectChartType(chartType)) }
    ) { filters ->
      onEvent(AllExpensesListEvents.ApplyFilter(filters))
    }
  }
}

@Composable
fun ChartDatFilterMenu(onFilterSelect: (String) -> Unit) {
  val context = LocalContext.current
  var expanded by remember { mutableStateOf(false) }
  var buttonText by remember {
    mutableStateOf(context.resources.getString(R.string.expenses_list_filter_data))
  }

  OutlinedButton(onClick = { expanded = !expanded }) {
    Text(text = buttonText, modifier = Modifier)
    Icon(imageVector = Icons.Rounded.ArrowDropDown, contentDescription = "")
  }

  Box {
    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = !expanded }) {
      DropdownMenuItem(
        text = {
          Text(text = "Category")
        },
        onClick = {
          expanded = !expanded
          buttonText = "Category"
          onFilterSelect(ChartDataEntityType.CATEGORY.name)
        }
      )

      DropdownMenuItem(
        text = {
          Text(text = "Months")
        },
        onClick = {
          expanded = !expanded
          buttonText = "Months"
          onFilterSelect(ChartDataEntityType.MONTH.name)
        }
      )

      DropdownMenuItem(
        text = {
          Text(text = "Concept")
        },
        onClick = {
          expanded = !expanded
          buttonText = "Concept"
          onFilterSelect(ChartDataEntityType.CONCEPT.name)
        }
      )

      DropdownMenuItem(
        text = {
          Text(text = "Concept quantity")
        },
        onClick = {
          expanded = !expanded
          buttonText = "Concept quantity"
          onFilterSelect(ChartDataEntityType.CONCEPT_QUANTITY.name)
        }
      )
    }
  }
}
