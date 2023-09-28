package com.avocado.expensescompose.data

import com.avocado.expensescompose.data.model.MyResult

interface AuthDataStore {
    suspend fun putString(key: String, value: String): MyResult<Boolean>
    suspend fun getString(key: String): MyResult<String?>
}