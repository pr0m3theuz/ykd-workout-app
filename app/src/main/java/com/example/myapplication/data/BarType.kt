package com.example.myapplication.data

import com.example.myapplication.newID

// @Entity(tableName = "barType")
data class BarType(
    val id: Long = newID(),
    val name: String,
    var barWeightPounds: Double,
    var barWeightKilograms: Double
)
