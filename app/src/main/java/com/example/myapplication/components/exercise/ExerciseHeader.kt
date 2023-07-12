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
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.myapplication.WorkoutViewModel
import com.example.myapplication.components.ExerciseOptionsDropdownMenu
import com.example.myapplication.data.ExerciseNote
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

    val exerciseNotes by workoutViewModel.getExerciseNoteByExercise(_exerciseSets.exercise.exerciseID)
        .collectAsStateWithLifecycle(initialValue = emptyList())

    ExerciseHeader(
        navController = navController,
        exerciseSet.value,
        exerciseNotes = exerciseNotes
    ) { exerciseSets: ExerciseSets -> workoutViewModel.removeExerciseSets(exerciseSets) }
}

@Composable
fun ExerciseHeader(
    navController: NavController,
    exerciseSets: ExerciseSets,
    exerciseNotes: List<ExerciseNote>,
    removeExercise: (exerciseSets: ExerciseSets) -> Unit,
) {
//    TODO: Create a button that toggle the notes
    val (notesVisible, setNotesVisible) = remember { mutableStateOf(false) }

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
                    text = exerciseSets.exercise.name,
                    style = MaterialTheme.typography.titleMedium
                )

                ExerciseOptionsDropdownMenu(
                    exerciseSets = exerciseSets,
                    addNotes = { setNotesVisible(true) },
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
                if (exerciseNotes.isEmpty()) {
                    ExerciseNotes(
                        exerciseNote = ExerciseNote(
                            exerciseID = exerciseSets.exercise.exerciseID,
                            workoutID = exerciseSets.workoutID
                        )
                    )
                } else {
                    exerciseNotes.forEach { note ->
                        ExerciseNotes(exerciseNote = note)
                    }
                }
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
            Text("Set", style = textStyle)
        }

        Box(
            modifier = Modifier
//                        .padding(4.dp)
                .width(110.dp),
            contentAlignment = Alignment.Center
        ) { Text("Previous", style = textStyle) }

        Box(
            modifier = Modifier
                .padding(PaddingValues(horizontal = 4.dp))
                .width(64.dp),
            contentAlignment = Alignment.Center
        ) { Text("Weight", style = textStyle) }

        Box(
            modifier = Modifier
                .padding(PaddingValues(horizontal = 4.dp))
                .width(76.dp),
            contentAlignment = Alignment.Center
        ) { Text("Reps", style = textStyle) }

        Box(
            modifier = Modifier
                .padding(0.dp)
                .width(40.dp),
            contentAlignment = Alignment.Center
        ) { Text("RIR", style = textStyle) }

        Spacer(
            Modifier
                .width(100.dp)
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
    val exerciseSets = exercisesSets.first { it.exSetsUUID == _exerciseSets.exSetsUUID }

//    val exerciseSets = _exerciseSets

    val totalVolume by remember { mutableDoubleStateOf(exerciseSets.totalVolume()) }
    val totalRepetitions by remember { mutableIntStateOf(exerciseSets.totalRepetitions()) }
    val averageWeight by remember { mutableDoubleStateOf(exerciseSets.averageWeight()) }

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
