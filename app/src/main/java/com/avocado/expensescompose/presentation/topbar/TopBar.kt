package com.avocado.expensescompose.presentation.topbar

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview

data class MenuItems(
  val text: String = "",
  val icon: ImageVector? = null,
  val action: () -> Unit
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBar(
  title: String,
  navigationIcon: ImageVector = Icons.Rounded.ArrowBack,
  buttonText: String = "",
  dropDownMenuItems: List<MenuItems> = emptyList(),
  onNavigationIconClick: () -> Unit = {},
  onActionButtonClick: () -> Unit = {}
) {
  TopAppBar(
    title = { Text(title, color = Color.White) },
    colors = TopAppBarDefaults.smallTopAppBarColors(
      containerColor = MaterialTheme.colorScheme.primaryContainer
    ),
    navigationIcon = {
      IconButton(
        onClick = { onNavigationIconClick() }
      ) {
        Icon(imageVector = navigationIcon, contentDescription = "")
      }
    },
    actions = {
      if (buttonText.isNotEmpty()) {
        Button(onClick = { onActionButtonClick() }) {
          Text(text = buttonText)
        }
      }
      if (dropDownMenuItems.isNotEmpty()) {
        ToolBarDropDownMenu(actionsList = dropDownMenuItems)
      }
    }
  )
}

@Composable
fun ToolBarDropDownMenu(
  actionsList: List<MenuItems> = emptyList()
) {
  var expanded by remember {
    mutableStateOf(false)
  }
  IconButton(onClick = { expanded = !expanded }) {
    Icon(imageVector = Icons.Rounded.MoreVert, contentDescription = "")
  }

  Column(
    horizontalAlignment = Alignment.End,
    verticalArrangement = Arrangement.Top
  ) {
    Box {
      DropdownMenu(
        expanded = expanded,
        onDismissRequest = { expanded = false }
      ) {
        actionsList.map { action ->
          DropdownMenuItem(
            text = { Text(text = action.text, modifier = Modifier.fillMaxWidth()) },
            onClick = {
              action.action.invoke()
              expanded = false
            },
            leadingIcon = {
              if (action.icon != null) {
                Icon(
                  imageVector = action.icon,
                  contentDescription = action.text
                )
              }
            }
          )
        }
      }
    }
  }
}

@Preview
@Composable
fun ToolBarPreview() {
  Scaffold(
    topBar = {
      AppBar(
        title = "Prueba",
        buttonText = "Guardar",
        dropDownMenuItems = listOf(
          MenuItems(
            text = "Editar",
            icon = Icons.Rounded.Edit,
            action = {}
          ),
          MenuItems(
            text = "Borrar",
            icon = Icons.Rounded.Delete,
            action = {}
          )
        )
      )
    }
  ) {
    Surface(modifier = Modifier.padding(it).fillMaxSize()) {
    }
  }
}
