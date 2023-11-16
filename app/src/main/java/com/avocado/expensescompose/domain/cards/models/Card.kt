package com.avocado.expensescompose.domain.cards.models

data class Card(
  val id: String,
  val userId: String = "",
  val alias: String,
  val bank: String,
  val isDigital: Boolean? = null,
  val isDebit: Boolean? = null
) {
  fun aliasWithBankText() = "${this.alias}-${this.bank}"
}
