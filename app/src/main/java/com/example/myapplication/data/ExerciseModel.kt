package com.example.myapplication.data

import com.example.myapplication.newID
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.toDuration

data class ExerciseModel(
    val id: Long = newID(),
    var name: String = "Exercise Name", // TODO Remove Assignment
    val exerciseCategory: String = "Exercise Category", // TODO Remove Assignment
    val bodyPart: String = "Exercise Body Part", // TODO Remove Assignment
    var notes: String? = null, // TODO Create Note Object that takes a string and a boolean for pinned status
    var pinnedNotes: String? = null,
    var notesPinned: Boolean = false,
    var barType: String? = null, // TODO Could be :BarType
    var weightUnit: String? = null, // TODO Could be ENUM -> Default, KG, LBS
    var workingSetRestTimer: Duration = 1.toDuration(DurationUnit.MINUTES),
    var warmUpSetRestTimer: Duration = 1.toDuration(DurationUnit.MINUTES),
    var workingSetRestTimerEnabled: Boolean = true,
    var warmUpSetRestTimerEnabled: Boolean = false
)

// TODO These are unnecessary. Remove
fun ExerciseModel.pinNotes(pinned: Boolean) {
    notesPinned = pinned
}

fun ExerciseModel.updateWorkingSetRestTimer(time: Duration) {
    workingSetRestTimer = time
}

fun ExerciseModel.updateWarmUpSetRestTimer(time: Duration) {
    warmUpSetRestTimer = time
}

fun ExerciseModel.enableworkingSetRestTimer(enable: Boolean) {
    workingSetRestTimerEnabled = enable
}

fun ExerciseModel.enableWarmUpSetRestTimer(enable: Boolean) {
    warmUpSetRestTimerEnabled = enable
}
