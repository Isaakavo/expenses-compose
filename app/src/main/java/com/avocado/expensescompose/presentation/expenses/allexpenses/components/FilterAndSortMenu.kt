package com.avocado.expensescompose.presentation.expenses.allexpenses.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.avocado.expensescompose.data.adapters.adapt
import com.avocado.expensescompose.data.model.card.Card
import com.avocado.expensescompose.data.model.expense.Expense
import com.avocado.type.Category

enum class Filters {
  CATEGORY,
  CARDS,
  CASH,
  RESET
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterAndSortMenu(
  list: List<Expense> = emptyList(),
  cards: Set<Card> = emptySet(),
  onFilterSelect: (Map<Filters, List<String>>) -> Unit
) {
  var showBottomSheet by remember {
    mutableStateOf(false)
  }
  val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

  var rememberFilterToApply by remember {
    mutableStateOf(emptyMap<Filters, List<String>>())
  }
  var rememberSelectedCategories by remember {
    mutableStateOf(emptyList<String>())
  }
  var rememberSelectedCards by remember {
    mutableStateOf(emptyList<String>())
  }
  var rememberCashSelected by remember {
    mutableStateOf(false)
  }

  Button(onClick = { showBottomSheet = !showBottomSheet }) {
    Text(text = "Sort & Filter")
  }

  if (showBottomSheet) {
    ModalBottomSheet(
      modifier = Modifier.fillMaxSize(),
      sheetState = sheetState,
      onDismissRequest = { showBottomSheet = false }
    ) {
      Scaffold(
        bottomBar = {
          Row(
            modifier = Modifier
              .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Button(
              modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
              onClick = {
                showBottomSheet = false
                if (rememberFilterToApply.containsKey(Filters.RESET)) {
                  rememberFilterToApply = rememberFilterToApply.filter { it.key != Filters.RESET }
                }
                onFilterSelect(rememberFilterToApply)
              }
            ) {
              Text(text = "Done")
            }
          }
        }
      ) { padding ->
        Column(
          modifier = Modifier
            .padding(start = 20.dp, end = 20.dp),
          verticalArrangement = Arrangement.spacedBy(15.dp)
        ) {
          // Sort By
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text(text = "Sort by", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            TextButton(
              modifier = Modifier,
              onClick = {
                rememberCashSelected = false
                rememberSelectedCategories = emptyList()
                rememberSelectedCards = emptyList()
                rememberFilterToApply = mapOf(Filters.RESET to emptyList())
                showBottomSheet = false
                onFilterSelect(rememberFilterToApply)
              }
            ) {
              Text(text = "Reset")
            }
          }
          OutlinedButton(modifier = Modifier.fillMaxWidth(), onClick = { /*TODO*/ }) {
            Text(text = "Name (A-Z)")
          }

          HorizontalDivider()

          // Filter By
          Text(text = "Filter by", fontWeight = FontWeight.Bold, fontSize = 16.sp)
          CashOnlyCheckBox(selected = rememberCashSelected) { selected ->
            rememberCashSelected = !rememberCashSelected
            rememberFilterToApply = rememberFilterToApply + mapOf(Filters.CASH to listOf(selected.toString()))
          }
          CategoryCheckBox(list = list, selectedList = rememberSelectedCategories) { category ->
            rememberSelectedCategories = if (rememberSelectedCategories.contains(category)) {
              rememberSelectedCategories.filter { it != category }
            } else {
              listOf(category) + rememberSelectedCategories
            }
            rememberFilterToApply = rememberFilterToApply + mapOf(Filters.CATEGORY to rememberSelectedCategories)
          }
          cards.takeIf { it.isNotEmpty() }?.let {
            CardsCheckBox(list = list, cards = cards, selectedList = rememberSelectedCards) { card ->
              rememberSelectedCards = if (rememberSelectedCards.contains(card)) {
                rememberSelectedCards.filter { it != card }
              } else {
                listOf(card) + rememberSelectedCards
              }
              rememberFilterToApply = rememberFilterToApply + mapOf(Filters.CARDS to rememberSelectedCards)
            }
          }
        }
      }
    }
  }
}

@Composable
fun CategoryCheckBox(
  list: List<Expense>,
  selectedList: List<String>,
  onSelected: (String) -> Unit
) {
  Column {
    Text(text = "Category")
    LazyVerticalGrid(columns = GridCells.Fixed(2)) {
      items(Category.knownValues()) { category ->
        val numberOfCategoryInList = list.filter { it.category == category }.size
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.Start
        ) {
          Checkbox(
            checked = selectedList.contains(category.name),
            onCheckedChange = { onSelected(category.name) }
          )
          category.adapt().takeIf { it != 0 }?.let {
            Text(
              modifier = Modifier.align(Alignment.CenterVertically),
              text = "${stringResource(it)} ($numberOfCategoryInList)",
              textAlign = TextAlign.End
            )
          }
        }
      }
    }
  }
}

@Composable
fun CardsCheckBox(
  list: List<Expense>,
  cards: Set<Card>,
  selectedList: List<String>,
  onSelected: (String) -> Unit
) {
  Column {
    Text(text = "Cards")
    Column {
      cards.map { card ->
        val numberOfCardsInList = list.filter { it.card?.bank == card.bank }.size
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.Start
        ) {
          Checkbox(
            checked = selectedList.contains(card.bank),
            onCheckedChange = { onSelected(card.bank) }
          )
          Text(
            modifier = Modifier.align(Alignment.CenterVertically),
            text = "${card.bank} ($numberOfCardsInList)",
            textAlign = TextAlign.End
          )
        }
      }
    }
  }
}

@Composable
fun CashOnlyCheckBox(
  selected: Boolean,
  onSelected: (Boolean) -> Unit
) {
  Column {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.Start
    ) {
      Checkbox(
        checked = selected,
        onCheckedChange = { onSelected(it) }
      )
      Text(
        modifier = Modifier.align(Alignment.CenterVertically),
        text = "Only Cash",
        textAlign = TextAlign.End
      )
    }
  }
}

@Preview
@Composable
fun ShowBottomSheet() {
  Surface {
    FilterAndSortMenu(list = emptyList()) { type ->
    }
  }
}
