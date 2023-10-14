package com.example.myapplication.components.workout

import android.os.Build
import androidx.annotation.RequiresApi
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
import androidx.navigation.NavController
import com.example.myapplication.components.Screens
import com.example.myapplication.components.WorkoutViewModel
import com.example.myapplication.components.exercise.AddSet
import com.example.myapplication.components.exercise.ExerciseScreen
import com.example.myapplication.components.exercise.SetLazyListItem
import com.example.myapplication.components.exercise.SwipeToDismissItem
import com.example.myapplication.components.exercise.WorkoutNoteTextField
import com.example.myapplication.data.model.ExerciseExerciseSets
import com.example.myapplication.data.model.Set
import com.example.myapplication.data.model.WorkoutNote
import org.koin.androidx.compose.koinViewModel

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun ExercisesSetsScreen(
    navController: NavController,
    workoutViewModel: WorkoutViewModel = koinViewModel(),
) {
    val exercisesSets by workoutViewModel.exercisesSets.collectAsState(emptyList())
    val workoutNotes by workoutViewModel.workoutNotes.collectAsState(emptyList())

    ExerciseSetLazyColumn(
        navController = navController,
        exercises = exercisesSets,
        workoutNotes = workoutNotes
    )
}

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun ExerciseSetLazyColumn(
    navController: NavController,
    workoutViewModel: WorkoutViewModel = koinViewModel(),
    exercises: List<ExerciseExerciseSets>,
    workoutNotes: List<WorkoutNote>,
) {
    fun cancelWorkout() {
        workoutViewModel.cancelWorkout()
        navController.popBackStack(Screens.Workout.route, inclusive = true)
    }

    LazyColumn(
        modifier = Modifier.fillMaxWidth()
        //  verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        items(workoutNotes, key = { item -> item.lazyListID }) { workoutNote ->
            SwipeToDismissItem(
                onDismissRequest = { workoutViewModel.removeWorkoutNote(workoutNote) },
                dismissStateBoolean = true
            ) {
                WorkoutNoteTextField(
                    workoutNote = workoutNote,
                    placeholder = "Workout Note",
                )
            }
        }

        items(exercises, key = { item -> item.exSetsLazyListItemID }) { exerciseSets ->
//      Log.i(
//        "ExerciseSetLazyColumn",
//        "exSetsLazyListItemID: ${exerciseSets.exercise?.name}: ${exerciseSets.exSetsLazyListItemID}"
//      )
            //      Log.i("ExerciseSetLazyColumn", "${exerciseSets.notes}")
            //      Log.i("ExerciseSetLazyColumn", "${exerciseSets.sets}")
            ExerciseScreen(
                navController = navController,
                _exerciseSets = exerciseSets,
            )

            exerciseSets.sets.forEachIndexed { index: Int, set: Set ->
              SetLazyListItem(
                exerciseSets = exerciseSets,
                index = index,
                set = set,
                removeSet = { workoutViewModel.removeSet(exerciseSets, set) }
              )
            }

            AddSet(addSet = { workoutViewModel.addSet(exerciseSets) })
        }

        item { AddExercise() }
        item { CancelWorkout() { cancelWorkout() } }
        item { Box(modifier = Modifier
          .height(128.dp)
          .fillMaxWidth()) }
    }
}
