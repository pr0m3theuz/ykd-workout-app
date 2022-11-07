package com.example.myapplication.data

// @Entity(tableName = "barType")
data class BarTypeModel(
    val id: Int = 0,
    val name: String,
    var barWeightPounds: Float,
    var barWeightKilograms: Float
)
