package com.example.myapplication

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner

// **
// handle lifecycle events when app goes to background and comes back
// this handles closing and opening the database properly
// https://developer.android.com/jetpack/compose/side-effects#disposableeffect
// **
@Composable
fun MainView(startDatabase: () -> Unit, closeDatabase: () -> Unit) {
  val lifecycleOwner = LocalLifecycleOwner.current

  // Safely update the current lambdas when a new one is provided
  val currentOnStart by rememberUpdatedState(startDatabase)
  val currentOnStop by rememberUpdatedState(closeDatabase)

  // if lifecycleOwner changes, dispose and reset the effect
  DisposableEffect(lifecycleOwner) {
    // Create an observer that triggers our remembered callbacks
    val observer = LifecycleEventObserver { _, event ->
      when (event) {
        //        Restore Data from Active Workout
        Lifecycle.Event.ON_RESUME -> {
          Log.w(
            "event",
            "DEBUG:  DANGER, WILL ROBINSON!!  opening the database due to event: ${event.name}",
          )
          currentOnStart()
        }

        //        Save Data from Active Workout
        Lifecycle.Event.ON_PAUSE,
        Lifecycle.Event.ON_STOP -> {
          Log.w(
            "event",
            "DEBUG:  DANGER, WILL ROBINSON!!  closing the database due to event: ${event.name}",
          )
          currentOnStop()
        }

        else -> {
          Log.w(
            "event",
            "DEBUG:  DANGER, WILL ROBINSON!!  Event happened that we don't handle ${event.name}",
          )
        }
      }
    }
    // Add the observer to the lifecycle
    lifecycleOwner.lifecycle.addObserver(observer)

    // When the effect leaves the Composition, remove the observer
    onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
  }
}