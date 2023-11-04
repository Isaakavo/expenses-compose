package com.avocado.expensescompose.presentation.navigation

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.avocado.expensescompose.presentation.RoutesConstants
import com.avocado.expensescompose.presentation.incomes.addscreen.AddIncomeScreen
import com.avocado.expensescompose.presentation.incomes.homescreen.IncomesScreen
import com.avocado.expensescompose.presentation.incomes.incomewithexpense.IncomeExpensesScreen
import com.avocado.expensescompose.presentation.login.LoginScreen

@Composable
fun ExpensesApplication() {
  val navController = rememberNavController()
  NavHost(navController = navController, startDestination = "login_screen") {
    composable(RoutesConstants.LOGIN_SCREEN) {
      LoginScreen(navController)
    }

    composable(RoutesConstants.INCOME_OVERVIEW) {
      IncomesScreen(
        onNavigate = {
          Log.d("Navigate", "Navigating with $it")
          navController.navigate(
            "${RoutesConstants.INCOME_EXPENSES_LIST}/${it.paymentDate.toString()}"
          )
        },
        onAddIncomeNavigate = {
          navController.navigate(RoutesConstants.INCOME_ADD)
        }) {
        navController.navigate(RoutesConstants.LOGIN_SCREEN) {
          popUpTo(navController.graph.id) {
            inclusive = true
          }
        }
      }
    }

    composable(
      "${RoutesConstants.INCOME_EXPENSES_LIST}/{paymentDate}",
      arguments = listOf(
        navArgument("paymentDate") { type = NavType.StringType })
    ) { navBackStackEntry ->
      val paymentDate = navBackStackEntry.arguments?.getString("paymentDate") ?: ""
      IncomeExpensesScreen(paymentDate = paymentDate) {
        navController.popBackStack()
      }
    }

    composable(RoutesConstants.INCOME_ADD) {
      AddIncomeScreen {
        navController.popBackStack()
      }
    }
  }
}