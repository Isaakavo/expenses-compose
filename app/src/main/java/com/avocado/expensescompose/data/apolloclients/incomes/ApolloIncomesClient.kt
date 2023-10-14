package com.avocado.expensescompose.data.apolloclients.incomes

import android.util.Log
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.Optional
import com.apollographql.apollo3.exception.ApolloException
import com.avocado.HomeScreenAllIncomesQuery
import com.avocado.CreateIncomeMutation
import com.avocado.IncomeByIdWithExpensesListQuery
import com.avocado.expensescompose.data.adapters.formatDateToISO
import com.avocado.expensescompose.data.adapters.graphql.scalar.Date
import com.avocado.expensescompose.data.model.MyResult
import com.avocado.expensescompose.domain.income.IncomesClient
import com.avocado.expensescompose.domain.income.models.Fortnight
import com.avocado.expensescompose.domain.income.models.Income
import com.avocado.expensescompose.domain.income.models.IncomeWithExpenses
import com.avocado.expensescompose.domain.income.models.Incomes
import com.avocado.expensescompose.domain.income.models.PaymentDate
import com.avocado.type.IncomesAndExpensesByFortnightInput
import java.time.LocalDateTime

class ApolloIncomesClient(private val apolloClient: ApolloClient) : IncomesClient {
  override suspend fun getIncomesByMonth(date: String): List<Income> =
    listOf(Income(paymentDate = PaymentDate(date = null)))


  override suspend fun getAllIncomes(): MyResult<Incomes> {
    try {
      val responseIncome = apolloClient.query(HomeScreenAllIncomesQuery()).execute().data
      val incomesList = responseIncome?.incomesList?.incomes?.map { item ->
        item.toIncome()
      }
      val totalByMonth = responseIncome?.incomesList?.totalByMonth?.map {
        it.toTotalByMonth()
      }

      return MyResult.Success(
        Incomes(
          incomesList = incomesList ?: emptyList(),
          totalByMonth = totalByMonth ?: emptyList(),
          total = responseIncome?.incomesList?.total ?: 0.0
        )
      )
    } catch (e: ApolloException) {
      return MyResult.Error(uiText = e.message)
    }

  }

  override suspend fun getIncomeByIdWithExpenses(
    incomeId: String,
    payBefore: String
  ): MyResult<IncomeWithExpenses> {
    return try {
      val input = IncomesAndExpensesByFortnightInput(
        incomeId = incomeId,
        payBefore = payBefore.formatDateToISO()?.let { Date(it) } ?: Date(LocalDateTime.now())
      )
      val incomeWithExpenses = apolloClient.query(IncomeByIdWithExpensesListQuery(input))
        .execute().data?.incomeAndExpensesByFortnight
      val income = incomeWithExpenses?.income?.toIncome()
      val expensesList = incomeWithExpenses?.expenses?.map { expense ->
        expense.toExpense()
      }
      return MyResult.Success(
        IncomeWithExpenses(
          income = income ?: Income(paymentDate = PaymentDate(null)),
          expensesList = expensesList ?: emptyList(),
          expensesTotal = incomeWithExpenses?.expensesTotal ?: 0.0
        )
      )
    } catch (e: ApolloException) {
      MyResult.Error(uiText = e.message)
    }
  }


  override suspend fun insertIncome(
    total: Double,
    paymentDate: LocalDateTime,
    comment: String,
  ): MyResult<Income?> {
    try {
      val insertedIncome = apolloClient.mutation(
        CreateIncomeMutation(
          total = total,
          paymentDate = Date(paymentDate),
          comment = Optional.present(comment)
        )
      ).execute().data?.createIncome
        ?: return MyResult.Error(uiText = "Error extracting the response")

      return MyResult.Success(
        Income(
          id = insertedIncome.id,
          userId = insertedIncome.userId,
          total = insertedIncome.total,
          paymentDate = PaymentDate(
            date = insertedIncome.paymentDate.date.date,
            fortnight = Fortnight.valueOf(insertedIncome.paymentDate.fortnight.name)
          )
        )
      )
    } catch (e: ApolloException) {
      Log.d("Apollo Incomes", e.message.toString())
      return MyResult.Error(uiText = e.message.toString())
    }
  }
}