package com.avocado.expensescompose.data

interface AuthDataStore {
    suspend fun putString(key: String, value: String)
    suspend fun getString(key: String): String?
}