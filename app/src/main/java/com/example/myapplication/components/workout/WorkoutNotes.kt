package com.example.myapplication.components.exercise

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.example.myapplication.components.WorkoutViewModel
import com.example.myapplication.data.model.WorkoutNote
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalAnimationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun WorkoutNoteTextField(
    workoutNote: WorkoutNote,
    workoutViewModel: WorkoutViewModel = koinViewModel(),
    placeholder: String = "Write a note",
) {
  //        TODO Refactor this to workoutNotes

  var note by remember { mutableStateOf(TextFieldValue(workoutNote.note ?: "")) }
  val (pinned, onPin) = remember { mutableStateOf(workoutNote.pinned) }

  var placeholderText by remember { mutableStateOf(placeholder) }

  val noteButtonContainerColor =
    if (pinned) {
      MaterialTheme.colorScheme.primary.copy(1f)
    } else {
      MaterialTheme.colorScheme.primary.copy(0.25f)
    }

  val noteButtonContentColor =
    if (pinned) {
      MaterialTheme.colorScheme.onPrimary
    } else {
      MaterialTheme.colorScheme.primary.copy(0.75f)
    }

  val animateNoteButtonContainerColor = animateColorAsState(noteButtonContainerColor)
  val animateNoteButtonContentColor = animateColorAsState(noteButtonContentColor)

  if (note.text == "" && workoutNote.pinned) {
    placeholderText = "Pin a note for next time"
  } else if (note.text == "") {
    placeholderText = placeholder
  }

  //    TODO fun updateExerciseNotes() updates notes when workout is completed

//  Log.i("WorkoutNoteTextField()", "pinned: ${workoutNote.pinned}")

  Card(
    shape = RoundedCornerShape(CornerSize(0f)),
    backgroundColor = MaterialTheme.colorScheme.background
  ) {
    TextField(
        placeholder = { Text(placeholderText, color = MaterialTheme.colorScheme.secondary) },
        modifier = Modifier
            .padding(/*horizontal = */ 8.dp)
            .fillMaxWidth(),
        textStyle = MaterialTheme.typography.bodyMedium,
        value = note,
        onValueChange = {
            note = it
            workoutViewModel.setWorkoutNote(workoutNote, it.text)
        },
        trailingIcon = {
            Button(
                onClick = {
                    onPin(!pinned)
                    workoutViewModel.pinWorkoutNote(workoutNote, !workoutNote.pinned)
                },
                shape = MaterialTheme.shapes.small,
                contentPadding = PaddingValues(0.dp),
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .size(36.dp),
                colors =
                ButtonDefaults.buttonColors(
                    containerColor = animateNoteButtonContainerColor.value,
                    contentColor = animateNoteButtonContentColor.value
                )
            ) {
                Icon(
                    Icons.Default.PushPin,
                    contentDescription = "Localized description",
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    )
  }
}
