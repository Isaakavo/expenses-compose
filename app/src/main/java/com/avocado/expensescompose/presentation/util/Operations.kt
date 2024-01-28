package com.avocado.expensescompose.presentation.util

import android.util.Log

enum class Operations {
  UPDATE,
  DELETE,
  ADD,
  SUCCESS_LOGIN,
  FETCH,
  NONE
}

fun validateOperation(
  operation: String,
  onAdd: () -> Unit = {},
  onUpdate: () -> Unit = {},
  onDelete: () -> Unit = {},
  onFetch: () -> Unit = {},
  onSuccessLogin: () -> Unit = {},
  onAlwaysExecute: () -> Unit = {}
) {
  when (Operations.valueOf(operation)) {
    Operations.UPDATE -> {
      onUpdate()
      onAlwaysExecute()
    }

    Operations.DELETE -> {
      onDelete()
      onAlwaysExecute()
    }

    Operations.ADD -> {
      onAdd()
      onAlwaysExecute()
    }

    Operations.SUCCESS_LOGIN -> {
      onSuccessLogin()
      onAlwaysExecute()
    }

    Operations.FETCH -> {
      onFetch()
      onAlwaysExecute()
    }

    Operations.NONE -> Log.i("Operations", "None operation")
  }
}
