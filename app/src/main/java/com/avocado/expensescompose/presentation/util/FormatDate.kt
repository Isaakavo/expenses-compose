package com.avocado.expensescompose.presentation.util

import android.util.Log
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
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
    Log.d("Format Date to ISO", "Error al convertir la cadena a LocalDateTime: ${e.message}")
    null
  }
}

fun String.formatDateForRequest(): LocalDateTime? = try {
  val originalFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
  val localDate = LocalDate.parse(this, originalFormatter)
  localDate.atStartOfDay()
} catch (e: Exception) {
  Log.d("Format date for Request", "Error al convertir la cadena a LocalDateTime: ${e.message}")
  null
}

fun Long.formatDateFromMillis(): String = try {
  val currentTime = LocalTime.now().hour.toLong()
  val convertedDate =
    Instant.ofEpochMilli(this).atZone(ZoneId.systemDefault()).plusHours(currentTime).toLocalDateTime()
  convertedDate.formatDateWithYear()
} catch (e: Exception) {
  Log.d("formatDateFromMillis", "Error al convertir la cadena a LocalDateTime: ${e.message}")
  ""
}
