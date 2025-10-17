package com.example.myapplication.common.util

import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.UUID
import kotlin.random.Random
import kotlin.random.nextUInt
import kotlin.uuid.Uuid

val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyMMddhhmmAS")

fun newLazyListItemID(): Long {
    return UUID.randomUUID().toString().hashCode().toLong()
}

fun fakeULID(): Long {
    val timestamp: String = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC).toString()
    val randomLong: String = Random.nextUInt().toString()

    return timestamp.plus(randomLong.substring(0..5)).toLong()
}
