package com.example.myapplication.components.workout

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.WorkoutViewModel
import com.example.myapplication.components.DiscardWorkoutDialog
import com.example.myapplication.components.exercise.WorkoutNotes
import com.example.myapplication.components.setRestTimer.RestTimerButton
import com.example.myapplication.components.workoutOptionsDropdownMenu
import com.example.myapplication.data.WorkoutModel

// TODO Takes a Workout Object
@OptIn(ExperimentalLifecycleComposeApi::class)
@Composable
fun WorkoutScreen(workout: WorkoutModel, workoutViewModel: WorkoutViewModel = viewModel()) {
    val openDiscardWorkoutDialog = remember { mutableStateOf(false) }

    fun closeDiscardWorkoutDialog(close: Boolean) {
        openDiscardWorkoutDialog.value = close
    }

    if (openDiscardWorkoutDialog.value) {
        DiscardWorkoutDialog(
            openDialog = { close -> closeDiscardWorkoutDialog(close) },
            confirmCancelWorkout = { /* TODO cancel -> (cancel) */ }
        )
    }

    var workoutName by remember {
        mutableStateOf(
            TextFieldValue(
                workout.name.ifEmpty {
                    "Workout"
                }
            )
        )
    }

    val workoutDuration by workoutViewModel.durationWorkout.collectAsState()
    val exercisesSets by workoutViewModel.exercisesSets.collectAsState()

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BasicTextField(
                modifier = Modifier
                    .padding(PaddingValues(4.dp)),
                textStyle = TextStyle(
                    fontSize = 5.em,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onBackground
                ),
                value = workoutName,
                onValueChange = {
                    workoutName = it
                }
            )

            workoutOptionsDropdownMenu()
        }

        Text(modifier = Modifier.padding(8.dp), text = workoutDuration.toString())

        WorkoutNotes(
            workout = WorkoutModel(name = "Workout Name"),
            placeholder = "Workout Note"
        )

        Row(modifier = Modifier.padding(start = 8.dp)) { RestTimerButton() }

        ExercisesSetsScreen(
            exercises = exercisesSets
        )
    }
}
