package com.example.myapplication.components.setRestTimer

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import com.example.myapplication.components.WorkoutViewModel
import org.koin.androidx.compose.koinViewModel
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.toDuration

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun ManualRestTimerScreen(
  workoutViewModel: WorkoutViewModel = koinViewModel(),
  openDialog: (Boolean) -> Unit,
) {
  ManualRestTimerDialog(
    openDialog = { close -> openDialog(close) },
    startTimer = { time -> workoutViewModel.startRestTimer(time) },
  )
}

@RequiresApi(Build.VERSION_CODES.S)
@OptIn(ExperimentalComposeUiApi::class, ExperimentalAnimationApi::class)
@Composable
fun ManualRestTimerDialog(openDialog: (Boolean) -> Unit, startTimer: (Duration) -> Unit) {
  /* TODO create a function in the workoutViewModel that creates a timer and only takes a duration of time in milliseconds */

  var visible by remember { mutableStateOf(false) }
  var timeString: String by remember { mutableStateOf("") }

  val buttonModifier = Modifier.requiredSize(80.dp).padding(0.dp)

  val iconSizeModifier = Modifier.size(24.dp)

  var openTimerScreen by remember { mutableStateOf(false) }

  fun closeTimerScreen(close: Boolean) {
    openTimerScreen = close
  }

  if (openTimerScreen) {
    TimerScreen(openDialog = { close -> closeTimerScreen(close) })
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
      //            Log.i("GoogleClockTimer().remove() timeString.last()",
      // timeString.last().toString())
      timeString = timeString.dropLast(1)
      //            Log.i("setGoogleClockTimer().remove()", timeString)
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

    Log.i("setGoogleClockTimer().removeAll()", timeString)
  }

  fun convertTimeStringToDuration(timeString: String): Duration {
    val windows = timeString.padStart(6, '0').windowed(2, 2)

    val restTime =
      windows[0].toInt().toDuration(DurationUnit.HOURS) +
        windows[1].toInt().toDuration(DurationUnit.MINUTES) +
        windows[2].toInt().toDuration(DurationUnit.SECONDS)
    Log.i("setGoogleClockTimer() windows", windows.toList().toString())
    Log.i("setGoogleClockTimer() restTime", restTime.toString())

    return restTime
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
        IconButton(onClick = { openDialog(false) }) {
          Icon(
            Icons.Outlined.Close,
            contentDescription = "Localized description",
            modifier = Modifier.size(22.dp),
          )
        }
        Text(text = "Enter Time", fontSize = 28.sp, modifier = Modifier.padding(0.dp))
      }
    },
    text = {
      Column {
        Row(horizontalArrangement = Arrangement.Center) {
          Column(
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth(),
          ) {
            TimerText(time = timeString)

            Spacer(modifier = Modifier.height(20.dp))

            TimerNumberPad(
              { openDialog(false) },
              setTimer = {},
              add = { input -> add(input) },
              remove = { remove() },
              removeAll = { removeAll() },
              visible = visible,
            )
            Spacer(modifier = Modifier.height(4.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
              /*                            Button(
              onClick = { */
              /* TODO */
              /*
                  },
                  modifier = buttonModifier,
                  shape = RoundedCornerShape(100),
                  contentPadding = PaddingValues(0.dp)
              ) {
                  Icon(
                      Icons.Outlined.Delete,
                      contentDescription = "Localized description",
                      modifier = iconSizeModifier
                  )
              }*/
              Spacer(modifier = Modifier.width(80.dp))
              Box(modifier = Modifier.width(80.dp).padding(0.dp)) {
                this@Row.AnimatedVisibility(
                  visible = visible,
                  enter = scaleIn(animationSpec = tween(500)),
                  exit = scaleOut(animationSpec = tween(500)),
                ) {
                  Button(
                    onClick = {
                      startTimer(convertTimeStringToDuration(timeString))
                      openTimerScreen = true
                      openDialog(false)
                    },
                    modifier = buttonModifier,
                    shape = RoundedCornerShape(100),
                    contentPadding = PaddingValues(0.dp),
                  ) {
                    Icon(
                      Icons.Filled.PlayArrow,
                      contentDescription = "Localized description",
                      modifier = iconSizeModifier,
                    )
                  }
                }
              }
              Spacer(modifier = Modifier.width(80.dp))
            }
          }
        }
      }
    },
    confirmButton = {},
    dismissButton = { TextButton(onClick = { openDialog(false) }) { Text("Cancel") } },
  )
}
