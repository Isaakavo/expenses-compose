package com.avocado.expensescompose.presentation.cards

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.avocado.expensescompose.domain.cards.models.Card
import com.avocado.expensescompose.presentation.topbar.AppBar
import com.avocado.expensescompose.presentation.topbar.IconsActions
import kotlinx.coroutines.launch

@Composable
fun CardsScreen(
  viewModel: CardsScreenViewModel = hiltViewModel(),
  onPopBackStack: () -> Unit = {}
) {

  val state by viewModel.state.collectAsStateWithLifecycle()

  CardsScreenContent(
    cardsList = state.cardsList,
    bank = state.bank,
    alias = state.alias,
    openDropDownMenu = state.openDropDownMenu,
    openAddCardDialog = state.openAddCardDialog,
    isDebitCard = state.isDebit,
    isCreditCard = state.isCredit,
    isPhysical = state.isPhysical,
    isDigital = state.isDigital,
    isAdded = state.isAdded,
    onEvent = viewModel::onEvent,
    onPopBackStack = onPopBackStack
  )
}

@Composable
fun CardsScreenContent(
  cardsList: List<Card>,
  bank: String,
  alias: String,
  openDropDownMenu: Boolean,
  openAddCardDialog: Boolean,
  isDebitCard: Boolean,
  isCreditCard: Boolean,
  isPhysical: Boolean,
  isDigital: Boolean,
  isAdded: Boolean,
  onEvent: (event: CardsScreenEvents, cardType: String) -> Unit,
  onPopBackStack: () -> Unit,
) {

  val scope = rememberCoroutineScope()
  val snackbarHostState = remember { SnackbarHostState() }

  if (isAdded) {
    LaunchedEffect(key1 = snackbarHostState) {
      scope.launch {
        snackbarHostState.showSnackbar("Se agregó la tarjeta ${cardsList.last().alias}")
      }
    }
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
      onDismiss = { /*TODO*/ },
      onConfirm = { onEvent(CardsScreenEvents.CreateCard, "") },
      onCheckedChange = { onEvent(CardsScreenEvents.HandleCardType, it) }
    )
  }

  Scaffold(
    snackbarHost = {
      SnackbarHost(hostState = snackbarHostState)
    },
    topBar = {
      AppBar(
        title = "Tarjetas",
        actionsList = listOf(
          IconsActions(
            icon = Icons.Rounded.MoreVert,
            action = { onEvent(CardsScreenEvents.OpenDropDownMenu, "") })
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
      Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.Top
      ) {
        Box {
          DropdownMenu(
            expanded = openDropDownMenu,
            onDismissRequest = { onEvent(CardsScreenEvents.CloseDropDownMenu, "") },
          ) {
            DropdownMenuItem(
              text = { Text(text = "Agregar tarjeta") },
              onClick = { onEvent(CardsScreenEvents.OpenAddCardDialog, "") })
          }
        }

      }

      LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.padding(start = 12.dp, end = 12.dp, top = 8.dp),
      ) {
        items(cardsList) { card ->
          CardItem(
            alias = card.alias,
            bank = card.bank,
            cardType = if (card.isDebit == true) "Tarjeta de débito" else "Tarjeta de crédito"
          )
        }
      }
    }
  }
}

@Composable
fun CardItem(
  alias: String,
  bank: String,
  cardType: String
) {
  Card(
    shape = RoundedCornerShape(16.dp),
    modifier = Modifier
      .fillMaxWidth()
      .clickable { }) {
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
          text = "Nueva Tajerta",
          fontSize = 24.sp,
          style = MaterialTheme.typography.bodyLarge,
          modifier = Modifier.padding(bottom = 8.dp)
        )
        OutlinedTextField(
          value = bank,
          onValueChange = { onBankChange(it) },
          label = { Text(text = "Banco") })
        OutlinedTextField(
          value = alias,
          onValueChange = { onAliasChange(it) },
          label = { Text(text = "Alias") })

        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          Text(text = "Débito")
          Checkbox(checked = isDebitCard, onCheckedChange = { onCheckedChange("debit") })
          Text(text = "Crédito")
          Checkbox(checked = isCreditCard, onCheckedChange = { onCheckedChange("credit") })
        }
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          Text(text = "Fisica")
          Checkbox(checked = isPhysical, onCheckedChange = { onCheckedChange("physical") })
          Text(text = "Digital")
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
            Text("Cancelar")
          }
          TextButton(
            onClick = { onConfirm() },
            modifier = Modifier.padding(8.dp),
          ) {
            Text("Aceptar")
          }
        }
      }
    }
  }
}

//@Preview
//@Composable
//fun CardScreenPreview() {
//  CardsScreenContent()
//}