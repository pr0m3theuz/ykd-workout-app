package com.example.myapplication.features

import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavController
import com.example.myapplication.common.domain.model.ExerciseExerciseSets
import com.example.myapplication.common.domain.model.WorkoutComponent
import com.example.myapplication.features.addExercisesList.ReplaceExerciseScreen
import com.example.myapplication.features.workout.EquateVolumeScreen
import com.example.myapplication.features.workout.WorkoutViewModel
import com.example.myapplication.features.workout.feature.exercise.CreateSupersetScreen
import com.example.myapplication.features.workout.feature.setRestTimer.AutoRestTimerScreen
import kotlin.time.DurationUnit
import kotlin.time.toDuration

// import com.example.myapplication.components.RestTimer.RestTimePicker

@Composable
fun ExerciseOptionsDropdownMenu(
  workoutViewModel: WorkoutViewModel,
  exerciseSets: ExerciseExerciseSets,
  addNotes: () -> Unit,
  removeExerciseSets: () -> Unit,
  userDefaultWeightUnit: String,
  exerciseWeightUnit: String?,
  updateExerciseWeightUnit: (String?) -> Unit,
  navController: NavController,
  workoutComponents: List<WorkoutComponent> = listOf(),
  onCreateSuperset: (List<ExerciseExerciseSets>) -> Unit = {},
) {
  var expandedExerciseOptionsDropdownMenu by remember { mutableStateOf(false) }
  var expandedWeightUnitOptionsDropdownMenu by remember { mutableStateOf(false) }
  var expandedBarTypeOptionsDropdownMenu by remember { mutableStateOf(false) }
  var openAddWarmUpSetsDialog by remember { mutableStateOf(false) }
  var openAddWorkingSetsDialog by remember { mutableStateOf(false) }
  var openRestTimePicker by remember { mutableStateOf(false) }
  var openReplaceExerciseDialog by remember { mutableStateOf(false) }
  var openEquateVolumeDialog by remember { mutableStateOf(false) }
  var openReplaceExerciseScreen by remember { mutableStateOf(false) }
  var openCreateSupersetScreen by remember { mutableStateOf(false) }

  fun closeAddWarmUpSetsDialog(close: Boolean) {
    openAddWarmUpSetsDialog = close
  }

  fun closeWorkingSetsDialog(close: Boolean) {
    openAddWorkingSetsDialog = close
  }

  fun closeRestTimerDialog(close: Boolean) {
    openRestTimePicker = close
  }

  fun closeReplaceExerciseDialog(close: Boolean) {
    openReplaceExerciseDialog = close
  }

  fun closeEquateVolumeDialog(close: Boolean) {
    openEquateVolumeDialog = close
  }

  fun closeCreateSupersetDialog(close: Boolean) {
    openCreateSupersetScreen = close
  }

  val primaryColor = MaterialTheme.colorScheme.primary

  Box {
    IconButton(onClick = { expandedExerciseOptionsDropdownMenu = true }) {
      Icon(
        Icons.Outlined.MoreVert,
        contentDescription = "Exercise Options and Settings",
        tint = primaryColor
      )
    }
    DropdownMenu(
      expanded = expandedExerciseOptionsDropdownMenu,
      onDismissRequest = { expandedExerciseOptionsDropdownMenu = false }
    ) {
      DropdownMenuItem(
        text = { Text("Add Note") },
        onClick = {
          addNotes()
          expandedExerciseOptionsDropdownMenu = false
        }
      )
      DropdownMenuItem(
        text = { Text("Add Warm-up Sets") },
        onClick = { openAddWarmUpSetsDialog = true }
      )
      DropdownMenuItem(
        text = { Text("Add Working Sets") },
        onClick = { openAddWorkingSetsDialog = true }
      )
      DropdownMenuItem(
        text = { Text("Equate Volume") },
        onClick = { openEquateVolumeDialog = true }
      )
      DropdownMenuItem(
        text = { Text("Create Superset") },
        onClick = { openCreateSupersetScreen = true })
      //            DropdownMenuItem(
      //                text = { Text("Create Giant Set") },
      //                onClick = { /* TODO */ }
      //            )
      //            DropdownMenuItem(
      //                text = { Text("Create Drop Set") },
      //                onClick = { /* TODO */ }
      //            )
      //            DropdownMenuItem(
      //                text = { Text("Create Myo-Rep Set") },
      //                onClick = { /* TODO */ }
      //            )
      HorizontalDivider()
      DropdownMenuItem(
        text = { Text("Replace Exercise") },
        onClick = {
          expandedExerciseOptionsDropdownMenu = false
          openReplaceExerciseDialog = true
        }
      )
      HorizontalDivider()
      DropdownMenuItem(
        text = { Text("Auto Rest Timer") },
        trailingIcon = {
          exerciseSets.exercise
            .workingSetRestInWholeMilliseconds
            .toDuration(DurationUnit.MILLISECONDS)
            .let {
              Text(
                it.toComponents { minutes, seconds, _ ->
                  "$minutes:${seconds.toString().padStart(2, '0')}"
                },
                color = primaryColor
              )
            }
        },
        onClick = { openRestTimePicker = true }
      )
      DropdownMenuItem(
        text = { Text("Weight Unit") },
        trailingIcon = {
          Text(exerciseSets.exercise?.weightUnit ?: userDefaultWeightUnit, color = primaryColor)
        },
        onClick = {
          expandedExerciseOptionsDropdownMenu = false
          expandedWeightUnitOptionsDropdownMenu = true
        }
      )
      //          // TODO to keep or not to keep
      DropdownMenuItem(
        text = { Text("Bar Type") },
        trailingIcon = { Text("TODO", color = primaryColor) },
        onClick = {
          expandedExerciseOptionsDropdownMenu = false
          expandedBarTypeOptionsDropdownMenu = true
        }
      )
      HorizontalDivider()
      DropdownMenuItem(text = { Text("Remove Exercise") }, onClick = { removeExerciseSets() })
    }

    WeightUnitOptionsDropdownMenu(
      expandMenu = { expandedWeightUnitOptionsDropdownMenu },
      onClickArrowBack = { expandedExerciseOptionsDropdownMenu = true },
      onDismiss = { expandedWeightUnitOptionsDropdownMenu = false },
      userDefaultWeightUnit = userDefaultWeightUnit,
      exerciseWeightUnit = exerciseWeightUnit,
      updateExerciseWeightUnit = { weightUnit: String? -> updateExerciseWeightUnit(weightUnit) }
    )
    BarTypeDropdownMenu(
      expandMenu = { expandedBarTypeOptionsDropdownMenu },
      onClickArrowBack = { expandedExerciseOptionsDropdownMenu = true },
      onDismiss = { expandedBarTypeOptionsDropdownMenu = false }
    )

    // TODO Add exerciseSets to each of the below functions
    if (openAddWarmUpSetsDialog) {
      AddWarmUpSetsScreen(
        openDialog = { close -> closeAddWarmUpSetsDialog(close) },
        exerciseSets = exerciseSets
      )
    }
    if (openAddWorkingSetsDialog) {
      AddWorkingSetsScreen(
        workoutViewModel = workoutViewModel,
        openDialog = { close -> closeWorkingSetsDialog(close) },
        exerciseSets = exerciseSets
      )
    }

    if (openRestTimePicker) {
      AutoRestTimerScreen(
        workoutViewModel = workoutViewModel,
        openDialog = { close -> closeRestTimerDialog(close) }, exercise = exerciseSets.exercise
      )
    }

    if (openReplaceExerciseDialog) {
      ReplaceExerciseDialog(
        openDialog = { closeReplaceExerciseDialog(it) },
        closeMenu = { expandedExerciseOptionsDropdownMenu = true },
        openReplaceExerciseScreen = { openReplaceExerciseScreen = it }
      )
    }

    if (openEquateVolumeDialog) {
      EquateVolumeScreen(
        workoutViewModel = workoutViewModel,
        openDialog = { close -> closeEquateVolumeDialog(close) },
        exerciseSets = exerciseSets
      )
    }

    if (openReplaceExerciseScreen) {
      ReplaceExerciseScreen(
        workoutViewModel = workoutViewModel,
        exerciseSets = exerciseSets,
        setOpenDialog = { close -> openReplaceExerciseScreen = close },
        navController = navController,
      )
    }

    if (openCreateSupersetScreen) {
      CreateSupersetScreen(
        onDismiss = { openCreateSupersetScreen = false },
        workoutComponents = workoutComponents,
        onCreateSuperset = { selectedExercises ->
          onCreateSuperset(selectedExercises)
        },
        onShowMessage = { _ -> },
      )
    }
  }
}
