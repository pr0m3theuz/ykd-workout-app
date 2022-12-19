package com.example.myapplication.components.workout

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.WorkoutViewModel
import com.example.myapplication.components.exercise.AddSet
import com.example.myapplication.components.exercise.ExerciseScreen
import com.example.myapplication.components.exercise.SetItem
import com.example.myapplication.data.ExerciseSetModel
import com.example.myapplication.data.SetModel
import com.example.myapplication.data.SetTypeModel

@Composable
fun ExercisesSetsScreen(
    workoutViewModel: WorkoutViewModel = viewModel(),
    exercises: List<ExerciseSetModel>
    /*addSet: (exerciseSet: ExerciseSetModel) -> Unit,
    startRestTimer: (exerciseSet: ExerciseSetModel, setID: Long) -> Unit*/
) {
//    val exercisesSets by workoutViewModel.exercisesSets.collectAsState()

//    val exercises = remember { mutableStateOf(exercises) }

    ExerciseSetLazyColumn(
        exercises = exercises
/*        addSet = { exerciseSet: ExerciseSetModel -> addSet(exerciseSet) },
        startRestTimer = { exerciseSet: ExerciseSetModel, setID: Long ->
            workoutViewModel.startSetCompletedTimer(
                exerciseSet,
                setID
            )
        }*/

    )
}

@Composable
fun ExerciseSetLazyColumn(
    workoutViewModel: WorkoutViewModel = viewModel(),
    exercises: List<ExerciseSetModel>
) {
    LazyColumn(
        modifier = Modifier.fillMaxWidth()
        //  verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        items(
            exercises,
            key = { item -> item.id }
        ) { exerciseSets ->
            ExerciseScreen(
                _exerciseSets = exerciseSets
            )

            exerciseSets.sets.forEachIndexed { index: Int, set: SetModel ->
                SetItem(
                    index = index,
                    set = set,
                    removeSet = { workoutViewModel.removeSet(exerciseSets, set) },
                    updateSetWeight = { setID: Long, newWeight: Double? ->
                        workoutViewModel.updateSetWeight(
                            exerciseSets = exerciseSets,
                            setID = setID,
                            newWeight = newWeight
                        )
                    },
                    updateSetReps = { setID: Long, newReps: Int? ->
                        workoutViewModel.updateSetReps(
                            exerciseSets = exerciseSets,
                            setID,
                            newReps
                        )
                    },
                    updateSetRIR = { setID: Long, newRIR: Double? ->
                        workoutViewModel.updateSetRIR(
                            exerciseSets = exerciseSets,
                            setID,
                            newRIR
                        )
                    },
                    updateSetRPE = { setID: Long, newRPE: Double? ->
                        workoutViewModel.updateSetRPE(
                            exerciseSets = exerciseSets,
                            setID,
                            newRPE
                        )
                    },
                    updateSetType = { setID: Long, setType: SetTypeModel ->
                        workoutViewModel.updateSetType(
                            exerciseSets = exerciseSets,
                            setID,
                            setType
                        )
                    },
                    setComplete = { setComplete: Boolean ->
                        workoutViewModel.setComplete(
                            exerciseSets,
                            set.id,
                            setComplete
                        )
                    },
                    startRestTimer = {
                        workoutViewModel.startSetCompletedTimer(
                            exerciseSets,
                            set.id
                        )
                    }
                )
            }

            AddSet(addSet = { workoutViewModel.addSet(exerciseSets) })
        }

        item {
            AddExercise()
        }
        item {
            CancelWorkout()
        }
    }
}
