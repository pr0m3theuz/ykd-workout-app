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
import com.example.myapplication.components.exercise.SetContent
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
    exercises = exercisesSets,
    workoutNotes = workoutNotes,
    enableCancelWorkout = true,
    WorkoutNoteItem = { workoutNote ->
      SwipeToDismissWorkoutNote(
        workoutNote,
        removeWorkoutNote = { workoutViewModel.removeWorkoutNote(workoutNote) },
      )
    },
    ExerciseItem = { exerciseSets ->
      ExerciseScreen(
        navController,
        _exerciseSets = exerciseSets,
        weightUnit = workoutViewModel.getWeightUnit(exerciseSets),
        exerciseWeightUnit = exerciseSets.exercise?.weightUnit,
        userDefaultWeightUnit = workoutViewModel.userDefaultWeightUnit,
        removeExerciseSets = { workoutViewModel.removeExerciseSets(exerciseSets) },
        addExerciseNote = { workoutViewModel.addExerciseNote(exerciseSets) },
        removeExerciseNote = { note -> workoutViewModel.removeExerciseNote(exerciseSets, note) },
        pinExerciseNote = { note, pinned ->
          workoutViewModel.pinExerciseNote(exerciseSets, note, pinned)
        },
        updateExerciseWeightUnit = { weightUnit ->
          workoutViewModel.updateExerciseWeightUnit(exerciseSets.exercise, weightUnit)
        },
      )
    },
    SetItem = { exerciseSets, set, index ->
      SetLazyListItem(
        exerciseSets = exerciseSets,
        set = set,
        index = index,
        removeSet = { workoutViewModel.removeSet(exerciseSets, set) },
        setContent = {
          val setUUID = set.uuid
          SetLazyListItem(
            exerciseSets = exerciseSets,
            set = set,
            index = index,
            removeSet = { workoutViewModel.removeSet(exerciseSets, set) },
            setContent = {
              SetContent(
                exerciseSets,
                index,
                set,
                getWeightUnit = { workoutViewModel.getWeightUnit(exerciseSets) },
                populatePreviousSetField = {
                  exerciseSets.exercise?.let { exercise ->
                    set.setNumber?.let { setNumber ->
                      workoutViewModel.populatePreviousSetField(exercise._id, setNumber = setNumber)
                    }
                  }
                },
                updateSetReps = { reps ->
                  workoutViewModel.updateSetReps(
                    exerciseSets = exerciseSets,
                    setUUID = setUUID,
                    newReps = reps.toIntOrNull(),
                  )
                },
                updateSetRIR = { rir ->
                  workoutViewModel.updateSetRIR(
                    exerciseSets = exerciseSets,
                    setUUID = setUUID,
                    newRIR = rir.toDoubleOrNull(),
                  )
                },
                updateSetWeight = { weight ->
                  workoutViewModel.updateSetWeight(
                    exerciseSets = exerciseSets,
                    setUUID = setUUID,
                    newWeight = weight.toDoubleOrNull(),
                  )
                },
                isTimerRunning = false,
                updateSetType = { setTypeID ->
                  workoutViewModel.updateSetType(
                    exerciseSets = exerciseSets,
                    setUUID = setUUID,
                    setTypeID = setTypeID,
                  )
                },
                setComplete = { setComplete ->
                  workoutViewModel.completeSet(
                    exerciseSets = exerciseSets,
                    setUUID = setUUID,
                    completed = setComplete,
                  )
                },
                startSetCompletedTimer = {
                  workoutViewModel.startSetCompletedTimer(
                    exerciseSets,
                    setUUID,
                    setLazyListItemID = set.lazyListItemID,
                  )
                },
              )
            },
          )
        },
      )
    },
    AddSet = { exerciseSets -> AddSet(addSet = { workoutViewModel.addSet(exerciseSets) }) },
    AddExercise = { AddExercise(navController = navController) },
    CancelWorkout = {
      CancelWorkout() {
        workoutViewModel.cancelWorkout()
        navController.popBackStack(Screens.Workout.route, inclusive = true)
      }
    },
  )
}

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun ExerciseSetLazyColumn(
  //  navController: NavController,
  //  workoutViewModel: WorkoutViewModel = koinViewModel(),
  exercises: List<ExerciseExerciseSets>,
  workoutNotes: List<WorkoutNote>,
  //  removeWorkoutNote: (WorkoutNote) -> Unit,
  enableCancelWorkout: Boolean,
  WorkoutNoteItem: @Composable (workoutNote: WorkoutNote) -> Unit,
  ExerciseItem: @Composable (exerciseSets: ExerciseExerciseSets) -> Unit,
  SetItem: @Composable (exerciseSets: ExerciseExerciseSets, set: Set, index: Int) -> Unit,
  AddSet: @Composable (exerciseSets: ExerciseExerciseSets) -> Unit,
  AddExercise: @Composable () -> Unit,
  CancelWorkout: @Composable () -> Unit,
) {
  //  fun cancelWorkout() {
  //    workoutViewModel.cancelWorkout()
  //    navController.popBackStack(Screens.Workout.route, inclusive = true)
  //  }

  LazyColumn(
    modifier = Modifier.fillMaxWidth()
    //  verticalArrangement = Arrangement.spacedBy(4.dp)
  ) {
    items(workoutNotes, key = { item -> item.lazyListID }) { workoutNote ->
      WorkoutNoteItem(workoutNote)
    }

    items(exercises, key = { item -> item.exSetsLazyListItemID }) { exerciseSets ->
      //      Log.i(
      //        "ExerciseSetLazyColumn",
      //        "exSetsLazyListItemID: ${exerciseSets.exercise?.name}:
      // ${exerciseSets.exSetsLazyListItemID}"
      //      )
      //      Log.i("ExerciseSetLazyColumn", "${exerciseSets.notes}")
      //      Log.i("ExerciseSetLazyColumn", "${exerciseSets.sets}")

      ExerciseItem(exerciseSets)

      exerciseSets.sets.forEachIndexed { index: Int, set: Set -> SetItem(exerciseSets, set, index) }

      AddSet(exerciseSets)
    }

    item { AddExercise() }
    if (enableCancelWorkout) item { CancelWorkout() }
    item { Box(modifier = Modifier.height(128.dp).fillMaxWidth()) }
  }
}

@Composable
fun SwipeToDismissWorkoutNote(workoutNote: WorkoutNote, removeWorkoutNote: () -> Unit) {
  SwipeToDismissItem(onDismissRequest = { removeWorkoutNote() }, dismissStateBoolean = true) {
    WorkoutNoteTextField(workoutNote = workoutNote, placeholder = "Workout Note")
  }
}
