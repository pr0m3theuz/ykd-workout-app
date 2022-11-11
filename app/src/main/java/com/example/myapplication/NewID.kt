package com.example.myapplication

import com.example.myapplication.components.formatter
import java.time.LocalDateTime
import kotlin.random.Random

fun newID(): Long {
    return LocalDateTime.now().format(formatter).toLong().div(Random.nextLong(1, 999))
}
