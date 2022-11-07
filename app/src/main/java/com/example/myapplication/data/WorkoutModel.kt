package com.example.myapplication.data

import java.util.*
import kotlin.time.Duration

data class WorkoutModel(
    val id: Int = 0,
    var name: String,
    var notes: String,
    var workoutDuration: Duration,
    var workoutDate: Date
)
