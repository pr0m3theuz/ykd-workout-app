package com.example.myapplication.components.exercise

import android.util.Log
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.DismissDirection
import androidx.compose.material.DismissValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FractionalThreshold
import androidx.compose.material.SwipeToDismiss
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.rememberDismissState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.example.myapplication.data.ExerciseNote
import com.example.myapplication.ui.theme.White
import kotlinx.coroutines.launch

private const val infoTag = "ExerciseLayout().pinNote()"

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ExerciseNotes(
  exerciseNote: ExerciseNote,
  _pinNote: (pin: Boolean) -> Unit,
  placeholder: String = "Write a note",
) {
  Log.i("ExerciseNotes", "$exerciseNote")

  var note by remember { mutableStateOf(TextFieldValue(exerciseNote.note ?: "")) }

  var placeholderText by remember { mutableStateOf(placeholder) }

  //    var notesVisible by remember { mutableStateOf(exercise.pinned) }
  var notesPinned by remember { mutableStateOf(exerciseNote.pinned) }

  fun pinNote() {
    exerciseNote.pinned = !exerciseNote.pinned // TODO Add callback function
    notesPinned = exerciseNote.pinned
    _pinNote(exerciseNote.pinned)
    //    Log.i(infoTag, exerciseNote.pinned.toString())
    //    Log.i(infoTag, notesPinned.toString())
  }

  val noteButtonContainerColor =
    if (notesPinned) {
      MaterialTheme.colorScheme.primary.copy(1f)
    } else {
      MaterialTheme.colorScheme.primary.copy(0.25f)
    }

  val noteButtonContentColor =
    if (notesPinned) {
      MaterialTheme.colorScheme.onPrimary
    } else {
      MaterialTheme.colorScheme.primary.copy(0.75f)
    }

  val animateNoteButtonContainerColor = animateColorAsState(noteButtonContainerColor)
  val animateNoteButtonContentColor = animateColorAsState(noteButtonContentColor)

  if (exerciseNote.note == "" && notesPinned) {
    placeholderText = "Pin a note for next time"
  } else if (exerciseNote.note == "") {
    placeholderText = placeholder
  }

  Card(
    shape = RoundedCornerShape(CornerSize(0f)),
    backgroundColor = MaterialTheme.colorScheme.background
  ) {
    TextField(
      placeholder = { Text(placeholderText, color = MaterialTheme.colorScheme.secondary) },
      modifier = Modifier
        .padding(horizontal = 8.dp)
        .fillMaxWidth(),
      textStyle = MaterialTheme.typography.bodyMedium,
      value = note,
      onValueChange = { note = it },
      trailingIcon = {
        Button(
          onClick = { pinNote() },
          shape = MaterialTheme.shapes.small,
          contentPadding = PaddingValues(0.dp),
          modifier = Modifier
            .padding(8.dp)
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

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SwipeToDismissItem(
  onDismissRequest: () -> Unit,
  dismissStateBoolean: Boolean = true,
  content: @Composable () -> Unit
) {
  //    TODO fun updateExerciseNotes() updates notes when workout is completed
  val coroutineScope = rememberCoroutineScope()
  //  val setCompletedState by remember { mutableStateOf(set.setCompleted) }
  val openDialog = remember { mutableStateOf(true) }

  val dismissState = rememberDismissState(confirmStateChange = { dismissStateBoolean })

  fun deleteNote(delete: Boolean) {
    if (delete) {
      onDismissRequest()
    } else {
      coroutineScope.launch {
        dismissState.animateTo(targetValue = DismissValue.Default, anim = TweenSpec())
      }
    }
  }

  fun openConfirmCompletedSetDeletionDialog(open: Boolean) {
    openDialog.value = open
  }

  if (dismissState.isDismissed(DismissDirection.EndToStart)) {
    Log.i("SwipeToDismissItem()", "onDismissRequest() called")
    onDismissRequest()
  } /*else if (dismissState.isDismissed(DismissDirection.EndToStart)) {
      ConfirmCompletedSetDeletionDialog(
        { open -> openConfirmCompletedSetDeletionDialog(open) },
        { delete -> deleteCompletedSet(delete) }
      )
    }*/

  SwipeToDismiss(
    state = dismissState,
    directions = setOf(DismissDirection.EndToStart),
    dismissThresholds = { dismissDirection ->
      FractionalThreshold(if (dismissDirection == DismissDirection.EndToStart) 0.66f else 1f)
    },
    background = {
      val color by
      animateColorAsState(
        targetValue =
        when (dismissState.targetValue) {
          DismissValue.Default -> MaterialTheme.colorScheme.primary
          DismissValue.DismissedToStart -> Color.Red
          DismissValue.DismissedToEnd -> MaterialTheme.colorScheme.primary
        },
        animationSpec = TweenSpec(durationMillis = 1000)
      )
      val alignment = Alignment.CenterEnd
      val icon = Icons.Filled.Delete
      val scale by
      animateFloatAsState(
        if (dismissState.targetValue == DismissValue.Default) 1f else 1.33f,
        animationSpec = TweenSpec(500)
      )

      Box(
        Modifier
          .fillMaxSize()
          .background(color)
          .padding(horizontal = 12.dp),
        contentAlignment = alignment
      ) {
        Icon(
          icon,
          tint = White,
          contentDescription = "Localized description",
          modifier = Modifier.scale(scale)
        )
      }
    }
  ) {
    content()
  }
}
