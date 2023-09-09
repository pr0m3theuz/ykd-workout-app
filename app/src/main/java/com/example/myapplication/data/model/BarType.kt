package com.example.myapplication.data.model

import com.example.myapplication.utils.newID

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
