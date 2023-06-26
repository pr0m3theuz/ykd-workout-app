package com.example.myapplication.components.workout

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.myapplication.WorkoutViewModel
import com.example.myapplication.components.exercise.AddSet
import com.example.myapplication.components.exercise.ExerciseScreen
import com.example.myapplication.components.exercise.SetItem
import com.example.myapplication.data.ExerciseSetModel
import com.example.myapplication.data.SetModel

@Composable
fun ExercisesSetsScreen(
    navController: NavController,
    workoutViewModel: WorkoutViewModel = viewModel(),
) {
    val exercisesSets by workoutViewModel.exercisesSets.collectAsState()

    ExerciseSetLazyColumn(
        navController = navController,
        exercises = exercisesSets
    )
}

@Composable
fun ExerciseSetLazyColumn(
    navController: NavController,
    workoutViewModel: WorkoutViewModel = viewModel(),
    exercises: List<ExerciseSetModel>,
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
                navController = navController,
                _exerciseSets = exerciseSets
            )

            exerciseSets.sets.forEachIndexed { index: Int, set: SetModel ->
                SetItem(
                    exerciseSets = exerciseSets,
                    index = index,
                    set = set,
                    removeSet = { workoutViewModel.removeSet(exerciseSets, set) }
                )
            }

            AddSet(addSet = { workoutViewModel.addSet(exerciseSets) })
        }

        item {
            AddExercise(navController = navController)
        }
        item {
            CancelWorkout()
        }
    }
}
