package com.example.myapplication.data

import java.util.*

data class ProgramBlockModel(
    val id: Int = 0,
    var name: String,
    var notes: String,
    var startDate: Date,
    var endDate: Date,
    var tags: String // TODO Should be a List<String>
)
