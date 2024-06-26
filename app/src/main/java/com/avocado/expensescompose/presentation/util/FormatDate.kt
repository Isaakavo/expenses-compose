package com.avocado.expensescompose.presentation.util

import com.avocado.expensescompose.data.model.total.Total
import com.avocado.expensescompose.data.model.total.TotalFortnight
import com.avocado.type.Fortnight
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.YearMonth
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import timber.log.Timber

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

fun LocalDateTime.formatDateMonthWithYear(): String {
  val format = DateTimeFormatter.ofPattern("MMMM yyyy", Locale.getDefault())
  return this.format(format)
}

fun LocalDateTime.convertDateToMillis(): Long {
  return this.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
}

fun TotalFortnight.prepareDateForRequest(): String? {
  return when (Fortnight.valueOf(this.fortnight?.name.orEmpty())) {
    Fortnight.FIRST -> this.date?.getFifteenDayOfMonth()
    Fortnight.SECOND -> this.date?.getLastDayOfMonth()
    else -> ""
  }
}

fun String.getFifteenDayOfMonth(): String {
  val dateArr = this.split("-")

  val year = dateArr[0]
  val month = dateArr[1]

  return "$year-$month-15"
}

fun String.getLastDayOfMonth(): String {
  val localDateTime = LocalDateTime.parse("${this}T00:00", DateTimeFormatter.ISO_LOCAL_DATE_TIME)
  val yearMonth = YearMonth.from(localDateTime)
  val lastDay = yearMonth.atEndOfMonth().atTime(23, 59, 59).dayOfMonth
  val dateArr = this.split("-")

  val year = dateArr[0]
  val month = dateArr[1]

  return "$year-$month-$lastDay"
}

fun String.formatDateMonthWithYear(): String {
  val format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm", Locale.getDefault())
  // TODO find a way to translate this
  return LocalDateTime.parse("$this 00:00", format).formatDateMonthWithYear()
}

fun String.formatDateOnlyMonth(): String {
  val format = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.getDefault())
  // TODO find a way to translate this
  return LocalDate.parse(this, format).month.name
}

fun String.formatDateToISO(): LocalDateTime? {
  val originalFormatter = DateTimeFormatter.ofPattern("dd MMMM yyyy")
  return try {
    val localDate = LocalDate.parse(this, originalFormatter)

    localDate.atStartOfDay()
  } catch (e: Exception) {
    Timber.d("Error converting string to LocalDateTime: ${e.message}")
    null
  }
}

fun String.formatDateForRequest(): LocalDateTime? = try {
  val originalFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
  val localDate = LocalDate.parse(this, originalFormatter)
  localDate.atStartOfDay()
} catch (e: Exception) {
  Timber.d("Error al convertir la cadena a LocalDateTime: " + e.message)
  null
}

fun String.formatDateForRequestPayBefore(): LocalDateTime? = try {
  val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
  val localDateValue = LocalDate.parse(this, formatter)
  localDateValue.atStartOfDay()
} catch (e: Exception) {
  Timber.d("Error al convertir la cadena a LocalDateTime: " + e.message)
  null
}

fun Long.formatDateFromMillis(): String = try {
  val currentTime = LocalTime.now().hour.toLong()
  val convertedDate =
    Instant.ofEpochMilli(this).atZone(ZoneId.systemDefault()).plusHours(currentTime)
      .toLocalDateTime()
  convertedDate.formatDateWithYear()
} catch (e: Exception) {
  Timber.d("Error al convertir la cadena a LocalDateTime: " + e.message)
  ""
}

fun getMonthTotal(totalByMonth: List<Total?>, month: String, year: String) =
  totalByMonth.find { totalByMont ->
    totalByMont?.date?.formatDateOnlyMonth()
      ?.uppercase(Locale.ROOT) == month && totalByMont.year == year
  }?.total ?: 0.0
