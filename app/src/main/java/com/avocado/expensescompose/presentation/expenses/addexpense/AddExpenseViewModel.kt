package com.avocado.expensescompose.presentation.expenses.addexpense

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.avocado.expensescompose.data.model.MyResult
import com.avocado.expensescompose.data.model.card.Card
import com.avocado.expensescompose.domain.cards.usecase.GetCardsUseCase
import com.avocado.expensescompose.domain.expense.CreateExpenseUseCase
import com.avocado.expensescompose.domain.tags.models.Tag
import com.avocado.expensescompose.domain.tags.usecase.GetTagsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class AddExpenseEvent {
  object SelectTag : AddExpenseEvent()
  object SelectCard : AddExpenseEvent()
  object UpdateConcept : AddExpenseEvent()
  object UpdateComment : AddExpenseEvent()
  object UpdateTotal : AddExpenseEvent()
  object UpdateDate : AddExpenseEvent()
  object UpdateNewTag : AddExpenseEvent()
  object AddExpense : AddExpenseEvent()
  object AddNewTag : AddExpenseEvent()
  object DateDialogOpen : AddExpenseEvent()
  object DateDialogClose : AddExpenseEvent()
  object TagDialogOpen : AddExpenseEvent()
  object TagDialogClose : AddExpenseEvent()
  object OpenCardMenu : AddExpenseEvent()
  object CloseCardMenu : AddExpenseEvent()
}

data class AddExpensesState(
  val cardsList: List<Card> = emptyList(),
  val tagList: List<Tag> = emptyList(),
  val selectedCard: Card? = null,
  val selectedTags: List<Tag> = emptyList(),
  val newTag: String = "",
  val newTags: List<String> = emptyList(),
  val showToast: Boolean = false,
  val toastMessage: String = "",
  val concept: String = "",
  val comment: String = "",
  val total: String = "",
  val date: String = "",
  val expenseAdded: Boolean = false,
  val expenseAddedError: Boolean = false,
  val openDateDialog: Boolean = false,
  val openTagDialog: Boolean = false,
  val openCardMenu: Boolean = false,
  val loadingCard: Boolean = true,
  val isAdded: Boolean = false
)

@HiltViewModel
class AddExpenseViewModel @Inject constructor(
  private val getTagsUseCase: GetTagsUseCase,
  private val getCardsUseCase: GetCardsUseCase,
  private val createExpenseUseCase: CreateExpenseUseCase
) : ViewModel() {
  private val _state = MutableStateFlow(AddExpensesState())
  val state = _state.asStateFlow()

  companion object {
    private const val TAG_LIST_MAX_SIZE = 10
  }

  init {
    viewModelScope.launch {
      getAllCards()
      getAllTags()
    }
  }

  fun <T> onEvent(event: AddExpenseEvent, params: T) {
    when (event) {
      is AddExpenseEvent.SelectTag -> {
        addSelectedTag(params as String)
      }

      is AddExpenseEvent.SelectCard -> {
        addSelectedCard(params as String)
      }

      is AddExpenseEvent.UpdateConcept -> {
        _state.update { it.copy(concept = params as String) }
      }

      is AddExpenseEvent.UpdateComment -> {
        _state.update { it.copy(comment = params as String) }
      }

      is AddExpenseEvent.UpdateTotal -> {
        _state.update { it.copy(total = params as String) }
      }

      is AddExpenseEvent.UpdateDate -> {
        _state.update { it.copy(date = params as String) }
      }

      is AddExpenseEvent.UpdateNewTag -> {
        _state.update { it.copy(newTag = params as String) }
      }

      is AddExpenseEvent.AddExpense -> {
        val selectedTags = _state.value.selectedTags.toMutableList()
        selectedTags.addAll(_state.value.newTags.map {
          Tag(id = "", name = it, selected = true)
        })
        viewModelScope.launch {
          createExpenseUseCase(
            concept = _state.value.concept,
            comment = _state.value.comment,
            date = _state.value.date,
            total = _state.value.total.toDouble(),
            tags = selectedTags,
            cardId = _state.value.selectedCard?.id,
          ).collect { expenseResult ->
            when (expenseResult) {
              is MyResult.Success -> {
                _state.emit(
                  value = AddExpensesState(
                    expenseAdded = true,
                    isAdded = true,
                    cardsList = _state.value.cardsList,
                    tagList = _state.value.tagList
                  )
                )
              }

              is MyResult.Error -> {
                _state.emit(value = AddExpensesState(expenseAddedError = true))
              }
            }

          }
        }
      }

      is AddExpenseEvent.AddNewTag -> {
        val newTagsMutable = _state.value.newTags.toMutableList()
        newTagsMutable.add(_state.value.newTag)
        _state.update {
          it.copy(
            newTag = "",
            newTags = newTagsMutable
          )
        }
      }

      is AddExpenseEvent.DateDialogOpen -> {
        _state.update { it.copy(openDateDialog = true) }
      }

      is AddExpenseEvent.DateDialogClose -> {
        _state.update { it.copy(openDateDialog = false) }
      }

      is AddExpenseEvent.TagDialogClose -> {
        _state.update { it.copy(openTagDialog = false) }
      }

      is AddExpenseEvent.TagDialogOpen -> {
        _state.update { it.copy(openTagDialog = true) }
      }

      is AddExpenseEvent.OpenCardMenu -> {
        _state.update { it.copy(openCardMenu = true) }
      }

      is AddExpenseEvent.CloseCardMenu -> {
        _state.update { it.copy(openCardMenu = false) }
      }
    }
  }

  private fun showToast(message: String) {
    _state.update { it.copy(showToast = true, toastMessage = message) }
  }

  fun resetToast() {
    _state.update { it.copy(showToast = false, toastMessage = "") }
  }

  private fun addSelectedCard(cardId: String) {
    val selectedCard = _state.value.cardsList.firstOrNull() { it.id == cardId }
    if (selectedCard != null) {
      _state.update { it.copy(selectedCard = selectedCard) }
    }
  }

  private fun addSelectedTag(tagId: String) {
    val newTagList = _state.value.selectedTags.toMutableList()
    if (newTagList.size >= TAG_LIST_MAX_SIZE) {
      newTagList.last().selected = false
      newTagList.removeLast()
      _state.update {
        it.copy(
          showToast = true,
          toastMessage = "Solo puedes elegir un máximo de 10 tags",
          selectedTags = newTagList
        )
      }
      return
    }
    val selectedTag = _state.value.tagList.find { it.id == tagId } ?: return
    if (selectedTag.selected) {
      changeTagState(selectedTag, false, newTagList)
      return
    } else {
      changeTagState(selectedTag, true, newTagList)
      return
    }
  }

  private fun changeTagState(selectedTag: Tag, value: Boolean, newTagList: MutableList<Tag>) {
    selectedTag.selected = value
    if (selectedTag.selected) {
      newTagList.add(selectedTag)
      _state.update {
        it.copy(selectedTags = newTagList.toList())
      }
    } else {
      val selectedTags = newTagList.filter { it.selected }
      _state.update {
        it.copy(selectedTags = selectedTags)
      }
    }
  }

  private suspend fun getAllTags() {
    when (val tagsResult = getTagsUseCase()) {
      is MyResult.Success -> {
        _state.update { it.copy(tagList = tagsResult.data) }
      }

      is MyResult.Error -> {

      }
    }
  }

  private suspend fun getAllCards() {
    _state.update { it.copy(loadingCard = true) }
    when (val cards = getCardsUseCase()) {
      is MyResult.Success -> {
        Log.d("AddExpenseViewModel", "SUCCESS -> Obtained ${cards.data.size} cards")
        _state.update { it.copy(cardsList = cards.data) }
        if (cards.data.isEmpty()) {
          showToast(message = "Agrega una tarjeta para poder selecionarla")
        }
        _state.update { it.copy(loadingCard = false) }
      }

      is MyResult.Error -> {
        _state.update { it.copy(loadingCard = false) }
      }
    }
  }
}