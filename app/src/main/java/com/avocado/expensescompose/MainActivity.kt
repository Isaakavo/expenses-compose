package com.avocado.expensescompose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.avocado.expensescompose.presentation.navigation.ExpensesApplication
import com.avocado.expensescompose.ui.theme.ExpensesComposeTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ExpensesComposeTheme {
               ExpensesApplication()
            }
        }
    }
}
