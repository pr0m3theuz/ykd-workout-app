package com.example.myapplication.components

import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material3.AlertDialog
import androidx.compose.runtime.Composable

@Composable
fun DiscardWorkoutDialog(
    openDialog: (Boolean) -> Unit,
    confirmCancelWorkout: (Boolean) -> Unit
) {
    AlertDialog(
        onDismissRequest = {
            // Dismiss the dialog when the user clicks outside the dialog or on the back
            // button. If you want to disable that functionality, simply use an empty
            // onCloseRequest.
            openDialog(false)
        },
        title = {
            Text(text = "Discard workout?")
        },
        text = { Text("Are you sure you want to discard this workout? This cannot be undone.") },
        confirmButton = {
            TextButton(
                onClick = {
                    openDialog(false)
                    confirmCancelWorkout(true)
                }
            ) {
                Text("Cancel Workout")
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    openDialog(false)
                    confirmCancelWorkout(false)
                }
            ) {
                Text("Resume")
            }
        }
    )
}
