package com.example.myapplication.components.exercise

import androidx.compose.animation.ExperimentalAnimationApi
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.WorkoutViewModel

@OptIn(ExperimentalAnimationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun WorkoutNotes(
    workoutViewModel: WorkoutViewModel = viewModel(),
    placeholder: String = "Write a note",
    pinNote: (Boolean) -> Unit,
    notesPinned: Boolean,
) {
//        TODO Refactor this to workoutNotes
    val workout by workoutViewModel.workout.collectAsState()

    var workoutNotes by remember {
        mutableStateOf(
            TextFieldValue(
                if (workout.notesPinned && (workout.pinnedNotes?.isNotEmpty() == true)) {
                    workout.pinnedNotes.orEmpty()
                } else {
                    workout.notes.orEmpty()
                }
            )
        )
    }

    var placeholderText by remember { mutableStateOf(placeholder) }

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

    if (workoutNotes.text == "" && notesPinned) {
        placeholderText = "Pin a note for next time"
    } else if (workoutNotes.text == "") {
        placeholderText = placeholder
    }

//    TODO fun updateExerciseNotes() updates notes when workout is completed

    TextField(
        placeholder = { Text(placeholderText) },
        modifier = Modifier
            .padding(/*horizontal = */8.dp)
            .fillMaxWidth(),
        textStyle = TextStyle(
            fontSize = 5.em,
            color = MaterialTheme.colorScheme.onBackground
        ),
        value = workoutNotes,
        onValueChange = {
            workoutNotes = it
            workoutViewModel.setWorkoutNote(it.text)
        },
        trailingIcon = {
            Button(
                onClick = { pinNote(!workout.notesPinned) },
                shape = RoundedCornerShape(15),
                contentPadding = PaddingValues(0.dp),
                modifier = androidx.compose.ui.Modifier
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
                    modifier = androidx.compose.ui.Modifier.size(24.dp)
                )
            }
        }
    )
}
