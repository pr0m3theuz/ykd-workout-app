package com.example.myapplication.data

import android.net.Uri
import androidx.compose.ui.graphics.Color
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "setType")
data class SetType(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val name: String,
    val description: String? = null,
    var uri: Uri? = null,
    val textColor: Color,
)
