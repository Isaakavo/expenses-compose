package com.avocado.expensescompose.data.apolloclients

import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.ApolloResponse
import com.apollographql.apollo3.api.Mutation
import com.apollographql.apollo3.api.Query
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface GraphQlClient {
  suspend fun <D : Query.Data> query(
    query: Query<D>
  )

  suspend fun <D : Mutation.Data> mutate(
    mutation: Mutation<D>
  ): Flow<ApolloResponse<D>>
}

class GraphQlClientImpl @Inject constructor(private val apolloClient: ApolloClient) :
  GraphQlClient {
  override suspend fun <D : Query.Data> query(query: Query<D>) {
    TODO("Not yet implemented")
  }

  override suspend fun <D : Mutation.Data> mutate(mutation: Mutation<D>): Flow<ApolloResponse<D>> {
    return apolloClient.mutation(mutation).toFlow()
  }
}