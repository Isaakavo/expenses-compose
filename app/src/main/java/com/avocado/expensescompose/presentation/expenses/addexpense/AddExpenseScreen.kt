package com.avocado.expensescompose.presentation.expenses.addexpense

import android.content.Context
import android.util.Log
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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
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
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.avocado.expensescompose.R
import com.avocado.expensescompose.domain.cards.models.Card
import com.avocado.expensescompose.domain.tags.models.Tag
import com.avocado.expensescompose.presentation.topbar.AppBar
import com.avocado.expensescompose.presentation.util.formatDateFromMillis
import kotlinx.coroutines.launch

const val ADD_EXPENSE_SCREEN_LOG = "AddExpenseScreen"

@Composable
fun AddExpenseScreen(
  viewModel: AddExpenseViewModel = hiltViewModel(),
  context: Context = LocalContext.current,
  onPopBackStack: () -> Unit = {}
) {
  val state by viewModel.state.collectAsStateWithLifecycle()

  if (state.showToast && !state.loadingCard) {
    Toast.makeText(context, state.toastMessage, Toast.LENGTH_LONG).show()
    viewModel.resetToast()
  }

  AddExpenseScreenContent(
    cards = state.cardsList,
    selectedCard = state.selectedCard,
    tags = state.tagList,
    loadingCard = state.loadingCard,
    newTag = state.newTag,
    newTags = state.newTags,
    concept = state.concept,
    comment = state.comment,
    total = state.total,
    date = state.date,
    openDateDialog = state.openDateDialog,
    openTagDialog = state.openTagDialog,
    openCardMenu = state.openCardMenu,
    expenseAdded = state.expenseAdded,
    expenseAddedError = state.expenseAddedError,
    onEvent = viewModel::onEvent,
    onPopBackStack = onPopBackStack
  )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddExpenseScreenContent(
  cards: List<Card>,
  selectedCard: Card?,
  tags: List<Tag>,
  loadingCard: Boolean,
  newTag: String,
  newTags: List<String>,
  concept: String,
  comment: String,
  total: String,
  date: String,
  openDateDialog: Boolean,
  openTagDialog: Boolean,
  openCardMenu: Boolean,
  expenseAdded: Boolean,
  expenseAddedError: Boolean,
  onEvent: (event: AddExpenseEvent, elementId: String?) -> Unit,
  onPopBackStack: () -> Unit = {}
) {
  val datePickerState = rememberDatePickerState()
  val scope = rememberCoroutineScope()
  val snackBarHostState = remember { SnackbarHostState() }

  //TODO implement snack bar
  LaunchedEffect(Unit) {
    if (expenseAdded || expenseAddedError) {
      scope.launch {
        snackBarHostState.showSnackbar(if (expenseAdded) "Gasto añadido correctamente" else "Ocurrió un error al añadir el gasto")
      }
    }
  }

  Scaffold(
    topBar = {
      AppBar(
        title = "Agregar gasto", buttonText = "Agregar",
        actionItemOnClick = {
          onEvent(AddExpenseEvent.AddExpense, null)
        },
        onNavigationIconClick = { onPopBackStack() }
      )
    },
    snackbarHost = {
      SnackbarHost(hostState = snackBarHostState)
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
          OutlinedTextField(
            value = concept,
            onValueChange = { onEvent(AddExpenseEvent.UpdateConcept, it) },
            label = { Text(text = "Concepto") })
        }
        AddExpenseRow {
          Icon(
            painter = painterResource(R.drawable.round_comment_24),
            contentDescription = "Comment"
          )
          OutlinedTextField(
            value = comment,
            onValueChange = { onEvent(AddExpenseEvent.UpdateComment, it) },
            label = { Text(text = "Comentario") })
        }
        AddExpenseRow {
          Icon(
            painter = painterResource(id = R.drawable.baseline_calendar_month_24),
            contentDescription = "Fecha"
          )
          ClickableTextField(value = date, label = "Fecha") {
            onEvent(AddExpenseEvent.DateDialogOpen, null)
          }
          if (openDateDialog) {
            DatePickerDialog(
              onDismissRequest = { onEvent(AddExpenseEvent.DateDialogClose, null) },
              confirmButton = {
                TextButton(
                  onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                      val formattedDate = millis.formatDateFromMillis()
                      onEvent(AddExpenseEvent.UpdateDate, formattedDate)
                    }
                    onEvent(AddExpenseEvent.DateDialogClose, null)

                  }) {
                  Text(text = "Aceptar")
                }
              },
              dismissButton = {
                TextButton(onClick = { onEvent(AddExpenseEvent.DateDialogClose, null) }) {
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
            value = total,
            onValueChange = { onEvent(AddExpenseEvent.UpdateTotal, it) },
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
            expanded = openCardMenu,
            onExpandedChange = {
              if (openCardMenu) {
                onEvent(AddExpenseEvent.OpenCardMenu, null)
              } else {
                onEvent(AddExpenseEvent.CloseCardMenu, null)
              }
            }
          ) {
            OutlinedTextField(
              readOnly = true,
              value = selectedCard?.aliasWithBankText() ?: "",
              onValueChange = { },
              label = { Text("Asociar Tarjeta") },
              trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(
                  expanded = openCardMenu
                )
              },
              enabled = cards.isNotEmpty(),
              modifier = Modifier.menuAnchor()
              // colors = ExposedDropdownMenuDefaults.textFieldColors()
            )
            ExposedDropdownMenu(
              expanded = if (cards.isEmpty() && !loadingCard) {
                false
              } else openCardMenu,
              onDismissRequest = {
                onEvent(AddExpenseEvent.CloseCardMenu, null)
              },
            ) {
              Log.d(ADD_EXPENSE_SCREEN_LOG, "Number of cards: ${cards.size}")
              cards.map {
                DropdownMenuItem(
                  text = {
                    Text(text = "${it.alias}-${it.bank}")
                  },
                  onClick = {
                    onEvent(AddExpenseEvent.SelectCard, it.id)
                    onEvent(AddExpenseEvent.CloseCardMenu, null)
                  }
                )
              }
            }
          }
        }
        AddExpenseRow {
          Icon(
            painter = painterResource(id = R.drawable.round_sell_24),
            contentDescription = "Tags"
          )
          ClickableTextField(value = newTags.toString(), label = "Agregar nuevo tag") {
            onEvent(AddExpenseEvent.TagDialogOpen, null)
          }
          if (openTagDialog) {
            AddTagsDialog(
              value = newTag,
              onDismiss = { onEvent(AddExpenseEvent.TagDialogClose, null) },
              onConfirm = {
                onEvent(AddExpenseEvent.AddNewTag, null)
                onEvent(AddExpenseEvent.TagDialogClose, null)
              },
              onValueChange = { onEvent(AddExpenseEvent.UpdateNewTag, it) }
            )
          }
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

@Composable
fun ClickableTextField(value: String, label: String, action: () -> Unit) {
  OutlinedTextField(
    value = value,
    onValueChange = {},
    label = { Text(text = label) },
    interactionSource = remember {
      MutableInteractionSource()
    }.also { interactionSource ->
      LaunchedEffect(key1 = interactionSource) {
        interactionSource.interactions.collect { interaction ->
          if (interaction is PressInteraction.Release) {
            action()
          }
        }
      }
    })
}

@Composable
fun AddTagsDialog(
  value: String,
  onDismiss: () -> Unit,
  onConfirm: () -> Unit,
  onValueChange: (String) -> Unit
) {
  Dialog(onDismissRequest = { onDismiss() }) {
    Card(
      modifier = Modifier
        .fillMaxWidth()
        .height(275.dp)
        .padding(16.dp),
      shape = RoundedCornerShape(16.dp),
    ) {
      Column(
        modifier = Modifier
          .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
      ) {
        Text(text = "Añadir nuevo tag", modifier = Modifier.padding(16.dp))
        Row(
          modifier = Modifier
            .fillMaxWidth(),
          horizontalArrangement = Arrangement.Center,
        ) {
          OutlinedTextField(
            value = value,
            onValueChange = { onValueChange(it) },
            modifier = Modifier.padding(start = 16.dp, end = 16.dp)
          )
        }
        Row(
          modifier = Modifier
            .fillMaxWidth(),
          horizontalArrangement = Arrangement.Center,
        ) {
          TextButton(
            onClick = { onDismiss() },
            modifier = Modifier.padding(8.dp),
          ) {
            Text("Cancelar")
          }
          TextButton(
            onClick = { onConfirm() },
            modifier = Modifier.padding(8.dp),
          ) {
            Text("Aceptar")
          }
        }
      }
    }
  }
}

//@Preview
//@Composable
//fun AddExpenseScreenContentPreview() {
//  AddExpenseScreenContent(tags = emptyList(), onEvent = (AddExpenseEvent.SelectTag, "") -> Unit)
//}