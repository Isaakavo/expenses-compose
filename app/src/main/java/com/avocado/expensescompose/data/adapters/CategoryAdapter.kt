package com.avocado.expensescompose.data.adapters

import com.avocado.expensescompose.R
import com.avocado.type.Category

fun Category.adapt(): Int = when (this) {
  Category.SAVINGS -> R.string.category_savings
  Category.COMMUNICATION -> R.string.category_communication
  Category.BILLS -> R.string.category_bills
  Category.FOOD -> R.string.category_food
  Category.CAR -> R.string.category_car
  Category.CLOTHES -> R.string.category_clothes
  Category.EATING_OUT -> R.string.category_eating_out
  Category.ENTERTAINMENT -> R.string.category_entertainment
  Category.GIFTS -> R.string.category_gifts
  Category.HANG_OUT -> R.string.category_hang_out
  Category.HEALTH -> R.string.category_health
  Category.HOUSE -> R.string.category_house
  Category.INSURANCE -> R.string.category_insurance
  Category.PETS -> R.string.category_pets
  Category.SPORTS -> R.string.category_sports
  Category.SUPER_MARKET -> R.string.category_super_market
  Category.TRANSPORT -> R.string.category_transport
  Category.SUBSCRIPTION -> R.string.category_subscription
  Category.FIXED_EXPENSE -> R.string.category_fixed_expense
  Category.MONTHS_WITHOUT_INTEREST -> R.string.category_months_without_interest
  else -> 0
}
