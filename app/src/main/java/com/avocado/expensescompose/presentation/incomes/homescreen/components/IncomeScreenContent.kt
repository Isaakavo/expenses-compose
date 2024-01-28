package com.avocado.expensescompose.presentation.incomes.homescreen.components

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.FilledTonalButton
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
import com.avocado.expensescompose.data.model.total.Total
import com.avocado.expensescompose.domain.income.models.Income
import com.avocado.expensescompose.presentation.incomes.homescreen.NavigationIncomeDetails
import com.avocado.expensescompose.presentation.incomes.homescreen.viewmodel.BackPress
import com.avocado.expensescompose.presentation.incomes.homescreen.viewmodel.IncomeEvent
import com.avocado.expensescompose.presentation.navigation.NavigateEvent
import com.avocado.expensescompose.presentation.topbar.AppBar
import com.avocado.expensescompose.presentation.util.Operations
import com.avocado.expensescompose.presentation.util.getMonthTotal
import com.avocado.expensescompose.presentation.util.validateOperation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

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
