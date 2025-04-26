package com.example.myapplication.features.history.ui

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.myapplication.common.domain.model.formatLongWorkoutStarted
import com.example.myapplication.common.domain.model.formatWorkoutDuration
import com.example.myapplication.features.workout.WorkoutViewModel
import com.example.myapplication.features.workout.feature.exercise.AddSet
import com.example.myapplication.features.workout.feature.exercise.ExerciseScreen
import com.example.myapplication.features.workout.feature.exercise.ExerciseSetLazyColumn
import com.example.myapplication.features.workout.feature.exercise.SetContent
import com.example.myapplication.features.workout.feature.exercise.SetLazyListItem
import com.example.myapplication.features.workout.feature.exercise.SwipeToDismissWorkoutNote
import com.example.myapplication.features.workout.ui.AddExercise
import com.example.myapplication.features.workout.ui.WorkoutOptionsDropdownMenu
import org.bson.types.ObjectId
import org.koin.androidx.compose.koinViewModel
import kotlin.uuid.ExperimentalUuidApi

@RequiresApi(Build.VERSION_CODES.S)
@OptIn(
  ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class,
  ExperimentalUuidApi::class
)
@Composable
fun EditWorkoutScreen(
  workoutViewModel: WorkoutViewModel = koinViewModel(),
  workoutID: String,
  //  getExercise: (exerciseID: ObjectId) -> Exercise?,
  navController: NavController,
  editable: Boolean,
  //  exerciseExerciseSets: List<ExerciseExerciseSets>
) {
  val userDefaultWeightUnit = workoutViewModel.userDefaultWeightUnit

  if (workoutID.isNotBlank()) {
    workoutViewModel.loadWorkoutFromPreviousWorkout(ObjectId(workoutID))
  } else {
    NoWorkoutFound()
  }

  val workout by workoutViewModel.workout.collectAsState(null)
  val workoutComponents by workoutViewModel.workoutComponents.collectAsState()
  val workoutNotes by workoutViewModel.workoutNotes.collectAsState()
  val workoutName by workoutViewModel.workoutName.collectAsState()

  val (openSaveChangesDialog, setOpenSaveChangesDialog) = remember { mutableStateOf(false) }

  if (openSaveChangesDialog) {
    ConfirmChangesToWorkout(
      onDismissRequest = { setOpenSaveChangesDialog(false) },
      saveWorkout = { workoutViewModel.saveWorkout() },
    )
  }

  workout?.let { itWorkout ->
    Scaffold(
      modifier = Modifier.imePadding(),
      topBar = {
        TopAppBar(
          title = {
            workoutName?.let { workoutName ->
              BasicTextField(
                enabled = editable,
                value = workoutName,
                textStyle =
                MaterialTheme.typography.titleLarge.merge(
                  color = MaterialTheme.colorScheme.onSurface
                ),
                onValueChange = { newName -> workoutViewModel.setWorkoutName(newName) },
                cursorBrush = SolidColor(MaterialTheme.colorScheme.onBackground),
              )
            }
          },
          navigationIcon = {
            IconButton(onClick = { navController.navigateUp() }) {
              Icon(
                imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                tint = MaterialTheme.colorScheme.onSurface,
                contentDescription = "Go to the previous screen",
              )
            }
          },
          actions = {
            WorkoutOptionsDropdownMenu(
              addWorkoutNote = { if (editable) workoutViewModel.addWorkoutNote() },
              enableWorkoutNameTextfield = {},
              workoutNameDialog = {},
            )
          },
        )
      },
      floatingActionButton = {
        if (editable) {
          FloatingActionButton(onClick = { setOpenSaveChangesDialog(true) }) {
            Icon(imageVector = Icons.Filled.Save, contentDescription = "Save Changes")
          }
        }
      },
      bottomBar = { /*BottomNavigationBar(navController = navController) */ },
    ) { contentPadding ->
      Column(Modifier.padding(contentPadding), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
          modifier = Modifier
            .padding(horizontal = 8.dp)
            .fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically,
        ) {
          Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.TopStart) {
            Text(itWorkout.formatLongWorkoutStarted())
          }
        }
        Row(
          modifier = Modifier
            .padding(horizontal = 8.dp)
            .fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically,
        ) {
          Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.TopStart) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
              Icon(Icons.Filled.Timer, contentDescription = null)
              Text(text = itWorkout.formatWorkoutDuration())
            }
          }
        }

        if (workoutComponents.isNotEmpty()) {
          ExerciseSetLazyColumn(
            workoutComponents = workoutComponents,
            workoutNotes = workoutNotes,
            enableCancelWorkout = true,
            WorkoutNoteItem = { workoutNote ->
              SwipeToDismissWorkoutNote(
                workoutViewModel = workoutViewModel,
                workoutNote = workoutNote,
                removeWorkoutNote = { workoutViewModel.removeWorkoutNote(workoutNote) },
              )
            },
            ExerciseItem = { exerciseSets ->
              ExerciseScreen(
                navController = navController,
                _exerciseSets = exerciseSets,
                weightUnit = workoutViewModel.getWeightUnit(exerciseSets),
                exerciseWeightUnit = exerciseSets.exercise.weightUnit,
                userDefaultWeightUnit = workoutViewModel.userDefaultWeightUnit,
                removeExerciseSets = { workoutViewModel.removeExerciseSets(exerciseSets) },
                addExerciseNote = {
                  workoutViewModel.addExerciseNote(exerciseSets /*, itWorkout._id*/)
                },
                removeExerciseNote = { note ->
                  workoutViewModel.removeExerciseNote(exerciseSets, note)
                },
                pinExerciseNote = { note, pinned ->
                  workoutViewModel.pinExerciseNote(exerciseSets, note, pinned)
                },
                updateExerciseWeightUnit = {
                  workoutViewModel.updateExerciseWeightUnit(exerciseSets.exercise, it)
                },
                workoutComponents = workoutComponents,
                onCreateSuperset = {},
                workoutViewModel = workoutViewModel,
              )
            },
            SetItem = { exerciseSets, set, index ->
              val setUUID = set.uuid
              SetLazyListItem(
                exerciseSets = exerciseSets,
                set = set,
                index = index,
                removeSet = { workoutViewModel.removeSet(exerciseSets, set) },
                setContent = {
                  SetContent(
                    workoutViewModel = workoutViewModel,
                    exerciseSets,
                    index,
                    set,
                    getWeightUnit = { workoutViewModel.getWeightUnit(exerciseSets) },
                    populatePreviousSetField = {
                      exerciseSets.exercise.let { exercise ->
                        set.setNumber?.let { setNumber ->
                          workoutViewModel.populatePreviousSetField(
                            exercise.id,
                            setNumber = setNumber,
                          )
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
                    startSetCompletedTimer = {},
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
            CancelWorkout = {},
          )
        }
      }
    }
  }
}