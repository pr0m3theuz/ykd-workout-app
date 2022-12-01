package com.example.myapplication.components.setRestTimer

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.progressSemantics
import androidx.compose.material3.Button
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.WorkoutViewModel
import com.example.myapplication.ui.theme.Purple40

@Composable
fun TimerScreen(
    workoutViewModel: WorkoutViewModel = viewModel()
) {
    val remainingTimeString by workoutViewModel.timeString.collectAsState()
    val timerProgress by workoutViewModel.remainingTimeProgress.collectAsState()
    TimerClock(
        remainingTimeString,
        timerProgress,
        { workoutViewModel.cancelSetCompletedTimer() },
        { workoutViewModel.moreRestTime() },
        { workoutViewModel.lessRestTime() }
    )
}

// @Preview
@Composable
fun TimerClock(
    remainingTimeString: String,
    timerProgress: Float,
    cancelTimer: () -> Unit,
    moreTime: () -> Unit,
    lessTime: () -> Unit
) {
    val progress by remember { mutableStateOf(timerProgress) }
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxHeight()
    ) {
        Text("Adjust duration via the +/- buttons", modifier = Modifier.align(Alignment.Start))
        Box(
            contentAlignment = Alignment.Center
        ) {
//            CircularProgressIndicator(
//                progress = animatedProgress,
//                modifier = Modifier.size(200.dp)
//            )
            Canvas(
                modifier = Modifier
                    .progressSemantics(animatedProgress)
                    .size(200.dp)
            ) {
                // Start at 12 o'clock
                val startAngle = 270f
                val sweep = animatedProgress * 360f
                val stroke = Stroke(
                    width = ProgressIndicatorDefaults.CircularStrokeWidth.toPx(),
                    cap = StrokeCap.Round
                )

                val diameterOffset = stroke.width / 2
                val arcDimen = size.width - 2 * diameterOffset

                drawArc(
                    color = Purple40,
                    startAngle = startAngle,
                    sweepAngle = sweep,
                    useCenter = false,
                    topLeft = Offset(diameterOffset, diameterOffset),
                    size = Size(arcDimen, arcDimen),
                    style = stroke
                )
            }

            Text(remainingTimeString)
        }
        Row {
            Button(
                onClick = { moreTime() }
            ) {
                Text("+ ??? secs")
            }
            Button(
                onClick = { lessTime() }
            ) {
                Text("- ??? secs")
            }
            Button(
                onClick = { cancelTimer() }
            ) {
                Text("Skip")
            }
        }
    }
}
