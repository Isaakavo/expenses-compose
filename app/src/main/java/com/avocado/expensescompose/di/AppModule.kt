package com.avocado.expensescompose.di

import android.content.Context
import com.apollographql.apollo3.ApolloClient
import com.avocado.expensescompose.data.ApolloExpenseClient
import com.avocado.expensescompose.data.ExpensesClient
import com.avocado.expensescompose.data.interceptor.AuthorizationInterceptor
import com.avocado.expensescompose.data.repositories.DataStoreRepository
import com.avocado.expensescompose.domain.GetExpensesUseCase
import com.avocado.expensescompose.domain.income.GetIncomeUseCase
import com.avocado.expensescompose.domain.login.GetLoginUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
  @Provides
  @Singleton
  fun provideApolloClient(dataStoreRepository: DataStoreRepository): ApolloClient =
    ApolloClient.Builder()
      .serverUrl("http://10.0.2.2:4000/graphql")
      .addHttpInterceptor(AuthorizationInterceptor(dataStoreRepository = dataStoreRepository))
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
  fun provideLoginUseCase(expensesClient: ExpensesClient): GetLoginUseCase =
    GetLoginUseCase(expensesClient)

  @Provides
  @Singleton
  fun provideDataStoreRepository(
    @ApplicationContext app: Context
  ) = DataStoreRepository(app)
}