package com.example.myapplication.features.history

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

@Composable
fun ConfirmChangesToWorkout(onDismissRequest: () -> Unit, saveWorkout: () -> Unit) {
  AlertDialog(
    onDismissRequest = { onDismissRequest() },
    icon = { Icon(imageVector = Icons.Filled.Warning, contentDescription = null) },
    title = { Text(text = "Save Changes To Workout") },
    text = { Text("Are you sure you want to save the changes to this workout?") },
    confirmButton = {
      TextButton(
        onClick = {
          onDismissRequest()
          saveWorkout()
        }
      ) {
        Text("Save")
      }
    },
    dismissButton = { TextButton(onClick = { onDismissRequest() }) { Text("Cancel") } },
  )
}