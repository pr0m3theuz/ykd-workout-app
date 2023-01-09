package com.example.myapplication.components.exercise

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.WorkoutViewModel
import com.example.myapplication.components.ExerciseOptionsDropdownMenu
import com.example.myapplication.data.ExerciseSetModel
import com.example.myapplication.data.averageWeight
import com.example.myapplication.data.totalRepetitions
import com.example.myapplication.data.totalVolume
import java.math.RoundingMode
import java.text.DecimalFormat

@Composable
fun ExerciseScreen(
    workoutViewModel: WorkoutViewModel = viewModel(),
    _exerciseSets: ExerciseSetModel
) {
    val exerciseSet = remember { mutableStateOf(_exerciseSets) }

    ExerciseLayout(
/*        exerciseSet.exercise,
        exerciseSet.sets,*/
        exerciseSet.value,
        /*{ exerciseSets: ExerciseSetModel -> workoutViewModel.addSet(exerciseSets) },
        { exerciseSets: ExerciseSetModel, setID: Long ->
            workoutViewModel.removeSet(
                exerciseSets,
                setID
            )
        },*/
/*        { setID: Long, newWeight: Double? -> workoutViewModel.updateSetWeight(setID, newWeight) },
        { setID: Long, newReps: Int? -> workoutViewModel.updateSetReps(setID, newReps) },
        { setID: Long, newRIR: Double? -> workoutViewModel.updateSetRIR(setID, newRIR) },
        { setID: Long, newRPE: Double? -> workoutViewModel.updateSetRPE(setID, newRPE) },
        { setID: Long, setType: SetTypeModel -> workoutViewModel.updateSetType(setID, setType) },*/
/*        { exerciseSets: ExerciseSetModel, setID: Long, setComplete: Boolean ->
            workoutViewModel.setComplete(
                exerciseSets,
                setID,
                setComplete
            )
        },
        { setID: Long -> startRestTimer(setID) },*/
        { exerciseSets: ExerciseSetModel -> workoutViewModel.removeExerciseSets(exerciseSets) }
    )
}

@Composable
fun ExerciseLayout(
    exerciseSets: ExerciseSetModel,
    removeExercise: (exerciseSets: ExerciseSetModel) -> Unit
) {
    var notesVisible by remember { mutableStateOf(exerciseSets.exercise.notesPinned) }
    val totalVolume by remember { mutableStateOf(exerciseSets.totalVolume()) }
    val totalRepetitions by remember { mutableStateOf(exerciseSets.totalRepetitions()) }
    val averageWeight by remember { mutableStateOf(exerciseSets.averageWeight()) }

    val df = DecimalFormat("#,###.##")
    df.roundingMode = RoundingMode.HALF_UP

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
                    modifier = Modifier.padding(8.dp),
                    text = "Exercise Name"
                )

                ExerciseOptionsDropdownMenu(
                    exerciseSets = exerciseSets,
                    addNotes = { notesVisible = true },
                    removeExercise = { removeExercise(exerciseSets) }
                )
            }

//            TODO Give option to show either Total Volume or Total Working Volume
//            TODO Update Values on user input
            Row(
                modifier = Modifier.fillMaxWidth().padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "Volume: ${df.format(totalVolume)}${exerciseSets.exercise.weightUnit}")
                Text(text = "Reps: $totalRepetitions")
                Text(text = "Avg Weight: ${df.format(averageWeight)}")
            }

            AnimatedVisibility(
                visible = notesVisible,
                enter = slideInHorizontally(animationSpec = tween(250)),
                exit = slideOutHorizontally(animationSpec = tween(250))
            ) {
// TODO Add swipe to dismiss functionality and maybe wrap in a LazyColumn
                ExerciseNotes(exercise = exerciseSets.exercise)
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier.padding(0.dp).width(48.dp),
                    contentAlignment = Alignment.Center

                ) {
                    Text("SET")
                }

                Box(
                    modifier = Modifier
//                        .padding(4.dp)
                        .width(128.dp),
                    contentAlignment = Alignment.Center
                ) { Text("PREVIOUS") }

                Box(
                    modifier = Modifier.padding(PaddingValues(horizontal = 4.dp)).width(72.dp),
                    contentAlignment = Alignment.Center
                ) { Text("WEIGHT") }

                Box(
                    modifier = Modifier.padding(PaddingValues(horizontal = 4.dp)).width(60.dp),
                    contentAlignment = Alignment.Center
                ) { Text("REPS") }

                Box(
                    modifier = Modifier.padding(4.dp).width(40.dp),
                    contentAlignment = Alignment.Center
                ) { Text("RIR") }

                Spacer(
                    Modifier.width(40.dp).padding(PaddingValues(horizontal = 4.dp))
                )
            }
        }
    }
}
