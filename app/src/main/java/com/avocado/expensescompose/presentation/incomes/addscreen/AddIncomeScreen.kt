package com.avocado.expensescompose.presentation.incomes.addscreen

import android.icu.util.Calendar
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.avocado.expensescompose.presentation.util.formatDateDaysWithMonth
import com.avocado.expensescompose.presentation.topbar.AppBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddIncomeScreen(
  viewModel: AddIncomeViewModel = hiltViewModel(),
  onPopBackStack: () -> Unit = {}
) {
  val state by viewModel.state.collectAsStateWithLifecycle()
  val calendar = Calendar.getInstance()
  calendar.set(2023, 8, 30) // add year, month (Jan), date
  val datePickerState = rememberDatePickerState(initialSelectedDateMillis = viewModel.initialDate)
  var showDatePicker by remember {
    mutableStateOf(false)
  }

  Scaffold(
    topBar = {
      AppBar(
        title = "Agregar ingreso",
        navigationIcon = Icons.Rounded.ArrowBack,
        buttonText = "Guardar",
        onNavigationIconClick = { onPopBackStack() }) {
        viewModel.onEvent(AddIncomeEvent.InsertIncome)
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
        OutlinedTextField(
          value = viewModel.total,
          label = { Text(text = "Total") },
          onValueChange = { viewModel.setTotalIncome(it) },
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
        )

        if (showDatePicker) {
          DatePickerDialog(
            onDismissRequest = {
              showDatePicker = false
            },
            confirmButton = {
              TextButton(
                onClick = {
                  datePickerState.selectedDateMillis?.let { millis ->
                    viewModel.setFormattedDate(millis)
                  }
                  showDatePicker = false

                }) {
                Text(text = "Aceptar")
              }
            },
            dismissButton = {
              TextButton(onClick = { showDatePicker = false }) {
                Text(text = "Cancelar")
              }
            }
          ) {
            DatePicker(state = datePickerState)
          }
        }

        OutlinedTextField(
          value = viewModel.paymentDate,
          label = { Text(text = "Fecha de pago") },
          onValueChange = {},
          interactionSource = remember {
            MutableInteractionSource()
          }.also { interactionSource ->
            LaunchedEffect(key1 = interactionSource) {
              interactionSource.interactions.collect { interaction ->
                if (interaction is PressInteraction.Release) {
                  showDatePicker = true
                }
              }
            }
          }
        )

        OutlinedTextField(
          value = viewModel.comments,
          label = { Text(text = "Comentarios") },
          onValueChange = { viewModel.setComment(it) },
          maxLines = 12,
        )
      }

      if(state.isInserted) {
        AlertDialog(
          title = {
            Text(text = "¡Se agregó el ingreso correctamente!")
          },
          //TODO improve dialog to show better the data
          text = {
            Row {
              Text(text = state.insertedIncome?.paymentDate?.date?.formatDateDaysWithMonth() ?: "")
              Text(text = "$${state.insertedIncome?.total.toString()}")
            }
          },
          onDismissRequest = {
            viewModel.resetInputs()
            viewModel.closeDialog()
          },
          confirmButton = {
            TextButton(
              onClick = {
                //TODO check how to detect in home screen that we need to recall the query
                onPopBackStack()
              }
            ) {
              Text("Continuar")
            }
          },
          dismissButton = {
            TextButton(
              onClick = {
                viewModel.resetInputs()
                viewModel.closeDialog()
              }
            ) {
              Text("Agregar otro ingreso")
            }
          }
        )
      }
    }
  }

}

@Preview
@Composable
fun AddScreenPreview() {
  AddIncomeScreen()
}