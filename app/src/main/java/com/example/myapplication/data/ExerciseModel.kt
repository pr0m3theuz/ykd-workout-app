package com.example.myapplication.data

data class ExerciseModel(
    val id: Int = 0,
    var name: String,
    val exerciseCategory: String,
    val bodyPart: String,
    var notes: String, // TODO Create Note Object that takes a string and a boolean for pinned status
    var barType: String, // TODO Could be :BarType
    var weight: String // TODO Could be ENUM -> Default, KG, LBS
)
