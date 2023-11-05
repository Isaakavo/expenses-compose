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
  val tagList: List<Tag> = emptyList(),
  val selectedTags: List<Tag> = emptyList(),
  val showToast: Boolean = false,
  val toastMessage: String = ""
)

sealed class AddExpenseEvent {
  object SelectTag : AddExpenseEvent()
}

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
          _state.update { it.copy(tagList = tagsResult.data) }
        }

        is MyResult.Error -> {

        }
      }

    }
  }

  fun <T> onEvent(event: AddExpenseEvent, params: T) {
    when (event) {
      is AddExpenseEvent.SelectTag -> {
        addSelectedTag(params as String)
      }
    }
  }

  fun showToast(value: Boolean) {
    _state.update { it.copy(showToast = value) }
  }

  private fun addSelectedTag(tagId: String) {
    val newTagList = _state.value.selectedTags.toMutableList()
    if (newTagList.size >= 3) {
      _state.update {
        it.copy(
          showToast = true,
          toastMessage = "Solo puedes elegir un máximo de 10 tags"
        )
      }
      return
    }
    val selectedTag = _state.value.tagList.find { it.id == tagId } ?: return
    selectedTag.selected = true
    newTagList.add(selectedTag)
    _state.update {
      it.copy(selectedTags = newTagList.toList())
    }
  }
}