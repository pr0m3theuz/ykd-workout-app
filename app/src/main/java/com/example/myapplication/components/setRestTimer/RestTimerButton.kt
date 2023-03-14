package com.example.myapplication.components.setRestTimer

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.WorkoutViewModel

@OptIn(ExperimentalTextApi::class)
@Preview
@Composable
fun RestTimerButton(
    workoutViewModel: WorkoutViewModel = viewModel(),
) {
    var progress by remember { mutableStateOf(1f) }
    val remainingTimeProgress by workoutViewModel.remainingTimeProgress.collectAsState()
    val timerState by workoutViewModel.workoutTimerState.collectAsState()

    progress = remainingTimeProgress

    val remainingTimeString by workoutViewModel.remainingTimeString.collectAsState()
    val GradientColors = listOf(White, Black)

    val animatedSize by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(
            durationMillis = 1000,
            easing = LinearEasing
        )
    )

    val animatedEndX by animateFloatAsState(
        targetValue = 1000f * progress,
        animationSpec = tween(
            durationMillis = 1000,
            easing = LinearEasing
        )
    )

    val animatedBoxWidth by animateDpAsState(
        targetValue = if (timerState == TimerState.RUNNING) 100.dp else 40.dp
    )

    val animatedProgressBarColor by animateColorAsState(
        targetValue = if (timerState == TimerState.RUNNING) MaterialTheme.colorScheme.primary else Color.Transparent
    )

    var remainingTimeVisible by remember { mutableStateOf(false) }

    remainingTimeVisible = timerState == TimerState.RUNNING

    var openTimerScreen by remember { mutableStateOf(false) }

    fun closeTimerScreen(close: Boolean) {
        openTimerScreen = close
    }

    if (openTimerScreen) {
//        TODO Navigate to
//        TODO openTimerClockDialog
        TimerScreen(openDialog = { close -> closeTimerScreen(close) })
    }

    var openEnterTimeScreen by remember { mutableStateOf(false) }

    fun closeEnterTimeScreen(close: Boolean) {
        openEnterTimeScreen = close
    }

    if (openEnterTimeScreen) {
        ManualRestTimerScreen(openDialog = { close -> closeEnterTimeScreen(close) })
    }

    val timerIconModifier = Modifier.size(30.dp)

    val remainingTimeTextFontSize = 18.sp

    BoxWithConstraints(
        contentAlignment = Alignment.CenterStart,
        modifier = Modifier
            .size(height = 40.dp, width = animatedBoxWidth)
            .clip(RoundedCornerShape(25))
            .background(MaterialTheme.colorScheme.secondaryContainer)
            .clickable(
                onClick = {
                    if (timerState == TimerState.RUNNING) {
                        openTimerScreen = true
                    } else {
                        openEnterTimeScreen = true
                    }
                },
                onClickLabel = "Open Rest Timer",
                indication = rememberRipple(),
                interactionSource = remember { MutableInteractionSource() }
            )
    ) {
        Box(
            modifier = Modifier
                .size(width = maxWidth.times(animatedSize), height = maxHeight)
                .background(color = animatedProgressBarColor)
        ) {}

        BoxWithConstraints(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(width = maxWidth, height = maxHeight)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    Icons.Outlined.Timer,
                    contentScale = ContentScale.Crop,
                    contentDescription = "Localized description",
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary),
                    modifier = timerIconModifier
                )

                AnimatedVisibility(
                    visible = remainingTimeVisible,
                    enter = slideInHorizontally(animationSpec = tween(250)),
                    exit = slideOutHorizontally(animationSpec = tween(250))
                ) {
                    Text(
                        text = remainingTimeString,
                        fontSize = remainingTimeTextFontSize,
                        maxLines = 1,
                        overflow = TextOverflow.Clip,
                        style =
                        androidx.compose.ui.text.TextStyle(color = MaterialTheme.colorScheme.primary),
                        modifier = Modifier
                            .padding(start = 4.dp)

                    )
                }
            }
        }

        BoxWithConstraints(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(width = maxWidth, height = maxHeight)
                .drawWithContent {
                    if (layoutDirection == LayoutDirection.Rtl) {
                        clipRect(left = size.width * animatedSize) {
                            this@drawWithContent.drawContent()
                        }
                    } else {
                        clipRect(right = size.width * animatedSize) {
                            this@drawWithContent.drawContent()
                        }
                    }
                }

        ) {
            AnimatedVisibility(
                visible = remainingTimeVisible,
                enter = slideInHorizontally(animationSpec = tween(250)),
                exit = slideOutHorizontally(animationSpec = tween(250))
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        Icons.Outlined.Timer,
                        contentScale = ContentScale.Crop,
                        contentDescription = "Localized description",
                        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onPrimary),
                        modifier = timerIconModifier

                    )

                    Text(
                        text = remainingTimeString,
                        fontSize = remainingTimeTextFontSize,
                        maxLines = 1,
                        overflow = TextOverflow.Clip,
                        style =
                        androidx.compose.ui.text.TextStyle(color = MaterialTheme.colorScheme.onPrimary),
                        modifier = Modifier
                            .padding(start = 4.dp)

                    )
                }
            }
        }
    }
}
