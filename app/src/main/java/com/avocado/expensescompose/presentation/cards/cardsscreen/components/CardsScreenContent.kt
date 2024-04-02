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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.avocado.expensescompose.ui.theme.LocalSnackBarHostState
import kotlinx.coroutines.launch

@Composable
fun CardsScreenContent(
  cardsList: List<Card>,
  uiError: Int,
  isAdded: Boolean,
  onEvent: (event: CardsScreenEvents, cardType: String) -> Unit,
  onNavigate: (navigateEvent: NavigateEvent, cardId: String) -> Unit
) {
  val scope = rememberCoroutineScope()
  val snackBarHostState = LocalSnackBarHostState.current
  val context = LocalContext.current

  LaunchedEffect(key1 = isAdded) {
    if (isAdded) {
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

//  LaunchedEffect(key1 = operation) {
//    validateOperation(
//      operation,
//      onDelete = {
//        scope.launch {
//          snackBarHostState.showSnackbar(context.resources.getString(R.string.cards_delete))
//        }
//      }
//    )
//  }

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
