package com.avocado.expensescompose.data.model

typealias SimpleResource = MyResult<Unit>

sealed class MyResult<out R> {
  data class Success<out T>(val data: T) : MyResult<T>()
  data class Error<out T>(
    val data: T? = null,
    val uiText: Int? = null,
    var uiErrorText: String? = null,
    val exception: Throwable? = null
  ) : MyResult<T>()
}

suspend fun <D, R> MyResult<D>.successOrError(
  onSuccess: suspend (success: MyResult.Success<D>) -> R,
  onError: suspend (error: MyResult.Error<D>) -> R
): R = when (this) {
  is MyResult.Success -> {
    onSuccess(this)
  }

  is MyResult.Error -> {
    onError(this)
  }
}
