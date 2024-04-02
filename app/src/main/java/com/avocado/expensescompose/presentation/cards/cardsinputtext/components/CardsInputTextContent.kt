package com.avocado.expensescompose.presentation.cards.cardsinputtext.components

import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.avocado.expensescompose.R
import com.avocado.expensescompose.data.model.card.Card
import com.avocado.expensescompose.presentation.cards.cardsinputtext.viewmodel.CardsInputTextEvents
import com.avocado.expensescompose.presentation.shared.DropDownMenu
import timber.log.Timber

@Composable
fun CardsInputTextContent(
  openCardMenu: Boolean,
  selectedCard: Card?,
  cards: List<Card>,
  isLoadingCards: Boolean,
  onEvent: (event: CardsInputTextEvents) -> Unit,
  onSelectCard: (Card?) -> Unit
) {
  Icon(
    painter = painterResource(id = R.drawable.baseline_credit_card_24),
    contentDescription = "Credit card"
  )
  DropDownMenu(
    expanded = openCardMenu,
    textFieldLabel = stringResource(id = R.string.add_expense_link_card),
    textFieldValue = selectedCard?.aliasWithBankText() ?: selectedCard?.bank ?: "",
    textFieldEnabled = cards.isNotEmpty(),
    dropDownMenuEnabled = cards.isEmpty() && !isLoadingCards,
    onOpenEvent = { onEvent(CardsInputTextEvents.OpenCardMenu) },
    onCloseEvent = { onEvent(CardsInputTextEvents.CloseCardMenu) }
  ) {
    Timber.d(cards.toString())
    cards.map {
      DropdownMenuItem(
        text = {
          Text(text = "${it.alias}-${it.bank}")
        },
        onClick = {
          onSelectCard(it)
          onEvent(CardsInputTextEvents.CloseCardMenu)
        }
      )
    }
  }
}
