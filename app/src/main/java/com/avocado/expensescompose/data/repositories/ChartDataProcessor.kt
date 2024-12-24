package com.avocado.expensescompose.data.repositories

class ChartDataProcessor<T>(private val expensesList: List<T>) {
  fun groupExpenseBy(groupBy: (T) -> String, sumBy: (T) -> Float): Map<String, Float> =
    expensesList.groupBy(groupBy).mapValues { (_, expenses) ->
      expenses.fold(0f) { acc, it -> acc + sumBy(it) }
    }
}
