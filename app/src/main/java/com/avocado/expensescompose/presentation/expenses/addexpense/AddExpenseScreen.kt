package com.avocado.expensescompose.presentation.expenses.addexpense

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.InputChip
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.avocado.expensescompose.R
import com.avocado.expensescompose.domain.tags.models.Tag
import com.avocado.expensescompose.presentation.topbar.AppBar

@Composable
fun AddExpenseScreen(
  viewModel: AddExpenseViewModel = hiltViewModel(),
  context: Context = LocalContext.current,
) {
  val state by viewModel.state.collectAsStateWithLifecycle()

  if (state.showToast) {
    Toast.makeText(context, state.toastMessage, Toast.LENGTH_LONG).show()
    viewModel.showToast(false)
  }

  AddExpenseScreenContent(tags = state.tagList, onEvent = viewModel::onEvent)
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddExpenseScreenContent(
  tags: List<Tag>,
  onEvent: (event: AddExpenseEvent, tagId: String) -> Unit
) {
  val datePickerState = rememberDatePickerState()
  var openDialog by remember { mutableStateOf(false) }
  var expanded by remember {
    mutableStateOf(false)
  }

  Scaffold(
    topBar = {
      AppBar(title = "Agregar gasto")
    }
  ) { paddingValues ->
    Surface(
      modifier = Modifier
        .padding(paddingValues)
        .fillMaxSize()
    ) {
      Column(
        modifier = Modifier
          .fillMaxSize()
          .padding(start = 22.dp)
          .verticalScroll(state = rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        AddExpenseRow {
          Icon(
            painter = painterResource(id = R.drawable.round_description_24),
            contentDescription = "Credit card"
          )
          OutlinedTextField(value = "", onValueChange = {}, label = { Text(text = "Concepto") })
        }
        AddExpenseRow {
          Icon(
            painter = painterResource(R.drawable.round_comment_24),
            contentDescription = "Comment"
          )
          OutlinedTextField(value = "", onValueChange = { }, label = { Text(text = "Comentario") })
        }
        AddExpenseRow {
          Icon(
            painter = painterResource(id = R.drawable.baseline_calendar_month_24),
            contentDescription = "Fecha"
          )
          OutlinedTextField(
            value = "",
            onValueChange = {},
            label = { Text(text = "Quincena") },
            interactionSource = remember {
              MutableInteractionSource()
            }.also { interactionSource ->
              LaunchedEffect(key1 = interactionSource) {
                interactionSource.interactions.collect { interaction ->
                  if (interaction is PressInteraction.Release) {
                    openDialog = true
                  }
                }
              }
            })
          if (openDialog) {
            DatePickerDialog(
              onDismissRequest = { openDialog = false },
              confirmButton = {
                TextButton(
                  onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                      //viewModel.setFormattedDate(millis)
                    }
                    openDialog = false

                  }) {
                  Text(text = "Aceptar")
                }
              },
              dismissButton = {
                TextButton(onClick = { openDialog = false }) {
                  Text(text = "Cancelar")
                }
              }) {
              DatePicker(state = datePickerState)
            }
          }

        }
        AddExpenseRow {
          Icon(
            painter = painterResource(id = R.drawable.round_attach_money_24),
            contentDescription = "total"
          )
          OutlinedTextField(
            value = "",
            onValueChange = { },
            label = { Text(text = "Total") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
          )
        }
        AddExpenseRow {
          Icon(
            painter = painterResource(id = R.drawable.baseline_credit_card_24),
            contentDescription = "Credit card"
          )
          ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = {
              expanded = !expanded
            }
          ) {
            OutlinedTextField(
              readOnly = true,
              value = "",
              onValueChange = { },
              label = { Text("Asociar Tarjeta") },
              trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(
                  expanded = expanded
                )
              },
              modifier = Modifier.menuAnchor()
              // colors = ExposedDropdownMenuDefaults.textFieldColors()
            )
            ExposedDropdownMenu(
              expanded = expanded,
              onDismissRequest = {
                expanded = false
              }
            ) {
              DropdownMenuItem(
                text = {
                  Text(text = "Tarjeta")
                },
                onClick = {}
              )
            }
          }
        }
        AddExpenseRow {
          //TODO make a dialog to add the new tags and add the selected tags to
          // the text field
          Icon(
            painter = painterResource(id = R.drawable.round_sell_24),
            contentDescription = "Tags"
          )
          OutlinedTextField(
            value = "",
            onValueChange = {},
            label = { Text(text = "Agregar nuevo tag") })
        }
        Row(modifier = Modifier.fillMaxWidth()) {
          Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(text = "Tags disponibles")
            FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
              tags.map {
                InputChip(
                  selected = it.selected,
                  onClick = { onEvent(AddExpenseEvent.SelectTag, it.id) },
                  label = { Text(text = it.name) })
              }
            }
          }
        }
      }
    }
  }
}

@Composable
fun AddExpenseRow(content: @Composable () -> Unit) {
  Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.spacedBy(12.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    content()
  }
}

//@Preview
//@Composable
//fun AddExpenseScreenContentPreview() {
//  AddExpenseScreenContent(tags = emptyList(), onEvent = (AddExpenseEvent.SelectTag, "") -> Unit)
//}