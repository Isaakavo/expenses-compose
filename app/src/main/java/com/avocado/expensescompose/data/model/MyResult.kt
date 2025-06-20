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

inline fun <reified D, reified R> MyResult<D>.successOrError(
  onSuccess: (success: MyResult.Success<D>) -> R,
  onError: (error: MyResult.Error<D>) -> R
): R = when (this) {
  is MyResult.Success -> {
    onSuccess(this)
  }

  is MyResult.Error -> {
    onError(this)
  }
}

inline fun <T> MyResult<T>.onSuccess(block: (T) -> Unit): MyResult<T> {
  if (this is MyResult.Success) block(data)
  return this
}

inline fun <T> MyResult<T>.onError(block: (Throwable) -> Unit): MyResult<T> {
  if (this is MyResult.Error) exception?.let { block(it) }
  return this
}
