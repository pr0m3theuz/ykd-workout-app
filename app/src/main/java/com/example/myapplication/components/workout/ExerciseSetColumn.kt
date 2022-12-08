package com.example.myapplication.components.workout

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.WorkoutViewModel
import com.example.myapplication.components.exercise.ExerciseScreen
import com.example.myapplication.data.ExerciseSetModel

@Composable
fun ExerciseSetColumn(
    workoutViewModel: WorkoutViewModel = viewModel(),
    exercises: List<ExerciseSetModel>,
    addSet: (exerciseSet: ExerciseSetModel) -> Unit,
    startRestTimer: (exerciseSet: ExerciseSetModel, setID: Long) -> Unit
) {
    val scrollState = rememberScrollState()
//    val exercisesSets by workoutViewModel.exercisesSets.collectAsState()

    Column(modifier = Modifier.verticalScroll(scrollState)) {
        exercises.forEachIndexed { index, exerciseSets ->
            Log.i("ExerciseSetColumn() exerciseSets", "$exerciseSets")
            ExerciseScreen(
                _exerciseSets = exerciseSets
/*                addSet = { addSet(exerciseSets) },
                startRestTimer = { setID -> startRestTimer(exerciseSets, setID) }*/
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = { /* TODO Do something! */ }) {
                Text("Add Exercise")
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = { /* TODO Do something! */ }) {
                Text("Cancel Workout", color = Color.Red)
            }
        }
    }
}
