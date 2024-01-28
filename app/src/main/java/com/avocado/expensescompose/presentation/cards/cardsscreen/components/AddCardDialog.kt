package com.avocado.expensescompose.presentation.cards.cardsscreen.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.avocado.expensescompose.R

@Composable
fun AddCardDialog(
  bank: String,
  alias: String,
  isCreditCard: Boolean,
  isDebitCard: Boolean,
  isPhysical: Boolean,
  isDigital: Boolean,
  onBankChange: (String) -> Unit,
  onAliasChange: (String) -> Unit,
  onDismiss: () -> Unit,
  onConfirm: () -> Unit,
  onCheckedChange: (String) -> Unit
) {
  Dialog(
    onDismissRequest = { onDismiss() },
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Card(
      modifier = Modifier
        .padding(16.dp)
        .height(375.dp),
      shape = RoundedCornerShape(16.dp)

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
          onValueChange = { onBankChange(it) },
          label = { Text(text = stringResource(id = R.string.cards_add_card_bank)) }
        )
        OutlinedTextField(
          value = alias,
          onValueChange = { onAliasChange(it) },
          label = { Text(text = "Alias") }
        )

        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          Text(text = stringResource(id = R.string.cards_add_card_debit))
          Checkbox(checked = isDebitCard, onCheckedChange = { onCheckedChange("debit") })
          Text(text = stringResource(id = R.string.cards_add_card_credit))
          Checkbox(checked = isCreditCard, onCheckedChange = { onCheckedChange("credit") })
        }
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          Text(text = stringResource(id = R.string.cards_add_card_physical))
          Checkbox(checked = isPhysical, onCheckedChange = { onCheckedChange("physical") })
          Text(text = stringResource(id = R.string.cards_add_card_digital))
          Checkbox(checked = isDigital, onCheckedChange = { onCheckedChange("digital") })
        }

        Row(
          modifier = Modifier
            .fillMaxWidth(),
          horizontalArrangement = Arrangement.Center
        ) {
          TextButton(
            onClick = { onDismiss() },
            modifier = Modifier.padding(8.dp)
          ) {
            Text(stringResource(id = R.string.cards_add_card_cancel))
          }
          TextButton(
            onClick = { onConfirm() },
            modifier = Modifier.padding(8.dp)
          ) {
            Text(stringResource(id = R.string.cards_add_card_accept))
          }
        }
      }
    }
  }
}
