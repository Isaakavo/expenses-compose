package com.avocado.expensescompose.domain.tags.usecase

import com.avocado.expensescompose.data.model.MyResult
import com.avocado.expensescompose.domain.tags.TagsClient
import com.avocado.expensescompose.domain.tags.models.Tag

class GetTagsUseCase(
  private val tagsClient: TagsClient
) {
  suspend operator fun invoke(): MyResult<List<Tag>> {
    return tagsClient.getAllTags()
  }
}