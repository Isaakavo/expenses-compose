package com.avocado.expensescompose.di

import com.avocado.LoginQuery
import com.avocado.expensescompose.data.repositories.login.loginvalidation.LoginValidationRepository
import com.avocado.expensescompose.data.repositories.login.loginvalidation.LoginValidationRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

  @Binds
  @Singleton
  abstract fun bindLoginValidationRepository(
    loginValidationRepositoryImpl: LoginValidationRepositoryImpl // Use the concrete type here
  ): LoginValidationRepository<LoginQuery.Data?>
}
