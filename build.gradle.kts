// Top-level build file where you can add configuration options common to all sub-projects/modules.
android {
  namespace = "com.avocado.expensescompose"
  compileSdk = 34
}

plugins {
  alias(libs.plugins.android.application)
  alias(libs.plugins.kotlin.android)
  alias(libs.plugins.compose.compiler) apply false
  alias(libs.plugins.ksp) apply false
  alias(libs.plugins.hilt) apply false
}