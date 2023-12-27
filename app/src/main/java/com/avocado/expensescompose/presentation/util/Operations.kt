package com.avocado.expensescompose.presentation.util

import android.util.Log

enum class Operations {
  UPDATE,
  DELETE,
  ADD,
  SUCCESS_LOGIN,
  NONE
}

fun validateOperation(
  operation: String,
  onAdd: () -> Unit = {},
  onUpdate: () -> Unit = {},
  onDelete: () -> Unit = {},
  onSuccessLogin: () -> Unit = {}
) {
  when (Operations.valueOf(operation)) {
    Operations.UPDATE -> {
      onUpdate()
    }

    Operations.DELETE -> {
      onDelete()
    }

    Operations.ADD -> {
      onAdd()
    }

    Operations.SUCCESS_LOGIN -> {
      onSuccessLogin()
    }

    Operations.NONE -> Log.i("Operations", "None operation")
  }
}