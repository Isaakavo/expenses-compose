package com.avocado.expensescompose.data.repositories.login.loginvalidation

import com.avocado.expensescompose.data.model.MyResult

interface LoginValidationRepository<T> {
  suspend fun validate(): MyResult<T>
}
