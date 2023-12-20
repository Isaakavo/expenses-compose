package com.avocado.expensescompose.presentation.incomes.homescreen

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.avocado.expensescompose.R
import com.avocado.expensescompose.presentation.util.formatDateDaysWithMonth
import com.avocado.expensescompose.data.adapters.formatMoney
import com.avocado.expensescompose.data.model.total.Total
import com.avocado.expensescompose.domain.income.models.Income
import com.avocado.expensescompose.presentation.navigation.NavigateEvent
import com.avocado.expensescompose.presentation.topbar.AppBar
import com.avocado.expensescompose.presentation.util.formatDateOnlyMonth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.util.Locale

data class NavigationIncomeDetails(
  val paymentDate: LocalDateTime?
)

@Composable
fun IncomesScreen(
  shouldRefresh: Boolean = false,
  isSuccessLogin: Boolean = false,
  viewModel: IncomesViewModel = hiltViewModel(),
  onNavigate: (navigateEvent: NavigateEvent, income: NavigationIncomeDetails?) -> Unit
) {
  val state by viewModel.state.collectAsStateWithLifecycle()

  LaunchedEffect(key1 = Unit) {
    if (shouldRefresh || isSuccessLogin) {
      viewModel.onEvent(IncomeEvent.FetchIncomes)
    }
  }

  IncomeScreenContent(
    backPressState = state.backPressState,
    isLoading = state.isLoading,
    shouldRefresh = shouldRefresh,
    incomesMap = state.incomesMap,
    totalByMonth = state.totalByMonth,
    showToast = state.showToast,
    onNavigate = onNavigate,
    onEvent = viewModel::onEvent
  )
}

@Composable
fun IncomeScreenContent(
  backPressState: BackPress?,
  isLoading: Boolean,
  shouldRefresh: Boolean,
  incomesMap: Map<String, MutableMap<String, MutableMap<String, MutableList<Income>?>>>?,
  totalByMonth: List<Total?>,
  showToast: Boolean,
  onNavigate: (navigateEvent: NavigateEvent, income: NavigationIncomeDetails?) -> Unit,
  onEvent: (IncomeEvent) -> Unit = {}
) {
  val scope: CoroutineScope = rememberCoroutineScope()
  val drawerState: DrawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
  val snackBarHostState = remember { SnackbarHostState() }

  if (shouldRefresh) {
    LaunchedEffect(key1 = Unit) {
      scope.launch {
        snackBarHostState.showSnackbar("Income added successfully")
      }
    }
  }

  ModalNavigationDrawer(
    drawerState = drawerState,
    drawerContent = {
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
            onClick = { onNavigate(NavigateEvent.NavigateCardsScreen, null) },
            contentPadding = PaddingValues(start = 24.dp)
          ) {
            Icon(
              painter = painterResource(id = R.drawable.baseline_credit_card_24),
              contentDescription = ""
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(text = "Tarjetas", textAlign = TextAlign.Start)
            Spacer(modifier = Modifier.weight(1f))
          }
        }
      }
    }
  ) {
    Scaffold(
      topBar = {
        AppBar(
          title = "Ingresos",
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
      snackbarHost = {
        SnackbarHost(hostState = snackBarHostState)
      },
      floatingActionButton = {
        FabAddIncome {
          onNavigate(NavigateEvent.NavigationAddIncomeScreen, null)
        }
      }
    ) { paddingValues ->
      Surface(
        modifier = Modifier
          .fillMaxSize()
          .padding(paddingValues)
      ) {
        if (isLoading) {
          Column(
            modifier = Modifier.padding(paddingValues),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            //CircularProgressIndicator(strokeWidth = 6.dp)
          }
        } else {
          if (incomesMap?.isNotEmpty() == true) {
            LazyColumn(
              contentPadding = PaddingValues(start = 24.dp, end = 24.dp),
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
                    elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
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
                text = "Aun no tienes ingresos añadidos, no te preocupes, eso se puede solucionar facil",
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
        Toast.makeText(LocalContext.current, "Presiona de nuevo para salir", Toast.LENGTH_LONG)
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
    modifier = Modifier
      .clickable {
        onNavigate(
          NavigateEvent.NavigateIncomeExpensesList, NavigationIncomeDetails(
            paymentDate = items[0].paymentDate.date
          )
        )
      }
  ) {
    Text(text = "$fortnight quincena")
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
fun FabAddIncome(
  onNavigate: () -> Unit
) {
  FloatingActionButton(onClick = { onNavigate() }) {
    Icon(
      Icons.Rounded.Add, contentDescription = ""
    )
  }

}

fun getMonthTotal(totalByMonth: List<Total?>, month: String, year: String) =
  totalByMonth.find { totalByMont ->
    totalByMont?.date?.formatDateOnlyMonth()
      ?.uppercase(Locale.ROOT) == month && totalByMont.year == year
  }?.total ?: 0.0

