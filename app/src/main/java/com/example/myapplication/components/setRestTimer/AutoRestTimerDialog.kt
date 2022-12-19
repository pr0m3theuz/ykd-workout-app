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
import com.example.myapplication.data.ExerciseModel
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime
import kotlin.time.toDuration

// @Preview
@Composable
fun AutoRestTimerScreen(
    openDialog: (Boolean) -> Unit,
    exercise: ExerciseModel
) {
    AutoRestTimerDialog(
        { close -> openDialog(close) },
        exercise,
        workSetRestTime = exercise.workingSetRestDuration,
        isWorkingSetRestTimerEnabled = exercise.workingSetRestTimerEnabled,
        warmUpSetRestTime = exercise.warmUpSetRestDuration,
        isWarmUpSetRestTimerEnabled = exercise.warmUpSetRestTimerEnabled
    )
}

@OptIn(ExperimentalComposeUiApi::class, ExperimentalTime::class)
@Composable
fun AutoRestTimerDialog(
    openDialog: (Boolean) -> Unit,
    exercise: ExerciseModel,
    workSetRestTime: Duration,
    isWorkingSetRestTimerEnabled: Boolean,
    warmUpSetRestTime: Duration,
    isWarmUpSetRestTimerEnabled: Boolean

) {
    val titles = listOf("Work Set", "Warm Up Set")
    var tabRowState by remember { mutableStateOf(0) }
    var checked by remember { mutableStateOf(true) }
    var enableWorkSetRestTimer by remember { mutableStateOf(isWorkingSetRestTimerEnabled) }
    var enableWarmUpSetRestTimer by remember { mutableStateOf(isWarmUpSetRestTimerEnabled) }
    val timerEnabled = mutableListOf(enableWorkSetRestTimer, enableWarmUpSetRestTimer)
    var visible by remember { mutableStateOf(false) }

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

    workSetRestTimeString =
        workSetRestTime.toComponents { hours, minutes, seconds, nanoseconds ->
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

//    val restTimesList = mutableListOf(workSetRestTime, warmUpSetRestTime)
    val restTimesStringsList = mutableListOf(workSetRestTimeString, warmUpSetRestTimeString)

    var timeString: String by remember { mutableStateOf(workSetRestTimeString) }

//    TODO Rename windows to better name

    fun convertTimeStringToDuration(timeString: String): Duration {
        val windows = timeString.padStart(6, '0').windowed(2, 2)

        val restTime = windows[0].toInt().toDuration(DurationUnit.HOURS) +
            windows[1].toInt().toDuration(DurationUnit.MINUTES) +
            windows[2].toInt().toDuration(DurationUnit.SECONDS)
        Log.i("setGoogleClockTimer() windows", windows.toList().toString())
        Log.i("setGoogleClockTimer() restTime", restTime.toString())

        return restTime
    }

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

        restTimesStringsList[tabRowState] = timeString

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

        restTimesStringsList[tabRowState] = timeString

        if (timeString.isEmpty()) {
            visible = false
        }
    }

    fun removeAll() {
        if (timeString.isNotEmpty()) {
            timeString = ""
        }

        restTimesStringsList[tabRowState] = timeString

        if (timeString.isEmpty()) {
            visible = false
        }

        Log.i("setGoogleClockTimer().removeAll()", timeString)
    }

    Log.i("setRestTimerDialog() tabRowState", tabRowState.toString())
    Log.i("setRestTimerDialog() timeString", timeString)

    //    TODO Doesn't change enable or disable. FIX!
    fun changeRestTimerStatus() {
        if (tabRowState == 1) {
            exercise.warmUpSetRestTimerEnabled = enableWarmUpSetRestTimer
        } else if (tabRowState == 0) {
            exercise.workingSetRestTimerEnabled = enableWorkSetRestTimer
        }
    }

    //    TODO Doesn't update duration. FIX!
    fun setRestTimersDuration() {
        if (tabRowState == 1) {
            exercise.warmUpSetRestDuration =
                convertTimeStringToDuration(restTimesStringsList[tabRowState])
        } else if (tabRowState == 0) {
            exercise.workingSetRestDuration =
                convertTimeStringToDuration(restTimesStringsList[tabRowState])
        }

        changeRestTimerStatus()
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
                                enableWorkSetRestTimer = it
                            } else {
                                enableWarmUpSetRestTimer = it
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
                            setTimer = { setRestTimersDuration() },
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
        confirmButton = {
            TextButton(
                onClick = {
                    openDialog(false)
                    setRestTimersDuration()
                }
            ) {
                Text("Confirm")
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    openDialog(false)
                }
            ) {
                Text("Cancel")
            }
        }
    )
}
