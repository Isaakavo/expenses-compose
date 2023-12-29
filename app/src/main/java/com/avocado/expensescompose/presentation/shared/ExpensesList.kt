package com.avocado.expensescompose.presentation.shared

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.avocado.expensescompose.data.adapters.formatMoney
import com.avocado.expensescompose.data.model.expense.Expense
import com.avocado.expensescompose.presentation.util.formatDateDaysWithMonth
import java.time.LocalDateTime

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ExpensesList(
  expenseList: List<Expense>,
  onEdit: (expenseId: String) -> Unit = {},
  onDelete: (expenseId: String) -> Unit = {}
) {
  Text(
    text = "Transacciones",
    modifier = Modifier
      .fillMaxWidth()
      .padding(start = 8.dp),
    textAlign = TextAlign.Start
  )
  LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
    itemsIndexed(expenseList, key = { index, item -> item.id }) { index, expense ->
      ExpenseDateRow(payBefore = expense.payBefore, index = index, expenseList = expenseList)
      Row(modifier = Modifier.animateItemPlacement()) {
        ExpenseItem(
          expense = expense,
          onEdit = onEdit,
          onDelete = onDelete
        )
      }
    }
  }
}

@Composable
fun ExpenseItem(
  expense: Expense,
  onDelete: (expenseId: String) -> Unit = {},
  onEdit: (expenseId: String) -> Unit = {}
) {
  var expanded by remember { mutableStateOf(false) }
  val expandedTextFont = if (!expanded) FontWeight.Normal else FontWeight.Bold
  val expandedPadding = if (!expanded) Modifier.padding(
    start = 8.dp,
    end = 8.dp,
    top = 4.dp,
    bottom = 4.dp
  ) else Modifier.padding(start = 8.dp, end = 8.dp, top = 8.dp, bottom = 8.dp)
  Box(
    modifier = Modifier
      .background(color = MaterialTheme.colorScheme.surfaceVariant)
      .animateContentSize()
      .wrapContentHeight()
      .fillMaxWidth()
      .clickable(
        interactionSource = remember {
          MutableInteractionSource()
        },
        indication = null
      ) {
        expanded = !expanded
      }
  ) {
    Column(
      modifier = expandedPadding,
      verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = expense.concept,
          fontSize = 18.sp,
          fontWeight = expandedTextFont,
          overflow = if (!expanded) TextOverflow.Ellipsis else TextOverflow.Clip,
          maxLines = if (!expanded) 1 else 3,
          modifier = if (!expanded) Modifier.weight(1f) else Modifier.weight(0.5f)
        )
        Text(
          text = expense.total.formatMoney(),
          fontSize = 18.sp,
          textAlign = TextAlign.End,
          fontWeight = expandedTextFont,
          modifier = Modifier.padding(start = 22.dp)
        )
      }
      if (expense.card != null) {
        Row {
          if (expanded) {
            Text(
              text = if (!expense.card.alias.isNullOrEmpty()) "${expense.card.alias}" else expense.card.bank,
              fontSize = 18.sp
            )
          } else {
            Text(text = "Tarjeta", fontSize = 14.sp)
          }
        }
      }

      if (expanded) {
        if (!expense.comment.isNullOrEmpty()) {
          Row(modifier = Modifier.fillMaxWidth()) {
            Text(text = "${expense.comment}", fontSize = 16.sp, maxLines = 5)
          }
        }
        Column {
          Row(
            horizontalArrangement = Arrangement.Absolute.Right,
            modifier = Modifier.fillMaxWidth()
          ) {
            TextButton(
              onClick = { onDelete(expense.id) }
            ) {
              Text(text = "Borrar")
            }
            TextButton(
              onClick = { onEdit(expense.id) }
            ) {
              Text(text = "Editar")
            }
          }
        }
      }
    }
  }
}

@Composable
fun ExpenseDateRow(payBefore: LocalDateTime?, index: Int, expenseList: List<Expense>) {
  val date = payBefore?.formatDateDaysWithMonth()
  if (index != 0 && expenseList[index - 1].payBefore?.formatDateDaysWithMonth() != date) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(start = 6.dp, bottom = 6.dp)
    ) {
      Text(text = date ?: "", fontSize = 18.sp, fontWeight = FontWeight.Bold)
    }
  } else if (index == 0) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(start = 6.dp, bottom = 6.dp)
    ) {
      Text(text = date ?: "", fontSize = 18.sp, fontWeight = FontWeight.Bold)
    }
  }
}

@Preview
@Composable
fun ExpenseListPreview() {
  val expenseList = listOf(
    Expense(
      total = 18063.0,
      incomeId = "1",
      concept = "Gasto de prueba lasdhjflhsdfklhjasdfhklasdhfdfadsflkasdhflkadhs",
      createdAt = LocalDateTime.now(),
      payBefore = LocalDateTime.now(),
      comment = "Comentario de prueba",
      id = "1",
      card = com.avocado.expensescompose.data.model.card.Card(
        bank = "Banco de prueba",
        alias = "Tarjeta Naranja",
        id = "kajshdfkajd"
      )
    ),
    Expense(
      total = 5500.0,
      incomeId = "1",
      concept = "Gasto de prueba",
      createdAt = LocalDateTime.now(),
      payBefore = LocalDateTime.of(2023, 11, 22, 0, 0, 0),
      comment = "Comentario mega largo de prueba alv como no apoco si",
      id = "2",
      card = com.avocado.expensescompose.data.model.card.Card(
        bank = "Banco de prueba 2",
        id = "kajshdfkajd"
      )
    ),
    Expense(
      total = 5500.0,
      incomeId = "1",
      concept = "Gasto de prueba",
      createdAt = LocalDateTime.now(),
      payBefore = LocalDateTime.now(),
      comment = "",
      id = "3",
      card = com.avocado.expensescompose.data.model.card.Card(
        bank = "Test",
        alias = "alv",
        id = "kajshdfkajd"
      )
    ),
    Expense(
      total = 5500.0,
      incomeId = "1",
      concept = "Gasto de prueba",
      createdAt = LocalDateTime.now(),
      payBefore = LocalDateTime.now(),
      comment = "",
      id = "4",
      card = com.avocado.expensescompose.data.model.card.Card(
        bank = "Test",
        alias = "alv",
        id = "kajshdfkajd"
      )
    ),
    Expense(
      total = 5500.0,
      incomeId = "1",
      concept = "Gasto de prueba sin tarjeta",
      payBefore = LocalDateTime.now(),
      createdAt = LocalDateTime.now(),
      comment = "",
      id = "5"
    ),
  )
  Surface {
    ExpensesList(
      expenseList = expenseList
    )
  }
}