package com.avocado.expensescompose.data.model

typealias SimpleResource = MyResult<Unit>

sealed class MyResult<out R> {
    data class Success<out T>(val data: T) : MyResult<T>()
    data class Error<out T>(val data: T? = null, val uiText: String?) : MyResult<T>()
}

fun <D> MyResult<D>.successOrError(
    onSuccess: (success: MyResult.Success<D>) -> Unit,
    onError: (error: MyResult.Error<D>) -> Unit
) {
  when (this) {
    is MyResult.Success -> {
      onSuccess(this)
    }

    is MyResult.Error -> {
      onError(this)
    }
  }
}