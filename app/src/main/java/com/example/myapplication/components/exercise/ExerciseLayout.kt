package com.example.myapplication.components.exercise

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.WorkoutViewModel
import com.example.myapplication.components.ExerciseOptionsDropdownMenu
import com.example.myapplication.data.ExerciseSetModel

@Composable
fun ExerciseScreen(
    workoutViewModel: WorkoutViewModel = viewModel(),
    _exerciseSets: ExerciseSetModel
/*    addSet: (exerciseSets: ExerciseSetModel) -> Unit,
    startRestTimer: (setID: Long) -> Unit*/
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

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun ExerciseLayout(
/*    exercise: ExerciseModel,
    exerciseSets: List<SetModel>,*/
    exerciseSets: ExerciseSetModel,
//    addSet: (exerciseSets: ExerciseSetModel) -> Unit,
//    removeSet: (exerciseSets: ExerciseSetModel, setID: Long) -> Unit,
/*    updateSetWeight: (setID: Long, newWeight: Double?) -> Unit,
    updateSetReps: (setID: Long, newReps: Int?) -> Unit,
    updateSetRIR: (setID: Long, newRIR: Double?) -> Unit,
    updateSetRPE: (setID: Long, newRPE: Double?) -> Unit,
    updateSetType: (setID: Long, setType: SetTypeModel) -> Unit,*/
//    setComplete: (exerciseSets: ExerciseSetModel, setID: Long, setComplete: Boolean) -> Unit,
//    startRestTimer: (setID: Long) -> Unit,
    removeExercise: (exerciseSets: ExerciseSetModel) -> Unit
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
                    modifier = Modifier.padding(8.dp),
                    text = "Exercise Name"
                )

                ExerciseOptionsDropdownMenu(
                    addNotes = { notesVisible = true },
                    removeExercise = { removeExercise(exerciseSets) }
                )
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

/*            Column {
                SetsScreen(
                    exerciseSets.sets,
                    { setID: Long -> removeSet(exerciseSets, setID) },
/*                { *//*  setID: Long, newWeight: Double? -> updateSetWeight(setID, newWeight)  *//* },
                { *//*  setID: Long, newReps: Int? -> updateSetReps(setID, newReps)  *//* },
                { *//*  setID: Long, newRIR: Double? -> updateSetWeight(setID, newRIR)  *//* },
                { *//*  setID: Long, newRPE: Double? -> updateSetWeight(setID, newRPE)  *//* },

                { *//*  setID: Long, setType: SetTypeModel -> updateSetType(setID, setType)  *//* },*/

                    { setID: Long, setComplete: Boolean ->
                        setComplete(
                            exerciseSets,
                            setID,
                            setComplete
                        )
                    },
                    { setID: Long -> startRestTimer(setID) }
                )
            } */
//            SetColumn(
//                exerciseSets.sets,
//                { setID: Long -> removeSet(exerciseSets, setID) },
// /*                { *//*  setID: Long, newWeight: Double? -> updateSetWeight(setID, newWeight)  *//* },
//                { *//*  setID: Long, newReps: Int? -> updateSetReps(setID, newReps)  *//* },
//                { *//*  setID: Long, newRIR: Double? -> updateSetWeight(setID, newRIR)  *//* },
//                { *//*  setID: Long, newRPE: Double? -> updateSetWeight(setID, newRPE)  *//* },
//
//                { *//*  setID: Long, setType: SetTypeModel -> updateSetType(setID, setType)  *//* },*/
//
//                { setID: Long, setComplete: Boolean ->
//                    setComplete(
//                        exerciseSets,
//                        setID,
//                        setComplete
//                    )
//                },
//                { setID: Long -> startRestTimer(setID) }
//            )

//            AddSet { addSet(exerciseSets) }
        }
    }
}

// // TODO Remove
// val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyMMddhhmmAS")
//
// private fun initExerciseSets(): List<SetModel> {
//    return List(6) { index ->
//        SetModel(
//            setNumber = index,
//            exerciseWeight = 100.00,
//            repetitions = 10
//        )
//    }
// }

@Composable
fun AddSet(addSet: () -> Unit) {
//    addSet(exerciseSets)
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextButton(onClick = { addSet() }) {
            Text("Add Set")
        }
    }
}
