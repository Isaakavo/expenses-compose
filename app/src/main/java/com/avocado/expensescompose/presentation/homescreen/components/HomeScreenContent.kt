package com.avocado.expensescompose.presentation.homescreen.components

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.avocado.expensescompose.R
import com.avocado.expensescompose.presentation.cards.cardsscreen.CardsScreen
import com.avocado.expensescompose.presentation.expenses.allexpenses.AllExpensesListScreen
import com.avocado.expensescompose.presentation.homescreen.viewmodel.BackPress
import com.avocado.expensescompose.presentation.homescreen.viewmodel.HomeScreenEvents
import com.avocado.expensescompose.presentation.homescreen.viewmodel.HomeScreens
import com.avocado.expensescompose.presentation.homescreen.viewmodel.toText
import com.avocado.expensescompose.presentation.incomes.incomeslist.IncomesList
import com.avocado.expensescompose.presentation.navigation.NavigateEvent
import com.avocado.expensescompose.presentation.shared.CustomScaffold
import com.avocado.expensescompose.presentation.shared.DateRangeDialog
import java.time.LocalDateTime
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenContent(
  screens: HomeScreens?,
  screenTitle: Int,
  tabs: List<HomeScreens>,
  selectedTabIndex: Int,
  backPressState: BackPress?,
  showToast: Boolean,
  onNavigate: (navigateEvent: NavigateEvent, income: LocalDateTime?) -> Unit,
  onEvent: (HomeScreenEvents, screens: HomeScreens?) -> Unit = { one, two -> },
  onNavigateCardsScreen: (navigateEvent: NavigateEvent, operation: String) -> Unit = { one, two -> }
) {
  val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())
  CustomScaffold(
    modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
    topBar = {
      CenterAlignedTopAppBar(
        title = { Text(text = stringResource(id = screenTitle)) },
        scrollBehavior = scrollBehavior,
        colors = TopAppBarDefaults.topAppBarColors(
          containerColor = MaterialTheme.colorScheme.primaryContainer,
          titleContentColor = MaterialTheme.colorScheme.primary
        )
      )
    },
    floatingActionButton = {
      FabAddButtons(
        onNavigateAddIncome = {
          onNavigate(
            NavigateEvent.NavigationAddIncomeScreen,
            null
          )
        },
        onNavigateAddExpense = {
          onNavigate(NavigateEvent.NavigateAddExpenseScreen, null)
        },
        onNavigateAddCard = {
          onNavigate(NavigateEvent.NavigateAddCardsScreen, null)
        }
      )
    }
  ) {
    Column {
      TabRow(
        selectedTabIndex = selectedTabIndex,
        containerColor = MaterialTheme.colorScheme.surfaceVariant,
        modifier = Modifier
          .padding(22.dp)
          .clip(RoundedCornerShape(50))

      ) {
        tabs.forEachIndexed { index, screen ->
          Tab(
            selected = index == selectedTabIndex,
            onClick = {
              onEvent(HomeScreenEvents.ScreenToDisplay, screen)
            },
            modifier = Modifier.padding(8.dp)
          ) {
            Text(text = stringResource(id = screen.toText()))
          }
        }
      }

      when (screens) {
        HomeScreens.INCOME -> {
          IncomesList {
            onNavigate(NavigateEvent.NavigateIncomeExpensesList, it)
          }
        }

        HomeScreens.CARDS -> {
          CardsScreen(
            onNavigate = onNavigateCardsScreen
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
