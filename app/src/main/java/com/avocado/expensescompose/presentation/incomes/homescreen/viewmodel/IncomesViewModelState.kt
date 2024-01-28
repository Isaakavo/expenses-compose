package com.avocado.expensescompose.presentation.incomes.homescreen.viewmodel

import com.avocado.expensescompose.data.model.total.Total
import com.avocado.expensescompose.domain.income.models.Income

data class IncomesViewModelState(
  val incomesMap: Map<String, MutableMap<String, MutableMap<String, MutableList<Income>?>>>? = null,
  val totalByMonth: List<Total?> = emptyList(),
  val showAddButtons: Boolean = false,
  val backPressState: BackPress = BackPress.Idle,
  val showToast: Boolean = false,
  val isLoading: Boolean = false,
  val uiError: Int = 0,
  val errorMessage: Int? = 0
)
