package com.example.myapplication.features.workout.ui

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

@Composable
fun DiscardWorkoutDialog(
    closeDialog: () -> Unit,
    cancelWorkout: () -> Unit
) {
    AlertDialog(
        onDismissRequest = {
            // Dismiss the dialog when the user clicks outside the dialog or on the back
            // button. If you want to disable that functionality, simply use an empty
            // onCloseRequest.
            closeDialog()
        },
        title = {
            Text(text = "Discard workout?")
        },
        text = { Text("Are you sure you want to discard this workout? This cannot be undone.") },
        confirmButton = {
            TextButton(
                onClick = {
                    closeDialog()
                    cancelWorkout()
                }
            ) {
                Text("Cancel Workout", color = MaterialTheme.colorScheme.error)
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    closeDialog()
                }
            ) {
                Text("Resume")
            }
        }
    )
}
