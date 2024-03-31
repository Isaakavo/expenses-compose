package com.avocado.expensescompose.presentation.incomes.addscreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apollographql.apollo3.api.Optional
import com.avocado.IncomeByIdQuery
import com.avocado.UpdateIncomeMutation
import com.avocado.expensescompose.R
import com.avocado.expensescompose.data.adapters.graphql.scalar.Date
import com.avocado.expensescompose.data.adapters.graphql.utils.validateDataWithoutErrors
import com.avocado.expensescompose.data.apolloclients.GraphQlClientImpl
import com.avocado.expensescompose.data.model.successOrError
import com.avocado.expensescompose.domain.income.models.Income
import com.avocado.expensescompose.domain.income.usecase.CreateIncomeUseCase
import com.avocado.expensescompose.presentation.util.convertDateToMillis
import com.avocado.expensescompose.presentation.util.formatDateToISO
import com.avocado.expensescompose.presentation.util.formatDateWithYear
import com.avocado.type.UpdateIncomeInput
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDateTime
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed class AddIncomeEvent {
  object InsertIncome : AddIncomeEvent()
  object UpdateIncome : AddIncomeEvent()
  object UpdateDate : AddIncomeEvent()
  object DateDialogClose : AddIncomeEvent()
  object UpdateTotal : AddIncomeEvent()
  object UpdateComment : AddIncomeEvent()
}

data class AddIncomeState(
  val incomeId: String = "",
  val insertedIncome: Income? = null,
  val isInserted: Boolean = false,
  val isUpdated: Boolean = false,
  val loading: Boolean = true,
  val userMessage: String = "",
  val date: String = "",
  val openDateDialog: Boolean = false,
  val total: String = "",
  val comments: String = "",
  val initialDate: Long = 0L,
  val uiError: Int = 0
)

@HiltViewModel
class AddIncomeViewModel @Inject constructor(
  private val createIncomeUseCase: CreateIncomeUseCase,
  private val graphQlClientImpl: GraphQlClientImpl
) : ViewModel() {

  private val _state = MutableStateFlow(AddIncomeState())
  val state = _state.asStateFlow()

  init {
    _state.update { it.copy(date = LocalDateTime.now().formatDateWithYear(), loading = false) }
  }

  fun onEvent(addIncomeEvent: AddIncomeEvent, param: String? = null) {
    when (addIncomeEvent) {
      is AddIncomeEvent.InsertIncome -> {
        viewModelScope.launch {
          val insertedIncome = createIncomeUseCase(
            total = _state.value.total.toDouble(),
            paymentDate = _state.value.date.formatDateToISO() ?: LocalDateTime.now(),
            comment = _state.value.comments
          )

          insertedIncome.successOrError(
            onSuccess = {
              _state.update {
                it.copy(
                  isInserted = true,
                  isUpdated = false,
                  insertedIncome = it.insertedIncome
                )
              }
            },
            onError = {}
          )
        }
      }

      AddIncomeEvent.UpdateIncome -> {
        updateIncome()
      }

      AddIncomeEvent.UpdateDate -> {
        _state.update { it.copy(date = param.orEmpty()) }
      }

      AddIncomeEvent.DateDialogClose -> {
        _state.update { it.copy(openDateDialog = false) }
      }

      AddIncomeEvent.UpdateComment -> {
        _state.update { it.copy(comments = param as String) }
      }

      AddIncomeEvent.UpdateTotal -> {
        _state.update { it.copy(total = param as String) }
      }
    }
  }

  private fun updateIncome() {
    viewModelScope.launch {
      val total = _state.value.total.toDouble()
      val comment = state.value.comments
      val date = _state.value.date.formatDateToISO() ?: return@launch

      val input = UpdateIncomeInput(
        incomeId = _state.value.incomeId,
        total = total,
        comment = if (comment.isNotEmpty()) Optional.present(comment) else Optional.absent(),
        paymentDate = Date(date)
      )
      graphQlClientImpl.mutate(UpdateIncomeMutation(input = Optional.present(input)))
        .map { validateDataWithoutErrors(it) }
        .collect { result ->
          result.successOrError(
            onSuccess = { successResult ->
              this.launch {
                _state.emit(
                  AddIncomeState(
                    isUpdated = true,
                    isInserted = false
                  )
                )
              }
            },
            onError = {}
          )
        }
    }
  }

  fun getIncomeById(incomeId: String) {
    viewModelScope.launch {
      graphQlClientImpl.query(
        IncomeByIdQuery(incomeId = incomeId),
        onError = { _state.emit(AddIncomeState(uiError = R.string.general_error)) }
      ).map {
        validateDataWithoutErrors(it)
      }.collect { collectResult ->
        collectResult.successOrError(
          onSuccess = {
            this.launch {
              val incomeData = it.data.incomeById
              val date = incomeData?.paymentDate?.date?.date
              val formattedDate = date?.formatDateWithYear().orEmpty()
              val initialDate =
                date?.convertDateToMillis()
              _state.emit(
                AddIncomeState(
                  incomeId = incomeId,
                  total = incomeData?.total.toString(),
                  comments = incomeData?.comment.orEmpty(),
                  date = formattedDate,
                  initialDate = initialDate ?: 0,
                  loading = false
                )
              )
            }
          },
          onError = {}
        )
      }
    }
  }
}
