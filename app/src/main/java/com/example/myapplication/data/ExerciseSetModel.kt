package com.example.myapplication.data

import com.example.myapplication.newID

data class ExerciseSetModel(
    val id: Long = newID(),
    val exercise: ExerciseModel,
    val sets: List<SetModel>
)
