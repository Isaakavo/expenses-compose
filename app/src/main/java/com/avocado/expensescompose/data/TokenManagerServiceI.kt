package com.avocado.expensescompose.data

import com.avocado.expensescompose.data.model.MyResult

interface TokenManagerServiceI {
  suspend fun saveAccessToken(value: String): MyResult<Unit>
  suspend fun getAccessToken(): MyResult<String?>
  suspend fun deleteAccessToken(): MyResult<Unit>
  suspend fun saveRefreshToken(value: String): MyResult<Unit>
  suspend fun getRefreshToken(): MyResult<String?>
  suspend fun deleteRefreshToken(): MyResult<Unit>
}
