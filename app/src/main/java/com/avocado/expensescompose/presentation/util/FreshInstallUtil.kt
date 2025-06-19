package com.avocado.expensescompose.presentation.util

import android.content.Context

fun isFirstInstall(context: Context): Boolean {
  val packageName = context.packageName
  val packageManager = context.packageManager
  val packageInfo = packageManager.getPackageInfo(packageName, 0)
  return packageInfo.firstInstallTime == packageInfo.lastUpdateTime
}
