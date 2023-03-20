package com.example.myapplication.components.workout

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.WorkoutViewModel
import com.example.myapplication.components.FinishWorkoutButton
import com.example.myapplication.components.WorkoutOptionsDropdownMenu
import com.example.myapplication.components.exercise.WorkoutNotes
import com.example.myapplication.components.setRestTimer.RestTimerButton

// TODO Takes a Workout Object

@Composable
fun WorkoutScreen(workoutViewModel: WorkoutViewModel = viewModel()) {
    val workout by workoutViewModel.workout.collectAsState()

    val openFinishWorkoutDialog = remember { mutableStateOf(false) }

    if (openFinishWorkoutDialog.value) {
        FinishWorkoutButton()
        openFinishWorkoutDialog.value = false
    }

    val workoutName by workoutViewModel.workoutName.collectAsState()

    val workoutDuration by workoutViewModel.workoutDurationString.collectAsState()
    val exercisesSets by workoutViewModel.exercisesSets.collectAsState()

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            WorkoutNameTextField(
                workoutName,
                setWorkoutName = { name -> workoutViewModel.setWorkoutName(name) }
            )

            WorkoutOptionsDropdownMenu()
        }

        Text(modifier = Modifier.padding(8.dp), text = workoutDuration)

        WorkoutNotes(
            placeholder = "Workout Note",
            pinNote = { pin -> workoutViewModel.pinWorkoutNote(pin) },
            notesPinned = workout.notesPinned
        )

        Row(
            modifier = Modifier.padding(start = 8.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            RestTimerButton()
            FinishWorkoutButton()
        }

        ExercisesSetsScreen(
            exercises = exercisesSets
        )
    }
}

@Composable
private fun WorkoutNameTextField(
    workoutName: String,
    setWorkoutName: (String) -> Unit,
) {
    var name by remember { mutableStateOf(workoutName) }

    BasicTextField(
        modifier = Modifier
            .padding(PaddingValues(4.dp)),
        textStyle = TextStyle(
            fontSize = 5.em,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onBackground
        ),
        value = name,
        onValueChange = {
            name = it
            setWorkoutName(it)
        }
    )
}
