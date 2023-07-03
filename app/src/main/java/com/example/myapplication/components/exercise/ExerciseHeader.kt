package com.example.myapplication.components.exercise

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.myapplication.WorkoutViewModel
import com.example.myapplication.components.ExerciseOptionsDropdownMenu
import com.example.myapplication.data.ExerciseSets
import com.example.myapplication.data.averageWeight
import com.example.myapplication.data.totalRepetitions
import com.example.myapplication.data.totalVolume
import java.math.RoundingMode
import java.text.DecimalFormat

@Composable
fun ExerciseScreen(
    navController: NavController,
    workoutViewModel: WorkoutViewModel = viewModel(),
    _exerciseSets: ExerciseSets,
) {
    val exerciseSet = remember { mutableStateOf(_exerciseSets) }

    ExerciseHeader(
        navController = navController,
        exerciseSet.value
    ) { exerciseSets: ExerciseSets -> workoutViewModel.removeExerciseSets(exerciseSets) }
}

@Composable
fun ExerciseHeader(
    navController: NavController,
    exerciseSets: ExerciseSets,
    removeExercise: (exerciseSets: ExerciseSets) -> Unit,
) {
    var notesVisible by remember { mutableStateOf(exerciseSets.exercise.notesPinned) }

//    TODO fun updateExerciseNotes() updates notes when workout is completed

    Row(modifier = Modifier.fillMaxWidth()) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // TODO Change to a button that takes the user to a new screen with more info about the exercise
                Text(
                    modifier = Modifier
                        .padding(8.dp)
                        .clickable { navController.navigate("exerciseDetails/${exerciseSets.exercise.exerciseID}") },
                    text = "Exercise Name",
                    style = MaterialTheme.typography.titleMedium
                )

                ExerciseOptionsDropdownMenu(
                    exerciseSets = exerciseSets,
                    addNotes = { notesVisible = true },
                    removeExercise = { removeExercise(exerciseSets) }
                )
            }

            ExerciseSetsWorkoutSummary(_exerciseSets = exerciseSets)

            AnimatedVisibility(
                visible = notesVisible,
                enter = slideInHorizontally(animationSpec = tween(250)),
                exit = slideOutHorizontally(animationSpec = tween(250))
            ) {
// TODO Add swipe to dismiss functionality and maybe wrap in a LazyColumn
                ExerciseNotes(exercise = exerciseSets.exercise)
            }

            SetContentHeader()
        }
    }
}

@Composable
private fun SetContentHeader() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        val textStyle = MaterialTheme.typography.titleSmall

        Box(
            modifier = Modifier
                .padding(0.dp)
                .width(48.dp),
            contentAlignment = Alignment.Center

        ) {
            Text("SET", style = textStyle)
        }

        Box(
            modifier = Modifier
//                        .padding(4.dp)
                .width(128.dp),
            contentAlignment = Alignment.Center
        ) { Text("PREVIOUS", style = textStyle) }

        Box(
            modifier = Modifier
                .padding(PaddingValues(horizontal = 4.dp))
                .width(72.dp),
            contentAlignment = Alignment.Center
        ) { Text("WEIGHT", style = textStyle) }

        Box(
            modifier = Modifier
                .padding(PaddingValues(horizontal = 4.dp))
                .width(60.dp),
            contentAlignment = Alignment.Center
        ) { Text("REPS", style = textStyle) }

        Box(
            modifier = Modifier
                .padding(4.dp)
                .width(40.dp),
            contentAlignment = Alignment.Center
        ) { Text("RIR", style = textStyle) }

        Spacer(
            Modifier
                .width(40.dp)
                .padding(PaddingValues(horizontal = 4.dp))
        )
    }
}

@Composable
private fun ExerciseSetsWorkoutSummary(
    workoutViewModel: WorkoutViewModel = viewModel(),
    _exerciseSets: ExerciseSets,
) {
// TODO Give option to show either Total Volume or Total Working Volume
// TODO Update Values on user input, cause keyboard to
    val textStyle = MaterialTheme.typography.titleSmall

    val exercisesSets by workoutViewModel.exercisesSets.collectAsStateWithLifecycle()
    val exerciseSets = exercisesSets.first { it.uuid == _exerciseSets.uuid }

//    val exerciseSets = _exerciseSets

    val totalVolume by remember { mutableStateOf(exerciseSets.totalVolume()) }
    val totalRepetitions by remember { mutableStateOf(exerciseSets.totalRepetitions()) }
    val averageWeight by remember { mutableStateOf(exerciseSets.averageWeight()) }

    val df = DecimalFormat("#,###.##")
    df.roundingMode = RoundingMode.HALF_UP

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "Volume: ${df.format(totalVolume)}${exerciseSets.exercise.weightUnit}",
            style = textStyle
        )
        Text(text = "Reps: $totalRepetitions", style = textStyle)
        Text(text = "Avg Weight: ${df.format(averageWeight)}", style = textStyle)
    }
}
