package com.example.myapplication.data

import com.example.myapplication.newID

// TODO Create screen to edit/create bar type
// TODO Could be useful for Plate Calculator Feature
// @Entity(tableName = "barType")
data class BarType(
    val id: Long = newID(),
    val name: String,
    var barWeightPounds: Double,
    var barWeightKilograms: Double
)

data class WeightUnit(
    val id: Long = newID(),
    val name: String,
    val abbreviation: String,
)
