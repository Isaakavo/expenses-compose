package com.avocado.expensescompose.presentation.topbar

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBar(
  title: String,
  icon: ImageVector,
  buttonText: String = "",
  iconClickAction: () -> Unit,
  actionItemOnClick: () -> Unit
) {
  TopAppBar(title = { Text(title, color = Color.White) },
    colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
    navigationIcon = {
      Icon(
        imageVector = icon,
        contentDescription = "",
        modifier = Modifier
          .padding(horizontal = 12.dp)
          .clickable {
            iconClickAction.invoke()
          },
        tint = Color.White
      )
    },
    actions = {
      if (buttonText.isNotEmpty()) {
        Button(onClick = { actionItemOnClick.invoke() }) {
          Text(text = buttonText)
        }
      }
    })
}