package com.avocado.expensescompose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.avocado.expensescompose.presentation.expenses.ExpensesScreen
import com.avocado.expensescompose.presentation.login.LoginScreen
import com.avocado.expensescompose.ui.theme.ExpensesComposeTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ExpensesComposeTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // TODO implement navigation and remove call back in login
                    LoginScreen() {
                        if (it) {
                            ExpensesScreen()
                        }
                    }
                }
            }
        }
    }
}
