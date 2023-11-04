package com.avocado.expensescompose.data.adapters

import java.time.LocalDate
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

fun String.formatDateToISO(): LocalDateTime? {
  val originalFormatter = DateTimeFormatter.ofPattern("dd MMMM yyyy")
  return try {
    val localDate = LocalDate.parse(this, originalFormatter)

    localDate.atStartOfDay()
  } catch (e: Exception) {
    println("Error al convertir la cadena a LocalDateTime: ${e.message}")
    null
  }
}

fun String.formatDateForRequest(): LocalDateTime? = try {
  val originalFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
  val localDate = LocalDate.parse(this, originalFormatter)
  localDate.atStartOfDay()
} catch (e: Exception) {
  println("Error al convertir la cadena a LocalDateTime: ${e.message}")
  null
}
