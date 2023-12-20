package com.avocado.expensescompose.data.model.card

data class Card(
  val id: String = "",
  val userId: String = "",
  val alias: String? = null,
  val bank: String = "",
  val isDigital: Boolean? = null,
  val isDebit: Boolean? = null
) {
  fun aliasWithBankText() = "${this.alias}-${this.bank}"
}
