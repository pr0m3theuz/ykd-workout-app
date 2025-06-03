package com.example.myapplication.features.workout.ui

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.myapplication.common.ui.theme.MyApplicationTheme

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
            Text(text = "Discard Workout?")
        },
        text = { Text("Are you sure you want to discard this workout? This cannot be undone.") },
        confirmButton = {
            Button(
                onClick = {
                    closeDialog()
                    cancelWorkout()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
              Text("DISCARD", color = MaterialTheme.colorScheme.onError)
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    closeDialog()
                }
            ) {
                Text("RESUME")
            }
        }
    )
}

@Preview
@Composable
fun PreviewDiscardWorkoutDialog() {
  MyApplicationTheme {
    DiscardWorkoutDialog(
      closeDialog = {},
      cancelWorkout = {}
    )
  }
}