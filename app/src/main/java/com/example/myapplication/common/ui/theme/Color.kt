package com.example.myapplication.common.ui.theme

import androidx.compose.ui.graphics.Color

val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)

val White = Color(0xFFFFFFFF)
val MetallicOrange = Color(0xFFe26310)
val Grey400 = Color(0xFFbdbdbd)
val Red = Color(0xFFFF0000)

val Ultramarine40 = Color(0xFF648FFF) // #648fff
val Indigo50 = Color(0xFF785EF0) // #785ef0
val Magenta50 = Color(0xFFDC267F) // #dc267f
val Orange40 = Color(0xFFFE6100) // #fe6100
val Gold20 = Color(0xFFFFB000) // #ffb000

object IBMColorBlindPalette {
  val colors = listOf(
    Ultramarine40,
    Indigo50,
    Magenta50,
    Orange40,
    Gold20
  )
  private var currentIndex = 0
  fun next(): Color { // Simple sequential assignment for stability
    val color = colors[currentIndex % colors.size]
    currentIndex++
    return color
  }

  // Or, implement a stable random based on an ID/hash if preferred
  fun stableColorFor(id: Any): Color {
    val hashCode = id.hashCode()
    return colors[kotlin.math.abs(hashCode) % colors.size]
  }
}
