package com.avocado.expensescompose.presentation.homescreen.components.preview

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.avocado.expensescompose.presentation.homescreen.components.FabAddButtons

@Preview
@Composable
fun FABPrev() {
  Scaffold(floatingActionButton = {
    FabAddButtons()
  }) {
    Surface(modifier = Modifier.padding(it)) {
    }
  }
}
