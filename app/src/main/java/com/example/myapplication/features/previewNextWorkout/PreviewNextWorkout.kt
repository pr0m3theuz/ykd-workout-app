@file:OptIn(ExperimentalUuidApi::class)

package com.example.myapplication.features.previewNextWorkout

import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.AnimatedPane
import androidx.compose.material3.adaptive.layout.PaneAdaptedValue
import androidx.compose.material3.adaptive.layout.SupportingPaneScaffold
import androidx.compose.material3.adaptive.layout.SupportingPaneScaffoldRole
import androidx.compose.material3.adaptive.navigation.rememberSupportingPaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.myapplication.common.domain.model.ExerciseExerciseSets
import com.example.myapplication.common.domain.model.MassUnit
import com.example.myapplication.common.domain.model.Set
import com.example.myapplication.common.domain.model.SetType
import com.example.myapplication.features.ExerciseOptionsDropdownMenu
import com.example.myapplication.features.history.ui.ConfirmChangesToWorkout
import com.example.myapplication.features.workout.WorkoutViewModel
import com.example.myapplication.features.workout.feature.exercise.AddSet
import com.example.myapplication.features.workout.feature.exercise.ExerciseScreen
import com.example.myapplication.features.workout.feature.exercise.ExerciseSetLazyColumn
import com.example.myapplication.features.workout.feature.exercise.SetContent
import com.example.myapplication.features.workout.feature.exercise.SetLazyListItem
import com.example.myapplication.features.workout.feature.exercise.SwipeToDismissWorkoutNote
import com.example.myapplication.features.workout.ui.AddExercise
import kotlinx.coroutines.launch
import org.bson.types.ObjectId
import kotlin.uuid.ExperimentalUuidApi

// TODO Call conditionally

@RequiresApi(Build.VERSION_CODES.S)
@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun PreviewNextWorkoutScreen(
  navController: NavController,
  workoutViewModel: WorkoutViewModel,
  workoutID: String,
  WorkoutOverview: @Composable () -> Unit,
) {
  val coroutineScope = rememberCoroutineScope()
  val scaffoldNavigator = rememberSupportingPaneScaffoldNavigator()

  BackHandler(scaffoldNavigator.canNavigateBack()) {
    coroutineScope.launch { scaffoldNavigator.navigateBack() }
  }

  Surface {
    SupportingPaneScaffold(
      directive = scaffoldNavigator.scaffoldDirective,
      value = scaffoldNavigator.scaffoldValue,
      mainPane = {
        AnimatedPane() {
          Column {
            Row(
              modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.Start,
            ) {
              if (
                scaffoldNavigator.scaffoldValue[SupportingPaneScaffoldRole.Supporting] ==
                PaneAdaptedValue.Hidden
              ) {
                Button(
                  modifier = Modifier.wrapContentSize(),
                  onClick = {
                    coroutineScope.launch {
                      scaffoldNavigator.navigateTo(SupportingPaneScaffoldRole.Supporting)
                    }
                  },
                ) {
                  Text("Show Previous Workout")
                }
              } /*else {
                  Text("Supporting pane is shown")
                }*/
            }

            val workout by workoutViewModel.workout.collectAsState(null)
            val workoutComponents by workoutViewModel.workoutComponents.collectAsState()
            val workoutNotes by workoutViewModel.workoutNotes.collectAsState()
            val workoutName by workoutViewModel.workoutName.collectAsState()

            EditNextWorkoutScreenStateHolder(
              workoutViewModel = workoutViewModel,
              workoutID = workoutID,
              navController = navController,
              editable = true,

            )
          }
        }
      },
      supportingPane = {
        AnimatedPane(modifier = Modifier.safeContentPadding()) {
          // Supporting pane content
          Column {
            Row(
              modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.Start,
            ) {
              Text("Previous Workout")
            }

            WorkoutOverview()
          }
        }
      },
    )
  }
}

@RequiresApi(Build.VERSION_CODES.S)
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun EditNextWorkoutScreenStateHolder(
  workoutViewModel: WorkoutViewModel,
  workoutID: String,
  navController: NavController,
  editable: Boolean,
  // workout: Workout? = null, // These are now part of UiState
  // workoutName: String? = null,
  // workoutNotes: List<WorkoutNote> = emptyList(),
  // workoutComponents: List<WorkoutComponent> = emptyList(),
) {
  // Collect the UI state from the ViewModel
  val uiState by workoutViewModel.uiState.collectAsState()

  // Trigger initial load when the composable enters the composition
  LaunchedEffect(workoutID) {
    if (workoutID.isNotBlank()) {
      workoutViewModel.onEvent(EditNextWorkoutUiEvent.LoadWorkout(ObjectId(workoutID)))
    } else {
      // Handle case where workoutID is blank, e.g., navigate back or show error
      // For now, we'll let NoWorkoutFound handle it in the content.
    }
  }

  // Pass the collected state and the event handler to the content composable
  EditNextWorkoutScreenContent(
    uiState = uiState,
    onEvent = workoutViewModel::onEvent, // Pass the event handler
    navController = navController,
    editable = editable,
    populatePreviousSetField = workoutViewModel::populatePreviousSetField,
    ExerciseOptionsDropdownMenu = { exerciseSets, addNotes ->
      ExerciseOptionsDropdownMenu(
        workoutViewModel = workoutViewModel,
        exerciseSets = exerciseSets,
        addNotes = { addNotes() },
        removeExerciseSets = { workoutViewModel.removeExerciseSets(exerciseSets) },
        userDefaultWeightUnit = uiState.userDefaultWeightUnit, /*TODO Replace with user default */
        exerciseWeightUnit = exerciseSets.exercise.weightUnit,
        updateExerciseWeightUnit = { weightUnit ->
          workoutViewModel.updateExerciseWeightUnit(exerciseSets.exercise, weightUnit)
        },
        navController = navController,
        workoutComponents = uiState.workoutComponents,
        onCreateSuperset = { selectedExercises -> /*TODO*/ },
      )
    }
  )
}


@RequiresApi(Build.VERSION_CODES.S)
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun EditNextWorkoutScreenContent(
  navController: NavController,
  uiState: EditNextWorkoutUiState,
  onEvent: (EditNextWorkoutUiEvent) -> Unit,
  editable: Boolean,
  populatePreviousSetField: (exerciseID: ObjectId, setNumber: Int, setType: SetType) -> Set?,
  ExerciseOptionsDropdownMenu: @Composable (exerciseSets: ExerciseExerciseSets, addNotes: () -> Unit) -> Unit,
) {

  // Destructure uiState for easier access
  val workout = uiState.workout
  val workoutComponents = uiState.workoutComponents
  val workoutNotes = uiState.workoutNotes
  val workoutName = uiState.workoutName
  val isLoading = uiState.isLoading
  val errorMessage = uiState.errorMessage
  val userDefaultWeightUnit = uiState.userDefaultWeightUnit

  val (openSaveChangesDialog, setOpenSaveChangesDialog) = remember { mutableStateOf(false) }

  if (openSaveChangesDialog) {
    ConfirmChangesToWorkout(
      onDismissRequest = { setOpenSaveChangesDialog(false) },
      saveWorkout = { onEvent(EditNextWorkoutUiEvent.SaveWorkout) },
    )
  }

  workout?.let { _ ->
    Scaffold(
      topBar = {
        TopAppBar(
          title = {
            workoutName?.let { workoutName ->
              Text(
                workoutName, autoSize = TextAutoSize.StepBased(
                  MaterialTheme.typography.titleSmall.fontSize,
                  MaterialTheme.typography.titleLarge.fontSize
                )
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
            /*WorkoutOptionsDropdownMenu(
              addWorkoutNote = { if (editable) workoutViewModel.addWorkoutNote() },
              enableWorkoutNameTextfield = {},
              workoutNameDialog = {},
            )*/
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
        /*Row(
          modifier = Modifier.padding(horizontal = 8.dp).fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically,
        ) {
          Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.TopStart) {
            Text(itWorkout.formatLongWorkoutStarted())
          }
        }
        Row(
          modifier = Modifier.padding(horizontal = 8.dp).fillMaxWidth(),
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
        }*/

        if (workoutComponents.isNotEmpty()) {
          ExerciseSetLazyColumn(
            workoutComponents = workoutComponents,
            workoutNotes = workoutNotes,
            enableCancelWorkout = true,
            WorkoutNoteItem = { workoutNote ->
              SwipeToDismissWorkoutNote(
                workoutNote = workoutNote,
                pinWorkoutNote = { note, pinned ->
                  onEvent(EditNextWorkoutUiEvent.PinWorkoutNote(note, pinned))
                },
                updateWorkoutNote = { note, text ->
                  onEvent(EditNextWorkoutUiEvent.UpdateWorkoutNote(note, text))
                },
                removeWorkoutNote = { onEvent(EditNextWorkoutUiEvent.RemoveWorkoutNote(workoutNote)) },
              )
            },
            ExerciseItem = { exerciseSets ->

              /*TODO Replace with user default */
              val weightUnit = exerciseSets.exercise.weightUnit ?: MassUnit.POUNDS.abbreviation

              ExerciseScreen(
                navController,
                _exerciseSets = exerciseSets,
                weightUnit = weightUnit,
                exerciseWeightUnit = exerciseSets.exercise.weightUnit,
                userDefaultWeightUnit = weightUnit,
                removeExerciseSets = {
                  onEvent(
                    EditNextWorkoutUiEvent.RemoveExerciseSets(
                      exerciseSets
                    )
                  )
                },
                addExerciseNote = {
                  onEvent(EditNextWorkoutUiEvent.AddExerciseNote(exerciseSets /*, itWorkout._id*/))
                },
                removeExerciseNote = { note ->
                  onEvent(EditNextWorkoutUiEvent.RemoveExerciseNote(exerciseSets, note))
                },
                pinExerciseNote = { note, pinned ->
                  onEvent(EditNextWorkoutUiEvent.PinExerciseNote(exerciseSets, note, pinned))
                },
                updateExerciseWeightUnit = {
                  onEvent(
                    EditNextWorkoutUiEvent.UpdateExerciseWeightUnit(
                      exerciseSets.exercise,
                      it
                    )
                  )
                },
                workoutComponents = workoutComponents,
                onCreateSuperset = { /* TODO */ },
//                workoutViewModel = workoutViewModel,
                ExerciseOptionsDropdownMenu = { addNotes ->
                  ExerciseOptionsDropdownMenu(exerciseSets, addNotes)
                }
              )
            },
            SetItem = { supersetUuid, exerciseSets, set, index ->
              val setUUID = set.uuid

              /*TODO Replace with user default */
              val weightUnit = exerciseSets.exercise.weightUnit ?: MassUnit.POUNDS.abbreviation

              SetLazyListItem(
                exerciseSets = exerciseSets,
                set = set,
                index = index,
                removeSet = { onEvent(EditNextWorkoutUiEvent.RemoveSet(exerciseSets, set)) },
                setContent = {
                  SetContent(
                    setIndex = index,
                    set = set,
                    getWeightUnit = { weightUnit },
                    populatePreviousSetField = {
                      exerciseSets.exercise.let { exercise ->
                        set.setNumber?.let { setNumber ->
                          populatePreviousSetField(
                            exercise.id,
                            setNumber,
                            set.setTypeID
                          )
                        }
                      }
                    },
                    updateSetReps = { reps ->
                      onEvent(
                        EditNextWorkoutUiEvent.UpdateSetReps(
                        exerciseSets = exerciseSets,
                        setUUID = setUUID,
                        newReps = reps.toIntOrNull(),
                        )
                      )
                    },
                    updateSetRIR = { rir ->
                      onEvent(
                        EditNextWorkoutUiEvent.UpdateSetRIR(
                        exerciseSets = exerciseSets,
                        setUUID = setUUID,
                        newRIR = rir.toDoubleOrNull(),
                        )
                      )
                    },
                    updateSetWeight = { weight ->
                      onEvent(
                        EditNextWorkoutUiEvent.UpdateSetWeight(
                        exerciseSets = exerciseSets,
                        setUUID = setUUID,
                        newWeight = weight.toDoubleOrNull(),
                        )
                      )
                    },
                    updateSetType = { setType ->
                      onEvent(
                        EditNextWorkoutUiEvent.UpdateSetType(
                        exerciseSets = exerciseSets,
                        setUUID = setUUID,
                        setType = setType,
                        )
                      )
                    },
                    setComplete = { setComplete ->
                      onEvent(
                        EditNextWorkoutUiEvent.CompleteSet(
                        exerciseSets = exerciseSets,
                        setUUID = setUUID,
                        completed = setComplete,
                        )
                      )
                    },
                    startSetCompletedTimer = {},
                  )
                },
              )
            },
            AddSet = { supersetUuid, targetExSetsUuid ->
              AddSet(
                addSet = {
                  onEvent(
                    EditNextWorkoutUiEvent.AddSet(
                    supersetUuid = supersetUuid,
                    targetExSetsUuid = targetExSetsUuid,
                    )
                  )
                }
              )
            },
            AddExercise = { AddExercise(navController = navController) },
            CancelWorkout = {},
          )
        }
      }
    }
  }
}
