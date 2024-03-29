package com.avocado.expensescompose.presentation.shared

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun CustomScaffold(
  topBar: @Composable () -> Unit = {},
  snackBarHost: @Composable () -> Unit = {},
  floatingActionButton: @Composable () -> Unit = {},
  mainContent: @Composable (PaddingValues) -> Unit
) {
  Scaffold(
    topBar = topBar,
    snackbarHost = snackBarHost,
    floatingActionButton = floatingActionButton
  ) { paddingValues ->
    Surface(
      modifier = Modifier
        .padding(paddingValues)
        .fillMaxWidth()
    ) {
      mainContent(paddingValues)
    }
  }
}
