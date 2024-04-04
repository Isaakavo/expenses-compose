package com.avocado.expensescompose.data.apolloclients

import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.ApolloResponse
import com.apollographql.apollo3.api.Mutation
import com.apollographql.apollo3.api.Query
import com.avocado.expensescompose.presentation.util.logWithThread
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onStart
import timber.log.Timber

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
  override suspend fun <D : Query.Data> query(
    query: Query<D>,
    onError: suspend () -> Unit
  ): Flow<ApolloResponse<D>> {
    return apolloClient.query(query)
      .toFlow()
      .onStart { logWithThread("Started Query ${query.name()}") }
      .catch {
        Timber.e("Apollo error ${it.message}")
        onError()
      }
      .flowOn(Dispatchers.IO)
  }

  override suspend fun <D : Mutation.Data> mutate(mutation: Mutation<D>): Flow<ApolloResponse<D>> {
    return apolloClient
      .mutation(mutation)
      .toFlow()
      .onStart { logWithThread("Started Mutation ${mutation.name()})") }
      .flowOn(Dispatchers.IO)
  }
}
