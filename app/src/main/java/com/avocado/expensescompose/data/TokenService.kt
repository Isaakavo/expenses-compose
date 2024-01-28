package com.avocado.expensescompose.data

import com.avocado.expensescompose.data.model.MyResult

interface TokenService {
  suspend fun saveAccessToken(value: String): MyResult<Boolean>
  suspend fun getAccessToken(): MyResult<String?>
  suspend fun deleteAccessToken(): MyResult<Boolean>
  suspend fun saveRefreshToken(value: String): MyResult<Boolean>
  suspend fun getRefreshToken(): MyResult<String?>
  suspend fun deleteRefreshToken(): MyResult<Boolean>
}
