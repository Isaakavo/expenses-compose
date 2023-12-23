package com.avocado.expensescompose.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.avocado.expensescompose.presentation.RoutesConstants
import com.avocado.expensescompose.presentation.cards.cardsscreen.CardsScreen
import com.avocado.expensescompose.presentation.cards.expensesbycard.ExpensesByCardScreen
import com.avocado.expensescompose.presentation.cards.expensestotalbycard.DataSelector
import com.avocado.expensescompose.presentation.cards.expensestotalbycard.ExpensesTotalByCardScreen
import com.avocado.expensescompose.presentation.expenses.addexpense.AddExpenseScreen
import com.avocado.expensescompose.presentation.incomes.addscreen.AddIncomeScreen
import com.avocado.expensescompose.presentation.incomes.homescreen.IncomesScreen
import com.avocado.expensescompose.presentation.incomes.incomewithexpense.IncomeExpensesScreen
import com.avocado.expensescompose.presentation.login.LoginScreen

sealed class NavigateEvent {
  object NavigateLogin : NavigateEvent()
  object NavigateIncomeOverview : NavigateEvent()
  object NavigateIncomeExpensesList : NavigateEvent()
  object NavigationAddIncomeScreen : NavigateEvent()
  object NavigationEditIncomeScreen : NavigateEvent()
  object NavigateCardsScreen : NavigateEvent()
  object NavigateAddExpenseScreen : NavigateEvent()
  object NavigateCardsWithExpenseScreen : NavigateEvent()
  object NavigateExpensesByCardScreen : NavigateEvent()
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

    is NavigateEvent.NavigateIncomeOverview -> {
      navController.navigate("${RoutesConstants.INCOME_OVERVIEW}/${param}")
    }

    is NavigateEvent.NavigateIncomeExpensesList -> {
      navController.navigate(
        "${RoutesConstants.INCOME_EXPENSES_LIST}/${param}"
      )
    }

    is NavigateEvent.NavigationAddIncomeScreen -> {
      navController.navigate(RoutesConstants.INCOME_ADD) {
        launchSingleTop = true
        popUpTo("${RoutesConstants.INCOME_OVERVIEW}/${param}") {
          inclusive = true
        }
      }
    }

    is NavigateEvent.NavigationEditIncomeScreen -> {
      navController.navigate("${RoutesConstants.INCOME_ADD}/$param") {
        launchSingleTop = true
        popUpTo("${RoutesConstants.INCOME_OVERVIEW}/${param}") {
          inclusive = true
        }
      }
    }

    is NavigateEvent.NavigateCardsScreen -> {
      navController.navigate(RoutesConstants.CARDS_SCREEN)
    }

    is NavigateEvent.NavigateCardsWithExpenseScreen -> {
      navController.navigate("${RoutesConstants.CARDS_EXPENSE_SCREEN}/${param}")
    }

    is NavigateEvent.NavigateAddExpenseScreen -> {
      navController.navigate(RoutesConstants.EXPENSE_ADD)
    }

    is NavigateEvent.NavigateExpensesByCardScreen -> {
      navController.navigate("${RoutesConstants.EXPENSES_CARD_SCREEN}/$param")
    }
  }
}

@Composable
fun ExpensesApplication() {
  val navController = rememberNavController()
  NavHost(navController = navController, startDestination = "login_screen") {

    // Login Screen
    composable(RoutesConstants.LOGIN_SCREEN) {
      LoginScreen(
        onNavigate = { event, shouldRefresh, isSuccessLogin ->
          if (isSuccessLogin) {
            navigate(event, navController, "$shouldRefresh/$isSuccessLogin")
          }
        }
      )
    }

    // Incomes Screen
    composable(
      "${RoutesConstants.INCOME_OVERVIEW}/{shouldRefresh}/{isSuccessLogin}",
      arguments = listOf(
        navArgument("shouldRefresh") { type = NavType.StringType },
        navArgument("isSuccessLogin") { type = NavType.BoolType }
      )
    ) {
      val shouldRefresh = it.arguments?.getString("shouldRefresh") ?: ""
      val isSuccessLogin = it.arguments?.getBoolean("isSuccessLogin") ?: false
      IncomesScreen(
        shouldRefresh = shouldRefresh,
        isSuccessLogin = isSuccessLogin,
        onNavigate = { navigateEventVal, incomeDetails ->
          navigate(navigateEventVal, navController, incomeDetails?.paymentDate.toString())
        }
      )
    }

    // Incomes and Expenses Screen
    composable(
      "${RoutesConstants.INCOME_EXPENSES_LIST}/{paymentDate}",
      arguments = listOf(
        navArgument("paymentDate") { type = NavType.StringType })
    ) { navBackStackEntry ->
      val paymentDate = navBackStackEntry.arguments?.getString("paymentDate") ?: ""
      IncomeExpensesScreen(
        paymentDate = paymentDate,
        onNavigateBack = {
          navController.popBackStack()
        },
        onNavigate = { event, shouldRefresh, isSuccessLogin ->
          navigate(event, navController, "$shouldRefresh/$isSuccessLogin")
        },
        onEditIncome = { event, incomeId ->
          navigate(event, navController, incomeId)
        }
      )
    }

    // Add Income Screen
    composable(RoutesConstants.INCOME_ADD) {
      AddIncomeScreen(
        onPopBackStack = { navController.popBackStack() },
        onNavigate = { navigateEvent, shouldRefresh, isSuccessLogin ->
          navigate(navigateEvent, navController, "$shouldRefresh/$isSuccessLogin")
        })
    }

    // Edit Income Screen
    composable(
      "${RoutesConstants.INCOME_ADD}/{incomeId}",
      arguments = listOf(navArgument("incomeId") { type = NavType.StringType })
    ) { navBackStackEntry ->
      val incomeId = navBackStackEntry.arguments?.getString("incomeId") ?: ""
      AddIncomeScreen(
        incomeId = incomeId,
        onPopBackStack = { navController.popBackStack() },
        onNavigate = { navigateEvent, shouldRefresh, isSuccessLogin ->
          navigate(navigateEvent, navController, "$shouldRefresh/$isSuccessLogin")
        }
      )
    }

    // Add Expense Screen
    composable(RoutesConstants.EXPENSE_ADD) {
      AddExpenseScreen {
        navController.popBackStack()
      }
    }

    // Cards Screen
    composable(RoutesConstants.CARDS_SCREEN) {
      CardsScreen(
        onPopBackStack = { navController.popBackStack() },
        onNavigate = { event, cardId ->
          navigate(event, navController, cardId)
        }
      )
    }

    // Card Expense Screen
    composable(
      "${RoutesConstants.CARDS_EXPENSE_SCREEN}/{cardId}",
      arguments = listOf(navArgument("cardId") {
        type =
          NavType.StringType
      })
    ) { navBackStackEntry ->
      val cardId = navBackStackEntry.arguments?.getString("cardId") ?: ""
      ExpensesTotalByCardScreen(
        cardId = cardId,
        onPopBackStack = { navController.popBackStack() },
        onNavigate = { event, param ->
          navigate(event, navController, param)
        })
    }

    // Expenses by card Screen
    composable(
      "${RoutesConstants.EXPENSES_CARD_SCREEN}/{payBefore}/{cardId}/{dataSelector}",
      arguments = listOf(
        navArgument("cardId") { type = NavType.StringType },
        navArgument("payBefore") {
          type = NavType.StringType
        },
        navArgument("dataSelector") {
          type = NavType.StringType
        }
      )
    ) { navBackStackEntry ->
      val cardId = navBackStackEntry.arguments?.getString("cardId").orEmpty()
      val payBefore = navBackStackEntry.arguments?.getString("payBefore").orEmpty()
      val dataSelector = navBackStackEntry.arguments?.getString("dataSelector").orEmpty()
      ExpensesByCardScreen(
        cardId = cardId,
        payBefore = payBefore,
        dataSelector = DataSelector.valueOf(dataSelector),
        onPopBackStack = { navController.popBackStack() }
      )
    }
  }
}