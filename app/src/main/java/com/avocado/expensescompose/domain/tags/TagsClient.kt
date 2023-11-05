package com.avocado.expensescompose.domain.tags

import com.avocado.expensescompose.data.model.MyResult
import com.avocado.expensescompose.domain.tags.models.Tag

interface TagsClient {
  suspend fun getAllTags(): MyResult<List<Tag>>
}