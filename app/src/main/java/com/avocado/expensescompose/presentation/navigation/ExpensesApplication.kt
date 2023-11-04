package com.avocado.expensescompose.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.avocado.expensescompose.presentation.RoutesConstants
import com.avocado.expensescompose.presentation.cards.CardsScreen
import com.avocado.expensescompose.presentation.incomes.addscreen.AddIncomeScreen
import com.avocado.expensescompose.presentation.incomes.homescreen.IncomesScreen
import com.avocado.expensescompose.presentation.incomes.incomewithexpense.IncomeExpensesScreen
import com.avocado.expensescompose.presentation.login.LoginScreen

sealed class NavigateEvent {
  object NavigateLogin : NavigateEvent()
  object NavigateIncomeExpensesList : NavigateEvent()
  object NavigationAddIncomeScreen : NavigateEvent()
  object NavigateCardsScreen : NavigateEvent()
}

private fun <T> navigate(navigateEvent: NavigateEvent, navController: NavController, param: T) {
  when (navigateEvent) {
    is NavigateEvent.NavigateLogin -> {
      navController.navigate(RoutesConstants.LOGIN_SCREEN) {
        popUpTo(navController.graph.id) {
          inclusive = true
        }
      }
    }

    is NavigateEvent.NavigateIncomeExpensesList -> {
      navController.navigate(
        "${RoutesConstants.INCOME_EXPENSES_LIST}/${param}"
      )
    }

    is NavigateEvent.NavigationAddIncomeScreen -> {
      navController.navigate(RoutesConstants.INCOME_ADD)
    }

    is NavigateEvent.NavigateCardsScreen -> {
      navController.navigate(RoutesConstants.CARDS_SCREEN)
    }
  }
}

@Composable
fun ExpensesApplication() {
  val navController = rememberNavController()
  NavHost(navController = navController, startDestination = "login_screen") {

    composable(RoutesConstants.LOGIN_SCREEN) {
      LoginScreen(navController)
    }

    composable(RoutesConstants.INCOME_OVERVIEW) {
      IncomesScreen(
        onNavigate = { navigateEventVal, incomeDetails ->
          navigate(navigateEventVal, navController, incomeDetails?.paymentDate.toString())
        }
      )
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

    composable(RoutesConstants.CARDS_SCREEN) {
      CardsScreen()
    }
  }
}