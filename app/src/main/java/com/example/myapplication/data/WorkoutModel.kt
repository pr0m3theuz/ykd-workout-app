package com.example.myapplication.data

import android.util.Log
import com.example.myapplication.newID
import kotlin.time.Duration

data class WorkoutModel(
    val id: Long = newID(),
    var name: String,
    var notes: String? = null, // TODO Create Note Object that takes a string and a boolean for pinned status
    var pinnedNotes: String? = null,
    var notesPinned: Boolean = false,
    var workoutDuration: Long = Duration.ZERO.inWholeMilliseconds,
    var workoutType: String? = null
//    val workoutStarted = LocalDateTime.now(), TODO
//    val workoutEnded = LocalDateTime.now(), TODO
//    var workoutDate: Date? = LocalDateTime.now()
)

fun WorkoutModel.pinNotes(pinned: Boolean) {
    notesPinned = pinned
    Log.i("pinNotes()", notesPinned.toString())
}
