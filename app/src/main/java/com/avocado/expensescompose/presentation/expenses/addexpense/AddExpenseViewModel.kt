package com.avocado.expensescompose.presentation.expenses.addexpense

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.avocado.expensescompose.data.model.MyResult
import com.avocado.expensescompose.domain.tags.models.Tag
import com.avocado.expensescompose.domain.tags.usecase.GetTagsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AddExpensesState(
  val tags: List<Tag> = emptyList()
)

@HiltViewModel
class AddExpenseViewModel @Inject constructor(
  private val getTagsUseCase: GetTagsUseCase
) : ViewModel() {
  private val _state = MutableStateFlow(AddExpensesState())
  val state = _state.asStateFlow()

  init {
    viewModelScope.launch {
      when (val tagsResult = getTagsUseCase()) {
        is MyResult.Success -> {
          _state.update { it.copy(tags = tagsResult.data) }
        }

        is MyResult.Error -> {

        }
      }

    }
  }
}