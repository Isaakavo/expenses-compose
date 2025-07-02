package com.avocado.expensescompose.di

import com.avocado.expensescompose.data.auth.AuthProvider
import com.avocado.expensescompose.data.network.CognitoRetrofitWebClient
import com.avocado.expensescompose.data.source.CognitoAuthProvider
import com.avocado.expensescompose.data.source.CognitoAuthService
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AuthProviderModule {

  @Provides
  @Singleton
  fun provideAuthService(
    cognitoRetrofitWebClient: CognitoRetrofitWebClient,
    gson: Gson
  ): CognitoAuthService = CognitoAuthService(
    cognitoRetrofitWebClient = cognitoRetrofitWebClient,
    gson = gson
  )

  @Provides
  @Singleton
  fun provideAuthProvider(
    cognitoAuthService: CognitoAuthService
  ): AuthProvider = CognitoAuthProvider(cognitoAuthService)

  @Provides
  @Singleton
  fun provideGson(): Gson = Gson()
}
