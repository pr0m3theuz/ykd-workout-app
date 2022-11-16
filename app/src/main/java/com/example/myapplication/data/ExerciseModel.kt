package com.example.myapplication.data

import kotlin.time.DurationUnit

data class ExerciseModel(
    val id: Int = 0,
    var name: String,
    val exerciseCategory: String,
    val bodyPart: String,
    var notes: String, // TODO Create Note Object that takes a string and a boolean for pinned status
    var pinnedNotes: String,
    var notesPinned: Boolean,
    var barType: String, // TODO Could be :BarType
    var weight: String, // TODO Could be ENUM -> Default, KG, LBS
    var workingSetRest: DurationUnit,
    var warmUpSetRest: DurationUnit
)
