package com.avocado.expensescompose.presentation.expenses.addexpense

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.avocado.expensescompose.R
import com.avocado.expensescompose.data.adapters.adapt
import com.avocado.expensescompose.data.model.card.Card
import com.avocado.expensescompose.presentation.shared.DateDialog
import com.avocado.expensescompose.presentation.topbar.AppBar
import com.avocado.type.Category
import com.avocado.type.FixedExpenseFrequency
import kotlinx.coroutines.launch

@Composable
fun AddExpenseScreen(
  expenseId: String = "",
  viewModel: AddExpenseViewModel = hiltViewModel(),
  onPopBackStack: () -> Unit = {}
) {
  val state by viewModel.state.collectAsStateWithLifecycle()

  if (expenseId.isNotEmpty()) {
    LaunchedEffect(key1 = Unit) {
      viewModel.getExpenseById(expenseId = expenseId)
    }
  }

  if (state.isAdded) {
    LaunchedEffect(key1 = Unit) {
      onPopBackStack()
    }
  }

  AddExpenseScreenContent(
    expenseId = expenseId,
    cards = state.cardsList,
    selectedCard = state.selectedCard,
    categories = state.category,
    loadingCard = state.loadingCard,
    concept = state.concept,
    comment = state.comment,
    total = state.total,
    uiError = state.uiError,
    initialSelectedDate = state.initialDate,
    buttonText = state.buttonText,
    openCardMenu = state.openCardMenu,
    openCategoryList = state.openCategoryList,
    openFrequencyList = state.openFixedFrequencyList,
    expenseAddedError = state.expenseAddedError,
    loading = state.loading,
    isMonthWithoutInterest = state.isMonthWithoutInterest,
    recurrentExpenseFrequency = state.recurrentExpenseFrequency,
    numberOfMonthsOrWeeks = state.numberOfMonthsOrWeeks,
    onEvent = viewModel::onEvent,
    onPopBackStack = onPopBackStack
  )
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun AddExpenseScreenContent(
  expenseId: String,
  cards: List<Card>,
  selectedCard: Card?,
  categories: Category,
  loadingCard: Boolean,
  concept: String,
  comment: String,
  total: String,
  uiError: String?,
  initialSelectedDate: Long,
  buttonText: String,
  openCardMenu: Boolean,
  openCategoryList: Boolean,
  openFrequencyList: Boolean,
  expenseAddedError: Boolean,
  loading: Boolean,
  isMonthWithoutInterest: Boolean,
  recurrentExpenseFrequency: FixedExpenseFrequency,
  numberOfMonthsOrWeeks: String,
  onEvent: (event: AddExpenseEvent, elementId: String?) -> Unit,
  onPopBackStack: () -> Unit = {}
) {
  val scope = rememberCoroutineScope()
  val snackBarHostState = remember { SnackbarHostState() }
  val focusRequester = remember { FocusRequester() }
  val keyboardController = LocalSoftwareKeyboardController.current

  LaunchedEffect(key1 = snackBarHostState, key2 = expenseAddedError) {
    if (expenseAddedError) {
      keyboardController?.hide()
      scope.launch {
        snackBarHostState.showSnackbar(
          uiError.orEmpty()
        )
      }
      onEvent(AddExpenseEvent.ClearError, "")
    }
  }

  Scaffold(
    topBar = {
      AppBar(
        title = stringResource(id = R.string.add_expense_add_expense),
        buttonText = buttonText,
        onActionButtonClick = {
          if (expenseId.isEmpty()) {
            onEvent(AddExpenseEvent.AddExpense, null)
          } else {
            onEvent(
              AddExpenseEvent.UpdateExpense,
              expenseId
            )
          }
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
        if (!loading && !loadingCard) {
          DateDialog(
            initialSelectedDate = initialSelectedDate,
            iconResource = R.drawable.baseline_calendar_month_24,
            onConfirm = { formattedDate -> onEvent(AddExpenseEvent.UpdateDate, formattedDate) },
            modifier = Modifier.padding(start = 8.dp)
          )
        }

        AddExpenseRow {
          Icon(
            painter = painterResource(id = R.drawable.round_description_24),
            contentDescription = "Credit card"
          )
          OutlinedTextField(
            value = concept,
            onValueChange = { onEvent(AddExpenseEvent.UpdateConcept, it) },
            label = { Text(text = stringResource(id = R.string.add_expense_concept)) },
            modifier = Modifier.fillMaxWidth(),
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
            label = { Text(text = stringResource(id = R.string.add_expense_total)) },
            modifier = Modifier.fillMaxWidth(),
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
            painter = painterResource(id = R.drawable.baseline_credit_card_24),
            contentDescription = "Credit card"
          )
          DropDownMenu(
            expanded = openCardMenu,
            textFieldLabel = stringResource(id = R.string.add_expense_link_card),
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
            textFieldLabel = stringResource(id = R.string.add_expense_category),
            textFieldValue = categories.name,
            onOpenEvent = { onEvent(AddExpenseEvent.CategoryListOpen, null) },
            onCloseEvent = { onEvent(AddExpenseEvent.CategoryListClose, null) }
          ) {
            Category.values().filter { it != Category.UNKNOWN__ }.map {
              DropdownMenuItem(
                text = {
                  Text(text = stringResource(it.adapt()))
                },
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                  onEvent(AddExpenseEvent.SelectCategory, it.name)
                  onEvent(AddExpenseEvent.CategoryListClose, null)
                }
              )
            }
          }
        }

        if (isMonthWithoutInterest) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(start = 34.dp, end = 20.dp)
          ) {
            DropDownMenu(
              expanded = openFrequencyList,
              textFieldLabel = stringResource(id = R.string.add_expense_frequency),
              textFieldValue = recurrentExpenseFrequency.name,
              onOpenEvent = { onEvent(AddExpenseEvent.FixedFrequencyListOpen, null) },
              onCloseEvent = { onEvent(AddExpenseEvent.FixedFrequencyListClose, null) }
            ) {
              FixedExpenseFrequency.values().filter { it != FixedExpenseFrequency.UNKNOWN__ }.map {
                DropdownMenuItem(
                  text = {
                    Text(text = stringResource(it.adapt()))
                  },
                  onClick = {
                    onEvent(AddExpenseEvent.SelectFrequency, it.name)
                    onEvent(AddExpenseEvent.FixedFrequencyListClose, null)
                  }
                )
              }
            }
          }
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(start = 34.dp, end = 20.dp)
          ) {
            OutlinedTextField(
              value = numberOfMonthsOrWeeks,
              onValueChange = { onEvent(AddExpenseEvent.UpdateNumberOfMonthsOrWeeks, it) },
              label = {
                Text(
                  text = stringResource(id = if (recurrentExpenseFrequency.name == FixedExpenseFrequency.Monthly.name) R.string.add_expense_number_months else R.string.add_expense_number_weeks)
                )
              },
              modifier = Modifier.fillMaxWidth(),
              keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Decimal,
                imeAction = ImeAction.Next
              ),
              keyboardActions = KeyboardActions(
                onNext = { focusRequester.requestFocus() }
              )
            )
          }
        }

        AddExpenseRow {
          Icon(
            painter = painterResource(R.drawable.round_comment_24),
            contentDescription = "Comment"
          )
          OutlinedTextField(
            value = comment,
            onValueChange = { onEvent(AddExpenseEvent.UpdateComment, it) },
            label = { Text(text = stringResource(id = R.string.add_expense_comment)) },
            modifier = Modifier
              .focusRequester(focusRequester)
              .fillMaxWidth()
              .height(120.dp),
            maxLines = 3
          )
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
      modifier = Modifier
        .menuAnchor()
        .fillMaxWidth()
    )
    ExposedDropdownMenu(
      expanded = if (dropDownMenuEnabled) false else expanded,
      onDismissRequest = {
        onCloseEvent()
      }
    ) {
      menuItems()
    }
  }
}

@Composable
fun AddExpenseRow(content: @Composable () -> Unit) {
  Row(
    horizontalArrangement = Arrangement.spacedBy(12.dp),
    verticalAlignment = Alignment.CenterVertically,
    modifier = Modifier
      .fillMaxWidth()
      .padding(end = 20.dp)
  ) {
    content()
  }
}

// @Preview
// @Composable
// fun AddExpenseScreenContentPreview() {
//  AddExpenseScreenContent(tags = emptyList(), onEvent = (AddExpenseEvent.SelectTag, "") -> Unit)
// }
