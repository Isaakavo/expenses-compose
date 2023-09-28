package com.avocado.expensescompose.di

import android.content.Context
import com.apollographql.apollo3.ApolloClient
import com.avocado.expensescompose.data.ApolloExpenseClient
import com.avocado.expensescompose.data.ExpensesClient
import com.avocado.expensescompose.data.LoginJwtTokenClient
import com.avocado.expensescompose.data.interceptor.AuthorizationInterceptor
import com.avocado.expensescompose.data.model.auth.Constants
import com.avocado.expensescompose.data.repositories.DataStoreRepository
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
    fun provideApolloClient(dataStoreRepository: DataStoreRepository): ApolloClient {
        return ApolloClient.Builder()
            .serverUrl("http://10.0.2.2:4000/graphql")
            // TODO implement logic to extract this from login
            .addHttpInterceptor(AuthorizationInterceptor(dataStoreRepository = dataStoreRepository))
            .build()
    }

    @Provides
    @Singleton
    fun provideExpenseClient(apolloClient: ApolloClient): ExpensesClient {
        return ApolloExpenseClient(apolloClient)
    }

    @Provides
    @Singleton
    fun provideExpenseUseCase(expensesClient: ExpensesClient): GetExpensesUseCase {
        return GetExpensesUseCase(expensesClient)
    }

    @Provides
    @Singleton
    fun provideIncomeUseCase(expensesClient: ExpensesClient): GetIncomeUseCase {
        return GetIncomeUseCase(expensesClient)
    }

    @Provides
    @Singleton
    fun provideAuthClient(): LoginJwtTokenClient =
        Retrofit.Builder().baseUrl(Constants.AWS_PROVIDER)
            .addConverterFactory(GsonConverterFactory.create())
            .build().create(LoginJwtTokenClient::class.java)

    @Provides
    @Singleton
    fun provideDataStoreRepository(
        @ApplicationContext app: Context
    ) = DataStoreRepository(app)
}