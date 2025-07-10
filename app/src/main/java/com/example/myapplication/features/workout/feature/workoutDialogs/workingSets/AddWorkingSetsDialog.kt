@file:OptIn(ExperimentalUuidApi::class, ExperimentalMaterial3ExpressiveApi::class)

package com.example.myapplication.features

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonShapes
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableStateSetOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.common.domain.model.ExerciseExerciseSets
import com.example.myapplication.common.domain.model.Set
import com.example.myapplication.common.domain.model.SetType
import com.example.myapplication.common.util.ModernizedLoadRIR
import com.example.myapplication.common.util.checkDoubleInput
import com.example.myapplication.common.util.doubleFormatted
import com.example.myapplication.features.workout.WorkoutViewModel
import com.example.myapplication.features.workout.feature.workoutDialogs.DialogInput
import com.example.myapplication.features.workout.feature.workoutDialogs.ModernizedLoadRIRHeaderRow
import com.example.myapplication.features.workout.feature.workoutDialogs.SetsHeaderRow
import com.example.myapplication.features.workout.feature.workoutDialogs.SwipeToDismissBoxListItem
import com.example.myapplication.features.workout.feature.workoutDialogs.workingSets.WorkingSetLazyColumn
import com.example.myapplication.features.workout.feature.workoutDialogs.workingSets.WorkingSetViewModel
import org.koin.androidx.compose.koinViewModel
import kotlin.uuid.ExperimentalUuidApi

// @Preview

// TODO Add Target RIR & Target Reps & Use Rep Capacity to determine 1RM%
//  make these optional

@Composable
fun AddWorkingSetsScreen(
  workingSetViewModel: WorkingSetViewModel = koinViewModel(),
  workoutViewModel: WorkoutViewModel,
  openDialog: (Boolean) -> Unit,
  exerciseSets: ExerciseExerciseSets,
) {
  workingSetViewModel.getExerciseSets(exerciseSets)

  val weightUnit = exerciseSets.exercise.weightUnit ?: workingSetViewModel.userDefaultWeightUnit

  /* TODO Get User's One Rep Max or Estimated One Rep Max*/
  workingSetViewModel.updateWorkingSetWeight(
    workoutViewModel.getWorkingSetWeight(exerciseSets) ?: 0.0
  )

  var selectedSets = remember { mutableStateSetOf<Set>() }

  fun selectSet(set: Set, isSelected: Boolean) {
    if (isSelected) {
      Log.d("AddWarmUpSetsScreen", "selectSet add set: $set")
      selectedSets.add(set)
      Log.d("AddWarmUpSetsScreen", "selectedSets: $selectedSets")
    } else {
      Log.d("AddWarmUpSetsScreen", "selectSet remove set: $set")
      selectedSets.remove(set)
    }
  }

  fun clearSelectedSets() {
    selectedSets = mutableStateSetOf()
    // TODO Clear set checkbox. Use a wrapper class to hold the set & checkbox state
  }

  fun applySettingsToSelectedSets(modernizedLoadRIR: ModernizedLoadRIR?) {
    Log.d("AddWarmUpSetsScreen", "applySettingsToSelectedSets called")
    Log.d("AddWarmUpSetsScreen", "applySettingsToSelectedSets: $modernizedLoadRIR")
    Log.d("AddWarmUpSetsScreen", "selectedSets: $selectedSets")
    workingSetViewModel.applyModernizedLoadRIRSettingsToSelectedSets(
      sets = selectedSets.toList(), // selectedSets.value.keys.toList(),
      modernizedLoadRIRSetting = modernizedLoadRIR,
    )
    Log.d("AddWarmUpSetsScreen", "selectedSets cleared")
    clearSelectedSets()
  }

  var currentModernizedLoadRIRState by remember { mutableStateOf<ModernizedLoadRIR?>(null) }

  AddWorkingSetsDialog(
    openDialog = { close -> openDialog(close) },
    insertSets = { sets ->
      workoutViewModel.addWorkingSets(exerciseSets, sets)
      openDialog(false)
    },
    replaceExistingSets = { sets ->
      workoutViewModel.replaceWorkingSets(exerciseSets, sets)
      openDialog(false) // TODO onNavigateBack()
    },
    insertBackOffSets = { sets ->
      workoutViewModel.addBackOffSets(exerciseSets, sets)
      openDialog(false)
    },
    weightUnit = weightUnit,
    onLoadRIRChange = { currentModernizedLoadRIRState = it },
    applySettingsToSelectedSetsButton = {
      Button(
        onClick = {
          currentModernizedLoadRIRState?.let {
            applySettingsToSelectedSets(currentModernizedLoadRIRState)
          }
        },
        shapes =
          ButtonShapes(
            shape = MaterialTheme.shapes.small,
            pressedShape = MaterialTheme.shapes.small,
          ),
      ) {
        Text("Apply Settings to Selected Sets")
      }
    },
    onCheckedChange = { set, isChecked -> selectSet(set, isChecked) },
  )
}

@Composable
fun AddWorkingSetsDialog(
  workingSetViewModel: WorkingSetViewModel = viewModel(),
  openDialog: (Boolean) -> Unit,
  insertSets: (workingSets: List<Set>) -> Unit,
  replaceExistingSets: (newSets: List<Set>) -> Unit,
  insertBackOffSets: (backOffSets: List<Set>) -> Unit,
  applySettingsToSelectedSetsButton: @Composable () -> Unit,
  weightUnit: String,
  onLoadRIRChange: (ModernizedLoadRIR?) -> Unit,
  onCheckedChange: (warmUpSet: Set, isChecked: Boolean) -> Unit,
) {
  val workingSets by workingSetViewModel.sets.collectAsState()

  val workingSetWeight by workingSetViewModel.workingSetWeight.collectAsState()

  var workingSetWeightText by remember { mutableStateOf(doubleFormatted(workingSetWeight)) }

  val modernizedLoadRIRList by workingSetViewModel.modernizedLoadRIRList.collectAsState()

  AlertDialog(
    properties = DialogProperties(usePlatformDefaultWidth = false),
    onDismissRequest = {
      // Dismiss the dialog when the user clicks outside the dialog or on the back
      // button. If you want to disable that functionality, simply use an empty
      // onDismissRequest.
      openDialog(false)
    },
    title = { Text(text = "Add working sets") },
    text = {
      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(text = "1RM or Estimated 1RM ($weightUnit)") // TODO USE USER SELECTED WEIGHT

        DialogInput(
          text = workingSetWeightText,
          onValueChange = {
            workingSetWeightText =
              when {
                it.isBlank() -> ""
                else -> checkDoubleInput(it.take(7)) ?: workingSetWeightText
              }
            workingSetViewModel.updateWorkingSetWeight(workingSetWeightText.toDoubleOrNull() ?: 0.0)
          },
        )

        modernizedLoadRIRList
          .takeIf { it.isNotEmpty() }
          ?.let { list ->
            val selectedRir =
              list.firstOrNull {
                it.repsPerformed ==
                        workingSets
                          .firstOrNull { it.setTypeID != SetType.warmUpSet }
                          ?.targetRepetitions ||
                        it.rir ==
                        workingSets
                          .firstOrNull { it.setTypeID != SetType.warmUpSet }
                          ?.targetRepetitionsInReserve
              } ?: list.first()
            ModernizedLoadRIRHeaderRow(list, selectedRir, onLoadRIRChange = onLoadRIRChange)
          }

        applySettingsToSelectedSetsButton()

        SetsHeaderRow()

        WorkingSetLazyColumn(
          sets = workingSets,
          //          removeSet = {workingSet -> workingSetViewModel.remove(workingSet)},
          addset = { workingSetViewModel.addSet() },
          workingSetItem = { workingSet, index, percentageText, repetitionsText, rirText, rpeText ->
            SwipeToDismissBoxListItem(
              removeSet = { workingSetViewModel.remove(workingSet) },
              SetLayout = {
                WarmUpSetLayout(
                  warmUpSet = workingSet,
                  setIndex = index,
                  percentageText = percentageText,
                  repetitionsText = repetitionsText,
                  rirText = rirText,
                  onCheckedChange = onCheckedChange,
                  onPercentageChanged = workingSetViewModel::updateSetPercentage,
                  onRepetitionsChanged = workingSetViewModel::updateSetReps,
                  onRirChanged = workingSetViewModel::updateSetRIR,
                )
              },
            )
          },
          actionRow = {
            Column(modifier = Modifier.fillMaxWidth()) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement =
                  Arrangement.spacedBy(32.dp, alignment = Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically,
              ) {
                TextButton(onClick = { replaceExistingSets(workingSets) }) {
                  Text("Replace Working Sets")
                }

                TextButton(onClick = { insertBackOffSets(workingSets) }) {
                  Text("Insert Back Off Sets")
                }
              }

              Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                TextButton(onClick = { openDialog(false) }) { Text("Cancel") }

                TextButton(onClick = { insertSets(workingSets) }) { Text("Insert Working Sets") }
              }
            }
          },
        )
      }
    },
    confirmButton = {},
    dismissButton = {},
  )
}
