package com.example.myapplication.components.workout

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.myapplication.WorkoutViewModel
import com.example.myapplication.components.exercise.AddSet
import com.example.myapplication.components.exercise.ExerciseScreen
import com.example.myapplication.components.exercise.SetItem
import com.example.myapplication.data.ExerciseSets
import com.example.myapplication.data.SetModel

@Composable
fun ExercisesSetsScreen(
  navController: NavController,
  workoutViewModel: WorkoutViewModel = hiltViewModel(),
  /*exercisesSets: List<ExerciseSets>,*/
) {
  val exercisesSets by workoutViewModel.exercisesSets.collectAsState(emptyList())
  ExerciseSetLazyColumn(navController = navController, exercises = exercisesSets)
}

@Composable
fun ExerciseSetLazyColumn(
  navController: NavController,
  workoutViewModel: WorkoutViewModel = viewModel(),
  exercises: List<ExerciseSets>,
) {
  fun cancelWorkout() {
    workoutViewModel.cancelWorkout()
    // navController.popBackStack()
  }

  LazyColumn(
    modifier = Modifier.fillMaxWidth()
    //  verticalArrangement = Arrangement.spacedBy(4.dp)
  ) {
    items(exercises, key = { item -> item.exSetsLazyListItemID }) { exerciseSets ->
      ExerciseScreen(navController = navController, _exerciseSets = exerciseSets)

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

    item { AddExercise() }
    item { CancelWorkout({ cancelWorkout() }) }
    item { Box(modifier = Modifier
      .height(128.dp)
      .fillMaxWidth()) }
  }
}
