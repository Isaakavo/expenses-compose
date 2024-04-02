package com.avocado.expensescompose.presentation.homescreen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.avocado.expensescompose.R
import com.avocado.expensescompose.presentation.homescreen.components.HomeScreenContent
import com.avocado.expensescompose.presentation.homescreen.viewmodel.HomeScreenViewModel
import com.avocado.expensescompose.presentation.navigation.NavigateEvent
import com.avocado.expensescompose.presentation.util.validateOperation
import com.avocado.expensescompose.ui.theme.LocalSnackBarHostState
import java.time.LocalDateTime
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
  operation: String = "",
  viewModel: HomeScreenViewModel = hiltViewModel(),
  onNavigate: (navigateEvent: NavigateEvent, income: LocalDateTime?) -> Unit,
  onNavigateCardsScreen: (navigateEvent: NavigateEvent, operation: String) -> Unit
) {
  val state by viewModel.state.collectAsStateWithLifecycle()

  val scope: CoroutineScope = rememberCoroutineScope()
  val snackBarHostState = LocalSnackBarHostState.current
  val context = LocalContext.current

  LaunchedEffect(key1 = operation) {
    if (operation.isNotEmpty()) {
      validateOperation(
        operation,
        onAdd = {
          scope.launch {
            snackBarHostState.showSnackbar(
              context.resources.getString(R.string.income_add_successfully)
            )
          }
        },
        onUpdate = {
          scope.launch {
            snackBarHostState.showSnackbar(
              context.resources.getString(R.string.income_update_successfully)
            )
          }
        },
        onDelete = {
          scope.launch {
            snackBarHostState.showSnackbar(
              context.resources.getString(R.string.income_delete_successfully)
            )
          }
        }
      )
    }
  }

  HomeScreenContent(
    screens = state.screen,
    backPressState = state.backPressState,
    operation = operation,
    showToast = state.showToast,
    onNavigate = onNavigate,
    onEvent = viewModel::onEvent,
    onNavigateCardsScreen = onNavigateCardsScreen
  )
}
