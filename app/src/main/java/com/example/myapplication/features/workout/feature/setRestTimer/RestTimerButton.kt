package com.example.myapplication.features.workout.feature.setRestTimer

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material.ripple
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.features.workout.WorkoutViewModel

@RequiresApi(Build.VERSION_CODES.S)
@OptIn(ExperimentalTextApi::class)
// @Preview
@Composable
fun RestTimerButton(workoutViewModel: WorkoutViewModel) {
  var progress by remember { mutableFloatStateOf(1f) }
  val remainingTimeProgress by workoutViewModel.remainingTimeProgress.collectAsState()
  val timerState by workoutViewModel.workoutTimerState.collectAsState()

  progress = remainingTimeProgress

  val remainingTimeString by workoutViewModel.remainingTimeString.collectAsState()
  val GradientColors = listOf(White, Black)

  val animatedSize by
    animateFloatAsState(
      targetValue = progress,
      animationSpec = tween(durationMillis = 1000, easing = LinearEasing),
    )

  val animatedEndX by
    animateFloatAsState(
      targetValue = 1000f * progress,
      animationSpec = tween(durationMillis = 1000, easing = LinearEasing),
    )

  val animatedBoxWidth by
    animateDpAsState(targetValue = if (timerState == TimerState.RUNNING) 100.dp else 40.dp)

  val animatedProgressBarColor by
    animateColorAsState(
      targetValue =
        if (timerState == TimerState.RUNNING) MaterialTheme.colorScheme.primary
        else Color.Transparent
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
    TimerScreen(
      workoutViewModel = workoutViewModel,
      openDialog = { close -> closeTimerScreen(close) },
    )
  }

  var openEnterTimeScreen by remember { mutableStateOf(false) }

  fun closeEnterTimeScreen(close: Boolean) {
    openEnterTimeScreen = close
  }

  if (openEnterTimeScreen) {
    ManualRestTimerScreen(
      workoutViewModel = workoutViewModel,
      openDialog = { close -> closeEnterTimeScreen(close) },
    )
  }

  val timerIconModifier = Modifier.size(24.dp)

  val remainingTimeTextFontSize = 18.sp

  BoxWithConstraints(
    contentAlignment = Alignment.CenterStart,
    modifier =
      Modifier
        .size(height = 40.dp, width = animatedBoxWidth)
        .clip(RoundedCornerShape(25))
        .background(MaterialTheme.colorScheme.primaryContainer)
        .clickable(
          onClick = {
            if (timerState == TimerState.RUNNING) {
              openTimerScreen = true
            } else {
              openEnterTimeScreen = true
            }
          },
          onClickLabel = "Open Rest Timer",
          indication = ripple(),
          interactionSource = remember { MutableInteractionSource() },
        ),
  ) {
    Box(
      modifier =
        Modifier
          .size(width = maxWidth.times(animatedSize), height = maxHeight)
          .background(color = animatedProgressBarColor)
    ) {}

    Box(
      contentAlignment = Alignment.Center,
      modifier = Modifier.size(width = maxWidth, height = maxHeight),
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Image(
          Icons.Outlined.Timer,
          contentScale = ContentScale.Crop,
          contentDescription = "Localized description",
          colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onPrimaryContainer),
          modifier = timerIconModifier,
        )

        AnimatedVisibility(
          visible = remainingTimeVisible,
          enter = slideInHorizontally(animationSpec = tween(250)),
          exit = slideOutHorizontally(animationSpec = tween(250)),
        ) {
          Text(
            text = remainingTimeString,
            fontSize = remainingTimeTextFontSize,
            maxLines = 1,
            overflow = TextOverflow.Clip,
            style = androidx.compose.ui.text.TextStyle(color = MaterialTheme.colorScheme.onPrimaryContainer),
            modifier = Modifier.padding(start = 4.dp),
          )
        }
      }
    }

    Box(
      contentAlignment = Alignment.Center,
      modifier =
        Modifier
          .size(width = maxWidth, height = maxHeight)
          .drawWithContent {
            if (layoutDirection == LayoutDirection.Rtl) {
              clipRect(left = size.width * animatedSize) { this@drawWithContent.drawContent() }
            } else {
              clipRect(right = size.width * animatedSize) { this@drawWithContent.drawContent() }
            }
          },
    ) {
      AnimatedVisibility(
        visible = remainingTimeVisible,
        enter = slideInHorizontally(animationSpec = tween(250)),
        exit = slideOutHorizontally(animationSpec = tween(250)),
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Image(
            Icons.Outlined.Timer,
            contentScale = ContentScale.Crop,
            contentDescription = "Localized description",
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onPrimary),
            modifier = timerIconModifier,
          )

          Text(
            text = remainingTimeString,
            fontSize = remainingTimeTextFontSize,
            maxLines = 1,
            overflow = TextOverflow.Clip,
            style = androidx.compose.ui.text.TextStyle(color = MaterialTheme.colorScheme.onPrimary),
            modifier = Modifier.padding(start = 4.dp),
          )
        }
      }
    }
  }
}
