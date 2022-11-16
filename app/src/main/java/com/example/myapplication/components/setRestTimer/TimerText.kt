package com.example.myapplication.components.setRestTimer

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalFoundationApi::class, ExperimentalAnimationApi::class)
// @Preview
@Composable
fun TimerText(time: String) {
    val timeTextSize = 48.sp
    val timeDisabledTextColor = Color.LightGray
    val timeEnabledTextColor = MaterialTheme.colorScheme.onSurface
    val timeUnitTextSize = 24.sp

    Row {
        Text(
            text = when (time.length) {
                5 -> time[0].toString().padStart(2, '0')
                6 -> time.substring(0, 2)
                else -> {
                    "00"
                }
            },
            modifier = Modifier.padding(0.dp).alignByBaseline(),
            fontSize = timeTextSize,
            color = if (time.length >= 5) {
                timeEnabledTextColor
            } else {
                timeDisabledTextColor
            }
        )
        Text(
            text = String.format(
                "h"
            ),
            modifier = Modifier.alignByBaseline().padding(end = 12.dp),
            fontSize = timeUnitTextSize,
            fontWeight = FontWeight.Light,
            color = if (time.length >= 5) {
                timeEnabledTextColor
            } else {
                timeDisabledTextColor
            }
        )
        Text(
            text = when (time.length) {
                3 -> time[0].toString().padStart(2, '0')
                4 -> time.substring(0, 2)
                5 -> time.substring(1, 3)
                6 -> time.substring(2, 4)
                else -> {
                    "00"
                }
            },
            modifier = Modifier.padding(0.dp).alignByBaseline(),
            fontSize = timeTextSize,
            color = if (time.length >= 3) {
                timeEnabledTextColor
            } else {
                timeDisabledTextColor
            }
        )
        Text(
            text = String.format(
                "m"
            ),
            modifier = Modifier.alignByBaseline().padding(end = 12.dp),
            fontSize = timeUnitTextSize,
            fontWeight = FontWeight.Light,
            color = if (time.length >= 3) {
                timeEnabledTextColor
            } else {
                timeDisabledTextColor
            }

        )
        Text(
            text = when (time.length) {
                1 -> time[0].toString().padStart(2, '0')
                2 -> time.substring(0, 2)
                3 -> time.substring(1, 3)
                4 -> time.substring(2, 4)
                5 -> time.substring(3, 5)
                6 -> time.substring(4, 6)
                else -> {
                    "00"
                }
            },
            modifier = Modifier.padding(0.dp).alignByBaseline(),
            fontSize = timeTextSize,
            color = if (time.isNotEmpty()) {
                timeEnabledTextColor
            } else {
                timeDisabledTextColor
            }
        )
        Text(
            text = String.format(
                "s"
            ),
            modifier = Modifier.alignByBaseline(),
            fontSize = timeUnitTextSize,
            fontWeight = FontWeight.Light,
            color = if (time.isNotEmpty()) {
                timeEnabledTextColor
            } else {
                timeDisabledTextColor
            }

        )
    }
}
