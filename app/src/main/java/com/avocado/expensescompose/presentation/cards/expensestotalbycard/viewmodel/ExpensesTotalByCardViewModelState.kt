package com.avocado.expensescompose.presentation.cards.expensestotalbycard.viewmodel

import com.avocado.expensescompose.data.model.total.Total
import com.avocado.expensescompose.data.model.total.TotalFortnight

data class ExpensesTotalByCardViewModelState(
  val totalByMonthList: List<Total> = emptyList(),
  val totalByFortnight: List<TotalFortnight> = emptyList(),
  val cardBank: String = "",
  val cardAlias: String = "",
  val dataSelector: DataSelector = DataSelector.FORTNIGHT,
  val uiError: Int? = 0,
  val isDeleted: Boolean = false
)
