package com.avocado.expensescompose.presentation.shared

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun ClickableText(text: String, modifier: Modifier,action: () -> Unit) {
  Text(
    text = text,
    modifier = modifier.wrapContentSize().clickable { action() },
  )
}