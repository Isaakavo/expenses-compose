package com.avocado.expensescompose.presentation.cards.addcards.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.avocado.expensescompose.R
import com.avocado.expensescompose.presentation.cards.addcards.viewmodel.AddCardEvents
import com.avocado.expensescompose.presentation.shared.CustomScaffold
import com.avocado.expensescompose.presentation.topbar.AppBar

@Composable
fun AddCardContent(
  bank: String,
  alias: String,
  isCreditCard: Boolean,
  isDebitCard: Boolean,
  isPhysical: Boolean,
  isDigital: Boolean,
  onEvent: (AddCardEvents, String) -> Unit,
  onPopBackStack: () -> Unit = {}
) {
  CustomScaffold(
    topBar = {
      AppBar(
        title = stringResource(id = R.string.cards_empty_list_button),
        buttonText = stringResource(id = R.string.generic_Add),
        onActionButtonClick = {
          onEvent(AddCardEvents.CreateCard, "")
        },
        onNavigationIconClick = { onPopBackStack() }
      )
    }
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .wrapContentHeight(),
      verticalArrangement = Arrangement.Center,
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Text(
        text = stringResource(id = R.string.cards_add_card_title),
        fontSize = 24.sp,
        style = MaterialTheme.typography.bodyLarge,
        modifier = Modifier.padding(bottom = 8.dp)
      )
      OutlinedTextField(
        value = bank,
        onValueChange = { onEvent(AddCardEvents.UpdateBank, it) },
        label = { Text(text = stringResource(id = R.string.cards_add_card_bank)) }
      )
      OutlinedTextField(
        value = alias,
        onValueChange = { onEvent(AddCardEvents.UpdateAlias, it) },
        label = { Text(text = "Alias") }
      )

      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        Text(text = stringResource(id = R.string.cards_add_card_debit))
        Checkbox(checked = isDebitCard, onCheckedChange = { onEvent(AddCardEvents.HandleCardType, "debit") })
        Text(text = stringResource(id = R.string.cards_add_card_credit))
        Checkbox(checked = isCreditCard, onCheckedChange = { onEvent(AddCardEvents.HandleCardType, "credit") })
      }
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        Text(text = stringResource(id = R.string.cards_add_card_physical))
        Checkbox(checked = isPhysical, onCheckedChange = { onEvent(AddCardEvents.HandleCardType, "physical") })
        Text(text = stringResource(id = R.string.cards_add_card_digital))
        Checkbox(checked = isDigital, onCheckedChange = { onEvent(AddCardEvents.HandleCardType, "digital") })
      }
    }
  }
}
