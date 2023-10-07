package com.avocado.expensescompose.domain.login

import com.apollographql.apollo3.api.ApolloResponse
import com.avocado.LoginMutation
import com.avocado.ValidateTokenMutation
import com.avocado.expensescompose.data.ExpensesClient
import com.avocado.expensescompose.data.model.MyResult
import com.avocado.expensescompose.data.model.auth.AuthParameters

class GetLoginUseCase(private val expenseClient: ExpensesClient) {
  suspend fun executeGetToken(
    auth: AuthParameters
  ): MyResult<ApolloResponse<LoginMutation.Data>> =
    MyResult.Success(expenseClient.getLoginToken(auth))

  suspend fun executeValidateToken(token: String): MyResult<ApolloResponse<ValidateTokenMutation.Data>> =
    MyResult.Success(expenseClient.validateToken(token))
}