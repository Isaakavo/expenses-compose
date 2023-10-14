package com.avocado.expensescompose.presentation.topbar

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
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

data class IconsActions(
  val icon: ImageVector,
  val action: () -> Unit
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBar(
  title: String,
  navigationIcon: ImageVector = Icons.Rounded.ArrowBack,
  buttonText: String = "",
  actionsList: List<IconsActions> = emptyList(),
  onNavigationIconClick: () -> Unit = {},
  actionItemOnClick: () -> Unit = {}
) {
  TopAppBar(title = { Text(title, color = Color.White) },
    colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
    navigationIcon = {
      Icon(
        imageVector = navigationIcon,
        contentDescription = "",
        modifier = Modifier
          .padding(horizontal = 12.dp)
          .clickable {
            onNavigationIconClick.invoke()
          },
        tint = Color.White
      )
    },
    actions = {
      if (buttonText.isNotEmpty() && actionsList.isEmpty()) {
        Button(onClick = { actionItemOnClick.invoke() }) {
          Text(text = buttonText)
        }
      } else if (actionsList.isNotEmpty()) {
        Row(
          modifier = Modifier.padding(end = 2.dp),
          horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          actionsList.map { action ->
            Icon(action.icon, contentDescription = "", modifier = Modifier.clickable {
              action.action.invoke()
            })
          }
        }
      }
    })
}