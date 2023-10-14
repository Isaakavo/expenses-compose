package com.avocado.expensescompose.presentation.incomes.homescreen

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.avocado.expensescompose.R
import com.avocado.expensescompose.data.adapters.formatDateDaysWithMonth
import com.avocado.expensescompose.data.adapters.formatDateOnlyMonth
import com.avocado.expensescompose.domain.income.models.Fortnight
import com.avocado.expensescompose.domain.income.models.Income
import com.avocado.expensescompose.domain.income.models.PaymentDate
import com.avocado.expensescompose.presentation.topbar.AppBar
import kotlinx.coroutines.delay
import java.time.LocalDateTime

sealed class BackPress {
  object Idle : BackPress()
  object InitialTouch : BackPress()
}

@Composable
fun IncomesScreen(
  viewModel: IncomesViewModel = hiltViewModel(),
  onNavigate: (incomeId: String) -> Unit = {},
  onAddIncomeNavigate: () -> Unit = {},
  onLogout: () -> Unit = {}
) {
  val state by viewModel.state.collectAsState()
  //TODO refactor this to use viewmodel
  var backPressState by remember {
    mutableStateOf<BackPress>(BackPress.Idle)
  }
  val context = LocalContext.current

  if (state.showToast) {
    Toast.makeText(context, "Presiona de nuevo para salir", Toast.LENGTH_LONG).show()
    viewModel.updateToast(false)
  }

  LaunchedEffect(key1 = backPressState) {
    if (backPressState == BackPress.InitialTouch) {
      delay(2000)
      backPressState = BackPress.Idle
    }
  }

  BackHandler(true) {
    if (backPressState == BackPress.InitialTouch) {
      onLogout()
    }
    backPressState = BackPress.InitialTouch
    viewModel.updateToast(true)
  }

  IncomeScreenContent(
    state = state, onAddIncomeNavigate = onAddIncomeNavigate, onNavigate = onNavigate
  ) {
    viewModel.onEvent(it)
  }

  if (state.isInvalidSession) {
    Toast.makeText(context, "Por favor, inicia sesión de nuevo", Toast.LENGTH_LONG).show()
    onLogout()
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IncomeScreenContent(
  state: IncomeState,
  onNavigate: (incomeId: String) -> Unit = {},
  onAddIncomeNavigate: () -> Unit = {},
  onEvent: (IncomeEvent) -> Unit = {}
) {
  Scaffold(topBar = {
    AppBar(
      title = "Ingresos",
      icon = Icons.Rounded.Menu,
      buttonText = "Refrescar",
      iconClickAction = {}) {
      onEvent(IncomeEvent.FetchQuery)
    }
  }, floatingActionButton = {
    FabAddIncome {
      onAddIncomeNavigate()
    }
  }) { paddingValues ->
    Surface(
      modifier = Modifier
        .fillMaxSize()
        .padding(paddingValues)
    ) {
      when (state.isLoading) {
        true -> {
          Column(
            modifier = Modifier.padding(paddingValues),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            CircularProgressIndicator(strokeWidth = 6.dp)
          }
        }

        false -> {
          if (state.incomesList.isNotEmpty()) {
            LazyColumn(
              contentPadding = PaddingValues(16.dp),
              verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
              itemsIndexed(state.incomesList) { index, income ->
                // TODO improve dates render
                val currentIncomeMonth = income.paymentDate.date?.formatDateOnlyMonth()
                val currentTotal = state.totalByMonth.find { totalByMont ->
                  totalByMont?.date == currentIncomeMonth
                }

                if (index != 0 && state.incomesList[index - 1].paymentDate.date?.formatDateOnlyMonth() != currentIncomeMonth) {
                  IncomeMonth(
                    monthTotal = currentTotal?.total.toString(),
                    incomeMonth = currentIncomeMonth ?: ""
                  )
                } else if (index == 0) {
                  IncomeMonth(
                    monthTotal = currentTotal?.total.toString(),
                    incomeMonth = currentIncomeMonth ?: ""
                  )
                }
                IncomeItem(item = income, onNavigate = onNavigate)
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
    }
  }
}

@Composable
fun IncomeItem(item: Income, onNavigate: (incomeId: String) -> Unit) {
  Card(
    shape = RoundedCornerShape(16.dp),
    modifier = Modifier
      .fillMaxWidth()
      .wrapContentHeight()
      .padding(start = 8.dp, end = 8.dp)
      .clickable { onNavigate(item.id) },
    elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
  ) {
    Column {
      Row(
        modifier = Modifier
          .padding(top = 12.dp, bottom = 12.dp, start = 24.dp, end = 24.dp)
          .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Text(
          text = item.paymentDate.date?.formatDateDaysWithMonth() ?: "",
          style = MaterialTheme.typography.titleMedium,
          textAlign = TextAlign.End
        )
        Text(text = "$${item.total}", style = MaterialTheme.typography.titleMedium)
        Icon(Icons.Filled.KeyboardArrowRight, "")
      }
    }

  }
}

@Composable
fun IncomeMonth(monthTotal: String, incomeMonth: String) {
  Row(
    modifier = Modifier
      .padding(top = 12.dp, bottom = 12.dp, start = 24.dp, end = 24.dp)
      .fillMaxWidth(),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.SpaceBetween
  ) {
    Text(text = incomeMonth, style = MaterialTheme.typography.headlineSmall)
    Text(text = "$$monthTotal", style = MaterialTheme.typography.headlineSmall)
  }
}

@Composable
fun FabAddIncome(
  onNavigate: () -> Unit
) {
  FloatingActionButton(onClick = { onNavigate() }) {
    Icon(
      imageVector = ImageVector.vectorResource(id = R.drawable.round_paid_24),
      contentDescription = ""
    )
  }

}

@Preview
@Composable
fun IncomeItemPreview() {
  IncomeScreenContent(
    IncomeState(
      showToast = false, incomesList = listOf(
        Income(
          userId = "alskhjdjkas",
          total = 13675.76,
          paymentDate = PaymentDate(date = LocalDateTime.now(), fortnight = Fortnight.FIRST),
          createdAt = LocalDateTime.now()
        )
      )
    )
  )

}