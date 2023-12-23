package com.avocado.expensescompose.presentation.expenses.addexpense

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.avocado.expensescompose.R
import com.avocado.expensescompose.data.model.card.Card
import com.avocado.expensescompose.presentation.shared.DateDialog
import com.avocado.expensescompose.presentation.topbar.AppBar
import com.avocado.type.Category
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

  if (state.isAdded) {
    LaunchedEffect(key1 = Unit) {
      onPopBackStack()
    }
  }

  AddExpenseScreenContent(
    cards = state.cardsList,
    selectedCard = state.selectedCard,
    categories = state.category,
    loadingCard = state.loadingCard,
    concept = state.concept,
    comment = state.comment,
    total = state.total,
    date = state.date,
    openDateDialog = state.openDateDialog,
    openCardMenu = state.openCardMenu,
    openCategoryList = state.openCategoryList,
    expenseAdded = state.expenseAdded,
    expenseAddedError = state.expenseAddedError,
    onEvent = viewModel::onEvent,
    onPopBackStack = onPopBackStack
  )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddExpenseScreenContent(
  cards: List<Card>,
  selectedCard: Card?,
  categories: Category,
  loadingCard: Boolean,
  concept: String,
  comment: String,
  total: String,
  date: String,
  openDateDialog: Boolean,
  openCardMenu: Boolean,
  openCategoryList: Boolean,
  expenseAdded: Boolean,
  expenseAddedError: Boolean,
  onEvent: (event: AddExpenseEvent, elementId: String?) -> Unit,
  onPopBackStack: () -> Unit = {}
) {
  val scope = rememberCoroutineScope()
  val snackBarHostState = remember { SnackbarHostState() }
  val focusRequester = remember { FocusRequester() }

  LaunchedEffect(key1 = snackBarHostState) {
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
        onActionButtonClick = {
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

        DateDialog(
          date = date,
          iconResource = R.drawable.baseline_calendar_month_24,
          openDateDialog = openDateDialog,
          onConfirm = { formattedDate -> onEvent(AddExpenseEvent.UpdateDate, formattedDate) },
          onDismiss = { onEvent(AddExpenseEvent.DateDialogClose, null) },
          onSelectTextField = { onEvent(AddExpenseEvent.DateDialogOpen, null) }
        )


        AddExpenseRow {
          Icon(
            painter = painterResource(id = R.drawable.round_description_24),
            contentDescription = "Credit card"
          )
          OutlinedTextField(
            value = concept,
            onValueChange = { onEvent(AddExpenseEvent.UpdateConcept, it) },
            label = { Text(text = "Concepto") },
            keyboardOptions = KeyboardOptions(
              imeAction = ImeAction.Next
            )
          )
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
            keyboardOptions = KeyboardOptions(
              keyboardType = KeyboardType.Decimal,
              imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
              onNext = { focusRequester.requestFocus() }
            )
          )
        }

        AddExpenseRow {
          Icon(
            painter = painterResource(R.drawable.round_comment_24),
            contentDescription = "Comment"
          )
          OutlinedTextField(
            value = comment,
            onValueChange = { onEvent(AddExpenseEvent.UpdateComment, it) },
            label = { Text(text = "Comentario") },
            modifier = Modifier.focusRequester(focusRequester),
            keyboardOptions = KeyboardOptions(
              imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
              onNext = { focusRequester.requestFocus() }
            )
          )
        }

        AddExpenseRow {
          Icon(
            painter = painterResource(id = R.drawable.baseline_credit_card_24),
            contentDescription = "Credit card"
          )
          DropDownMenu(
            expanded = openCardMenu,
            textFieldLabel = "Asociar Tarjeta",
            textFieldValue = selectedCard?.aliasWithBankText() ?: selectedCard?.bank ?: "",
            textFieldEnabled = cards.isNotEmpty(),
            dropDownMenuEnabled = cards.isEmpty() && !loadingCard,
            onOpenEvent = { onEvent(AddExpenseEvent.OpenCardMenu, null) },
            onCloseEvent = { onEvent(AddExpenseEvent.CloseCardMenu, null) }
          ) {
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

        AddExpenseRow {
          Icon(
            painter = painterResource(id = R.drawable.round_sell_24),
            contentDescription = "Tags"
          )
          DropDownMenu(
            expanded = openCategoryList,
            textFieldLabel = "Categorias",
            textFieldValue = categories.name,
            onOpenEvent = { onEvent(AddExpenseEvent.CategoryListOpen, null) },
            onCloseEvent = { onEvent(AddExpenseEvent.CategoryListClose, null) }) {
            Category.values().map {
              Log.d("Categories", it.name)
              DropdownMenuItem(
                text = {
                  Text(text = it.name)
                },
                onClick = {
                  onEvent(AddExpenseEvent.SelectCategory, it.name)
                  onEvent(AddExpenseEvent.CategoryListClose, null)
                }
              )
            }
          }
        }
      }
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropDownMenu(
  expanded: Boolean,
  textFieldLabel: String,
  textFieldValue: String,
  textFieldEnabled: Boolean = true,
  dropDownMenuEnabled: Boolean = false,
  onOpenEvent: () -> Unit,
  onCloseEvent: () -> Unit,
  menuItems: @Composable () -> Unit
) {
  ExposedDropdownMenuBox(
    expanded = expanded,
    onExpandedChange = {
      if (expanded) {
        onCloseEvent()
      } else {
        onOpenEvent()
      }
    }
  ) {
    OutlinedTextField(
      readOnly = true,
      value = textFieldValue,
      onValueChange = { },
      label = { Text(textFieldLabel) },
      trailingIcon = {
        ExposedDropdownMenuDefaults.TrailingIcon(
          expanded = expanded
        )
      },
      enabled = textFieldEnabled,
      modifier = Modifier.menuAnchor()
    )
    ExposedDropdownMenu(
      expanded = if (dropDownMenuEnabled) false else expanded,
      onDismissRequest = {
        onCloseEvent()
      },
    ) {
      menuItems()
    }
  }
}

@Composable
fun AddExpenseRow(content: @Composable () -> Unit) {
  Row(
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