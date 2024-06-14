package com.avocado.expensescompose.presentation.incomes.incomewithexpense.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import co.yml.charts.common.model.PlotType
import co.yml.charts.ui.piechart.models.PieChartData
import com.avocado.expensescompose.R
import com.avocado.expensescompose.data.adapters.adapt
import com.avocado.expensescompose.data.model.expense.Expense
import com.avocado.expensescompose.presentation.charts.Charts
import com.avocado.expensescompose.presentation.charts.configs.color
import com.avocado.expensescompose.presentation.charts.configs.defaultDonutChartConfig
import com.avocado.expensescompose.presentation.expenses.allexpenses.AllExpensesListScreen
import com.avocado.expensescompose.presentation.expenses.allexpenses.LocalExpensesListState
import com.avocado.expensescompose.presentation.incomes.incomewithexpense.IncomeWithExpenseEvent
import com.avocado.expensescompose.presentation.incomes.incomewithexpense.IncomeWithExpenseScreenType
import com.avocado.expensescompose.presentation.navigation.NavigateEvent
import com.avocado.expensescompose.presentation.shared.CustomScaffold
import com.avocado.expensescompose.presentation.shared.DeleteAlertDialog
import com.avocado.expensescompose.presentation.shared.topbar.AppBar
import com.avocado.expensescompose.presentation.shared.topbar.MenuItems
import com.avocado.expensescompose.presentation.util.Operations
import com.avocado.type.Category
import timber.log.Timber

@Composable
fun IncomeWithExpensesContent(
  incomeId: String,
  incomesTotal: Double,
  fortnight: String,
  month: String,
  remaining: Double,
  expended: Double,
  paymentDate: String,
  isLoading: Boolean = false,
  shouldDeleteIncome: Boolean = false,
  shouldDeleteExpense: Boolean = false,
  screenType: IncomeWithExpenseScreenType,
  onNavigateBack: () -> Unit = {},
  onNavigate: (navigateEvent: NavigateEvent, operation: String) -> Unit = { one, two -> },
  onEditIncome: (navigateEvent: NavigateEvent, incomeId: String) -> Unit = { one, two -> },
  onEvent: (event: IncomeWithExpenseEvent, param: String) -> Unit = { one, two -> }
) {
  var expensesListState by remember<MutableState<List<Expense>>> {
    mutableStateOf(emptyList())
  }

  val listState = rememberLazyListState()

  CustomScaffold(
    topBar = {
      AppBar(
        title = stringResource(id = R.string.income_fortnight, fortnight),
        onNavigationIconClick = { onNavigateBack() },
        dropDownMenuItems = listOf(
          MenuItems(
            text = stringResource(id = R.string.appbar_edit),
            icon = Icons.Rounded.Edit,
            action = {
              // Hardcoding the first id for the income
              Timber.d("Clicking on edit")
              onEditIncome(NavigateEvent.NavigationEditIncomeScreen, incomeId)
            }
          ),
          MenuItems(
            text = stringResource(id = R.string.appbar_delete),
            icon = Icons.Rounded.Delete,
            action = { onEvent(IncomeWithExpenseEvent.DeleteIncome, "") }
          ),
          MenuItems(
            text = stringResource(id = R.string.appbar_add_expense),
            icon = Icons.Rounded.Add,
            action = {
              onNavigate(
                NavigateEvent.NavigateAddExpenseScreen,
                Operations.FETCH.name
              )
            }
          )
        )
      )
    }
  ) {
    CompositionLocalProvider(value = LocalExpensesListState provides expensesListState) {
      Column(
        modifier = Modifier
          .fillMaxSize()
          .padding(top = 8.dp, start = 16.dp, end = 16.dp, bottom = 12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        when {
          isLoading -> {
            Column(
              modifier = Modifier
                .fillMaxSize()
                .padding(22.dp)
            ) {
              CircularProgressIndicator(strokeWidth = 6.dp)
            }
          }

          screenType == IncomeWithExpenseScreenType.INCOME_CHART -> {
            val incomeData = PieChartData(
              plotType = PlotType.Donut,
              slices = listOf(
                PieChartData.Slice(label = "Income", incomesTotal.toFloat(), Color(0xFF5F0A87)),
                PieChartData.Slice(label = "Remaining", remaining.toFloat(), Color(0xFFF53844))
              )
            )
            Charts(data = incomeData, chartConfig = defaultDonutChartConfig(backgroundColor = MaterialTheme.colorScheme.background, textColor = MaterialTheme.colorScheme.primary))
            AllExpensesListScreen(payBeforeInput = paymentDate, onNavigate = onNavigate)
          }

          // TODO add logic to switch screen type, add a button to switch between expenses charts and incomes charts
          screenType == IncomeWithExpenseScreenType.EXPENSES_CHART -> {
            if (expensesListState.isNotEmpty()) {
              val expensesListForChart = generateExpensesMap(expensesListState)
              val expensesCategoryData = PieChartData(
                plotType = PlotType.Pie,
                slices = expensesListForChart.map {
                  PieChartData.Slice(
                    label = stringResource(it.key.adapt()),
                    value = it.value,
                    color = it.key.color()
                  )
                }
              )
              Charts(
                data = expensesCategoryData,
                listState = listState,
                displayChipsLegends = true,
                chartConfig = defaultDonutChartConfig(
                  backgroundColor = MaterialTheme.colorScheme.background,
                  textColor = Color.White
                )
              )
            }
            AllExpensesListScreen(payBeforeInput = paymentDate, onNavigate = onNavigate) {
              expensesListState = it
            }
          }

          screenType == IncomeWithExpenseScreenType.LIST -> {
            DeleteAlertDialog(
              shouldDisplay = shouldDeleteIncome || shouldDeleteExpense,
              deleteMessage = stringResource(
                id = if (shouldDeleteIncome) R.string.income_expense_delete_income else R.string.income_expense_delete_expense
              ),
              onConfirmRequest = {
                if (shouldDeleteIncome) {
                  onEvent(
                    IncomeWithExpenseEvent.ConfirmDeleteIncome,
                    incomeId
                  )
                } else {
                  onEvent(IncomeWithExpenseEvent.ConfirmDeleteExpense, "")
                }
              },
              onDismissRequest = { onEvent(IncomeWithExpenseEvent.CancelDeleteIncome, "") }
            )

            IncomeDetails(
              incomesTotal = incomesTotal,
              remaining = remaining,
              expended = expended,
              month = month
            ) {
              onEvent(IncomeWithExpenseEvent.Charts, IncomeWithExpenseScreenType.EXPENSES_CHART.name)
            }
            AllExpensesListScreen(payBeforeInput = paymentDate, onNavigate = onNavigate) {
              expensesListState = it
            }
          }
        }
      }
    }
  }
}

fun generateExpensesMap(expensesListState: List<Expense>): Map<Category, Float> {
  val expensesMap = mutableMapOf<Category, Float>()
  for (expense in expensesListState) {
    val previousExpense = expensesMap[expense.category]
    expensesMap[expense.category] = if (previousExpense != null) (expense.total + previousExpense).toFloat() else expense.total.toFloat()
  }

  return expensesMap
}
