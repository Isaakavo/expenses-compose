package com.avocado.expensescompose.data.adapters.graphql.utils

import com.apollographql.apollo3.api.ApolloResponse
import com.apollographql.apollo3.api.Operation
import com.apollographql.apollo3.exception.ApolloException
import com.apollographql.apollo3.exception.ApolloHttpException
import com.avocado.expensescompose.R
import com.avocado.expensescompose.data.model.MyResult
import timber.log.Timber

fun <D> validateData(data: D): MyResult<D> = try {
  if (data != null) {
    MyResult.Success(data)
  } else {
    MyResult.Error(data = null, uiText = 0)
  }
} catch (e: ApolloException) {
  MyResult.Error(data = null, uiText = R.string.general_error)
}

fun <D : Operation.Data> validateDataWithoutErrors(
  data: ApolloResponse<D>,
  uiText: Int = R.string.general_error
): MyResult<D> =
  try {
    MyResult.Success(data.dataAssertNoErrors)
  } catch (e: ApolloException) {
    Timber.e(e.localizedMessage.orEmpty())
    MyResult.Error(data = data.data, uiText = uiText, exception = e)
  } catch (e: ApolloHttpException) {
    Timber.e(e.localizedMessage)
    MyResult.Error(data = data.data, uiText = uiText, exception = e)
  }