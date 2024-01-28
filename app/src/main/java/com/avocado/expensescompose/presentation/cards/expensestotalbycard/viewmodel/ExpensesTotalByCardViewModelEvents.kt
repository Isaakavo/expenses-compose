package com.avocado.expensescompose.presentation.cards.expensestotalbycard.viewmodel

sealed class ExpensesTotalByCardEvent {
  object MonthData : ExpensesTotalByCardEvent()
  object FortnightData : ExpensesTotalByCardEvent()
  object DeleteCard : ExpensesTotalByCardEvent()
}

enum class DataSelector {
  FORTNIGHT,
  MONTH
}
