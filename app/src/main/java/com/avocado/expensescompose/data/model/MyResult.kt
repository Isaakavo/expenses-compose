package com.avocado.expensescompose.data.model

sealed class MyResult<out R> {
    data class Success<out T>(val data: T) : MyResult<T>()

    data class Error<out T>(val data: T?, val exception: String?) : MyResult<T>()
}
