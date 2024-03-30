package com.avocado.expensescompose.presentation.expenses.allexpenses.components

import androidx.compose.animation.animateContentSize
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
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.avocado.expensescompose.R
import com.avocado.expensescompose.data.adapters.adapt
import com.avocado.expensescompose.data.adapters.formatMoney
import com.avocado.expensescompose.data.model.expense.Expense

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
