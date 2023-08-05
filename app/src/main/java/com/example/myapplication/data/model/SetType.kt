package com.example.myapplication.data.model

import androidx.compose.ui.graphics.Color
import io.realm.kotlin.types.annotations.PrimaryKey

// TODO To Implement in Realm
// TODO Create screen to edit/create set type
data class SetType(
    @PrimaryKey val setTypeID: Int,
    val name: String,
    val description: String? = null,
    var uri: String? = null,
    var textColor: Color,
)

