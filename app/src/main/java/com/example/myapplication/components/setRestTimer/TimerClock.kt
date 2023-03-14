package com.example.myapplication.components.setRestTimer

import androidx.compose.animation.*
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.progressSemantics
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.WorkoutViewModel

@Composable
fun TimerScreen(
    workoutViewModel: WorkoutViewModel = viewModel(),
    openDialog: (Boolean) -> Unit,
) {
    val remainingTimeString by workoutViewModel.remainingTimeString.collectAsState()
    val timerDurationString by workoutViewModel.timerDurationString.collectAsState()
    val restTimerAdjustmentString = workoutViewModel.restTimerAdjustmentString
    val timerProgress by workoutViewModel.remainingTimeProgress.collectAsState()
    val timerState by workoutViewModel.workoutTimerState.collectAsState()
    TimerClock(
        remainingTimeString,
        timerDurationString,
        restTimerAdjustmentString,
        timerProgress,
        { workoutViewModel.cancelSetCompletedTimer() },
        { workoutViewModel.moreRestTime() },
        { workoutViewModel.lessRestTime() },
        { close -> openDialog(close) },
        timerState,
        { workoutViewModel.startLastTimer() }
    )
}

// @Preview
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun TimerClock(
    remainingTimeString: String,
    timerDurationString: String,
    restTimerAdjustmentString: String,
    timerProgress: Float,
    cancelTimer: () -> Unit,
    moreTime: () -> Unit,
    lessTime: () -> Unit,
    openDialog: (Boolean) -> Unit,
    timerState: TimerState,
    startTimer: () -> Unit,
) {
//    Log.i("TimerClock(): timerProgress", "$timerProgress")

//    Log.i("Timer: ", System.currentTimeMillis().toString())
//    Log.i("TimerState: ", timerState.toString())
    val primaryColor: Color = MaterialTheme.colorScheme.primary

    var progress by remember { mutableStateOf(1f) }
    progress = timerProgress
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(
            durationMillis = 1000,
            easing = LinearEasing
        )
    )

    var remainingTimeVisible by remember {
        mutableStateOf(false)
    }

    remainingTimeVisible = timerState == TimerState.RUNNING || timerState == TimerState.EXPIRED

    var openEnterTimeScreen by remember { mutableStateOf(false) }

    fun closeEnterTimeScreen(close: Boolean) {
        openEnterTimeScreen = close
    }

    if (openEnterTimeScreen) {
        ManualRestTimerScreen(openDialog = { close -> closeEnterTimeScreen(close) })
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
                Text(text = "Rest Timer", fontSize = 28.sp, modifier = Modifier.padding(0.dp))
            }
        },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth()
            ) {
                Text(
                    "Adjust duration via the +/- buttons",
                    modifier = Modifier.align(Alignment.Start)
                )
                Box(
                    contentAlignment = Alignment.Center
                ) {
//                    CircularProgressIndicator(
//                        progress = animatedProgress,
//                        modifier = Modifier.size(200.dp)
//                    )
                    Canvas(
                        modifier = Modifier
                            .progressSemantics(animatedProgress)
                            .size(300.dp)
                    ) {
                        // Start at 12 o'clock
                        val startAngle = 270f
                        val sweep = animatedProgress * 360f
                        val stroke = Stroke(
                            width = 8.dp.toPx(),
                            cap = StrokeCap.Round
                        )

                        val diameterOffset = stroke.width / 2
                        val arcDimen = size.width - 2 * diameterOffset

                        drawArc(
                            color = Color.LightGray,
                            startAngle = 270f,
                            sweepAngle = 360f,
                            useCenter = false,
                            topLeft = Offset(diameterOffset, diameterOffset),
                            size = Size(arcDimen, arcDimen),
                            style = stroke
                        )

                        drawArc(
                            color = primaryColor,
                            startAngle = startAngle,
                            sweepAngle = sweep,
                            useCenter = false,
                            topLeft = Offset(diameterOffset, diameterOffset),
                            size = Size(arcDimen, arcDimen),
                            style = stroke
                        )
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Row { Text(text = remainingTimeString, fontSize = 60.sp) }
                        Row {
                            Text(
                                text = timerDurationString,
                                fontSize = 30.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }

                AnimatedVisibility(
                    visible = !remainingTimeVisible,
                    enter = fadeIn(animationSpec = tween(250)),
                    exit = fadeOut(animationSpec = tween(250))
                ) {
                    Row() {
                        Button(
                            onClick = { openEnterTimeScreen = true }
                        ) {
                            Text("Enter Rest Timer")
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Button(
                        onClick = { moreTime() }
                    ) {
                        Text("+$restTimerAdjustmentString secs")
                    }
                    Button(
                        onClick = { lessTime() }
                    ) {
                        Text("-$restTimerAdjustmentString secs")
                    }
                    Button(
                        onClick = {
                            cancelTimer()
//                            if (timerState == TimerState.RUNNING) cancelTimer() else startTimer()

                            openDialog(false)
                        }
                    ) {
                        Text("Skip")
//                        Text(if (timerState == TimerState.RUNNING) "Skip" else "Start")
                    }
                }
            }
        },
        confirmButton = { /* TODO */ },
        dismissButton = { /* TODO */ }
    )
}
