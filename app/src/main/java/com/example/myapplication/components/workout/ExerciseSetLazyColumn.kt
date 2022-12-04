package com.example.myapplication.components.workout

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.WorkoutViewModel
import com.example.myapplication.components.exercise.ExerciseScreen
import com.example.myapplication.data.ExerciseSetModel

@Composable
fun ExercisesSetsScreen(
    workoutViewModel: WorkoutViewModel = viewModel(),
    exercises: List<ExerciseSetModel>,
    addSet: (exerciseSet: ExerciseSetModel) -> Unit,
    startRestTimer: (exerciseSet: ExerciseSetModel, setID: Long) -> Unit
) {
//    val exercisesSets by workoutViewModel.exercisesSets.collectAsState()

//    val exercises = remember { mutableStateOf(exercises) }

    ExerciseSetLazyColumn(
        exercises = exercises,
        addSet = { exerciseSet: ExerciseSetModel -> addSet(exerciseSet) },
        startRestTimer = { exerciseSet: ExerciseSetModel, setID: Long ->
            workoutViewModel.startSetCompletedTimer(
                exerciseSet,
                setID
            )
        }

    )
}

@Composable
fun ExerciseSetLazyColumn(

    exercises: List<ExerciseSetModel>,
    addSet: (exerciseSet: ExerciseSetModel) -> Unit,
    startRestTimer: (exerciseSet: ExerciseSetModel, setID: Long) -> Unit
) {
//    val _exercisesSets by workoutViewModel.exercisesSets.collectAsState()
//    val exercisesSets = remember { mutableStateOf(_exercisesSets) }
//    val exercises = exercisesSets

    LazyColumn(
        modifier = Modifier.fillMaxWidth()
        //  verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        items(count = exercises.size, key = { index -> exercises[index].id }) { index ->
            val item = exercises[index]

            ExerciseScreen(
                _exerciseSets = item,
                addSet = { addSet(item) },
                startRestTimer = { setID -> startRestTimer(item, setID) }
            )
        }
/*        items(exercises, key = { item -> item.id }) { item ->
            ExerciseScreen(
                _exerciseSets = item,
                addSet = { addSet(item) },
                startRestTimer = { setID -> startRestTimer(item, setID) }
            )
        }*/

        item {
            AddExercise()
        }
        item {
            CancelWorkout()
        }
    }
}
