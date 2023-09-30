package com.avocado.expensescompose.presentation.incomes

import android.widget.Toast
import androidx.activity.compose.BackHandler
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
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.avocado.expensescompose.data.adapters.formatDateDaysWithMonth
import com.avocado.expensescompose.data.adapters.formatDateOnlyMonth
import com.avocado.expensescompose.data.adapters.formatDateWithYear
import com.avocado.expensescompose.data.model.incomes.Fortnight
import com.avocado.expensescompose.data.model.incomes.Income
import com.avocado.expensescompose.data.model.incomes.PaymentDate
import com.avocado.expensescompose.presentation.topbar.AppBar
import kotlinx.coroutines.delay
import java.time.LocalDateTime

sealed class BackPress {
    object Idle : BackPress()
    object InitialTouch : BackPress()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IncomesScreen(
    viewModel: IncomesViewModel = hiltViewModel(), onLogout: () -> Unit
) {
    val state by viewModel.state.collectAsState()
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

    Scaffold(topBar = {
        AppBar(title = "Ingresos",
            icon = Icons.Rounded.Menu,
            actionIcon = Icons.Rounded.Refresh,
            iconClickAction = {}) {
            viewModel.fetchQuery()
        }
    }) { paddingValues ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (state.isLoading) {
                Column(
                    modifier = Modifier.padding(paddingValues),
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(strokeWidth = 6.dp)
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    itemsIndexed(state.incomes.filterNotNull()) { index, income ->
                        // TODO improve dates render
                        val currentIncomeMonth = income.paymentDate.date.formatDateOnlyMonth()
                        val currentTotal = state.totalByMonth.find { totalByMont ->
                            totalByMont?.date == currentIncomeMonth
                        }

                        if (index != 0 && state.incomes[index - 1]?.paymentDate?.date?.formatDateOnlyMonth() != currentIncomeMonth) {
                            IncomeMonth(
                                monthTotal = currentTotal?.total.toString(),
                                incomeMonth = currentIncomeMonth
                            )
                        } else if (index == 0) {
                            IncomeMonth(
                                monthTotal = currentTotal?.total.toString(),
                                incomeMonth = currentIncomeMonth
                            )
                        }
                        IncomeItem(item = income)
                    }
                }
            }
        }
    }


}

@Composable
fun IncomeItem(item: Income) {
    Card(
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(start = 8.dp, end = 8.dp),
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
                    text = item.paymentDate.date.formatDateDaysWithMonth(),
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

@Preview
@Composable
fun IncomeItemPreview() {
    Surface(modifier = Modifier.fillMaxSize()) {
        IncomeMonth("23124", "Septiembre")
        IncomeItem(
            item = Income(
                userId = "hjkdsfhjkdfshjkdfshijkfgda",
                total = 4500.0,
                createdAt = LocalDateTime.now().formatDateWithYear(),
                paymentDate = PaymentDate(
                    date = LocalDateTime.now(), fortnight = Fortnight.FIRST
                )
            )
        )
    }

}