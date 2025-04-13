@file:OptIn(ExperimentalUuidApi::class)

package com.example.myapplication.components.exercise

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.myapplication.components.Screens
import com.example.myapplication.components.WorkoutViewModel
import com.example.myapplication.components.workout.AddExercise
import com.example.myapplication.components.workout.CancelWorkout
import com.example.myapplication.data.model.ExerciseExerciseSets
import com.example.myapplication.data.model.Set
import com.example.myapplication.data.model.SupersetExercisesSets
import com.example.myapplication.data.model.WorkoutComponent
import com.example.myapplication.data.model.WorkoutNote
import com.example.myapplication.ui.theme.IBMColorBlindPalette
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun ExercisesSetsScreen(
  navController: NavController,
  workoutViewModel: WorkoutViewModel,
) {
  val workoutComponents by workoutViewModel.workoutComponents.collectAsState(emptyList())
  val workoutNotes by workoutViewModel.workoutNotes.collectAsState(emptyList())

  ExerciseSetLazyColumn(
    workoutComponents = workoutComponents,
    workoutNotes = workoutNotes,
    enableCancelWorkout = true,
    WorkoutNoteItem = { workoutNote ->
      SwipeToDismissWorkoutNote(
        workoutViewModel = workoutViewModel,
        removeWorkoutNote = { workoutViewModel.removeWorkoutNote(workoutNote) },
        workoutNote = workoutNote,
      )
    },
    ExerciseItem = { exerciseSets ->
      ExerciseScreen(
        navController,
        _exerciseSets = exerciseSets,
        weightUnit = workoutViewModel.getWeightUnit(exerciseSets),
        exerciseWeightUnit = exerciseSets.exercise.weightUnit,
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
        workoutComponents = workoutComponents,
        onCreateSuperset = { selectedExercises ->
          workoutViewModel.createSupersetFromExercisesSets(selectedExercises)
        },
        workoutViewModel = workoutViewModel,
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
                workoutViewModel,
                exerciseSets,
                index,
                set,
                getWeightUnit = { workoutViewModel.getWeightUnit(exerciseSets) },
                populatePreviousSetField = {
                  exerciseSets.exercise.let { exercise ->
                    set.setNumber?.let { setNumber ->
                      workoutViewModel.populatePreviousSetField(exercise.id, setNumber = setNumber)
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
                updateSetType = { setType ->
                  workoutViewModel.updateSetType(
                    exerciseSets = exerciseSets,
                    setUUID = setUUID,
                    setType = setType,
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
//                    exerciseSets,
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
    AddSet = { supersetUuid, targetExSetsUuid ->
      AddSet(addSet = {
        workoutViewModel.addSet(
          supersetUuid = supersetUuid,
          targetExSetsUuid = targetExSetsUuid
        )
      })
    },
    AddExercise = { AddExercise(navController = navController) },
    CancelWorkout = {
      CancelWorkout {
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
  workoutComponents: List<WorkoutComponent>,
  workoutNotes: List<WorkoutNote>,
  //  removeWorkoutNote: (WorkoutNote) -> Unit,
  enableCancelWorkout: Boolean,
  WorkoutNoteItem: @Composable (workoutNote: WorkoutNote) -> Unit,
//  SupersetItem: @Composable (superset: SupersetExercisesSets) -> Unit,
  ExerciseItem: @Composable (exerciseSets: ExerciseExerciseSets) -> Unit,
  SetItem: @Composable (exerciseSets: ExerciseExerciseSets, set: Set, index: Int) -> Unit,
  AddSet: @Composable (supersetUuid: Uuid?, targetExSetsUuid: Uuid) -> Unit,
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
    items(
      workoutNotes,
      contentType = { "WorkoutNote" },
      key = { item -> item.lazyListID }) { workoutNote ->
      WorkoutNoteItem(workoutNote)
    }


    items(workoutComponents, contentType = { it.type },
      key = { component -> component.lazyListItemID }
    )
    { component ->
/*
Log.i(
  "ExerciseSetLazyColumn",
  "exSetsLazyListItemID: ${exerciseSets.exercise?.name}:
  $ { exerciseSets.exSetsLazyListItemID }"
)
Log.i("ExerciseSetLazyColumn", "${exerciseSets.notes}")
Log.i("ExerciseSetLazyColumn", "${exerciseSets.sets}")
*/

      when (component) {
        is ExerciseExerciseSets -> {
          key(component.lazyListItemID) { ExerciseItem(component) }

          component.sets.forEachIndexed { index: Int, set: Set ->
            key(set.lazyListItemID) { SetItem(component, set, index) }
          }

          AddSet(null, component.exSetsUUID)
        }

        is SupersetExercisesSets -> {
          val supersetCardBackgroundColor: Color =
            remember(component.lazyListItemID) { IBMColorBlindPalette.stableColorFor(component.uuid) }

          Card(
            modifier = Modifier
              .fillMaxWidth()
              .padding(vertical = 8.dp),
            shape = RoundedCornerShape(0.dp),
            colors = CardDefaults.cardColors(
              containerColor = supersetCardBackgroundColor
            )
          ) {
            Column(
              modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp, bottom = 4.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surface)
            ) {
              component.exercisesSets.forEach { exerciseSets: ExerciseExerciseSets ->
                key(exerciseSets.lazyListItemID) { ExerciseItem(exerciseSets) }

                exerciseSets.sets.forEachIndexed { setIndex: Int, set: Set ->
                  key(set.lazyListItemID) {
                    SetItem(
                      exerciseSets,
                      set,
                      setIndex
                    )
                  }
                }

                AddSet(component.uuid, exerciseSets.exSetsUUID)
              }
            }
          }
        }
      }
    }

    item { AddExercise() }
    if (enableCancelWorkout) item { CancelWorkout() }
    item {
      Box(
        modifier = Modifier
          .height(128.dp)
          .fillMaxWidth()
      )
    }
  }
}

@Composable
fun SwipeToDismissWorkoutNote(
  workoutViewModel: WorkoutViewModel,
  workoutNote: WorkoutNote,
  removeWorkoutNote: () -> Unit
) {
  SwipeToDismissItem(onDismissRequest = { removeWorkoutNote() }, dismissStateBoolean = true) {
    WorkoutNoteTextField(
      workoutViewModel = workoutViewModel,
      workoutNote = workoutNote,
      placeholder = "Workout Note"
    )
  }
}
