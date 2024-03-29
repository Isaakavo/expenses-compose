package com.avocado.expensescompose.presentation.homescreen.viewmodel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

@HiltViewModel
class HomeScreenViewModel @Inject constructor() : ViewModel() {
  private val _state = MutableStateFlow(HomeScreenState())
  val state = _state.asStateFlow()

  fun onEvent(incomeEvent: IncomeEvent) {
    when (incomeEvent) {
      is IncomeEvent.BackPressInitialTouch -> {
        _state.update {
          it.copy(backPressState = BackPress.InitialTouch, showToast = true)
        }
      }

      is IncomeEvent.BackPressIdle -> {
        _state.update {
          it.copy(backPressState = BackPress.Idle)
        }
      }

      IncomeEvent.CloseToast -> {
        _state.update {
          it.copy(showToast = false)
        }
      }

      IncomeEvent.OpenToast -> {
        _state.update {
          it.copy(showToast = true)
        }
      }

//      IncomeEvent.FetchIncomes -> {
//        viewModelScope.launch {
//          getAllIncomes()
//        }
//      }
    }
  }
}
