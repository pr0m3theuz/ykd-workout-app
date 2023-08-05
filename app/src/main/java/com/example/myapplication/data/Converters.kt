package com.example.myapplication.data

import java.time.LocalDateTime

class Converters {
    fun dateToDateTimestamp(date: LocalDateTime?): String {
        return date?.toString() ?: ""
    }

    fun datetimestampToDate(value: String): LocalDateTime? {
        return if (value.isEmpty()) null else LocalDateTime.parse(value)
    }
}
