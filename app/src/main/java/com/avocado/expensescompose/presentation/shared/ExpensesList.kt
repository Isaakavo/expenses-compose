package com.avocado.expensescompose.presentation.shared

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SuggestionChip
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
import com.avocado.expensescompose.domain.income.models.ExpenseTag
import com.avocado.expensescompose.presentation.util.formatDateDaysWithMonth
import java.time.LocalDateTime

@Composable
fun ExpensesList(expenseList: List<Expense>) {
  Text(
    text = "Transacciones", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.End
  )
  LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
    itemsIndexed(expenseList) { index, expense ->
      ExpenseDateRow(payBefore = expense.payBefore, index = index, expenseList = expenseList)
      ExpenseItem(expense = expense)
    }
  }
}

@Composable
fun ExpenseItem(expense: Expense) {
  val isCardAliasEmpty = expense.card?.alias?.isNotEmpty() == true
  val isCardBankEmpty = expense.card?.bank?.isNotEmpty() == true
  Card(
    modifier = Modifier.fillMaxWidth()
  ) {
    Column(modifier = Modifier.padding(top = 12.dp, bottom = 12.dp, start = 8.dp, end = 8.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        if (isCardAliasEmpty || isCardBankEmpty) {
          SuggestionChip(
            onClick = { },
            label = {
              expense.card?.alias?.ifEmpty { expense.card.bank }?.let {
                Text(
                  text = it,
                  style = MaterialTheme.typography.bodySmall
                )
              }
            }
          )
        }

        Text(
          text = expense.concept,
          style = MaterialTheme.typography.bodySmall,
          fontSize = 16.sp,
          fontWeight = FontWeight.SemiBold,
          overflow = TextOverflow.Ellipsis,
          maxLines = 1,
          modifier = Modifier
            .weight(1f)
            .padding(start = if (isCardAliasEmpty || isCardBankEmpty) 12.dp else 4.dp)
        )

        Text(
          text = expense.total.formatMoney(),
          style = MaterialTheme.typography.bodyLarge,
          textAlign = TextAlign.End,
          fontWeight = FontWeight.SemiBold,
          modifier = Modifier.padding(start = 22.dp)
        )
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
      Text(text = date ?: "", fontSize = 16.sp, fontWeight = FontWeight.W300)
    }
  } else if (index == 0) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(start = 6.dp, bottom = 6.dp)
    ) {
      Text(text = date ?: "", fontSize = 16.sp, fontWeight = FontWeight.W300)
    }
  }
}

@Preview
@Composable
fun ExpenseListPreview() {
  ExpensesList(
    expenseList = listOf(
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
        ),
        tags = listOf(
          ExpenseTag(
            id = "1",
            name = "Tag de prueba",
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
          ), ExpenseTag(
            id = "1",
            name = "Tag de prueba 2",
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
          ), ExpenseTag(
            id = "1",
            name = "Tag de prueba",
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
          ), ExpenseTag(
            id = "1",
            name = "Tag de prueba 2",
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
          )
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
        ),
        tags = listOf(
          ExpenseTag(
            id = "1",
            name = "Tag de prueba",
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
          ), ExpenseTag(
            id = "1",
            name = "Tag de prueba 2",
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
          ), ExpenseTag(
            id = "1",
            name = "Tag de prueba",
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
          ), ExpenseTag(
            id = "1",
            name = "Tag de prueba 2",
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
          )
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
        ),
        tags = listOf(
          ExpenseTag(
            id = "1",
            name = "Tag de prueba",
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
          ), ExpenseTag(
            id = "1",
            name = "Tag de prueba 2",
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
          ), ExpenseTag(
            id = "1",
            name = "Tag de prueba",
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
          ), ExpenseTag(
            id = "1",
            name = "Tag de prueba 2",
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
          )
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
        ),
        tags = listOf(
          ExpenseTag(
            id = "1",
            name = "Tag de prueba",
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
          ), ExpenseTag(
            id = "1",
            name = "Tag de prueba 2",
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
          ), ExpenseTag(
            id = "1",
            name = "Tag de prueba",
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
          ), ExpenseTag(
            id = "1",
            name = "Tag de prueba 2",
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
          )
        )
      ),
      Expense(
        total = 5500.0,
        incomeId = "1",
        concept = "Gasto de prueba sin tarjeta",
        createdAt = LocalDateTime.now(),
        comment = "",
        id = "1",
        tags = listOf(
          ExpenseTag(
            id = "1",
            name = "Tag de prueba",
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
          ), ExpenseTag(
            id = "1",
            name = "Tag de prueba 2",
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
          ), ExpenseTag(
            id = "1",
            name = "Tag de prueba",
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
          ), ExpenseTag(
            id = "1",
            name = "Tag de prueba 2",
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
          )
        )
      ),
    )
  )
}