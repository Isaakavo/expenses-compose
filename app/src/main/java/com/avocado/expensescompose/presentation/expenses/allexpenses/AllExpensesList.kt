package com.avocado.expensescompose.presentation.expenses.allexpenses

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.avocado.expensescompose.presentation.shared.CustomScaffold

@Composable
fun AllExpensesListScreen(
  viewModel: AllExpensesListViewModel = hiltViewModel()
) {
  val state by viewModel.state.collectAsStateWithLifecycle()
}

@Composable
fun AllExpensesListContent() {
  CustomScaffold(
    topBar = { /*TODO*/ },
    snackBarHost = { /*TODO*/ }
  ) {
    Column(
      modifier = Modifier.fillMaxSize()
    ) {
    }
  }
}
