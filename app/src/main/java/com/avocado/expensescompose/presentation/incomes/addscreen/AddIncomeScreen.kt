package com.avocado.expensescompose.presentation.incomes.addscreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.avocado.expensescompose.presentation.navigation.NavigateEvent
import com.avocado.expensescompose.presentation.shared.DateDialog
import com.avocado.expensescompose.presentation.topbar.AppBar

@Composable
fun AddIncomeScreen(
  incomeId: String = "",
  viewModel: AddIncomeViewModel = hiltViewModel(),
  onPopBackStack: () -> Unit = {},
  onNavigate: (navigateEvent: NavigateEvent, shouldRefresh: String, isSuccessLogin: Boolean) -> Unit,
) {
  val state by viewModel.state.collectAsStateWithLifecycle()

  LaunchedEffect(key1 = Unit) {
    if (incomeId.isNotEmpty()) {
      viewModel.getIncomeById(incomeId)
    }
  }

  AddIncomeContent(
    incomeId = state.incomeId,
    total = state.total,
    comment = state.comments,
    date = state.date,
    initialDate = state.initialDate,
    isInserted = state.isInserted,
    isUpdated = state.isUpdated,
    openDateDialog = state.openDateDialog,
    onPopBackStack = onPopBackStack,
    onNavigate = onNavigate,
    onEvent = viewModel::onEvent
  )

}

@Composable
fun AddIncomeContent(
  incomeId: String,
  total: String,
  comment: String,
  date: String,
  initialDate: Long?,
  isInserted: Boolean,
  isUpdated: Boolean,
  openDateDialog: Boolean,
  onPopBackStack: () -> Unit = {},
  onNavigate: (navigateEvent: NavigateEvent, shouldRefresh: String, isSuccessLogin: Boolean) -> Unit,
  onEvent: (addIncomeEvent: AddIncomeEvent, param: String?) -> Unit
) {
  val focusRequester = remember { FocusRequester() }

  Scaffold(
    topBar = {
      AppBar(
        title = "Agregar ingreso",
        navigationIcon = Icons.Rounded.ArrowBack,
        buttonText = "Guardar",
        onNavigationIconClick = { onPopBackStack() },
        onActionButtonClick = {
          if (incomeId.isEmpty()) {
            onEvent(AddIncomeEvent.InsertIncome, null)
          } else if (incomeId.isNotEmpty()) {
            onEvent(AddIncomeEvent.UpdateIncome, null)
          }
        }
      )
    }
  ) { paddingValues ->
    Surface(
      modifier = Modifier
        .padding(paddingValues)
        .fillMaxSize()
    ) {
      Column(
        modifier = Modifier.padding(top = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        DateDialog(
          date = date,
          openDateDialog = openDateDialog,
          modifier = Modifier.fillMaxWidth(),
          initialSelectedDate = initialDate,
          onConfirm = { formattedDate ->
            onEvent(
              AddIncomeEvent.UpdateDate,
              formattedDate
            )
          },
          onDismiss = { onEvent(AddIncomeEvent.DateDialogClose, null) },
          onSelectTextField = { onEvent(AddIncomeEvent.DateDialogOpen, null) }
        )
        OutlinedTextField(
          value = total,
          label = { Text(text = "Total") },
          onValueChange = { onEvent(AddIncomeEvent.UpdateTotal, it) },
          keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Decimal,
            imeAction = ImeAction.Next
          )
        )

        OutlinedTextField(
          value = comment,
          label = { Text(text = "Comentarios") },
          onValueChange = { onEvent(AddIncomeEvent.UpdateComment, it) },
          maxLines = 12,
          keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Done
          ),
          keyboardActions = KeyboardActions(
            onNext = { focusRequester.requestFocus() },
            onDone = { onEvent(AddIncomeEvent.InsertIncome, null) }
          )
        )
      }

      if (isInserted) {
        LaunchedEffect(key1 = Unit) {
          onNavigate(NavigateEvent.NavigateIncomeOverview, "ADDED", false)
        }
      } else if (isUpdated) {
        LaunchedEffect(key1 = Unit) {
          onNavigate(NavigateEvent.NavigateIncomeOverview, "UPDATED", false)
        }
      }
    }
  }
}
