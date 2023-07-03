package com.example.myapplication.data

import androidx.room.TypeConverter
import java.time.LocalDateTime

class Converters {
    @TypeConverter
    fun dateToDateTimestamp(date: LocalDateTime?): String {
        return date?.toString() ?: ""
    }

    @TypeConverter
    fun datetimestampToDate(value: String): LocalDateTime? {
        return if (value.isEmpty()) null else LocalDateTime.parse(value)
    }
}
