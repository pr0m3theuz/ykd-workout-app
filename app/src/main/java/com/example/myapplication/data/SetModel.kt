package com.example.myapplication.data

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.random.Random

data class SetModel(
    val id: Long = LocalDateTime.now().format(formatter).toLong().div(Random.nextLong(1, 1000)),
    val setNumber: Int? = null,
    val setType: SetTypeModel? = null,
    var exerciseWeight: Double? = null,
    var repetitions: Int? = null,
    var ratePerceivedEffort: Double? = null,
    var repetitionsInReserve: Double? = null,
    var setCompleted: Boolean = false
)

// TODO Remove
@RequiresApi(Build.VERSION_CODES.O)
val formatter = DateTimeFormatter.ofPattern("yyMMddhhmmAS")
