package com.avocado.expensescompose.presentation.incomes.addscreen

import android.icu.util.Calendar
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.avocado.expensescompose.presentation.navigation.NavigateEvent
import com.avocado.expensescompose.presentation.shared.DateDialog
import com.avocado.expensescompose.presentation.topbar.AppBar

@Composable
fun AddIncomeScreen(
  viewModel: AddIncomeViewModel = hiltViewModel(),
  onPopBackStack: () -> Unit = {},
  onNavigate: (navigateEvent: NavigateEvent, shouldRefresh: Boolean, isSuccessLogin: Boolean) -> Unit,
) {
  val state by viewModel.state.collectAsStateWithLifecycle()

  AddIncomeContent(
    total = state.total,
    comment = state.comments,
    date = state.date,
    initialDate = state.initialDate,
    isInserted = state.isInserted,
    openDateDialog = state.openDateDialog,
    onPopBackStack = onPopBackStack,
    onNavigate = onNavigate,
    onEvent = viewModel::onEvent
  )

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddIncomeContent(
  total: String,
  comment: String,
  date: String,
  initialDate: Long,
  isInserted: Boolean,
  openDateDialog: Boolean,
  onPopBackStack: () -> Unit = {},
  onNavigate: (navigateEvent: NavigateEvent, shouldRefresh: Boolean, isSuccessLogin: Boolean) -> Unit,
  onEvent: (addIncomeEvent: AddIncomeEvent, param: String?) -> Unit
) {
  val calendar = Calendar.getInstance()
  calendar.set(2023, 8, 30) // add year, month (Jan), date
  val datePickerState = rememberDatePickerState(initialSelectedDateMillis = initialDate)

  Scaffold(
    topBar = {
      AppBar(
        title = "Agregar ingreso",
        navigationIcon = Icons.Rounded.ArrowBack,
        buttonText = "Guardar",
        onNavigationIconClick = { onPopBackStack() }) {
        onEvent(AddIncomeEvent.InsertIncome, null)
      }
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
          datePickerState = datePickerState,
          modifier = Modifier.fillMaxWidth(),
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
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
        )

        OutlinedTextField(
          value = comment,
          label = { Text(text = "Comentarios") },
          onValueChange = { onEvent(AddIncomeEvent.UpdateComment, it) },
          maxLines = 12,
        )
      }

      if (isInserted) {
        LaunchedEffect(key1 = Unit) {
          onNavigate(NavigateEvent.NavigateIncomeOverview, true, false)
        }
      }
    }
  }
}
