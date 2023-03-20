package com.example.myapplication.data

import android.util.Log
import com.example.myapplication.newID
import java.time.LocalDateTime
import kotlin.time.Duration

data class WorkoutModel(
    val id: Long = newID(),
    var name: String,
    var notes: String? = null, // TODO Create Note Object that takes a string and a boolean for pinned status
    var pinnedNotes: String? = null,
    var notesPinned: Boolean = false,
    var workoutDuration: Long = Duration.ZERO.inWholeMilliseconds,
    var workoutType: String? = null,
    val workoutStarted: LocalDateTime = LocalDateTime.now(),
    var workoutEnded: LocalDateTime? = null,
)

fun WorkoutModel.pinNotes(pinned: Boolean) {
    notesPinned = pinned
    Log.i("pinNotes()", notesPinned.toString())
}
