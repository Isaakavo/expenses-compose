package com.avocado.expensescompose.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.avocado.expensescompose.presentation.RoutesConstants
import com.avocado.expensescompose.presentation.incomes.IncomesScreen
import com.avocado.expensescompose.presentation.login.LoginScreen

@Composable
fun ExpensesApplication() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "login_screen") {
        composable(RoutesConstants.LOGIN_SCREEN) {
            LoginScreen(navController)
        }

        composable(RoutesConstants.INCOME_OVERVIEW) {
            IncomesScreen() {
                navController.navigate(RoutesConstants.LOGIN_SCREEN) {
                    popUpTo(navController.graph.id) {
                        inclusive = true
                    }
                }
            }
        }
    }
}