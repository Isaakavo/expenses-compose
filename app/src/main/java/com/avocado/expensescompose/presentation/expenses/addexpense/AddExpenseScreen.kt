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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
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
import com.avocado.expensescompose.presentation.cards.cardsinputtext.CardsInputText
import com.avocado.expensescompose.presentation.shared.CustomScaffold
import com.avocado.expensescompose.presentation.shared.DateDialog
import com.avocado.expensescompose.presentation.shared.DropDownMenu
import com.avocado.expensescompose.presentation.shared.topbar.AppBar
import com.avocado.expensescompose.ui.theme.LocalSnackBarHostState
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

  LaunchedEffect(key1 = expenseId) {
    if (expenseId.isNotEmpty()) {
      viewModel.getExpenseById(expenseId = expenseId)
    }
  }

  LaunchedEffect(key1 = state.isAdded) {
    if (state.isAdded) {
      onPopBackStack()
    }
  }

  AddExpenseScreenContent(
    expenseId = expenseId,
    categories = state.category,
    concept = state.concept,
    selectedCard = state.selectedCard,
    comment = state.comment,
    total = state.total,
    uiError = state.uiError,
    initialSelectedDate = state.initialDate,
    buttonText = state.buttonText,
    openCategoryList = state.openCategoryList,
    openFrequencyList = state.openFixedFrequencyList,
    expenseAddedError = state.expenseAddedError,
    loading = state.loading,
    isMonthWithoutInterest = state.isMonthWithoutInterest,
    recurrentExpenseFrequency = state.recurrentExpenseFrequency,
    numberOfMonthsOrWeeks = state.numberOfMonthsOrWeeks,
    onEvent = viewModel::onEvent,
    onCardSelect = viewModel::addSelectedCard,
    onPopBackStack = onPopBackStack
  )
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun AddExpenseScreenContent(
  expenseId: String,
  categories: Category,
  concept: String,
  selectedCard: Card?,
  comment: String,
  total: String,
  uiError: String?,
  initialSelectedDate: Long,
  buttonText: String,
  openCategoryList: Boolean,
  openFrequencyList: Boolean,
  expenseAddedError: Boolean,
  loading: Boolean,
  isMonthWithoutInterest: Boolean,
  recurrentExpenseFrequency: FixedExpenseFrequency,
  numberOfMonthsOrWeeks: String,
  onEvent: (event: AddExpenseEvent, elementId: String?) -> Unit,
  onCardSelect: (card: Card?) -> Unit,
  onPopBackStack: () -> Unit = {}
) {
  val scope = rememberCoroutineScope()
  val snackBarHostState = LocalSnackBarHostState.current
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

  CustomScaffold(
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
    }
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(start = 22.dp)
        .verticalScroll(state = rememberScrollState()),
      verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      // TODO create a component to get the list of cards
      when {
        loading -> {
          CircularProgressIndicator()
        }

        else -> {
          DateDialog(
            initialSelectedDate = initialSelectedDate,
            iconResource = R.drawable.baseline_calendar_month_24,
            onConfirm = { formattedDate -> onEvent(AddExpenseEvent.UpdateDate, formattedDate) },
            modifier = Modifier.padding(start = 8.dp)
          )

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
            CardsInputText(selectedCard = selectedCard) { card ->
              onCardSelect(card)
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
