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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowDropDown
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.avocado.expensescompose.R
import com.avocado.expensescompose.data.adapters.adapt
import com.avocado.expensescompose.data.adapters.formatMoney
import com.avocado.expensescompose.data.model.card.Card
import com.avocado.expensescompose.data.model.expense.Expense
import com.avocado.expensescompose.presentation.util.formatDateDaysWithMonth
import com.avocado.expensescompose.util.expenseList
import com.avocado.type.Category
import java.time.LocalDateTime

fun filterList(type: String, name: String, list: List<Expense>) = when (type) {
  "CATEGORY" -> {
    list.filter {
      it.category.name == name
    }
  }

  "CARDS" -> {
    list.filter {
      it.card?.bank == name
    }
  }

  "CASH" -> {
    list.filter {
      it.card == null
    }
  }

  "RESET" -> {
    list
  }

  else -> list
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ExpensesList(
  expenseList: List<Expense>,
  cards: Set<Card> = setOf(),
  onEdit: (expenseId: String) -> Unit = {},
  onDelete: (expenseId: String) -> Unit = {}
) {
  var filteredList by remember {
    mutableStateOf(expenseList)
  }

  val totalFiltered = filteredList.reduceOrNull { acc, item ->
    Expense(
      id = item.id,
      category = item.category,
      concept = item.concept,
      total = acc.total + item.total
    )
  }

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
        totalFiltered?.total?.formatMoney() ?: "0.0"
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
        filteredList = filterList(type, name, expenseList)
      }
    )
  }
  LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
    itemsIndexed(filteredList, key = { _, item -> item.id }) { index, expense ->
      ExpenseDateRow(payBefore = expense.payBefore, index = index, expenseList = filteredList)
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
  val expandedPadding = if (!expanded) {
    Modifier.padding(
      start = 8.dp,
      end = 8.dp,
      top = 4.dp,
      bottom = 4.dp
    )
  } else {
    Modifier.padding(start = 8.dp, end = 8.dp, top = 8.dp, bottom = 8.dp)
  }
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

      Row(
        horizontalArrangement = Arrangement.Absolute.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
      ) {
        val fontSize = if (expanded) 18.sp else 14.sp

        expense.category.adapt().takeIf { it != 0 }?.let {
          Text(text = stringResource(id = it), fontSize = fontSize)
        }

        if (expanded) {
          val cardInfo = expense.card?.alias ?: expense.card?.bank
          cardInfo?.let { Text(text = it, fontSize = fontSize) }
        } else {
          expense.card?.let {
            Text(
              text = stringResource(id = R.string.expenses_list_card),
              fontSize = fontSize
            )
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
              Text(text = stringResource(id = R.string.expenses_list_delete))
            }
            TextButton(
              onClick = { onEdit(expense.id) }
            ) {
              Text(text = stringResource(id = R.string.expenses_list_edit))
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

@Composable
fun ExpenseCategoryList(
  categoryExpanded: Boolean,
  onCategoryExpandedChange: (Boolean) -> Unit,
  onFilterSelect: (String, String) -> Unit
) {
  DropdownMenu(
    expanded = categoryExpanded,
    onDismissRequest = {
      onCategoryExpandedChange(!categoryExpanded)
    },
    modifier = Modifier.height(250.dp)
  ) {
    Category.values().filter { it != Category.UNKNOWN__ }.forEach { category ->
      DropdownMenuItem(
        text = {
          category.adapt().takeIf { it != 0 }?.let {
            Text(text = stringResource(it))
          }
        },
        onClick = {
          onCategoryExpandedChange(false)
          onFilterSelect("CATEGORY", category.name)
        }
      )
    }
  }
}

@Composable
fun ExpenseCardsList(
  cards: Set<Card>,
  cardsExpanded: Boolean,
  onCardExpandedChange: (Boolean) -> Unit,
  onFilterSelect: (String, String) -> Unit
) {
  val startHeight = 60.dp
  val maxHeight = 250.dp
  val listHeight = (startHeight * cards.size).takeUnless { it > maxHeight } ?: maxHeight
  DropdownMenu(
    expanded = cardsExpanded,
    onDismissRequest = {
      onCardExpandedChange(!cardsExpanded)
    },
    modifier = Modifier.height(listHeight)
  ) {
    cards.map {
      DropdownMenuItem(
        text = { Text(text = it.bank) },
        onClick = {
          onCardExpandedChange(false)
          onFilterSelect("CARDS", it.bank)
        }
      )
    }
  }
}

// Adding client side logic to handle filters to avoid charge the server, I might move this to
// server side in a future
@Composable
fun ExpenseFilterMenu(
  cards: Set<Card>,
  onFilterSelect: (String, String) -> Unit
) {
  var expanded by remember { mutableStateOf(false) }
  var categoryExpanded by remember { mutableStateOf(false) }
  var cardsExpanded by remember { mutableStateOf(false) }

  OutlinedButton(onClick = { expanded = !expanded }) {
    Text(text = stringResource(id = R.string.expenses_list_filter), modifier = Modifier)
    Icon(imageVector = Icons.Rounded.ArrowDropDown, contentDescription = "")
  }

  Box {
    DropdownMenu(
      expanded = expanded,
      onDismissRequest = { expanded = !expanded }
    ) {
      // Category list
      DropdownMenuItem(
        text = { Text(text = stringResource(id = R.string.expenses_list_filter_category)) },
        onClick = {
          expanded = !expanded
          categoryExpanded = true
        }
      )

      // Cards List
      cards.takeIf { it.isNotEmpty() }?.let {
        DropdownMenuItem(
          text = { Text(text = stringResource(id = R.string.expenses_list_filter_cards)) },
          onClick = {
            expanded = !expanded
            cardsExpanded = true
          }
        )
      }

      // Cash expenses
      DropdownMenuItem(
        text = { Text(text = stringResource(id = R.string.expenses_list_filter_cash)) },
        onClick = {
          expanded = false
          onFilterSelect("CASH", "ALL")
        }
      )

      Divider()
      // Reset
      DropdownMenuItem(
        text = { Text(text = stringResource(R.string.expenses_list_filter_reset)) },
        onClick = {
          expanded = false
          onFilterSelect("RESET", "ALL")
        }
      )
    }
    ExpenseCategoryList(categoryExpanded = categoryExpanded, onCategoryExpandedChange = { categoryExpanded = it }) { field, name ->
      onFilterSelect(field, name)
    }
    ExpenseCardsList(
      cards = cards,
      cardsExpanded = cardsExpanded,
      onCardExpandedChange = { cardsExpanded = it }
    ) { field, bankName ->
      onFilterSelect(field, bankName)
    }
  }
}

@Preview
@Composable
fun ExpenseListPreview() {
  Surface(modifier = Modifier) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
      ExpensesList(
        cards = setOf(),
        expenseList = expenseList
      )
    }
  }
}
