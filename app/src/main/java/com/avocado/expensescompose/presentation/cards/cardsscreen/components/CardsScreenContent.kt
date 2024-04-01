package com.avocado.expensescompose.presentation.cards.cardsscreen.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.avocado.expensescompose.R
import com.avocado.expensescompose.data.model.card.Card
import com.avocado.expensescompose.presentation.cards.cardsscreen.CardsScreenEvents
import com.avocado.expensescompose.presentation.navigation.NavigateEvent
import com.avocado.expensescompose.presentation.shared.CustomScaffold
import com.avocado.expensescompose.presentation.topbar.AppBar
import com.avocado.expensescompose.presentation.topbar.MenuItems
import com.avocado.expensescompose.presentation.util.validateOperation
import kotlinx.coroutines.launch

@Composable
fun CardsScreenContent(
  operation: String,
  cardsList: List<Card>,
  bank: String,
  alias: String,
  uiError: Int,
  openAddCardDialog: Boolean,
  isDebitCard: Boolean,
  isCreditCard: Boolean,
  isPhysical: Boolean,
  isDigital: Boolean,
  isAdded: Boolean,
  onEvent: (event: CardsScreenEvents, cardType: String) -> Unit,
  onPopBackStack: () -> Unit,
  onNavigate: (navigateEvent: NavigateEvent, cardId: String) -> Unit
) {
  val scope = rememberCoroutineScope()
  val snackBarHostState = remember { SnackbarHostState() }
  val context = LocalContext.current

  if (isAdded) {
    LaunchedEffect(key1 = snackBarHostState) {
      scope.launch {
        snackBarHostState.showSnackbar(
          context.resources.getString(
            R.string.cards_add_successfully,
            cardsList.last().alias
          )
        )
      }
    }
  }

  LaunchedEffect(key1 = Unit) {
    validateOperation(
      operation,
      onDelete = {
        scope.launch {
          snackBarHostState.showSnackbar(context.resources.getString(R.string.cards_delete))
        }
      }
    )
  }

  if (openAddCardDialog) {
    AddCardDialog(
      isCreditCard = isCreditCard,
      isDebitCard = isDebitCard,
      isPhysical = isPhysical,
      isDigital = isDigital,
      bank = bank,
      alias = alias,
      onBankChange = { onEvent(CardsScreenEvents.UpdateBank, it) },
      onAliasChange = { onEvent(CardsScreenEvents.UpdateAlias, it) },
      onDismiss = { onEvent(CardsScreenEvents.CloseAddCardDialog, "") },
      onConfirm = { onEvent(CardsScreenEvents.CreateCard, "") },
      onCheckedChange = { onEvent(CardsScreenEvents.HandleCardType, it) }
    )
  }

  CustomScaffold(
    topBar = {
      AppBar(
        title = stringResource(id = R.string.appbar_cards),
        dropDownMenuItems = listOf(
          MenuItems(
            text = stringResource(id = R.string.appbar_cards_menu_item1),
            action = { onEvent(CardsScreenEvents.OpenAddCardDialog, "") }
          )
        ),
        onNavigationIconClick = { onPopBackStack() }
      ) {
      }
    }
  ) { paddingValues ->

    if (uiError != 0) {
      Column(
        modifier = Modifier
          .fillMaxSize()
          .padding(18.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
      ) {
        Text(text = stringResource(uiError), style = MaterialTheme.typography.headlineMedium)
      }
    } else if (cardsList.isEmpty()) {
      Card(
        modifier = Modifier
          .padding(16.dp)
          .wrapContentHeight(),
        shape = RoundedCornerShape(16.dp)
      ) {
        Column(
          modifier = Modifier
            .fillMaxSize()
            .padding(22.dp),
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
          Text(
            text = stringResource(id = R.string.cards_empty_list_title),
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(bottom = 16.dp)
          )
          Text(
            text = stringResource(id = R.string.cards_empty_list_body),
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 8.dp),
            textAlign = TextAlign.Center
          )
          Button(
            onClick = { onEvent(CardsScreenEvents.OpenAddCardDialog, "") },
            modifier = Modifier.align(Alignment.CenterHorizontally)
          ) {
            Icon(
              painterResource(id = R.drawable.baseline_credit_card_24),
              contentDescription = "",
              modifier = Modifier.padding(end = 12.dp)
            )
            Text(text = stringResource(id = R.string.cards_empty_list_button))
          }
        }
      }
    } else {
      LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.padding(start = 12.dp, end = 12.dp, top = 8.dp)
      ) {
        items(cardsList) { card ->
          CardItem(
            id = card.id,
            alias = card.alias ?: "",
            bank = card.bank,
            cardType = stringResource(
              if (card.isDebit == true) R.string.cards_list_type_debit else R.string.cards_list_type_credit
            ),
            onNavigate = onNavigate
          )
        }
      }
    }
  }
}
