package com.avocado.expensescompose.data.adapters

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

fun LocalDateTime.formatDateWithYear(): String {
    val format = DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale.getDefault())
    return this.format(format)
}

fun LocalDateTime.formatDateDaysWithMonth(): String {
    val format = DateTimeFormatter.ofPattern("dd MMMM", Locale.getDefault())
    return this.format(format)
}

fun LocalDateTime.formatDateOnlyMonth(): String {
    val format = DateTimeFormatter.ofPattern("MMMM", Locale.getDefault())
    return this.format(format)
}