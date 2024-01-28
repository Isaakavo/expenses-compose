package com.avocado.expensescompose.presentation.incomes.homescreen

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.avocado.expensescompose.R
import com.avocado.expensescompose.data.adapters.formatMoney
import com.avocado.expensescompose.data.model.total.Total
import com.avocado.expensescompose.domain.income.models.Income
import com.avocado.expensescompose.presentation.incomes.homescreen.viewmodel.BackPress
import com.avocado.expensescompose.presentation.incomes.homescreen.viewmodel.IncomeEvent
import com.avocado.expensescompose.presentation.incomes.homescreen.viewmodel.IncomesViewModel
import com.avocado.expensescompose.presentation.navigation.NavigateEvent
import com.avocado.expensescompose.presentation.topbar.AppBar
import com.avocado.expensescompose.presentation.util.Operations
import com.avocado.expensescompose.presentation.util.formatDateDaysWithMonth
import com.avocado.expensescompose.presentation.util.formatDateOnlyMonth
import com.avocado.expensescompose.presentation.util.validateOperation
import java.time.LocalDateTime
import java.util.Locale
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class NavigationIncomeDetails(
  val paymentDate: LocalDateTime?
)

@Composable
fun IncomesScreen(
  operation: String = "",
  viewModel: IncomesViewModel = hiltViewModel(),
  onNavigate: (navigateEvent: NavigateEvent, income: NavigationIncomeDetails?) -> Unit,
  onNavigateCardsScreen: (navigateEvent: NavigateEvent, operation: String) -> Unit
) {
  val state by viewModel.state.collectAsStateWithLifecycle()

  IncomeScreenContent(
    backPressState = state.backPressState,
    isLoading = state.isLoading,
    uiError = state.uiError,
    operation = operation,
    incomesMap = state.incomesMap,
    totalByMonth = state.totalByMonth,
    showToast = state.showToast,
    onNavigate = onNavigate,
    onEvent = viewModel::onEvent,
    onNavigateCardsScreen = onNavigateCardsScreen
  )
}

@Composable
fun IncomeScreenContent(
  backPressState: BackPress?,
  isLoading: Boolean,
  uiError: Int,
  operation: String,
  incomesMap: Map<String, MutableMap<String, MutableMap<String, MutableList<Income>?>>>?,
  totalByMonth: List<Total?>,
  showToast: Boolean,
  onNavigate: (navigateEvent: NavigateEvent, income: NavigationIncomeDetails?) -> Unit,
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
        },
        onAlwaysExecute = {
          onEvent(IncomeEvent.FetchIncomes)
        }
      )
    }
  }

  ModalNavigationDrawer(drawerState = drawerState, drawerContent = {
    ModalDrawerSheet {
      Row(modifier = Modifier.padding(start = 16.dp, top = 12.dp, bottom = 24.dp)) {
        Text(text = "Expenses App", style = MaterialTheme.typography.bodyLarge)
      }
      Divider(thickness = 1.dp)
      Row(
        modifier = Modifier
          .padding(start = 12.dp, end = 12.dp, top = 6.dp)
          .fillMaxWidth()
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
  }) {
    Scaffold(topBar = {
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
    }, snackbarHost = {
        SnackbarHost(hostState = snackBarHostState)
      }, floatingActionButton = {
        FabAddButtons(onNavigateAddIncome = {
          onNavigate(
            NavigateEvent.NavigationAddIncomeScreen,
            null
          )
        }, onNavigateAddExpense = { onNavigate(NavigateEvent.NavigateAddExpenseScreen, null) })
      }) { paddingValues ->
      Surface(
        modifier = Modifier
          .fillMaxSize()
          .padding(paddingValues)
      ) {
        if (uiError != 0) {
          Column(
            modifier = Modifier.padding(paddingValues),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Text(text = stringResource(uiError), style = MaterialTheme.typography.headlineLarge)
          }
        } else if (isLoading) {
          Column(
            modifier = Modifier.padding(paddingValues),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            CircularProgressIndicator(strokeWidth = 6.dp)
          }
        } else {
          if (incomesMap?.isNotEmpty() == true) {
            LazyColumn(
              contentPadding = PaddingValues(start = 24.dp, end = 24.dp)
            ) {
              items(incomesMap.toList()) { year ->
                YearRow(year = year.first)
                year.second.map { month ->
                  Card(
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                      .fillMaxWidth()
                      .wrapContentHeight()
                      .padding(bottom = 22.dp, start = 12.dp, end = 12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
                  ) {
                    Column(
                      modifier = Modifier.padding(
                        start = 12.dp,
                        end = 12.dp,
                        top = 12.dp,
                        bottom = 6.dp
                      )
                    ) {
                      MonthRow(
                        monthTotal = getMonthTotal(totalByMonth, month.key, year.first),
                        incomeMonth = month.key
                      )
                      month.value.map { income ->
                        IncomeItem(
                          items = income.value ?: emptyList(),
                          fortnight = income.key,
                          onNavigate = onNavigate
                        )
                      }
                    }
                  }
                }
              }
            }
          } else {
            Column(
              horizontalAlignment = Alignment.CenterHorizontally,
              verticalArrangement = Arrangement.Center,
              modifier = Modifier.padding(start = 12.dp, end = 12.dp)
            ) {
              Text(
                text = stringResource(id = R.string.income_empty_list),
                style = MaterialTheme.typography.headlineLarge
              )
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
}

@Composable
fun IncomeItem(
  items: List<Income>,
  fortnight: String,
  onNavigate: (navigateEvent: NavigateEvent, income: NavigationIncomeDetails) -> Unit
) {
  Column(
    modifier = Modifier.clickable {
      onNavigate(
        NavigateEvent.NavigateIncomeExpensesList,
        NavigationIncomeDetails(
          paymentDate = items[0].paymentDate.date
        )
      )
    }
  ) {
    Text(text = stringResource(id = R.string.income_fortnight, fortnight))
    if (items.size == 1) {
      IncomeItemRow(item = items[0], false)
    } else {
      Column {
        items.map { item ->
          IncomeItemRow(item = item, renderIcon = false)
        }
      }
    }
  }
}

@Composable
fun IncomeItemRow(item: Income, renderIcon: Boolean = true) {
  Row(
    modifier = Modifier
      .padding(top = 6.dp, bottom = 6.dp)
      .fillMaxWidth(),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.SpaceBetween
  ) {
    Text(
      text = item.paymentDate.date?.formatDateDaysWithMonth() ?: "",
      style = MaterialTheme.typography.titleMedium,
      textAlign = TextAlign.End
    )
    Text(text = item.total.formatMoney(), style = MaterialTheme.typography.titleMedium)
    if (renderIcon) {
      Icon(Icons.Filled.KeyboardArrowRight, "")
    }
  }
}

@Composable
fun YearRow(year: String) {
  Row(
    modifier = Modifier
      .padding(top = 12.dp, bottom = 4.dp, start = 24.dp)
      .fillMaxWidth(),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.Start
  ) {
    Text(text = year, style = MaterialTheme.typography.titleLarge)
  }
}

@Composable
fun MonthRow(monthTotal: Double, incomeMonth: String) {
  Row(
    modifier = Modifier
      .padding(top = 6.dp, bottom = 6.dp)
      .fillMaxWidth(),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.SpaceBetween
  ) {
    Text(text = incomeMonth, style = MaterialTheme.typography.titleMedium)
    Text(text = monthTotal.formatMoney(), style = MaterialTheme.typography.titleMedium)
  }
}

@Composable
fun FabAddButtons(
  onNavigateAddIncome: () -> Unit = {},
  onNavigateAddExpense: () -> Unit = {}
) {
  var expanded by remember {
    mutableStateOf(false)
  }
  val rotation = remember {
    Animatable(0f)
  }
  val scope = rememberCoroutineScope()

  Column(
    modifier = Modifier
      .wrapContentHeight()
      .wrapContentWidth()
      .animateContentSize()
      .padding(bottom = 8.dp, end = 4.dp),
    horizontalAlignment = Alignment.End
  ) {
    if (expanded) {
      ExtendedFloatingActionButton(
        elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation(1.dp),
        onClick = { onNavigateAddIncome() }
      ) {
        Icon(painterResource(id = R.drawable.round_account_balance_24), contentDescription = "")
        Text(text = stringResource(id = R.string.fab_add_income))
      }
      Spacer(modifier = Modifier.height(8.dp))
      ExtendedFloatingActionButton(
        elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation(1.dp),
        onClick = { onNavigateAddExpense() }
      ) {
        Icon(painterResource(id = R.drawable.round_attach_money_24), contentDescription = "")
        Text(text = stringResource(id = R.string.fab_add_expense))
      }
    }

    Spacer(modifier = Modifier.height(8.dp))
    Row(modifier = Modifier.wrapContentWidth(), horizontalArrangement = Arrangement.End) {
      FloatingActionButton(
        elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation(
          defaultElevation = 2.dp
        ),
        onClick = {
          expanded = !expanded
          scope.launch {
            if (expanded) {
              animateRotation(rotation, 45f, 800)
            } else {
              animateRotation(rotation, 0f, 800)
            }
          }
        }
      ) {
        Icon(
          Icons.Rounded.Add,
          contentDescription = "",
          modifier = Modifier.rotate(rotation.value)
        )
      }
    }
  }
}

fun getMonthTotal(totalByMonth: List<Total?>, month: String, year: String) =
  totalByMonth.find { totalByMont ->
    totalByMont?.date?.formatDateOnlyMonth()
      ?.uppercase(Locale.ROOT) == month && totalByMont.year == year
  }?.total ?: 0.0

suspend fun animateRotation(
  rotation: Animatable<Float, AnimationVector1D>,
  target: Float,
  durationMillis: Int
) {
  rotation.animateTo(
    targetValue = target,
    animationSpec = tween(durationMillis, easing = LinearEasing)
  )
}

@Preview
@Composable
fun FABPrev() {
  Scaffold(floatingActionButton = {
    FabAddButtons()
  }) {
    Surface(modifier = Modifier.padding(it)) {
    }
  }
}
