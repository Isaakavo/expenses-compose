package com.avocado.expensescompose.di

import android.content.Context
import com.apollographql.apollo3.ApolloClient
import com.avocado.expensescompose.data.ApolloExpenseClient
import com.avocado.expensescompose.data.ExpensesClient
import com.avocado.expensescompose.data.interceptor.AuthorizationInterceptor
import com.avocado.expensescompose.data.model.auth.Constants
import com.avocado.expensescompose.data.network.LoginJwtClient
import com.avocado.expensescompose.data.repositories.AuthRepository
import com.avocado.expensescompose.data.repositories.TokenManagerRepository
import com.avocado.expensescompose.domain.GetExpensesUseCase
import com.avocado.expensescompose.domain.income.GetIncomeUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
  @Provides
  @Singleton
  fun provideAuthClient(): LoginJwtClient =
    Retrofit.Builder().baseUrl(Constants.AWS_PROVIDER)
      .addConverterFactory(GsonConverterFactory.create())
      .build().create(LoginJwtClient::class.java)

  @Provides
  @Singleton
  fun provideApolloClient(
    authClient: AuthRepository,
    tokenManagerRepository: TokenManagerRepository
  ): ApolloClient =
    ApolloClient.Builder()
      .serverUrl("http://10.0.2.2:4000/graphql")
      .addHttpInterceptor(
        AuthorizationInterceptor(
          authClient = authClient,
          tokenManagerRepository = tokenManagerRepository
        )
      )
      .build()


  @Provides
  @Singleton
  fun provideExpenseClient(apolloClient: ApolloClient): ExpensesClient =
    ApolloExpenseClient(apolloClient)


  @Provides
  @Singleton
  fun provideExpenseUseCase(expensesClient: ExpensesClient): GetExpensesUseCase =
    GetExpensesUseCase(expensesClient)


  @Provides
  @Singleton
  fun provideIncomeUseCase(expensesClient: ExpensesClient): GetIncomeUseCase =
    GetIncomeUseCase(expensesClient)


  @Provides
  @Singleton
  fun provideDataStoreRepository(
    @ApplicationContext app: Context
  ) = TokenManagerRepository(app)
}