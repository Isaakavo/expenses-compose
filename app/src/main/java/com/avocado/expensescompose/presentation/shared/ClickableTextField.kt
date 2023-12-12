package com.avocado.expensescompose.presentation.shared

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember

@Composable
fun ClickableTextField(value: String, label: String, action: () -> Unit) {
  OutlinedTextField(
    value = value,
    onValueChange = {},
    label = { Text(text = label) },
    interactionSource = remember {
      MutableInteractionSource()
    }.also { interactionSource ->
      LaunchedEffect(key1 = interactionSource) {
        interactionSource.interactions.collect { interaction ->
          if (interaction is PressInteraction.Release) {
            action()
          }
        }
      }
    })
}