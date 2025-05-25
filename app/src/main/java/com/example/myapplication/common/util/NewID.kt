package com.example.myapplication.common.util

import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import kotlin.random.Random
import kotlin.random.nextUInt

val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyMMddhhmmAS")

fun newLazyListItemID(): Long {
    return LocalDateTime.now().format(formatter).toLong().div(Random.nextLong(1, 9999))
        .plus(Random.nextUInt().toLong())
}

fun fakeULID(): Long {
    val timestamp: String = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC).toString()
    val randomLong: String = Random.nextUInt().toString()

    return timestamp.plus(randomLong.substring(0..5)).toLong()
}
