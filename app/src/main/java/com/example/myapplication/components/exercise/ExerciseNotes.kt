package com.example.myapplication.components.exercise

import android.util.Log
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PushPin
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import com.example.myapplication.data.ExerciseNote

private const val infoTag = "ExerciseLayout().pinNote()"

@Composable
fun ExerciseNotes(
    exerciseNote: ExerciseNote,
    placeholder: String = "Write a note",
) {
    var note by remember {
        mutableStateOf(
            exerciseNote.note?.let {
                TextFieldValue(
                    it
                )
            }
        )
    }

    var placeholderText by remember { mutableStateOf(placeholder) }

//    var notesVisible by remember { mutableStateOf(exercise.pinned) }
    var notesPinned by remember { mutableStateOf(exerciseNote.pinned) }

    fun pinNote() {
        exerciseNote.pinned = !exerciseNote.pinned // TODO Add callback function
        notesPinned = exerciseNote.pinned
        Log.i(infoTag, exerciseNote.pinned.toString())
        Log.i(infoTag, notesPinned.toString())
    }

    val noteButtonContainerColor =
        if (notesPinned) {
            Log.i("ExerciseLayout(). noteButtonContainerColor", notesPinned.toString())
            MaterialTheme.colorScheme.primary.copy(1f)
        } else {
            Log.i(infoTag, notesPinned.toString())
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

//    TODO fun updateExerciseNotes() updates notes when workout is completed
    TextField(
        placeholder = { Text(placeholderText, color = Color.DarkGray) },
        modifier = Modifier
            .padding(horizontal = 8.dp)
            .fillMaxWidth(),
        textStyle = TextStyle(
            fontSize = 5.em,
            color = MaterialTheme.colorScheme.onBackground
        ),
        value = note ?: TextFieldValue(""),
        onValueChange = { note = it },
        trailingIcon = {
            Button(
                onClick = { pinNote() },
                shape = RoundedCornerShape(15),
                contentPadding = PaddingValues(0.dp),
                modifier = Modifier
                    .padding(8.dp)
                    .size(36.dp),
                colors = ButtonDefaults.buttonColors(
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
