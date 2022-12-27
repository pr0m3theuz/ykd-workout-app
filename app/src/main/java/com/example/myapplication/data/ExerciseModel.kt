package com.example.myapplication.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.myapplication.newID
import kotlin.time.DurationUnit
import kotlin.time.toDuration

@Entity(tableName = "exercises_table")
data class ExerciseModel(
    @PrimaryKey(autoGenerate = true) val pId: Int = 0,
    val id: Long = newID(),
    var name: String = "Exercise Name", // TODO Remove Assignment
    val exerciseCategory: String = "Exercise Category", // TODO Remove Assignment
    // TODO Make into a List<String> for Multiple Body Parts
    val bodyPart: String = "Exercise Body Part", // TODO Remove Assignment
    var notes: String? = null, // TODO Create Note Object that takes a string and a boolean for pinned status
    var pinnedNotes: String? = null,
    var notesPinned: Boolean = false,
    var barType: String? = null, // TODO Could be :BarType
    var weightUnit: String? = null, // TODO Could be ENUM -> Default, KG, LBS
    var workingSetRestInWholeMilliseconds: Long = 1.toDuration(DurationUnit.MINUTES).inWholeMilliseconds,
    var warmUpSetRestInWholeMilliseconds: Long = 1.toDuration(DurationUnit.MINUTES).inWholeMilliseconds,
    var workingSetRestTimerEnabled: Boolean = true,
    var warmUpSetRestTimerEnabled: Boolean = false,
    var videoUri: String? = null
)

// TODO These are unnecessary. Remove
fun ExerciseModel.pinNotes(pinned: Boolean) {
    notesPinned = pinned
}

fun ExerciseModel.updateWorkingSetRestTimer(time: Long) {
    workingSetRestInWholeMilliseconds = time
}

fun ExerciseModel.updateWarmUpSetRestTimer(time: Long) {
    warmUpSetRestInWholeMilliseconds = time
}

fun ExerciseModel.enableworkingSetRestTimer(enable: Boolean) {
    workingSetRestTimerEnabled = enable
}

fun ExerciseModel.enableWarmUpSetRestTimer(enable: Boolean) {
    warmUpSetRestTimerEnabled = enable
}
