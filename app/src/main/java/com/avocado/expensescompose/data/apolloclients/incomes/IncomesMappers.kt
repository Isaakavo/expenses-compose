package com.avocado.expensescompose.data.apolloclients.incomes

import com.avocado.HomeScreenAllIncomesQuery
import com.avocado.IncomeByIdWithExpensesListQuery
import com.avocado.expensescompose.domain.income.models.Expense
import com.avocado.expensescompose.domain.income.models.ExpenseTag
import com.avocado.expensescompose.domain.income.models.Fortnight
import com.avocado.expensescompose.domain.income.models.Income
import com.avocado.expensescompose.domain.income.models.IncomeTotalByMonth
import com.avocado.expensescompose.domain.income.models.PaymentDate

fun HomeScreenAllIncomesQuery.Income.toIncome(): Income {
  return Income(
    id = id,
    userId = userId,
    total = incomeFragment.total,
    createdAt = incomeFragment.createdAt?.date,
    paymentDate = PaymentDate(
      date = incomeFragment.paymentDate.date.date,
      fortnight = Fortnight.valueOf(incomeFragment.paymentDate.fortnight.name)
    )
  )
}

fun IncomeByIdWithExpensesListQuery.Income.toIncome(): Income {
  return Income(
    total = incomeFragment.total,
    createdAt = incomeFragment.createdAt?.date,
    paymentDate = PaymentDate(
      date = incomeFragment.paymentDate.date.date,
      fortnight = Fortnight.valueOf(incomeFragment.paymentDate.fortnight.name)
    )
  )
}

fun IncomeByIdWithExpensesListQuery.Expense.toExpense(): Expense {
  return Expense(
    id = id,
    concept = concept,
    comment = comment,
    total = total,
    payBefore = payBefore.date,
    createdAt = createdAt?.date,
    updatedAt = updatedAt?.date,
    tags = tags.map { ExpenseTag(id = it.id, name = it.name) }
  )
}

fun HomeScreenAllIncomesQuery.TotalByMonth.toTotalByMonth(): IncomeTotalByMonth {
  return IncomeTotalByMonth(
    date = date,
    total = total
  )
}


