plugins {
  alias(libs.plugins.android.application)
  alias(libs.plugins.kotlin.android)
  alias(libs.plugins.apollographql)
  alias(libs.plugins.ktlint)
  alias(libs.plugins.compose.compiler)
  alias(libs.plugins.ksp)
  alias(libs.plugins.hilt)
}

apollo {
  service("service") {
    packageName.set("com.avocado")
    mapScalar(
      "Date",
      "com.avocado.expensescompose.data.adapters.graphql.scalar.Date",
      "com.avocado.expensescompose.data.adapters.graphql.scalar.dateAdapter"
    )
  }
}

ktlint {
  android = true
  ignoreFailures = false
  version = "0.47.1"
}

android {
  namespace = "com.avocado.expensescompose"
  compileSdk = 34

  buildFeatures.buildConfig = true

  defaultConfig {
    applicationId = "com.avocado.expensescompose"
    minSdk = 28
    targetSdk = 34
    versionCode = 1
    versionName = "1.0"

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    vectorDrawables {
      useSupportLibrary = true
    }
  }

  buildTypes {
    release {
      isMinifyEnabled = false
      proguardFiles(
        getDefaultProguardFile("proguard-android-optimize.txt"),
        "proguard-rules.pro"
      )
      buildConfigField("String", "GRAPHQL_ENDPOINT", "\"https://expenses-graphql.fly.dev/graphql\"")
      signingConfig = signingConfigs.getByName("debug")
    }

    debug {
      applicationIdSuffix = ".debug"
      isDebuggable = true
      buildConfigField("String", "GRAPHQL_ENDPOINT", "\"http://10.0.2.2:4000/graphql\"")
    }

    create("staging") {
      initWith(getByName("debug"))
      manifestPlaceholders["hostNam"] = "internal.avocado.com"
      applicationIdSuffix = ".staging"
      buildConfigField("String", "GRAPHQL_ENDPOINT", "\"http://192.168.100.5:4000/graphql\"")
    }
  }

  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
  }

  kotlinOptions {
    jvmTarget = "17"
  }

  buildFeatures {
    compose = true
  }

  composeCompiler {
    enableStrongSkippingMode = true
  }

  packaging {
    resources {
      excludes += "/META-INF/{AL2.0,LGPL2.1}"
    }
  }
}

tasks.register("lintKotlin") {
  doLast {
    exec {
      commandLine("bash", "-c", "./gradlew ktlintCheck")
    }
  }
}

dependencies {
  val composeBom = platform(libs.androidx.compose.bom)
  implementation(composeBom)

  implementation(libs.androidx.core.ktx)
  implementation(libs.androidx.lifecycle.runtime)
  implementation(libs.androidx.lifecycle.runtime.compose)
  implementation(libs.androidx.lifecycle.viewModelCompose)
  implementation(libs.androidx.activity.compose)
  implementation(libs.androidx.compose.ui)
  implementation(libs.androidx.compose.ui.graphics)
  implementation(libs.androidx.compose.ui.tooling.preview)
  implementation(libs.androidx.compose.material3)

  // Grapqhl
  implementation(libs.apollo.graphql)

  // Hilt
  implementation(libs.androidx.hilt.navigation.compose)
  implementation(libs.hilt.android)
  ksp(libs.hilt.compiler)
  ksp(libs.hilt.ext.compiler)

  // Retrofit
  implementation(libs.squareup.retrofit2)
  implementation(libs.squareup.okhttp3)
  // GSON converter
  implementation(libs.squareup.retrofit2.converter.gson)

  // DataStore
  implementation(libs.androidx.datastore.preferences)

  // Navigation
  implementation(libs.androidx.navigation.compose)

  // Timber
  implementation(libs.timber)

  testImplementation(libs.junit)
  androidTestImplementation(libs.androidx.test.ext)
  androidTestImplementation(libs.androidx.test.espresso.core)
  androidTestImplementation(composeBom)
  androidTestImplementation(libs.androidx.compose.ui.test)
  debugImplementation(libs.androidx.compose.ui.tooling.debug)
  debugImplementation(libs.androidx.compose.ui.test.manifest)
}
