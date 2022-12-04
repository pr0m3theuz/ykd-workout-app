package com.example.myapplication

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.random.Random

fun newID(): Long {
    return LocalDateTime.now().format(formatter).toLong().div(Random.nextLong(1, 999))
}

val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyMMddhhmmAS")
