package com.avocado.expensescompose.data.model

typealias SimpleResource = MyResult<Unit>

sealed class MyResult<out R> {
    data class Success<out T>(val data: T) : MyResult<T>()
    data class Error<out T>(val data: T? = null, val uiText: String?) : MyResult<T>()
}
