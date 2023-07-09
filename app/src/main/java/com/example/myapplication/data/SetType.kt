package com.example.myapplication.data

import androidx.compose.ui.graphics.Color
import androidx.room.PrimaryKey

// @Entity(tableName = "setType")
data class SetType(
    @PrimaryKey(autoGenerate = true) val setTypeID: Int,
    val name: String,
    val description: String? = null,
    var uri: String? = null,
    var textColor: Color,
)

// TODO Create screen to edit/create set type