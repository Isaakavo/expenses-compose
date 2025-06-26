package com.avocado.expensescompose.data.repositories.login.loginvalidation

import com.avocado.LoginQuery
import com.avocado.expensescompose.data.adapters.graphql.utils.awaitResult
import com.avocado.expensescompose.data.apolloclients.GraphQlClientImpl
import com.avocado.expensescompose.data.model.MyResult
import javax.inject.Inject
import timber.log.Timber

class LoginValidationRepositoryImpl @Inject constructor(
  private val graphQlClientImpl: GraphQlClientImpl
) : LoginValidationRepository<LoginQuery.Data?> {
  override suspend fun validate(): MyResult<LoginQuery.Data?> =
    graphQlClientImpl
      .query(LoginQuery()) { throwable ->
        Timber.e("Error validating login: ${throwable.message}")
      }
      .awaitResult()
}
