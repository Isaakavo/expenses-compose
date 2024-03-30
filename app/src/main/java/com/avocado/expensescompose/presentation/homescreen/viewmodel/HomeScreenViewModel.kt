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

  fun onEvent(incomeEvent: HomeScreenEvents, screen: HomeScreens? = null) {
    when (incomeEvent) {
      HomeScreenEvents.ScreenToDisplay -> {
        _state.update {
          it.copy(screen = screen ?: HomeScreens.INCOME)
        }
      }

      is HomeScreenEvents.BackPressInitialTouch -> {
        _state.update {
          it.copy(backPressState = BackPress.InitialTouch, showToast = true)
        }
      }

      is HomeScreenEvents.BackPressIdle -> {
        _state.update {
          it.copy(backPressState = BackPress.Idle)
        }
      }

      HomeScreenEvents.CloseToast -> {
        _state.update {
          it.copy(showToast = false)
        }
      }

      HomeScreenEvents.OpenToast -> {
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
