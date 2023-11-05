package com.avocado.expensescompose.data.apolloclients.tags

import com.apollographql.apollo3.ApolloClient
import com.avocado.TagsQuery
import com.avocado.expensescompose.data.model.MyResult
import com.avocado.expensescompose.domain.tags.TagsClient
import com.avocado.expensescompose.domain.tags.models.Tag

class ApolloTagsClient(private val apolloClient: ApolloClient) : TagsClient {
  override suspend fun getAllTags(): MyResult<List<Tag>> {
    val tags = apolloClient.query(TagsQuery()).execute().data?.tags
      ?: return MyResult.Error(data = emptyList(), uiText = "No tags available")
    return MyResult.Success(
      tags.mapNotNull {
        it?.toTag()
      }
    )
  }
}