@file:OptIn(ExperimentalAnimationApi::class)

package com.example.myapplication.components.setRestTimer

import android.util.Log
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime
import kotlin.time.toDuration

// @Preview
@Composable
fun RestTimerScreen(
    openDialog: (Boolean) -> Unit
) {
    RestTimerDialog(
        { close -> openDialog(close) },
        workSetRestTime = 0.toDuration(DurationUnit.SECONDS),
        warmUpSetRestTime = 90.toDuration(DurationUnit.SECONDS)
    )
}

@OptIn(ExperimentalComposeUiApi::class, ExperimentalTime::class)
@Composable
fun RestTimerDialog(
    openDialog: (Boolean) -> Unit,
    workSetRestTime: Duration,
    warmUpSetRestTime: Duration
) {
    val titles = listOf("Work Set", "Warm Up Set")
    var tabRowState by remember { mutableStateOf(0) }
    var checked by remember { mutableStateOf(true) }
    var checkedWorkSet by remember { mutableStateOf(false) }
    var checkedWarmUpSet by remember { mutableStateOf(false) }
    val timerEnabled = mutableListOf(checkedWorkSet, checkedWarmUpSet)
    var visible by remember {
        mutableStateOf(false)
    }

    val buttonModifier = Modifier
        .requiredSize(80.dp)
        /*.aspectRatio(0.5F)*/
//        .wrapContentSize(align = Alignment.Center)
        .padding(0.dp)

    val iconSizeModifier = Modifier.size(24.dp)

    val icon: (@Composable () -> Unit)? = if (timerEnabled[tabRowState]) {
        {
            Icon(
                imageVector = Icons.Filled.Check,
                contentDescription = null,
                modifier = Modifier.size(SwitchDefaults.IconSize)
            )
        }
    } else {
        null
    }

    var workSetRestTimeString by remember { mutableStateOf("") }
    var warmUpSetRestTimeString by remember { mutableStateOf("") }
    val restTimes = mutableListOf(workSetRestTimeString, warmUpSetRestTimeString)

    workSetRestTimeString = workSetRestTime.toComponents { hours, minutes, seconds, nanoseconds ->
        (
            hours.toString().padStart(2, '0') +
                minutes.toString().padStart(2, '0') +
                seconds.toString().padStart(2, '0')
            ).toInt().toString().removePrefix("0")
    }

    warmUpSetRestTimeString =
        warmUpSetRestTime.toComponents { hours, minutes, seconds, nanoseconds ->
            (
                hours.toString().padStart(2, '0') +
                    minutes.toString().padStart(2, '0') +
                    seconds.toString().padStart(2, '0')
                ).toInt().toString().removePrefix("0")
        }

    var timeString: String by remember { mutableStateOf("") }

    val windows = timeString.padStart(6, '0').windowed(2, 2)

    val restTime = windows[0].toInt().toDuration(DurationUnit.HOURS) +
        windows[1].toInt().toDuration(DurationUnit.MINUTES) +
        windows[2].toInt().toDuration(DurationUnit.SECONDS)
    Log.i("setGoogleClockTimer() windows", windows.toList().toString())
    Log.i("setGoogleClockTimer() restTime", restTime.toString())

    fun onTabChange() {
        timeString = if (tabRowState == 0) {
            workSetRestTimeString
        } else {
            warmUpSetRestTimeString
        }
    }

    fun add(input: String) {
        if (timeString.length <= 3) {
            if ((input != "0") && (input != "00") && (timeString == "")) {
                timeString = input
                Log.i("setGoogleClockTimer().add() 0 && 00", timeString)
            } else if (timeString.isNotEmpty()) {
                timeString += input
                Log.i("setGoogleClockTimer().add() timeString.length >", timeString)
            }
        }

        restTimes[tabRowState] = timeString

        if (timeString.isNotEmpty()) {
            visible = true
        }
    }

    if (timeString.isNotEmpty()) {
        visible = true
    }

    fun remove() {
        Log.i("setGoogleClockTimer().remove()", "Called")
        if (timeString.isNotEmpty()) {
//            Log.i("GoogleClockTimer().remove() timeString.last()", timeString.last().toString())
            timeString = timeString.dropLast(1)
//            Log.i("setGoogleClockTimer().remove()", timeString)
        }

        restTimes[tabRowState] = timeString

        if (timeString.isEmpty()) {
            visible = false
        }
    }

    fun removeAll() {
        if (timeString.isNotEmpty()) {
            timeString = ""
        }

        restTimes[tabRowState] = timeString

        if (timeString.isEmpty()) {
            visible = false
        }

        Log.i("setGoogleClockTimer().removeAll()", timeString)
    }

    Log.i("setRestTimerDialog() tabRowState", tabRowState.toString())
    Log.i("setRestTimerDialog() timeString", timeString)

    fun setTimer() {
        if (tabRowState == 0) {
//            TODO = restTime
        } else {
//            TODO = restTime
        }
    }

    AlertDialog(
        modifier = Modifier.fillMaxSize(),
        properties = DialogProperties(usePlatformDefaultWidth = false),
        shape = RoundedCornerShape(0),
        onDismissRequest = {
            // Dismiss the dialog when the user clicks outside the dialog or on the back
            // button. If you want to disable that functionality, simply use an empty
            // onDismissRequest.
            openDialog(false)
        },
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = { openDialog(false) }
                ) {
                    Icon(
                        Icons.Outlined.Close,
                        contentDescription = "Localized description",
                        modifier = Modifier.size(22.dp)
                    )
                }
                Text(text = "Timer", fontSize = 28.sp, modifier = Modifier.padding(0.dp))
            }
        },
        text = {
            Column {
                TabRow(selectedTabIndex = tabRowState) {
                    titles.forEachIndexed { index, title ->
                        Tab(
                            selected = tabRowState == index,
                            onClick = {
                                tabRowState = index
                                onTabChange()
                            },
                            text = {
                                Text(
                                    text = title,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(text = "Enabled", fontSize = 14.sp/*, modifier = Modifier.padding(0.dp)*/)
                    Switch(
                        modifier = Modifier.semantics {
                            contentDescription = "Enable %s Timer".format(titles[tabRowState])
                        },
                        thumbContent = icon,
                        checked = timerEnabled[tabRowState],
                        onCheckedChange = {
                            checked = it
                            if (tabRowState == 0) {
                                checkedWorkSet = it
                            } else {
                                checkedWarmUpSet = it
                            }
                        }
                    )
                }
                Row(horizontalArrangement = Arrangement.Center) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        TimerText(time = timeString)

                        Spacer(modifier = Modifier.height(20.dp))

                        TimerNumberPad(
                            { openDialog(false) },
                            /*tabRowState = tabRowState,*/
                            setTimer = { setTimer() },
                            /*workSetRestTime = workSetRestTime,
                        warmUpSetRestTime = warmUpSetRestTime,*/
                            add = { input -> add(input) },
                            remove = { remove() },
                            removeAll = { removeAll() },
                            visible = visible
                        )
                        Spacer(modifier = Modifier.height(4.dp))

                        /*Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Button(
                                onClick = { *//* TODO *//* },
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
                                            setTimer()
                                            openDialog(false)
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
                        }*/
                    }
                }
            }
        },
        confirmButton = { },
        dismissButton = { }
    )
}
