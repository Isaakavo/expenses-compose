package com.avocado.expensescompose.data.adapters.graphql.utils

import com.apollographql.apollo3.api.ApolloResponse
import com.apollographql.apollo3.api.Operation
import com.apollographql.apollo3.exception.ApolloException
import com.apollographql.apollo3.exception.ApolloHttpException
import com.apollographql.apollo3.exception.ApolloParseException
import com.avocado.expensescompose.R
import com.avocado.expensescompose.data.model.MyResult
import timber.log.Timber

fun <D : Operation.Data> validateData(response: ApolloResponse<D>): MyResult<D?> = try {
  if (response.errors != null && response.errors?.isNotEmpty() == true) {
    val errorMessage = response.errors?.getOrNull(0)
    MyResult.Error(data = null, uiText = 0, uiErrorText = errorMessage?.message)
  } else {
    MyResult.Success(response.data)
  }
} catch (e: ApolloException) {
  val cause = e.cause as? ApolloParseException
  Timber.e(cause?.message)
  MyResult.Error(data = null, uiText = R.string.general_error)
}

fun <D : Operation.Data> validateDataWithoutErrors(
  data: ApolloResponse<D>,
  uiText: Int = R.string.general_error
): MyResult<D> =
  try {
    MyResult.Success(data.dataAssertNoErrors)
  } catch (e: ApolloException) {
    MyResult.Error(data = data.data, uiText = uiText, exception = e)
  } catch (e: ApolloHttpException) {
    Timber.e(e.localizedMessage)
    MyResult.Error(data = data.data, uiText = uiText, exception = e)
  }
