package com.avocado.expensescompose.data.adapters.graphql.utils

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