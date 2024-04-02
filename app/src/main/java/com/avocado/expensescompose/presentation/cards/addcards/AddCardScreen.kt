package com.avocado.expensescompose.presentation.cards.addcards

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.avocado.expensescompose.presentation.cards.addcards.components.AddCardContent
import com.avocado.expensescompose.presentation.cards.addcards.viewmodel.AddCardViewModel
import com.avocado.expensescompose.presentation.navigation.NavigateEvent
import com.avocado.expensescompose.presentation.util.Operations
import com.avocado.expensescompose.ui.theme.LocalSnackBarHostState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun AddCardScreen(
  viewModel: AddCardViewModel = hiltViewModel(),
  onPopBackStack: () -> Unit = {},
  onNavigate: (navigateEvent: NavigateEvent, operation: String) -> Unit
) {
  val state by viewModel.state.collectAsStateWithLifecycle()
  val scope: CoroutineScope = rememberCoroutineScope()
  val snackBarHostState = LocalSnackBarHostState.current
  val context = LocalContext.current

  LaunchedEffect(key1 = state.isAdded) {
    if (state.isAdded) {
      onNavigate(NavigateEvent.NavigateHomeScreen, Operations.ADD.name)
    }
  }

  LaunchedEffect(key1 = state.uiError) {
    if (state.uiError != 0) {
      scope.launch {
        snackBarHostState.showSnackbar(
          message = context.resources.getString(state.uiError)
        )
      }
    }
  }

  AddCardContent(
    bank = state.bank,
    alias = state.alias,
    isCreditCard = state.isCredit,
    isDebitCard = state.isDebit,
    isPhysical = state.isPhysical,
    isDigital = state.isDigital,
    onEvent = viewModel::onEvent,
    onPopBackStack = onPopBackStack
  )
}
