package com.avocado.expensescompose.presentation.expenses.allexpenses.components

import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.avocado.expensescompose.R
import com.avocado.expensescompose.data.adapters.formatMoney
import com.avocado.expensescompose.data.model.card.Card
import com.avocado.expensescompose.data.model.expense.Expense
import com.avocado.expensescompose.presentation.expenses.allexpenses.viewmodel.AllExpensesListEvents
import com.avocado.expensescompose.presentation.homescreen.components.FabNestedScrollConnection

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AllExpensesListContent(
  filteredList: List<Expense>,
  totalExpenses: Double,
  cards: Set<Card> = setOf(),
  isLoading: Boolean,
  onEdit: (expenseId: String) -> Unit = {},
  onEvent: (event: AllExpensesListEvents, expenseId: String, filterType: String?, filterName: String?) -> Unit = { one, two, three, four -> }
) {
  val fabNestedScrollConnection = remember {
    FabNestedScrollConnection
  }

  when {
    isLoading -> {
      Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
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
        ExpenseFilterMenu(
          cards = cards,
          onFilterSelect = { type, name ->
            onEvent(AllExpensesListEvents.ApplyFilter, "", type, name)
          }
        )
      }
      // What will be required if i want to add more scroll connections
      LazyColumn(
        modifier = Modifier.nestedScroll(fabNestedScrollConnection),
        verticalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        itemsIndexed(filteredList, key = { _, item -> item.id }) { index, expense ->
          ExpenseDateRow(payBefore = expense.payBefore, index = index, expenseList = filteredList)
          Card(
            modifier = Modifier.animateItemPlacement(
              animationSpec = tween(
                durationMillis = 500,
                easing = LinearOutSlowInEasing
              )
            ),
            shape = RoundedCornerShape(12.dp)
          ) {
            ExpenseItem(
              expense = expense,
              onEdit = onEdit,
              onDelete = { onEvent(AllExpensesListEvents.DeleteExpense, it, null, null) }
            )
          }
        }
      }
    }
  }
}
