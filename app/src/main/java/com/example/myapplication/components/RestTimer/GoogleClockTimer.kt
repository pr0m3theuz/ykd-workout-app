package com.example.myapplication.components.RestTimer

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.Backspace
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.toDuration

@OptIn(ExperimentalFoundationApi::class, ExperimentalAnimationApi::class)
// @Preview
@Composable
fun GoogleClockTimer(
    openDialog: () -> Unit,
    tabRowState: Int,
    workSetRestTime: Duration,
    warmUpSetRestTime: Duration,
    setTimer: (restTime: Duration) -> Unit,
    time: String
) {
    var timeString by remember { mutableStateOf("") }

    if (tabRowState == 0) {
        workSetRestTime.toComponents { hours, minutes, seconds, nanoseconds ->
            (
                hours.toString().padStart(2, '0') +
                    minutes.toString().padStart(2, '0') +
                    seconds.toString().padStart(2, '0')
                ).toInt().toString().removePrefix("0")
        }
    } else {
        warmUpSetRestTime.toComponents { hours, minutes, seconds, nanoseconds ->
            (
                hours.toString().padStart(2, '0') +
                    minutes.toString().padStart(2, '0') +
                    seconds.toString().padStart(2, '0')
                ).toInt().toString().removePrefix("0")
        }
    }
    Log.i("GoogleClockTimer() time", time)
    Log.i("GoogleClockTimer() timeString", timeString)

    val buttonModifier = Modifier
        .size(80.dp)
//        .wrapContentSize(align = Alignment.Center)
        .padding(0.dp)

    val iconSizeModifier = Modifier.size(24.dp)

    val buttonTextSize = 36.sp
    val timeTextSize = 48.sp
    val timeDisabledTextColor = Color.LightGray
    val timeEnabledTextColor = MaterialTheme.colorScheme.onSurface
    val timeUnitTextSize = 24.sp
    val buttonTextWeight = FontWeight(375)
    var visible by remember {
        mutableStateOf(false)
    }
    val windows = timeString.padStart(6, '0').windowed(2, 2)

    val restTime = windows[0].toInt().toDuration(DurationUnit.HOURS) +
        windows[1].toInt().toDuration(DurationUnit.MINUTES) +
        windows[2].toInt().toDuration(DurationUnit.SECONDS)
    Log.i("GoogleClockTimer() windows", windows.toList().toString())
    Log.i("GoogleClockTimer() restTime", restTime.toString())

    if (timeString.isNotEmpty()) {
        visible = true
    }

    fun add(input: String) {
        if (timeString.length <= 3) {
            if ((input != "0") && (input != "00") && (timeString == "")) {
                timeString = input
                Log.i("GoogleClockTimer().add() 0 && 00", timeString)
            } else if (timeString.isNotEmpty()) {
                timeString += input
                Log.i("GoogleClockTimer().add() timeString.length >", timeString)
            }
        }

        if (timeString.isNotEmpty()) {
            visible = true
        }
    }

    fun remove() {
        if (timeString.isNotEmpty()) {
//            Log.i("GoogleClockTimer().remove() timeString.last()", timeString.last().toString())
            timeString = timeString.dropLast(1)
            Log.i("GoogleClockTimer().remove()", timeString)
        }

        if (timeString.isEmpty()) {
            visible = false
        }
    }

    fun removeAll() {
        if (timeString.isNotEmpty()) {
            timeString = ""
        }

        if (timeString.isEmpty()) {
            visible = false
        }
    }

    Row(horizontalArrangement = Arrangement.Center) {
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row {
                Text(
                    text = when (timeString.length) {
                        5 -> timeString[0].toString().padStart(2, '0')
                        6 -> timeString.substring(0, 2)
                        else -> {
                            "00"
                        }
                    },
                    modifier = Modifier.padding(0.dp).alignByBaseline(),
                    fontSize = timeTextSize,
                    color = if (timeString.length >= 5) {
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
                    color = if (timeString.length >= 5) {
                        timeEnabledTextColor
                    } else {
                        timeDisabledTextColor
                    }
                )
                Text(
                    text = when (timeString.length) {
                        3 -> timeString[0].toString().padStart(2, '0')
                        4 -> timeString.substring(0, 2)
                        5 -> timeString.substring(1, 3)
                        6 -> timeString.substring(2, 4)
                        else -> {
                            "00"
                        }
                    },
                    modifier = Modifier.padding(0.dp).alignByBaseline(),
                    fontSize = timeTextSize,
                    color = if (timeString.length >= 3) {
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
                    color = if (timeString.length >= 3) {
                        timeEnabledTextColor
                    } else {
                        timeDisabledTextColor
                    }

                )
                Text(
                    text = when (timeString.length) {
                        1 -> timeString[0].toString().padStart(2, '0')
                        2 -> timeString.substring(0, 2)
                        3 -> timeString.substring(1, 3)
                        4 -> timeString.substring(2, 4)
                        5 -> timeString.substring(3, 5)
                        6 -> timeString.substring(4, 6)
                        else -> {
                            "00"
                        }
                    },
                    modifier = Modifier.padding(0.dp).alignByBaseline(),
                    fontSize = timeTextSize,
                    color = if (timeString.isNotEmpty()) {
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
                    color = if (timeString.isNotEmpty()) {
                        timeEnabledTextColor
                    } else {
                        timeDisabledTextColor
                    }

                )
            }
            Spacer(modifier = Modifier.height(20.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Button(
                    onClick = { add("1") },
                    modifier = buttonModifier,
                    shape = RoundedCornerShape(100),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text(
                        "1",
                        fontSize = buttonTextSize,
                        fontWeight = buttonTextWeight
                    )
                }
                Button(
                    onClick = { add("2") },
                    modifier = buttonModifier,
                    shape = RoundedCornerShape(100),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text(
                        "2",
                        fontSize = buttonTextSize,
                        fontWeight = buttonTextWeight
                    )
                }
                Button(
                    onClick = { add("3") },
                    modifier = buttonModifier,
                    shape = RoundedCornerShape(100),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text(
                        "3",
                        fontSize = buttonTextSize,
                        fontWeight = buttonTextWeight
                    )
                }
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Button(
                    onClick = { add("4") },
                    modifier = buttonModifier,
                    shape = RoundedCornerShape(100),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text(
                        "4",
                        fontSize = buttonTextSize,
                        fontWeight = buttonTextWeight
                    )
                }
                Button(
                    onClick = { add("5") },
                    modifier = buttonModifier,
                    shape = RoundedCornerShape(100),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text(
                        "5",
                        fontSize = buttonTextSize,
                        fontWeight = buttonTextWeight
                    )
                }
                Button(
                    onClick = { add("6") },
                    modifier = buttonModifier,
                    shape = RoundedCornerShape(100),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text(
                        "6",
                        fontSize = buttonTextSize,
                        fontWeight = buttonTextWeight
                    )
                }
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Button(
                    onClick = { add("7") },
                    modifier = buttonModifier,
                    shape = RoundedCornerShape(100),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text(
                        "7",
                        fontSize = buttonTextSize,
                        fontWeight = buttonTextWeight
                    )
                }
                Button(
                    onClick = { add("8") },
                    modifier = buttonModifier,
                    shape = RoundedCornerShape(100),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text(
                        "8",
                        fontSize = buttonTextSize,
                        fontWeight = buttonTextWeight
                    )
                }
                Button(
                    onClick = { add("9") },
                    modifier = buttonModifier,
                    shape = RoundedCornerShape(100),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text(
                        "9",
                        fontSize = buttonTextSize,
                        fontWeight = buttonTextWeight
                    )
                }
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Button(
                    onClick = {
                        add("00")
                    },
                    modifier = buttonModifier,
                    shape = RoundedCornerShape(100),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text(
                        text = "00",
                        fontSize = buttonTextSize,
                        fontWeight = buttonTextWeight,
                        overflow = TextOverflow.Visible
                    )
                }
                Button(
                    onClick = { add("0") },
                    modifier = buttonModifier,
                    shape = RoundedCornerShape(100),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text(
                        "0",
                        fontSize = buttonTextSize,
                        fontWeight = buttonTextWeight
                    )
                }
                Button(
                    onClick = { remove() },
                    modifier =
                    Modifier.combinedClickable(
                        onLongClickLabel = "Clear All",
//                            TODO May not work
                        onLongClick = { removeAll() },
                        onClickLabel = "Backspace",
                        onClick = { remove() }
                    ).then(buttonModifier),
                    shape = RoundedCornerShape(100),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Icon(
                        Icons.Outlined.Backspace,
                        contentDescription = "Localized description",
                        modifier = iconSizeModifier
                    )
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Button(
                    onClick = { /* TODO */ },
                    modifier = buttonModifier,
                    shape = RoundedCornerShape(100),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Icon(
                        Icons.Outlined.Delete,
                        contentDescription = "Localized description",
                        modifier = iconSizeModifier
                    )
                }
                Box(modifier = Modifier.width(80.dp).padding(0.dp)) {
                    this@Row.AnimatedVisibility(
                        visible = visible,
                        enter = scaleIn(animationSpec = tween(500)),
                        exit = scaleOut(animationSpec = tween(500))
                    ) {
                        Button(
                            onClick = {
                                setTimer(restTime)
                                openDialog()
                            },
                            modifier = buttonModifier,
                            shape = RoundedCornerShape(100),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Icon(
                                Icons.Filled.PlayArrow,
                                contentDescription = "Localized description",
                                modifier = iconSizeModifier
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.width(80.dp))
            }
        }
    }
}
