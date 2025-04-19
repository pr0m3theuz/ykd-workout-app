package com.example.myapplication.features.workout.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.AlertDialog
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp

@Composable
fun WorkoutOptionsDropdownMenu(
  addWorkoutNote: () -> Unit,
  enableWorkoutNameTextfield: () -> Unit,
  workoutNameDialog: @Composable (dismissWorkoutNameDialog: () -> Unit) -> Unit,
  //  workoutName: String,
  //  setWorkoutName: (String) -> Unit,
) {
  var expanded by remember { mutableStateOf(false) }

  val (openWorkoutNameDialog, setOpenWorkoutNameDialog) = remember { mutableStateOf(false) }

  Box {
    IconButton(onClick = { expanded = true }) {
      Icon(
        Icons.Outlined.MoreVert,
        contentDescription = "Workout Options and Settings",
        tint = MaterialTheme.colorScheme.primary
      )
    }
    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
      DropdownMenuItem(
        text = { Text("Rename Workout") },
        onClick = {
          setOpenWorkoutNameDialog(true)
          expanded = false
        }
      )
      DropdownMenuItem(
        text = { Text("Add Workout Note") },
        onClick = {
          addWorkoutNote()
          expanded = false
        }
      )
      DropdownMenuItem(
        text = { Text("Adjust Start/End Time") },
        onClick = { /* TODO */
          expanded = false
        }
      )
    }
  }

  if (openWorkoutNameDialog) {
    workoutNameDialog { setOpenWorkoutNameDialog(false) }
  }
}

@Composable
fun EditWorkoutOptionsDropdownMenu(
  editWorkout: () -> Unit,
  deleteWorkout: () -> Unit,
  saveWorkoutAsTemplate: () -> Unit,
//  workoutNameDialog: @Composable (dismissWorkoutNameDialog: () -> Unit) -> Unit,
  //  workoutName: String,
  //  setWorkoutName: (String) -> Unit,
) {
  var expanded by remember { mutableStateOf(false) }

  val (openWorkoutNameDialog, setOpenWorkoutNameDialog) = remember { mutableStateOf(false) }

  Box {
    IconButton(onClick = { expanded = true }) {
      Icon(
        Icons.Outlined.MoreVert,
        contentDescription = "Workout Options and Settings",
        tint = MaterialTheme.colorScheme.primary
      )
    }
    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
      DropdownMenuItem(text = { Text("Edit Workout") }, onClick = { editWorkout() })
      DropdownMenuItem(text = { Text("Save as Template") }, onClick = { saveWorkoutAsTemplate() })
      DropdownMenuItem(text = { Text("Delete Workout") }, onClick = { deleteWorkout() })
    }
  }

//  if (openWorkoutNameDialog) {
//    workoutNameDialog { setOpenWorkoutNameDialog(false) }
//  }
}

@Composable
fun WorkoutNameDialog(
  workoutName: String,
  dismissWorkoutNameDialog: () -> Unit,
  setWorkoutName: (String) -> Unit,
) {
  var newName: String by remember { mutableStateOf(workoutName) }

  AlertDialog(
    shape = MaterialTheme.shapes.large,
    onDismissRequest = { dismissWorkoutNameDialog() },
    confirmButton = {
      TextButton(
        onClick = {
          setWorkoutName(newName)
          dismissWorkoutNameDialog()
        },
      ) {
        Text("Confirm")
      }
    },
    title = { Text(text = "Rename Workout", style = MaterialTheme.typography.titleMedium) },
    text = {
      Column {
        Row(modifier = Modifier.fillMaxWidth()) {
          Box(
            Modifier
              .clip(MaterialTheme.shapes.small)
              .background(MaterialTheme.colorScheme.primaryContainer)
              .fillMaxWidth()
          ) {
            BasicTextField(
              modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
              value = newName,
              onValueChange = { newName = it },
              textStyle = TextStyle(fontSize = MaterialTheme.typography.bodyLarge.fontSize),
            )
          }
        }
      }
    }
  )
}
