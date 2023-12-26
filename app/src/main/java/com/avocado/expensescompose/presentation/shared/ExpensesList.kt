package com.avocado.expensescompose.presentation.shared

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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

@Composable
fun ExpensesList(expenseList: List<Expense>) {
  Text(
    text = "Transacciones",
    modifier = Modifier
      .fillMaxWidth()
      .padding(start = 8.dp),
    textAlign = TextAlign.Start
  )
  LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
    itemsIndexed(expenseList) { index, expense ->
      ExpenseDateRow(payBefore = expense.payBefore, index = index, expenseList = expenseList)
      ExpenseItem(expense = expense)
    }
  }
}

@Composable
fun ExpenseItem(expense: Expense) {
  Column(modifier = Modifier.padding(start = 8.dp, end = 8.dp)) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
        text = expense.concept,
        fontSize = 16.sp,
        fontWeight = FontWeight.Normal,
        overflow = TextOverflow.Ellipsis,
        maxLines = 1,
        modifier = Modifier.weight(1f)
      )
      Text(
        text = expense.total.formatMoney(),
        fontSize = 16.sp,
        textAlign = TextAlign.End,
        fontWeight = FontWeight.Normal,
        modifier = Modifier.padding(start = 22.dp)
      )
    }
    if (expense.card != null) {
      Row(modifier = Modifier) {
        Text(text = "Tarjeta", fontSize = 14.sp)
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
      total = 5500.0,
      incomeId = "1",
      concept = "Gasto de prueba lasdhjflhsdfklhjasdfhklasdhfdfadsflkasdhflkadhs",
      createdAt = LocalDateTime.now(),
      comment = "",
      id = "1",
      card = com.avocado.expensescompose.data.model.card.Card(
        bank = "Test",
        alias = "Tarjeta",
        id = "kajshdfkajd"
      )
    ),
    Expense(
      total = 5500.0,
      incomeId = "1",
      concept = "Gasto de prueba",
      createdAt = LocalDateTime.now(),
      comment = "",
      id = "1",
      card = com.avocado.expensescompose.data.model.card.Card(
        bank = "Test",
        id = "kajshdfkajd"
      )
    ),
    Expense(
      total = 5500.0,
      incomeId = "1",
      concept = "Gasto de prueba",
      createdAt = LocalDateTime.now(),
      comment = "",
      id = "1",
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
      comment = "",
      id = "1",
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
      createdAt = LocalDateTime.now(),
      comment = "",
      id = "1"
    ),
  )
  ExpensesList(
    expenseList = expenseList
  )
}