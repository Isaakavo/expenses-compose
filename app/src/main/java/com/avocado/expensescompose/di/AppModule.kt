package com.avocado.expensescompose.di

import android.content.Context
import com.apollographql.apollo3.ApolloClient
import com.avocado.expensescompose.data.apolloclients.incomes.ApolloIncomesClient
import com.avocado.expensescompose.domain.income.IncomesClient
import com.avocado.expensescompose.data.interceptor.AuthorizationInterceptor
import com.avocado.expensescompose.presentation.util.Constants
import com.avocado.expensescompose.data.network.LoginJwtClient
import com.avocado.expensescompose.data.repositories.AuthRepository
import com.avocado.expensescompose.data.repositories.TokenManagerRepository
import com.avocado.expensescompose.domain.income.usecase.CreateIncomeUseCase
import com.avocado.expensescompose.domain.income.usecase.GetAllIncomesUseCase
import com.avocado.expensescompose.domain.income.usecase.GetIncomeByIdWithExpensesUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton
import okhttp3.logging.HttpLoggingInterceptor

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

  @Provides
  @Singleton
  fun provideOkHttpClient(): OkHttpClient {
    return OkHttpClient.Builder()
      .addInterceptor(
        HttpLoggingInterceptor().apply {
          level = HttpLoggingInterceptor.Level.BODY
        }
      )
      .build()
  }

  @Provides
  @Singleton
  fun provideAuthClient(client: OkHttpClient): LoginJwtClient =
    Retrofit.Builder()
      .baseUrl(Constants.AWS_PROVIDER)
      .client(client)
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
  fun provideIncomesClient(apolloClient: ApolloClient): IncomesClient =
    ApolloIncomesClient(apolloClient)

  @Provides
  @Singleton
  fun provideIncomeUseCase(incomesClient: IncomesClient): GetAllIncomesUseCase =
    GetAllIncomesUseCase(incomesClient)

  @Provides
  @Singleton
  fun provideCreateIncomeUseCase(incomesClient: IncomesClient): CreateIncomeUseCase =
    CreateIncomeUseCase(incomesClient)

  @Provides
  @Singleton
  fun provideGetIncomeByIdWithExpensesUseCase(incomesClient: IncomesClient): GetIncomeByIdWithExpensesUseCase =
    GetIncomeByIdWithExpensesUseCase(incomesClient)

  @Provides
  @Singleton
  fun provideDataStoreRepository(
    @ApplicationContext app: Context
  ) = TokenManagerRepository(app)
}