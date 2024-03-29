package com.avocado.expensescompose.presentation.homescreen.components

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.avocado.expensescompose.R
import com.avocado.expensescompose.presentation.homescreen.viewmodel.BackPress
import com.avocado.expensescompose.presentation.homescreen.viewmodel.IncomeEvent
import com.avocado.expensescompose.presentation.incomes.incomeslist.IncomesList
import com.avocado.expensescompose.presentation.navigation.NavigateEvent
import com.avocado.expensescompose.presentation.shared.CustomScaffold
import com.avocado.expensescompose.presentation.topbar.AppBar
import com.avocado.expensescompose.presentation.util.Operations
import com.avocado.expensescompose.presentation.util.validateOperation
import java.time.LocalDateTime
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun HomeScreenContent(
  backPressState: BackPress?,
  isLoading: Boolean,
  uiError: Int,
  operation: String,
  showToast: Boolean,
  onNavigate: (navigateEvent: NavigateEvent, income: LocalDateTime?) -> Unit,
  onEvent: (IncomeEvent) -> Unit = {},
  onNavigateCardsScreen: (navigateEvent: NavigateEvent, operation: String) -> Unit = { one, two -> }
) {
  val scope: CoroutineScope = rememberCoroutineScope()
  val drawerState: DrawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
  val snackBarHostState = remember { SnackbarHostState() }
  val context = LocalContext.current

  if (operation.isNotEmpty()) {
    LaunchedEffect(key1 = Unit) {
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

  ModalNavigationDrawer(
    drawerState = drawerState,
    drawerContent = {
      ModalDrawerSheet(modifier = Modifier.requiredWidth(200.dp)) {
        Row(modifier = Modifier.padding(start = 16.dp, top = 12.dp, bottom = 24.dp)) {
          Text(text = "Expenses App", style = MaterialTheme.typography.bodyLarge)
        }
        Divider(thickness = 1.dp)
        Row(
          modifier = Modifier
            .padding(start = 12.dp, end = 12.dp, top = 6.dp)
        ) {
          FilledTonalButton(
            onClick = {
              onNavigateCardsScreen(
                NavigateEvent.NavigateCardsScreen,
                Operations.NONE.name
              )
            },
            contentPadding = PaddingValues(start = 24.dp)
          ) {
            Icon(
              painter = painterResource(id = R.drawable.baseline_credit_card_24),
              contentDescription = ""
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(text = stringResource(id = R.string.income_card), textAlign = TextAlign.Start)
            Spacer(modifier = Modifier.weight(1f))
          }
        }
      }
    }
  ) {
    CustomScaffold(
      topBar = {
        AppBar(
          title = stringResource(id = R.string.income_income),
          navigationIcon = Icons.Rounded.Menu,
          onNavigationIconClick = {
            scope.launch {
              drawerState.apply {
                if (isClosed) open() else close()
              }
            }
          }
        )
      },
      snackBarHost = {
        SnackbarHost(hostState = snackBarHostState)
      },
      floatingActionButton = {
        FabAddButtons(onNavigateAddIncome = {
          onNavigate(
            NavigateEvent.NavigationAddIncomeScreen,
            null
          )
        }, onNavigateAddExpense = { onNavigate(NavigateEvent.NavigateAddExpenseScreen, null) })
      }
    ) { paddingValues ->

      when {
        uiError != 0 -> {
          Column(
            modifier = Modifier.padding(paddingValues),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Text(text = stringResource(uiError), style = MaterialTheme.typography.headlineLarge)
          }
        }

        isLoading -> {
          Column(
            modifier = Modifier.padding(paddingValues),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            CircularProgressIndicator(strokeWidth = 6.dp)
          }
        }

        else -> {
          IncomesList {
            onNavigate(NavigateEvent.NavigateIncomeExpensesList, it)
          }
        }
      }
    }

    LaunchedEffect(key1 = backPressState) {
      if (backPressState == BackPress.InitialTouch) {
        delay(2000)
        onEvent(IncomeEvent.BackPressIdle)
      }
    }

    BackHandler(true) {
      if (backPressState == BackPress.InitialTouch) {
        onNavigate(NavigateEvent.NavigateLogin, null)
      }
      onEvent(IncomeEvent.BackPressInitialTouch)
    }

    if (showToast) {
      Toast.makeText(
        LocalContext.current,
        stringResource(id = R.string.income_press_again_exit),
        Toast.LENGTH_LONG
      )
        .show()
      onEvent(IncomeEvent.CloseToast)
    }
  }
}
