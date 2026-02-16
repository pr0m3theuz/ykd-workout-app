@file:OptIn(ExperimentalUuidApi::class, ExperimentalMaterial3Api::class)

package com.example.myapplication.features.workout.feature.workoutDialogs.workingSets

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
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
import com.example.myapplication.features.WarmUpSetLayout
import com.example.myapplication.features.workout.WorkoutViewModel
import com.example.myapplication.features.workout.feature.workoutDialogs.DialogInput
import com.example.myapplication.features.workout.feature.workoutDialogs.ModernizedLoadRIRHeaderRow
import com.example.myapplication.features.workout.feature.workoutDialogs.SetsHeaderRow
import com.example.myapplication.features.workout.feature.workoutDialogs.SwipeToDismissBoxListItem
import org.bson.types.ObjectId
import org.koin.androidx.compose.koinViewModel
import kotlin.uuid.ExperimentalUuidApi

// @Preview

// TODO Add Target RIR & Target Reps & Use Rep Capacity to determine 1RM%
//  make these optional

@Composable
fun AddWorkingSetsScreen(
  workingSetViewModel: WorkingSetViewModel = koinViewModel(),
  workoutViewModel: WorkoutViewModel,
//  openDialog: (Boolean) -> Unit,
//  exerciseSets: ExerciseExerciseSets,
  onNavigateBack: () -> Unit,
  exerciseSetsID: ObjectId,
) {


//  workingSetViewModel.getExerciseSets(exerciseSets)

  val _exerciseSets = workoutViewModel.getExerciseSetsByID(exerciseSetsID)
  _exerciseSets?.let {
    workingSetViewModel.setExerciseSets(it)
    workingSetViewModel.updateWorkingSetWeight(
      workoutViewModel.getWorkingSetWeight(it) ?: 0.0
    )
  }

  val exerciseSets = workingSetViewModel.exerciseSets.collectAsState().value

  val weightUnit = workingSetViewModel.getWeightUnit()

  /* TODO Get User's One Rep Max or Estimated One Rep Max*/
//  workingSetViewModel.updateWorkingSetWeight(
//    workoutViewModel.getWorkingSetWeight(exerciseSets) ?: 0.0
//  )

  var selectedSets = remember { mutableStateSetOf<Set>() }

  fun selectSet(set: Set, isSelected: Boolean) {
    if (isSelected) {
//       Log.d("AddWarmUpSetsScreen", "selectSet add set: $set")
      selectedSets.add(set)
//       Log.d("AddWarmUpSetsScreen", "selectedSets: $selectedSets")
    } else {
//       Log.d("AddWarmUpSetsScreen", "selectSet remove set: $set")
      selectedSets.remove(set)
    }
  }

  fun clearSelectedSets() {
    selectedSets = mutableStateSetOf()
    // TODO Clear set checkbox. Use a wrapper class to hold the set & checkbox state
  }

  fun applySettingsToSelectedSets(modernizedLoadRIR: ModernizedLoadRIR?) {
//     Log.d("AddWarmUpSetsScreen", "applySettingsToSelectedSets called")
//     Log.d("AddWarmUpSetsScreen", "applySettingsToSelectedSets: $modernizedLoadRIR")
//     Log.d("AddWarmUpSetsScreen", "selectedSets: $selectedSets")
    workingSetViewModel.applyModernizedLoadRIRSettingsToSelectedSets(
      sets = selectedSets.toList(), // selectedSets.value.keys.toList(),
      modernizedLoadRIRSetting = modernizedLoadRIR,
    )
//     Log.d("AddWarmUpSetsScreen", "selectedSets cleared")
    clearSelectedSets()
  }

  var currentModernizedLoadRIRState by remember { mutableStateOf<ModernizedLoadRIR?>(null) }

  AddWorkingSetsContent(
    onNavigateBack = onNavigateBack,
    insertSets = { sets ->
      exerciseSets?.let { workoutViewModel.addWorkingSets(exerciseSets, sets) }
      onNavigateBack()
    },
    replaceExistingSets = { sets ->
      exerciseSets?.let { workoutViewModel.replaceWorkingSets(exerciseSets, sets) }
      onNavigateBack() // TODO onNavigateBack()
    },
    insertBackOffSets = { sets ->
      exerciseSets?.let { workoutViewModel.addBackOffSets(exerciseSets, sets) }
      onNavigateBack()
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
      ) {
        Text("Apply Settings to Selected Sets")
      }
    },
    onCheckedChange = { set, isChecked -> selectSet(set, isChecked) },
  )
}

@Composable
fun AddWorkingSetsContent(
  workingSetViewModel: WorkingSetViewModel = viewModel(),
  onNavigateBack: () -> Unit,
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

  Scaffold(
    topBar = {
      TopAppBar(
        title = { Text(text = "Add Working Sets") },
        navigationIcon = {
          IconButton(onClick = onNavigateBack) {
            Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
          }
        },
        actions = {},
      )
    }
  ) { paddingValues ->
    Column(
      modifier =
        Modifier
          .padding(paddingValues)
          .padding(16.dp) // Add some padding around the content
          .fillMaxSize(),
      verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
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
            list.firstOrNull { first ->
              first.repsPerformed ==
                      workingSets
                        .firstOrNull { it.setTypeID != SetType.warmUpSet }
                        ?.targetRepetitions ||
                      first.rir ==
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
                userWeightUnit = weightUnit,
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
              TextButton(
                modifier = Modifier.padding(8.dp),
                onClick = { onNavigateBack() }) {
                Text(
                  "Cancel",
                  color = MaterialTheme.colorScheme.error
                )
              }

              TextButton(
                modifier = Modifier.padding(8.dp),
                onClick = { insertSets(workingSets) }) { Text("Insert Working Sets") }
            }
          }
        },
      )
    }
  }
}
