plugins {
  id("com.android.application")
  id("org.jetbrains.kotlin.android")
  id("com.apollographql.apollo3").version("3.8.2")
  id("com.google.dagger.hilt.android")
  kotlin("kapt")
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

kapt {
  correctErrorTypes = true
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
  composeOptions {
    kotlinCompilerExtensionVersion = "1.4.3"
  }
  packaging {
    resources {
      excludes += "/META-INF/{AL2.0,LGPL2.1}"
    }
  }
}


dependencies {

  implementation("androidx.core:core-ktx:1.12.0")
  implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
  implementation("androidx.lifecycle:lifecycle-runtime-compose:2.7.0")
  implementation("androidx.activity:activity-compose:1.8.2")
  implementation(platform("androidx.compose:compose-bom:2023.10.01"))
  implementation("androidx.compose.ui:ui")
  implementation("androidx.compose.ui:ui-graphics")
  implementation("androidx.compose.ui:ui-tooling-preview")
  implementation("androidx.compose.material3:material3:1.1.2")
  implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")

  //Grapqhl
  implementation("com.apollographql.apollo3:apollo-runtime:3.8.2")

  //Hilt
  implementation("com.google.dagger:hilt-android:2.50")
  implementation("androidx.hilt:hilt-navigation-compose:1.1.0")
  kapt("com.google.dagger:hilt-android-compiler:2.50")
  kapt("androidx.hilt:hilt-compiler:1.1.0")

  //Retrofit
  implementation("com.squareup.retrofit2:retrofit:2.9.0")
  implementation("com.squareup.okhttp3:logging-interceptor:5.0.0-alpha.12")
  //GSON converter
  implementation("com.squareup.retrofit2:converter-gson:2.9.0")

  //DataStore
  implementation("androidx.datastore:datastore-preferences:1.0.0")

  //Navigation
  implementation("androidx.navigation:navigation-compose:2.7.6")

  // Timber
  implementation("com.jakewharton.timber:timber:5.0.1")

  testImplementation("junit:junit:4.13.2")
  androidTestImplementation("androidx.test.ext:junit:1.1.5")
  androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
  androidTestImplementation(platform("androidx.compose:compose-bom:2023.10.01"))
  androidTestImplementation("androidx.compose.ui:ui-test-junit4")
  debugImplementation("androidx.compose.ui:ui-tooling")
  debugImplementation("androidx.compose.ui:ui-test-manifest")
}