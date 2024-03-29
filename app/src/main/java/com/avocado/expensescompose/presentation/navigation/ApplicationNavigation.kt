package com.avocado.expensescompose.presentation.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.EaseIn
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import com.avocado.expensescompose.presentation.cards.expensestotalbycard.ExpensesTotalByCardScreen
import com.avocado.expensescompose.presentation.cards.expensestotalbycard.viewmodel.DataSelector
import com.avocado.expensescompose.presentation.expenses.addexpense.AddExpenseScreen
import com.avocado.expensescompose.presentation.incomes.addscreen.AddIncomeScreen
import com.avocado.expensescompose.presentation.incomes.homescreen.IncomesScreen
import com.avocado.expensescompose.presentation.incomes.incomewithexpense.IncomeExpensesScreen
import com.avocado.expensescompose.presentation.login.LoginScreen
import com.avocado.expensescompose.presentation.util.Operations

sealed class NavigateEvent {
  object NavigateLogin : NavigateEvent()
  object NavigateIncomeOverview : NavigateEvent()
  object NavigateIncomeExpensesList : NavigateEvent()
  object NavigationAddIncomeScreen : NavigateEvent()
  object NavigationEditIncomeScreen : NavigateEvent()
  object NavigateCardsScreen : NavigateEvent()
  object NavigateAddExpenseScreen : NavigateEvent()
  object NavigateEditExpenseScreen : NavigateEvent()
  object NavigateCardsWithExpenseScreen : NavigateEvent()
  object NavigateExpensesByCardScreen : NavigateEvent()
}

private fun <T> navigate(navigateEvent: NavigateEvent, navController: NavController, param: T) {
  when (navigateEvent) {
    NavigateEvent.NavigateLogin -> {
      navController.navigate(RoutesConstants.LOGIN_SCREEN) {
        popUpTo(navController.graph.id) {
          inclusive = true
        }
      }
    }

    NavigateEvent.NavigateIncomeOverview -> {
      navController.navigate("${RoutesConstants.INCOME_OVERVIEW}/$param")
    }

    NavigateEvent.NavigateIncomeExpensesList -> {
      navController.navigate(
        "${RoutesConstants.INCOME_EXPENSES_LIST}/$param"
      )
    }

    NavigateEvent.NavigationAddIncomeScreen -> {
      navController.navigate(RoutesConstants.INCOME_ADD) {
        launchSingleTop = true
        popUpTo("${RoutesConstants.INCOME_OVERVIEW}/$param") {
          inclusive = true
        }
      }
    }

    NavigateEvent.NavigationEditIncomeScreen -> {
      navController.navigate("${RoutesConstants.INCOME_ADD}/$param") {
        launchSingleTop = true
        popUpTo("${RoutesConstants.INCOME_OVERVIEW}/$param") {
          inclusive = true
        }
      }
    }

    NavigateEvent.NavigateCardsScreen -> {
      navController.navigate("${RoutesConstants.CARDS_SCREEN}/$param")
    }

    NavigateEvent.NavigateCardsWithExpenseScreen -> {
      navController.navigate("${RoutesConstants.CARDS_EXPENSE_SCREEN}/$param") {
        launchSingleTop = true
        popUpTo("${RoutesConstants.CARDS_SCREEN}/$param") {
          inclusive = true
        }
      }
    }

    NavigateEvent.NavigateAddExpenseScreen -> {
      navController.navigate(RoutesConstants.EXPENSE_ADD)
    }

    NavigateEvent.NavigateEditExpenseScreen -> {
      navController.navigate("${RoutesConstants.EXPENSE_ADD}/$param")
    }

    NavigateEvent.NavigateExpensesByCardScreen -> {
      navController.navigate("${RoutesConstants.EXPENSES_CARD_SCREEN}/$param")
    }
  }
}

@Composable
fun ExpensesApplication() {
  val navController = rememberNavController()
  NavHost(
    navController = navController,
    startDestination = "login_screen",
    enterTransition = {
      fadeIn(
        animationSpec = tween(
          300,
          easing = LinearEasing
        )
      ) + slideIntoContainer(
        animationSpec = tween(350, easing = EaseIn),
        towards = AnimatedContentTransitionScope.SlideDirection.Start
      )
    },
    exitTransition = {
      fadeOut(
        animationSpec = tween(
          300,
          easing = LinearEasing
        )
      ) + slideOutOfContainer(
        animationSpec = tween(350, easing = EaseOut),
        towards = AnimatedContentTransitionScope.SlideDirection.End
      )
    }
  ) {
    // Login Screen
    composable(RoutesConstants.LOGIN_SCREEN) {
      LoginScreen(
        onNavigate = { event, operation ->
          if (Operations.valueOf(operation) == Operations.SUCCESS_LOGIN) {
            navigate(event, navController, operation)
          }
        }
      )
    }

    // Incomes Screen
    composable(
      "${RoutesConstants.INCOME_OVERVIEW}/{operation}",
      arguments = listOf(
        navArgument("operation") { type = NavType.StringType }
      )
    ) {
      val operation = it.arguments?.getString("operation").orEmpty()
      IncomesScreen(
        operation = operation,
        onNavigate = { navigateEventVal, incomeDetails ->
          navigate(navigateEventVal, navController, incomeDetails?.paymentDate.toString())
        },
        onNavigateCardsScreen = { event, navigateOperation ->
          navigate(event, navController, navigateOperation)
        }
      )
    }

    // Incomes and Expenses Screen
    composable(
      "${RoutesConstants.INCOME_EXPENSES_LIST}/{paymentDate}",
      arguments = listOf(
        navArgument("paymentDate") { type = NavType.StringType }
      )
    ) { navBackStackEntry ->
      val paymentDate = navBackStackEntry.arguments?.getString("paymentDate") ?: ""
      IncomeExpensesScreen(
        paymentDate = paymentDate,
        onNavigateBack = {
          navController.popBackStack()
        },
        onNavigate = { event, operation ->
          navigate(event, navController, operation)
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
        onNavigate = { navigateEvent, operation ->
          navigate(navigateEvent, navController, operation)
        }
      )
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
        onNavigate = { navigateEvent, operation ->
          navigate(navigateEvent, navController, operation)
        }
      )
    }

    // Add Expense Screen
    composable(RoutesConstants.EXPENSE_ADD) {
      AddExpenseScreen {
        navController.popBackStack()
      }
    }

    // Edit Expense Screen
    composable(
      route = "${RoutesConstants.EXPENSE_ADD}/{expenseId}",
      arguments = listOf(
        navArgument("expenseId") {
          type = NavType.StringType
        }
      )
    ) { navBackStackEntry ->
      val expenseId = navBackStackEntry.arguments?.getString("expenseId") ?: ""
      AddExpenseScreen(
        expenseId = expenseId,
        onPopBackStack = { navController.popBackStack() }
      )
    }

    // Cards Screen
    composable(
      "${RoutesConstants.CARDS_SCREEN}/{operation}",
      arguments = listOf(
        navArgument("operation") {
          type = NavType.StringType
        }
      )
    ) { navBackStackEntry ->
      val operation = navBackStackEntry.arguments?.getString("operation").orEmpty()
      CardsScreen(
        operation = operation,
        onPopBackStack = { navController.popBackStack() },
        onNavigate = { event, cardId ->
          navigate(event, navController, cardId)
        }
      )
    }

    // Card Expense Screen
    composable(
      "${RoutesConstants.CARDS_EXPENSE_SCREEN}/{cardId}",
      arguments = listOf(
        navArgument("cardId") {
          type =
            NavType.StringType
        }
      )
    ) { navBackStackEntry ->
      val cardId = navBackStackEntry.arguments?.getString("cardId") ?: ""
      ExpensesTotalByCardScreen(
        cardId = cardId,
        onPopBackStack = { navController.popBackStack() },
        onNavigate = { event, param ->
          navigate(event, navController, param)
        }
      )
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
        onPopBackStack = { navController.popBackStack() },
        onNavigate = { event, param ->
          navigate(event, navController, param)
        }
      )
    }
  }
}
