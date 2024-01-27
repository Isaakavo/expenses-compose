package com.avocado.expensescompose.presentation.cards.cardsscreen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.avocado.expensescompose.R
import com.avocado.expensescompose.data.model.card.Card
import com.avocado.expensescompose.presentation.navigation.NavigateEvent
import com.avocado.expensescompose.presentation.topbar.AppBar
import com.avocado.expensescompose.presentation.topbar.MenuItems
import com.avocado.expensescompose.presentation.util.validateOperation
import kotlinx.coroutines.launch

@Composable
fun CardsScreen(
  operation: String = "",
  viewModel: CardsScreenViewModel = hiltViewModel(),
  onPopBackStack: () -> Unit = {},
  onNavigate: (navigateEvent: NavigateEvent, cardId: String) -> Unit
) {

  val state by viewModel.state.collectAsStateWithLifecycle()

  CardsScreenContent(
    operation = operation,
    cardsList = state.cardsList,
    bank = state.bank,
    alias = state.alias,
    uiError = state.uiError,
    openAddCardDialog = state.openAddCardDialog,
    isDebitCard = state.isDebit,
    isCreditCard = state.isCredit,
    isPhysical = state.isPhysical,
    isDigital = state.isDigital,
    isAdded = state.isAdded,
    onEvent = viewModel::onEvent,
    onPopBackStack = onPopBackStack,
    onNavigate = onNavigate
  )
}

@Composable
fun CardsScreenContent(
  operation: String,
  cardsList: List<Card>,
  bank: String,
  alias: String,
  uiError: String,
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

  Scaffold(
    snackbarHost = {
      SnackbarHost(hostState = snackBarHostState)
    },
    topBar = {
      AppBar(
        title = stringResource(id = R.string.appbar_cards),
        dropDownMenuItems = listOf(
          MenuItems(
            text = stringResource(id = R.string.appbar_cards_menu_item1),
            action = { onEvent(CardsScreenEvents.OpenAddCardDialog, "") })
        ),
        onNavigationIconClick = { onPopBackStack() }
      ) {

      }
    }
  ) { paddingValues ->
    Surface(
      modifier = Modifier
        .fillMaxSize()
        .padding(paddingValues)
    ) {
      if (uiError.isNotEmpty()) {
        Column(
          modifier = Modifier
            .fillMaxSize()
            .padding(18.dp),
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.Center
        ) {
          Text(text = uiError, style = MaterialTheme.typography.headlineMedium)
        }
      } else if (cardsList.isEmpty()) {
        Card(
          modifier = Modifier
            .padding(16.dp)
            .wrapContentHeight(),
          shape = RoundedCornerShape(16.dp),
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
          modifier = Modifier.padding(start = 12.dp, end = 12.dp, top = 8.dp),
        ) {
          items(cardsList) { card ->
            CardItem(
              id = card.id,
              alias = card.alias ?: "",
              bank = card.bank,
              cardType = stringResource(if (card.isDebit == true) R.string.cards_list_type_debit else R.string.cards_list_type_credit),
              onNavigate = onNavigate
            )
          }
        }
      }
    }
  }
}

@Composable
fun CardItem(
  id: String,
  alias: String,
  bank: String,
  cardType: String,
  onNavigate: (navigateEvent: NavigateEvent, cardId: String) -> Unit
) {
  Card(
    shape = RoundedCornerShape(16.dp),
    modifier = Modifier
      .fillMaxWidth()
      .clickable { onNavigate(NavigateEvent.NavigateCardsWithExpenseScreen, id) }
  ) {
    Column(
      modifier = Modifier.padding(16.dp),
      verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Text(text = alias, style = MaterialTheme.typography.headlineSmall)
        Text(text = bank, style = MaterialTheme.typography.headlineSmall)
      }
      Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
        Text(text = cardType, style = MaterialTheme.typography.bodyMedium)
      }
    }
  }
}

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
      shape = RoundedCornerShape(16.dp),

      ) {
      Column(
        modifier = Modifier
          .fillMaxSize()
          .wrapContentHeight(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
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
          label = { Text(text = stringResource(id = R.string.cards_add_card_bank)) })
        OutlinedTextField(
          value = alias,
          onValueChange = { onAliasChange(it) },
          label = { Text(text = "Alias") })

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
          horizontalArrangement = Arrangement.Center,
        ) {
          TextButton(
            onClick = { onDismiss() },
            modifier = Modifier.padding(8.dp),
          ) {
            Text(stringResource(id = R.string.cards_add_card_cancel))
          }
          TextButton(
            onClick = { onConfirm() },
            modifier = Modifier.padding(8.dp),
          ) {
            Text(stringResource(id = R.string.cards_add_card_accept))
          }
        }
      }
    }
  }
}

@Preview
@Composable
fun NoCardsPRev() {
  Card(
    modifier = Modifier
      .padding(16.dp)
      .wrapContentHeight(),
    shape = RoundedCornerShape(16.dp),
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(22.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
      Text(
        text = "No hay tarjetas ",
        style = MaterialTheme.typography.headlineLarge,
        modifier = Modifier.padding(bottom = 16.dp)
      )
      Text(
        text = "Agregar tarjetas te permitira trackear " +
            "los gastos que se ha realizado en cada una por mes o quincena",
        style = MaterialTheme.typography.headlineMedium,
        modifier = Modifier.padding(bottom = 8.dp),
        textAlign = TextAlign.Center
      )
      Button(onClick = { /* TODO */ }, modifier = Modifier.align(Alignment.CenterHorizontally)) {
        Icon(
          painterResource(id = R.drawable.baseline_credit_card_24),
          contentDescription = "",
          modifier = Modifier.padding(end = 12.dp)
        )
        Text(text = "Agregar tarjeta")
      }
    }
  }
}

//@Preview
//@Composable
//fun CardScreenPreview() {
//  CardsScreenContent()
//}