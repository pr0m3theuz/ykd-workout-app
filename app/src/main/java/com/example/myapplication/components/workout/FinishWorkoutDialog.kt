package com.example.myapplication.components

import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material3.AlertDialog
import androidx.compose.runtime.Composable

@Composable
fun FinishWorkoutDialog(
    openDialog: (Boolean) -> Unit,
    confirmFinishWorkout: (Boolean) -> Unit
) {
    AlertDialog(
        onDismissRequest = {
            // Dismiss the dialog when the user clicks outside the dialog or on the back
            // button. If you want to disable that functionality, simply use an empty
            // onCloseRequest.
            openDialog(false)
        },
        title = {
            Text(text = "Finish workout?")
        },
        text = { Text("Are you sure you want to discard this workout? This cannot be undone.") },
        confirmButton = {
            TextButton(
                onClick = {
                    openDialog(false)
                    confirmFinishWorkout(true)
                }
            ) {
                Text("Finish")
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    openDialog(false)
                    confirmFinishWorkout(false)
                }
            ) {
                Text("Cancel")
            }
        }
    )
}

/* TODO
    "Are you finished?"
    "All invalid/empty sets will be discarded. All sets with valid data will be automatically marked as complete."
    "Finish Workout"
*/

@Composable
fun PartialFinishWorkoutDialog(
    openDialog: (Boolean) -> Unit,
    confirmFinishWorkout: (Boolean) -> Unit
) {
    AlertDialog(
        onDismissRequest = {
            // Dismiss the dialog when the user clicks outside the dialog or on the back
            // button. If you want to disable that functionality, simply use an empty
            // onCloseRequest.
            openDialog(false)
        },
        title = {
            Text(text = "Are you finished?")
        },
        text = { Text("All invalid/empty sets will be discarded. All sets with valid data will be automatically marked as complete.") },
        confirmButton = {
            TextButton(
                onClick = {
                    openDialog(false)
                    confirmFinishWorkout(true)
                }
            ) {
                Text("Finish Workout")
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    openDialog(false)
                    confirmFinishWorkout(false)
                }
            ) {
                Text("Cancel")
            }
        }
    )
}
