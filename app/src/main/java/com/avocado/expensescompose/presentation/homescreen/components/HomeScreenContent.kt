package com.avocado.expensescompose.presentation.homescreen.components

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.avocado.expensescompose.R
import com.avocado.expensescompose.presentation.expenses.allexpenses.AllExpensesListScreen
import com.avocado.expensescompose.presentation.homescreen.viewmodel.BackPress
import com.avocado.expensescompose.presentation.homescreen.viewmodel.HomeScreenEvents
import com.avocado.expensescompose.presentation.homescreen.viewmodel.HomeScreens
import com.avocado.expensescompose.presentation.incomes.incomeslist.IncomesList
import com.avocado.expensescompose.presentation.navigation.NavigateEvent
import com.avocado.expensescompose.presentation.shared.CustomScaffold
import com.avocado.expensescompose.presentation.shared.DateRangeDialog
import com.avocado.expensescompose.presentation.topbar.AppBar
import com.avocado.expensescompose.presentation.topbar.MenuItems
import com.avocado.expensescompose.presentation.util.Operations
import java.time.LocalDateTime
import kotlinx.coroutines.delay

@Composable
fun HomeScreenContent(
  screens: HomeScreens?,
  backPressState: BackPress?,
  showToast: Boolean,
  onNavigate: (navigateEvent: NavigateEvent, income: LocalDateTime?) -> Unit,
  onEvent: (HomeScreenEvents, screens: HomeScreens?) -> Unit = { one, two -> },
  onNavigateCardsScreen: (navigateEvent: NavigateEvent, operation: String) -> Unit = { one, two -> }
) {
  CustomScaffold(
    topBar = {
      AppBar(
        title = stringResource(id = R.string.income_income),
        navigationIcon = Icons.Rounded.Menu,
        dropDownMenuItems = listOf(
          MenuItems(
            text = stringResource(id = R.string.homescreen_incomes_option),
            action = {
              onEvent(HomeScreenEvents.ScreenToDisplay, HomeScreens.INCOME)
            }
          ),
          MenuItems(
            text = stringResource(id = R.string.homescreen_cards_option),
            action = {
              onEvent(HomeScreenEvents.ScreenToDisplay, HomeScreens.CARDS)
            }
          ),
          MenuItems(
            text = stringResource(id = R.string.homescreen_expenses_option),
            action = {
              onEvent(HomeScreenEvents.ScreenToDisplay, HomeScreens.EXPENSES)
            }
          )
        )
      )
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

    when (screens) {
      HomeScreens.INCOME -> {
        IncomesList {
          onNavigate(NavigateEvent.NavigateIncomeExpensesList, it)
        }
      }

      HomeScreens.CARDS -> {
        onNavigateCardsScreen(
          NavigateEvent.NavigateCardsScreen,
          Operations.NONE.name
        )
      }

      HomeScreens.EXPENSES -> {
        var date by remember { mutableStateOf(LongRange.EMPTY) }
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp, start = 16.dp, end = 16.dp, bottom = 12.dp)
        ) {
          DateRangeDialog(
            iconResource = R.drawable.baseline_calendar_month_24,
            onConfirm = { date = it }
          )
          AllExpensesListScreen(
            dateRange = date,
            onNavigate = onNavigateCardsScreen
          )
        }
      }

      null -> TODO()
    }
  }

  LaunchedEffect(key1 = backPressState) {
    if (backPressState == BackPress.InitialTouch) {
      delay(2000)
      onEvent(HomeScreenEvents.BackPressIdle, null)
    }
  }

  BackHandler(true) {
    if (backPressState == BackPress.InitialTouch) {
      onNavigate(NavigateEvent.NavigateLogin, null)
    }
    onEvent(HomeScreenEvents.BackPressInitialTouch, null)
  }

  if (showToast) {
    Toast.makeText(
      LocalContext.current,
      stringResource(id = R.string.income_press_again_exit),
      Toast.LENGTH_LONG
    )
      .show()
    onEvent(HomeScreenEvents.CloseToast, null)
  }
}
