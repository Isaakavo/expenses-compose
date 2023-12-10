package com.avocado.expensescompose.domain.expense

import com.apollographql.apollo3.api.Optional
import com.avocado.CreateExpenseMutation
import com.avocado.expensescompose.data.adapters.graphql.fragments.toExpense
import com.avocado.expensescompose.data.adapters.graphql.scalar.Date
import com.avocado.expensescompose.data.apolloclients.GraphQlClient
import com.avocado.expensescompose.data.model.MyResult
import com.avocado.expensescompose.data.model.expense.Expense
import com.avocado.expensescompose.domain.tags.models.Tag
import com.avocado.expensescompose.presentation.util.formatDateToISO
import com.avocado.type.CreateExpenseInput
import com.avocado.type.ExpenseTagInput
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDateTime

class CreateExpenseUseCase(private val graphQlClient: GraphQlClient) {
  suspend operator fun invoke(
    concept: String,
    comment: String,
    date: String,
    total: Double,
    tags: List<Tag>,
    cardId: String?
  ): Flow<MyResult<Expense>> {
    //TODO add logic to handle input errors
    return graphQlClient.mutate(
      CreateExpenseMutation(
        CreateExpenseInput(
          concept = concept,
          comment = Optional.present(comment),
          payBefore = Date(date.formatDateToISO() ?: LocalDateTime.now()),
          total = total,
          tags = tags.map { ExpenseTagInput(name = Optional.present(it.name)) },
          cardId = Optional.present(cardId ?: "")
        )
      )
    ).map {
      val data = it.data?.createExpense?.expenseFragment?.toExpense()
      if (data != null) {
        MyResult.Success(data = data)
      } else {
        MyResult.Error(data = null, uiText = null)
      }
    }

  }
}