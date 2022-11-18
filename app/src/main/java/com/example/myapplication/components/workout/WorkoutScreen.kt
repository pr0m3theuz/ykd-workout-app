package com.example.myapplication.components.workout

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.myapplication.components.DiscardWorkoutDialog
import com.example.myapplication.components.exercise.ExerciseScreen
import com.example.myapplication.components.workoutOptionsDropdownMenu

// TODO Takes a Workout Object
@Composable
fun WorkoutScreen() {
    val openDiscardWorkoutDialog = remember { mutableStateOf(false) }

    fun closeDiscardWorkoutDialog(close: Boolean) {
        openDiscardWorkoutDialog.value = close
    }

    if (openDiscardWorkoutDialog.value) {
        DiscardWorkoutDialog(
            openDialog = { close -> closeDiscardWorkoutDialog(close) },
            confirmCancelWorkout = { /* TODO cancel -> (cancel) */ }
        )
    }

    Row {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(modifier = Modifier.padding(8.dp), text = "Workout Title")

                workoutOptionsDropdownMenu()
            }
            Text(modifier = Modifier.padding(8.dp), text = "Workout Duration")
            Text(modifier = Modifier.padding(8.dp), text = "Workout Notes")

            ExerciseScreen()

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = { /* TODO Do something! */ }) {
                    Text("Add Exercise")
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = { closeDiscardWorkoutDialog(true) }) {
                    Text("Cancel Workout", color = Color.Red)
                }
            }
        }
    }
}
