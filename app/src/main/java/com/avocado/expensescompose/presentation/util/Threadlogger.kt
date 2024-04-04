package com.avocado.expensescompose.presentation.util

import timber.log.Timber

fun logWithThread(message: String) = Timber.i("thread name ${Thread.currentThread().name} thread ID: ${Thread.currentThread().id} -> $message")
