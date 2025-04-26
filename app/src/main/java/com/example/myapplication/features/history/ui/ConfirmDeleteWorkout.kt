package com.example.myapplication.features.history.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

@Composable
fun ConfirmDeleteWorkout(onDismissRequest: () -> Unit, deleteWorkout: () -> Unit) {
  AlertDialog(
    onDismissRequest = { onDismissRequest() },
    icon = { Icon(imageVector = Icons.Filled.Warning, contentDescription = null) },
    title = { Text(text = "Confirm Delete Workout") },
    text = { Text("Are you sure you want to delete this workout?") },
    confirmButton = {
      TextButton(
        onClick = {
          onDismissRequest()
          deleteWorkout()
        }
      ) {
        Text("Delete")
      }
    },
    dismissButton = { TextButton(onClick = { onDismissRequest() }) { Text("Cancel") } },
  )
}