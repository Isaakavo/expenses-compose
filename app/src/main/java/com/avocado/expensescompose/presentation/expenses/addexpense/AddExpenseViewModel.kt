package com.avocado.expensescompose.presentation.expenses.addexpense

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apollographql.apollo3.api.Optional
import com.apollographql.apollo3.exception.ApolloException
import com.avocado.AllCardsQuery
import com.avocado.CreateExpenseMutation
import com.avocado.expensescompose.data.adapters.graphql.scalar.adaptDateForInput
import com.avocado.expensescompose.data.adapters.graphql.types.toCard
import com.avocado.expensescompose.data.adapters.graphql.utils.validateData
import com.avocado.expensescompose.data.apolloclients.GraphQlClientImpl
import com.avocado.expensescompose.data.model.MyResult
import com.avocado.expensescompose.data.model.card.Card
import com.avocado.expensescompose.data.model.successOrError
import com.avocado.expensescompose.presentation.util.formatDateDaysWithMonth
import com.avocado.type.Category
import com.avocado.type.CreateExpenseInput
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

sealed class AddExpenseEvent {
  object SelectCard : AddExpenseEvent()
  object SelectCategory : AddExpenseEvent()
  object UpdateConcept : AddExpenseEvent()
  object UpdateComment : AddExpenseEvent()
  object UpdateTotal : AddExpenseEvent()
  object UpdateDate : AddExpenseEvent()
  object AddExpense : AddExpenseEvent()
  object DateDialogOpen : AddExpenseEvent()
  object DateDialogClose : AddExpenseEvent()
  object CategoryListOpen : AddExpenseEvent()
  object CategoryListClose : AddExpenseEvent()
  object OpenCardMenu : AddExpenseEvent()
  object CloseCardMenu : AddExpenseEvent()
}

data class AddExpensesState(
  val cardsList: List<Card> = emptyList(),
  val category: Category = Category.BILLS,
  val selectedCard: Card? = null,
  val showToast: Boolean = false,
  val toastMessage: String = "",
  val concept: String = "",
  val comment: String = "",
  val total: String = "",
  val date: String = "",
  val expenseAdded: Boolean = false,
  val expenseAddedError: Boolean = false,
  val openDateDialog: Boolean = false,
  val openCategoryList: Boolean = false,
  val openCardMenu: Boolean = false,
  val loadingCard: Boolean = true,
  val isAdded: Boolean = false
)

@HiltViewModel
class AddExpenseViewModel @Inject constructor(
  private val graphQlClientImpl: GraphQlClientImpl
) : ViewModel() {
  private val _state = MutableStateFlow(AddExpensesState())
  val state = _state.asStateFlow()

  init {
    viewModelScope.launch {
      getAllCards()
    }
    _state.update { it.copy(date = LocalDateTime.now().formatDateDaysWithMonth()) }
  }

  fun <T> onEvent(event: AddExpenseEvent, params: T) {
    when (event) {
      is AddExpenseEvent.SelectCard -> {
        addSelectedCard(params as String)
      }

      is AddExpenseEvent.SelectCategory -> {
        _state.update { it.copy(category = Category.valueOf(params as String)) }
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

      is AddExpenseEvent.AddExpense -> {
        createExpense()
      }


      is AddExpenseEvent.DateDialogOpen -> {
        _state.update { it.copy(openDateDialog = true) }
      }

      is AddExpenseEvent.DateDialogClose -> {
        _state.update { it.copy(openDateDialog = false) }
      }

      is AddExpenseEvent.CategoryListClose -> {
        _state.update { it.copy(openCategoryList = false) }
      }

      is AddExpenseEvent.CategoryListOpen -> {
        _state.update { it.copy(openCategoryList = true) }
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

  private fun createExpense() {
    viewModelScope.launch {
      val input = CreateExpenseInput(
        concept = _state.value.concept,
        comment = Optional.present(_state.value.comment),
        payBefore = _state.value.date.adaptDateForInput(),
        total = _state.value.total.toDouble(),
        cardId = Optional.present(_state.value.selectedCard?.id),
        category = _state.value.category,
      )
      graphQlClientImpl.mutate(CreateExpenseMutation(input)).map { apolloResponse ->
        validateData(apolloResponse.data?.createExpense)
      }.collect { result ->
        result.successOrError(
          onSuccess = {
            this.launch {
              _state.emit(
                value = AddExpensesState(
                  expenseAdded = true,
                  isAdded = true,
                  cardsList = _state.value.cardsList,
                  category = _state.value.category,
                  date = _state.value.date
                )
              )
            }
          },
          onError = {
            this.launch {
              _state.emit(value = AddExpensesState(expenseAddedError = true))
            }
          }
        )
      }
    }
  }

  private fun addSelectedCard(cardId: String) {
    val selectedCard = _state.value.cardsList.firstOrNull() { it.id == cardId }
    if (selectedCard != null) {
      _state.update { it.copy(selectedCard = selectedCard) }
    }
  }

  private suspend fun getAllCards() {
    _state.update { it.copy(loadingCard = true) }
    graphQlClientImpl.query(AllCardsQuery()).map { apolloResponse ->
      try {
        val cardsList = apolloResponse.data?.cardList
        MyResult.Success(
          cardsList?.mapNotNull {
            it?.toCard()
          }
        )
      } catch (e: ApolloException) {
        MyResult.Error(data = null, uiText = "Something went wrong")
      }
    }.collect { result ->
      when (result) {
        is MyResult.Success -> {
          _state.update { it.copy(cardsList = result.data ?: emptyList(), loadingCard = false) }
        }

        is MyResult.Error -> {
          _state.update { it.copy(loadingCard = false) }
        }
      }
    }
  }
}