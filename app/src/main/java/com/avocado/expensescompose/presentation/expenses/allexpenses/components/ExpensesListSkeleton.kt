package com.avocado.expensescompose.presentation.expenses.allexpenses.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.avocado.expensescompose.presentation.shared.CustomScaffold
import com.avocado.expensescompose.presentation.shared.shimmerBackground

@Composable
fun ExpenseItemSkeleton() {
  Box(
    modifier = Modifier
      .background(color = MaterialTheme.colorScheme.surfaceVariant)
      .animateContentSize()
      .wrapContentHeight()
      .fillMaxWidth()
  ) {
    Column(
      verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(top = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Box(
          modifier = Modifier
            .padding(start = 12.dp, end = 64.dp)
            .size(18.dp)
            .shimmerBackground()
            .wrapContentWidth()
            .weight(0.5f)

        )

        Box(
          modifier = Modifier
            .size(18.dp)
            .padding(start = 64.dp, end = 12.dp)
            .shimmerBackground()
            .weight(0.5f)
        )
      }

      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(bottom = 8.dp),
        horizontalArrangement = Arrangement.Absolute.SpaceBetween
      ) {
        Box(
          modifier = Modifier
            .padding(start = 12.dp, end = 64.dp)
            .size(18.dp)
            .shimmerBackground()
            .weight(0.5f)
        )

        Box(
          modifier = Modifier
            .padding(start = 22.dp, end = 22.dp)
            .weight(0.5f)
        )
      }
    }
  }
}

@Preview
@Composable
fun Prev() {
  CustomScaffold {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
      repeat(15) {
        Row(
          modifier = Modifier
            .fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween

        ) {
          ExpenseItemSkeleton()
        }
      }
    }
  }
}
