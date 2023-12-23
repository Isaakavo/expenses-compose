package com.avocado.expensescompose.presentation.cards.expensestotalbycard

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.avocado.expensescompose.data.adapters.adapt
import com.avocado.expensescompose.data.adapters.formatMoney
import com.avocado.expensescompose.data.model.total.Total
import com.avocado.expensescompose.data.model.total.TotalFortnight
import com.avocado.expensescompose.presentation.navigation.NavigateEvent
import com.avocado.expensescompose.presentation.topbar.AppBar
import com.avocado.expensescompose.presentation.topbar.MenuItems
import com.avocado.expensescompose.presentation.util.formatDateMonthWithYear
import com.avocado.expensescompose.presentation.util.getLastDayOfMonth
import com.avocado.expensescompose.presentation.util.prepareDateForRequest

@Composable
fun ExpensesTotalByCardScreen(
  viewModel: ExpensesTotalByCardViewModel = hiltViewModel(),
  cardId: String,
  onPopBackStack: () -> Unit = {},
  onNavigate: (navigateEvent: NavigateEvent, param: String) -> Unit,
) {
  val state by viewModel.state.collectAsStateWithLifecycle()
  LaunchedEffect(key1 = Unit) {
    viewModel.fetchData(cardId)
  }

  CardWithExpenseContent(
    totalByMonth = state.totalByMonthList,
    totalByFortnight = state.totalByFortnight,
    cardId = cardId,
    cardAlias = state.cardAlias,
    cardBank = state.cardBank,
    uiError = state.uiError,
    openDropDownMenu = state.openDropDownMenu,
    dataSelector = state.dataSelector,
    onPopBackStack = onPopBackStack,
    onEvent = viewModel::onEvent,
    onNavigate = onNavigate
  )
}

@Composable
fun CardWithExpenseContent(
  totalByMonth: List<Total>,
  totalByFortnight: List<TotalFortnight>,
  cardId: String,
  cardAlias: String,
  cardBank: String,
  uiError: String,
  openDropDownMenu: Boolean,
  dataSelector: DataSelector,
  onPopBackStack: () -> Unit = {},
  onEvent: (event: ExpensesTotalByCardEvent) -> Unit,
  onNavigate: (navigateEvent: NavigateEvent, param: String) -> Unit
) {

  Scaffold(
    topBar = {
      AppBar(
        title = cardAlias.ifEmpty { cardBank },
        dropDownMenuItems = listOf(
          MenuItems(
            text = "Quincenal",
            action = { onEvent(ExpensesTotalByCardEvent.FortnightData) }
          ),
          MenuItems(
            text = "Mensual",
            action = { onEvent(ExpensesTotalByCardEvent.MonthData) }
          ),
        ),
        onNavigationIconClick = { onPopBackStack() })
    }
  ) { paddingValues ->
    Surface(
      modifier = Modifier
        .padding(paddingValues)
        .fillMaxSize()
    ) {
      if (uiError.isNotEmpty()) {
        Column(
          modifier = Modifier.fillMaxSize(),
          verticalArrangement = Arrangement.Center,
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Text(text = uiError, fontSize = 26.sp)
        }
      } else {
        Column(
          modifier = Modifier
            .fillMaxSize()
            .padding(top = 8.dp, start = 16.dp, end = 16.dp, bottom = 12.dp),
          verticalArrangement = Arrangement.spacedBy(22.dp)
        ) {
          when (dataSelector) {
            DataSelector.FORTNIGHT -> {
              TotalByFortnight(
                totalByFortnight = totalByFortnight,
                cardId = cardId,
                dataSelector = dataSelector,
                onNavigate = onNavigate
              )
            }

            DataSelector.MONTH -> {
              TotalByMonth(
                totalByMonth = totalByMonth,
                cardId = cardId,
                dataSelector = dataSelector,
                onNavigate = onNavigate
              )
            }
          }
        }
      }
    }
  }
}

@Composable
fun TotalByFortnight(
  totalByFortnight: List<TotalFortnight>,
  cardId: String,
  dataSelector: DataSelector,
  onNavigate: (navigateEvent: NavigateEvent, param: String) -> Unit
) {
  LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
    items(totalByFortnight) { item ->
      Card(modifier = Modifier
        .fillMaxWidth()
        .clickable {
          onNavigate(
            NavigateEvent.NavigateExpensesByCardScreen,
            "${item.prepareDateForRequest()}/$cardId/$dataSelector"
          )
        }) {
        Column(modifier = Modifier.padding(12.dp)) {
          Row(modifier = Modifier.fillMaxWidth()) {
            Text(
              text = item.date?.formatDateMonthWithYear() ?: "",
              fontSize = 22.sp,
              fontWeight = FontWeight.Bold
            )

          }
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text(text = item.fortnight?.adapt().orEmpty())
            Text(text = item.total?.formatMoney().orEmpty())
          }
        }
      }
    }
  }
}

@Composable
fun TotalByMonth(
  totalByMonth: List<Total>,
  cardId: String,
  dataSelector: DataSelector,
  onNavigate: (navigateEvent: NavigateEvent, param: String) -> Unit
) {
  LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
    items(totalByMonth) { item ->
      Card(modifier = Modifier
        .fillMaxWidth()
        .clickable {
          onNavigate(
            NavigateEvent.NavigateExpensesByCardScreen,
            "${item.date?.getLastDayOfMonth()}/$cardId/$dataSelector"
          )
        }) {
        Column(modifier = Modifier.padding(12.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text(
              text = item.date?.formatDateMonthWithYear() ?: "",
              fontSize = 22.sp,
              fontWeight = FontWeight.Bold
            )
            Text(text = item.total?.formatMoney().orEmpty())
          }
        }
      }
    }
  }
}
