package com.avocado.expensescompose.util

import com.avocado.expensescompose.data.model.card.Card
import com.avocado.expensescompose.data.model.expense.Expense
import com.avocado.type.Category
import java.time.LocalDateTime

val expenseList = listOf(
  Expense(
    total = 18063.0,
    incomeId = "1",
    category = Category.BILLS,
    concept = "Gasto de prueba lasdhjflhsdfklhjasdfhklasdhfdfadsflkasdhflkadhs",

    createdAt = LocalDateTime.now(),
    payBefore = LocalDateTime.now(),
    comment = "Comentario de prueba",
    id = "1",
    card = Card(
      bank = "Banco de prueba",
      alias = "Tarjeta Naranja",
      id = "kajshdfkajd"
    )
  ),
  Expense(
    total = 5500.0,
    incomeId = "1",
    category = Category.FOOD,
    concept = "Gasto de prueba",
    createdAt = LocalDateTime.now(),
    payBefore = LocalDateTime.of(2023, 11, 22, 0, 0, 0),
    comment = "Comentario mega largo de prueba alv como no apoco si",
    id = "2",
    card = Card(
      bank = "Banco de prueba 2",
      id = "kajshdfkajd"
    )
  ),
  Expense(
    total = 5500.0,
    incomeId = "1",
    category = Category.SAVINGS,
    concept = "Gasto de prueba",
    createdAt = LocalDateTime.now(),
    payBefore = LocalDateTime.now(),
    comment = "",
    id = "3",
    card = Card(
      bank = "Test",
      alias = "alv",
      id = "kajshdfkajd"
    )
  ),
  Expense(
    total = 5500.0,
    incomeId = "1",
    category = Category.COMMUNICATION,
    concept = "Gasto de prueba",
    createdAt = LocalDateTime.now(),
    payBefore = LocalDateTime.now(),
    comment = "",
    id = "4",
    card = Card(
      bank = "Test",
      alias = "alv",
      id = "kajshdfkajd"
    )
  ),
  Expense(
    total = 5500.0,
    incomeId = "1",
    category = Category.BILLS,
    concept = "Gasto de prueba sin tarjeta",
    payBefore = LocalDateTime.now(),
    createdAt = LocalDateTime.now(),
    comment = "",
    id = "5"
  )
)
