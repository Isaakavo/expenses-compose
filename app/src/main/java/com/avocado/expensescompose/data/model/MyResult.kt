package com.avocado.expensescompose.data.model

import com.avocado.expensescompose.R

typealias SimpleResource = MyResult<Unit>

sealed class MyResult<out R> {
  data class Success<out T>(val data: T) : MyResult<T>()
  data class Error<out T>(
    val data: T? = null,
    val uiText: Int = R.string.general_error,
    var uiErrorText: String? = null,
    val exception: Throwable? = null
  ) : MyResult<T>()
}

inline fun <reified D, reified R> MyResult<D>.successOrError(
  onSuccess: (success: MyResult.Success<D>) -> R,
  onError: (error: MyResult.Error<D>) -> R
): R = when (this) {
  is MyResult.Success -> {
    Result
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

inline fun <T> MyResult<T>.onError(block: (T?, Throwable?, Int) -> Unit): MyResult<T> {
  if (this is MyResult.Error) block(data, exception, uiText)
  return this
}

inline fun <T, R> MyResult<T>.map(transform: (T) -> R): MyResult<R> = when (this) {
  is MyResult.Success -> MyResult.Success(transform(data))
  is MyResult.Error -> MyResult.Error(exception = exception, uiText = uiText)
}

inline fun <T, R> MyResult<T>.fold(
  onSuccess: (T) -> R,
  onError: (Throwable?) -> R
): R = when (this) {
  is MyResult.Success -> onSuccess(data)
  is MyResult.Error -> onError(exception)
}

fun <T> MyResult<T>.isSuccess(): Boolean = this is MyResult.Success
fun <T> MyResult<T>.isError(): Boolean = this is MyResult.Error

inline fun <T, R> MyResult<T>.flatMapSuccess(transform: (T) -> MyResult<R>): MyResult<R> =
  when (this) {
    is MyResult.Success -> transform(data)
    is MyResult.Error -> MyResult.Error(data = null, uiText = uiText, exception = exception)
  }

// TODO create more utility functions for MyResult, like in Result.kt
