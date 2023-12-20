package com.avocado.expensescompose.data.adapters.graphql.utils

import android.util.Log
import com.apollographql.apollo3.api.ApolloResponse
import com.apollographql.apollo3.api.Operation
import com.apollographql.apollo3.exception.ApolloException
import com.avocado.expensescompose.data.model.MyResult

fun <D> validateData(data: D): MyResult<D> = try {
  if (data != null) {
    MyResult.Success(data)
  } else {
    MyResult.Error(data = null, uiText = "")
  }
} catch (e: ApolloException) {
  MyResult.Error(data = null, uiText = "Something went wrong from the server")
}

fun <D : Operation.Data> validateDataWithoutErrors(
  data: ApolloResponse<D>,
  uiText: String = "Algo salio mal Dx"
): MyResult<D> =
  try {
    MyResult.Success(data.dataAssertNoErrors)
  } catch (e: ApolloException) {
    Log.e("VALIDATE_DATA", e.localizedMessage.orEmpty())
    MyResult.Error(data = data.data, uiText = uiText, exception = e)
  }