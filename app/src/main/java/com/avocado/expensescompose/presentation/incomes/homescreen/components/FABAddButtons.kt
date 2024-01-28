package com.avocado.expensescompose.presentation.incomes.homescreen.components

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.avocado.expensescompose.R
import kotlinx.coroutines.launch

@Composable
fun FabAddButtons(
  onNavigateAddIncome: () -> Unit = {},
  onNavigateAddExpense: () -> Unit = {}
) {
  var expanded by remember {
    mutableStateOf(false)
  }
  val rotation = remember {
    Animatable(0f)
  }
  val scope = rememberCoroutineScope()

  Column(
    modifier = Modifier
      .wrapContentHeight()
      .wrapContentWidth()
      .animateContentSize()
      .padding(bottom = 8.dp, end = 4.dp),
    horizontalAlignment = Alignment.End
  ) {
    if (expanded) {
      ExtendedFloatingActionButton(
        elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation(1.dp),
        onClick = { onNavigateAddIncome() }
      ) {
        Icon(painterResource(id = R.drawable.round_account_balance_24), contentDescription = "")
        Text(text = stringResource(id = R.string.fab_add_income))
      }
      Spacer(modifier = Modifier.height(8.dp))
      ExtendedFloatingActionButton(
        elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation(1.dp),
        onClick = { onNavigateAddExpense() }
      ) {
        Icon(painterResource(id = R.drawable.round_attach_money_24), contentDescription = "")
        Text(text = stringResource(id = R.string.fab_add_expense))
      }
    }

    Spacer(modifier = Modifier.height(8.dp))
    Row(modifier = Modifier.wrapContentWidth(), horizontalArrangement = Arrangement.End) {
      FloatingActionButton(
        elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation(
          defaultElevation = 2.dp
        ),
        onClick = {
          expanded = !expanded
          scope.launch {
            if (expanded) {
              animateRotation(rotation, 45f, 800)
            } else {
              animateRotation(rotation, 0f, 800)
            }
          }
        }
      ) {
        Icon(
          Icons.Rounded.Add,
          contentDescription = "",
          modifier = Modifier.rotate(rotation.value)
        )
      }
    }
  }
}

suspend fun animateRotation(
  rotation: Animatable<Float, AnimationVector1D>,
  target: Float,
  durationMillis: Int
) {
  rotation.animateTo(
    targetValue = target,
    animationSpec = tween(durationMillis, easing = LinearEasing)
  )
}
