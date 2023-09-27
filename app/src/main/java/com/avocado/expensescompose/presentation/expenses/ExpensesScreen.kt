package com.avocado.expensescompose.presentation.expenses

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.avocado.expensescompose.domain.Expense

@Composable
fun ExpensesScreen(
    state: ExpensesViewModel.ExpensesState
) {
    Box(modifier = Modifier.fillMaxSize() ){
        if (state.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(state.expenses) { expense ->
                    ExpenseItem(expense = expense)
                }
            }
        }
    }
}

@Composable
fun ExpenseItem(
    expense: Expense,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        //TODO handle null values
        Text(text = expense.concept ?: "", fontSize = 30.sp)
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = expense.total.toString() ?: "")
    }
}