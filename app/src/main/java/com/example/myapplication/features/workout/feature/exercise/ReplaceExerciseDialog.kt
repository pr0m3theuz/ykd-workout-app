package com.example.myapplication.features.workout.feature.exercise

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

// TODO Complete this Feature
// TODO Fix Dark Mode Theme

@Composable
fun ReplaceExerciseDialog(
  openDialog: (Boolean) -> Unit,
  closeMenu: () -> Unit,
  openReplaceExerciseScreen: (Boolean) -> Unit,
) {

  AlertDialog(
    onDismissRequest = {
      // Dismiss the dialog when the user clicks outside the dialog or on the back
      // button. If you want to disable that functionality, simply use an empty
      // onCloseRequest.
      openDialog(false)
    },
    title = { Text(text = "Replace Exercise?", color = MaterialTheme.colorScheme.onSurface) },
    text = {
      Text(
        "The sets that you've already added to this exercise will be replaced.",
        color = MaterialTheme.colorScheme.onSurface,
      )
    },
    confirmButton = {
      TextButton(
        onClick = {
          openDialog(false)
          closeMenu()
          openReplaceExerciseScreen(true)
        }
      ) {
        Text("Replace", color = MaterialTheme.colorScheme.primary)
      }
    },
    dismissButton = {
      TextButton(onClick = { openDialog(false) }) {
        Text("Cancel", color = MaterialTheme.colorScheme.error)
      }
    },
  )
}
