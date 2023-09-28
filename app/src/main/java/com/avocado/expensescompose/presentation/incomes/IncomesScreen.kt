package com.avocado.expensescompose.presentation.incomes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IncomesScreen() {
    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(start = 16.dp, end = 16.dp)
        ) {
            Card(
                onClick = { /*TODO*/ },
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            ) {
                Text(text = "Hola mundo", modifier = Modifier.padding(12.dp))
            }
        }
    }
}