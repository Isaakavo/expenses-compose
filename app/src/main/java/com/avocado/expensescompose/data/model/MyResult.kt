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

inline fun <T, R> MyResult<T>.map(transform: (T) -> R): MyResult<R> = when (this) {
  is MyResult.Success -> MyResult.Success(transform(data))
  is MyResult.Error -> MyResult.Error(exception = exception)
}

inline fun <T, R> MyResult<T>.fold(
  onSuccess: (T) -> R,
  onError: (Throwable?) -> R
): R = when (this) {
  is MyResult.Success -> onSuccess(data)
  is MyResult.Error -> onError(exception)
}

// TODO create more utility functions for MyResult, like in Result.kt
