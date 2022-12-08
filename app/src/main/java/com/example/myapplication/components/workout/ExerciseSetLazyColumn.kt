package com.example.myapplication.components.workout

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.WorkoutViewModel
import com.example.myapplication.components.SetItem
import com.example.myapplication.components.exercise.AddSet
import com.example.myapplication.components.exercise.ExerciseScreen
import com.example.myapplication.data.ExerciseSetModel
import com.example.myapplication.data.SetModel

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
//    addSet: (exerciseSet: ExerciseSetModel) -> Unit,
//    startRestTimer: (exerciseSet: ExerciseSetModel, setID: Long) -> Unit
) {
//    val _exercisesSets by workoutViewModel.exercisesSets.collectAsState()
//    val exercisesSets = remember { mutableStateOf(_exercisesSets) }
//    val exercises = exercisesSets

//    val exerciseSets = exercises[0]

    LazyColumn(
        modifier = Modifier.fillMaxWidth()
        //  verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
/*items(count = exercises.size, key = { index -> exercises[index].id }) { index ->
            val item = exercises[index]

            ExerciseScreen(
                _exerciseSets = item,
                addSet = { addSet(item) },
                startRestTimer = { setID -> startRestTimer(item, setID) }
            )
        }*/
/*        items(exercises, key = { item -> item.id }) { item ->
            ExerciseScreen(
                _exerciseSets = item,
                addSet = { addSet(item) },
                startRestTimer = { setID -> startRestTimer(item, setID) }
            )
        }*/

        items(
            exercises,
            key = { item -> item.id }
        ) { exerciseSets ->
            ExerciseScreen(
                _exerciseSets = exerciseSets
/*            addSet = { exerciseSets -> exerciseSets.addSet() },
            startRestTimer = { setID -> setID.toULong() }*/
            )

            exerciseSets.sets.forEachIndexed { index: Int, set: SetModel ->
                SetItem(
                    index = index,
                    set = set,
                    removeSet = { workoutViewModel.removeSet(exerciseSets, set) },
                    startRestTimer = {
                        workoutViewModel.startSetCompletedTimer(
                            exerciseSets,
                            set.id
                        )
                    },
                    setComplete = { setComplete: Boolean ->
                        workoutViewModel.setComplete(
                            exerciseSets,
                            set.id,
                            setComplete
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

/*
fun LazyListScope.exerciseSetsItem(
    workoutViewModel: WorkoutViewModel = viewModel(),
    exerciseSets: ExerciseSetModel
) {
    item {
        ExerciseScreen(
            _exerciseSets = exerciseSets
*/
/*            addSet = { exerciseSets -> exerciseSets.addSet() },
            startRestTimer = { setID -> setID.toULong() }*//*

        )
    }

    itemsIndexed(exerciseSets.sets) { index: Int, set: SetModel ->
        SetItem(
            index = index,
            set = set,
            removeSet = { workoutViewModel.removeSet(exerciseSets, set.id) },
            startRestTimer = { workoutViewModel.startSetCompletedTimer(exerciseSets, set.id) },
            setComplete = { setID: Long, setComplete: Boolean ->
                workoutViewModel.setComplete(
                    exerciseSets,
                    setID,
                    setComplete
                )
            }
        )
    }

    item { AddSet(addSet = { workoutViewModel.addSet(exerciseSets) }) }
}
*/
