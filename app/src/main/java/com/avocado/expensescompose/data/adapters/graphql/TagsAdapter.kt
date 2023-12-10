package com.avocado.expensescompose.data.adapters.graphql

import com.avocado.TagsQuery
import com.avocado.expensescompose.domain.tags.models.Tag

fun TagsQuery.Tag.toTag(): Tag = Tag(
  id = id,
  name = name
)