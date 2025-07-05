package com.avocado.expensescompose.di

import android.content.Context
import com.apollographql.apollo.ApolloClient
import com.avocado.expensescompose.BuildConfig
import com.avocado.expensescompose.data.apolloclients.GraphQlClientImpl
import com.avocado.expensescompose.data.apolloclients.incomes.ApolloIncomesClient
import com.avocado.expensescompose.data.interceptor.AuthorizationInterceptor
import com.avocado.expensescompose.data.network.CognitoRetrofitWebClient
import com.avocado.expensescompose.data.repositories.AuthRepository
import com.avocado.expensescompose.data.repositories.TokenManagerService
import com.avocado.expensescompose.domain.income.IncomesClient
import com.avocado.expensescompose.domain.income.usecase.CreateIncomeUseCase
import com.avocado.expensescompose.presentation.util.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

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
  fun provideAuthClient(client: OkHttpClient): CognitoRetrofitWebClient =
    Retrofit.Builder()
      .baseUrl(Constants.AWS_PROVIDER)
      .client(client)
      .addConverterFactory(GsonConverterFactory.create())
      .build().create(CognitoRetrofitWebClient::class.java)

  @Provides
  @Singleton
  fun provideApolloClient(
    authClient: AuthRepository
  ): ApolloClient =
    ApolloClient.Builder()
      .serverUrl(BuildConfig.GRAPHQL_ENDPOINT)
      .addHttpInterceptor(
        AuthorizationInterceptor(authRepository = authClient)
      )
      .build()

  @Provides
  @Singleton
  fun provideGraphQlClient(apolloClient: ApolloClient): GraphQlClientImpl =
    GraphQlClientImpl(apolloClient)

  @Provides
  @Singleton
  fun provideIncomesClient(apolloClient: ApolloClient): IncomesClient =
    ApolloIncomesClient(apolloClient)

  @Provides
  @Singleton
  fun provideCreateIncomeUseCase(incomesClient: IncomesClient): CreateIncomeUseCase =
    CreateIncomeUseCase(incomesClient)

  @Provides
  @Singleton
  fun provideDataStoreRepository(
    @ApplicationContext app: Context
  ) = TokenManagerService(app)
}
