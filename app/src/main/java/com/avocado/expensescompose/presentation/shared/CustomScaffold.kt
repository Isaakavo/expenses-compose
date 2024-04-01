package com.avocado.expensescompose.presentation.shared

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier

val LocalSnackBarHostState = compositionLocalOf<SnackbarHostState> { error("No snack bar host state") }

@Composable
fun CustomScaffold(
  topBar: @Composable () -> Unit = {},
  snackBarHost: @Composable () -> Unit = {},
  floatingActionButton: @Composable () -> Unit = {},
  mainContent: @Composable (PaddingValues) -> Unit
) {
  val snackbarHostState = remember {
    SnackbarHostState()
  }

  CompositionLocalProvider(value = LocalSnackBarHostState provides snackbarHostState) {
    Scaffold(
      topBar = topBar,
      snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
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
}
