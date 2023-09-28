package com.avocado.expensescompose.data.adapters.graphql.scalar

import com.apollographql.apollo3.api.Adapter
import com.apollographql.apollo3.api.CustomScalarAdapters
import com.apollographql.apollo3.api.json.JsonReader
import com.apollographql.apollo3.api.json.JsonWriter
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId


class Date(val date: LocalDateTime)

val dateAdapter = object : Adapter<Date> {
    override fun fromJson(reader: JsonReader, customScalarAdapters: CustomScalarAdapters): Date {
        val instant = Instant.parse(reader.nextString())
        val date: LocalDateTime = instant.atZone(ZoneId.of("UTC")).toLocalDateTime()
        return Date(date)
    }

    override fun toJson(
        writer: JsonWriter, customScalarAdapters: CustomScalarAdapters, value: Date
    ) {
        writer.value(value.date.toString())
    }

}