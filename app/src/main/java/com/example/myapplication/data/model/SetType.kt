package com.example.myapplication.data.model

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import io.realm.kotlin.types.annotations.PrimaryKey

// TODO To Implement in Realm
// TODO Create screen to edit/create set type
data class SetType(
  @PrimaryKey val setTypeID: Int,
  val name: String,
  val description: AnnotatedString,
  var uri: String? = null,
  var lightTextColor: Color,
  var darkTextColor: Color,
  var abbreviation: String? = null,
)
