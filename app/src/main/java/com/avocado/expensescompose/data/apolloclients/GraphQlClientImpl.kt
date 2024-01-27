package com.avocado.expensescompose.data.apolloclients

import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.ApolloResponse
import com.apollographql.apollo3.api.Mutation
import com.apollographql.apollo3.api.Query
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import timber.log.Timber
import javax.inject.Inject

interface GraphQlClient {
  suspend fun <D : Query.Data> query(
    query: Query<D>,
    onError: suspend () -> Unit
  ): Flow<ApolloResponse<D>>

  suspend fun <D : Mutation.Data> mutate(
    mutation: Mutation<D>
  ): Flow<ApolloResponse<D>>
}

class GraphQlClientImpl @Inject constructor(private val apolloClient: ApolloClient) :
  GraphQlClient {

  suspend fun <D : Query.Data> queryWithCatch(query: Query<D>): Flow<ApolloResponse<D>> {
    return apolloClient.query(query).toFlow().catch { }
  }

  override suspend fun <D : Query.Data> query(
    query: Query<D>,
    onError: suspend () -> Unit
  ): Flow<ApolloResponse<D>> {
    return apolloClient.query(query).toFlow().catch {
      Timber.e("Apollo error ${it.message}")
      onError()
    }
  }

  override suspend fun <D : Mutation.Data> mutate(mutation: Mutation<D>): Flow<ApolloResponse<D>> {
    return apolloClient.mutation(mutation).toFlow()
  }
}