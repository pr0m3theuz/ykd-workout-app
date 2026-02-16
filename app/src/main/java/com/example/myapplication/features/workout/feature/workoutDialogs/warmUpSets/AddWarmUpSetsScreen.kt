@file:OptIn(
  ExperimentalMaterial3Api::class,
  ExperimentalUuidApi::class,
)

package com.example.myapplication.features.workout.feature.workoutDialogs.warmUpSets

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.myapplication.common.domain.model.Set
import com.example.myapplication.common.util.ModernizedLoadRIR
import com.example.myapplication.common.util.checkDoubleInput
import com.example.myapplication.common.util.doubleFormatted
import com.example.myapplication.features.workout.WorkoutViewModel
import com.example.myapplication.features.workout.feature.workoutDialogs.DialogInput
import com.example.myapplication.features.workout.feature.workoutDialogs.ModernizedLoadRIRHeaderRow
import com.example.myapplication.features.workout.feature.workoutDialogs.SetsHeaderRow
import org.bson.types.ObjectId
import org.koin.androidx.compose.koinViewModel
import kotlin.uuid.ExperimentalUuidApi

// TODO Add Target RIR & Target Reps & Use Rep Capacity to determine 1RM%
//  make these optional

// @Preview
@Composable
fun AddWarmUpSetsScreen(
  workoutViewModel: WorkoutViewModel,
  warmUpSetsViewModel: WarmUpSetsViewModel = koinViewModel(),
  //  openDialog: (Boolean) -> Unit,
  onNavigateBack: () -> Unit,
  exerciseSetsID: ObjectId,
) {
//  workoutViewModel.workoutComponents.collectAsState().value.forEachIndexed { index, workoutComponent
//    ->
//    when (workoutComponent) {
//      is ExerciseExerciseSets -> {
//        Log.d("AddWarmUpSetsScreen.Workout", "workout component: ${workoutComponent.id}")
//      }
//
//      is SupersetExercisesSets -> {
//        workoutComponent.exercisesSets.forEach { exerciseSets ->
//          Log.d("AddWarmUpSetsScreen.Workout", "workout component: ${exerciseSets.id}")
//        }
//      }
//    }
//  }

  val _exerciseSets = workoutViewModel.getExerciseSetsByID(exerciseSetsID)
  _exerciseSets?.let {
    warmUpSetsViewModel.setExerciseSets(it)
    warmUpSetsViewModel.updateWorkingSetWeight(
      workoutViewModel.getWorkingSetWeight(it) ?: 0.0
    )
  }

  val exerciseSets = warmUpSetsViewModel.exerciseSets.collectAsState().value

  val weightUnit = warmUpSetsViewModel.getWeightUnit()



  /*  val a = warmUpSets.associateWith { false }.toMutableMap()
  val selectedSets = remember { mutableStateSetOf(warmUpSets.associateWith { false }) }

  fun selectSet(set: Set, isSelected: Boolean) {
    if (isSelected) {
//       Log.d("AddWarmUpSetsScreen", "selectSet add set: $set")
      selectedSets.value = selectedSets.value.toMutableMap().apply {
        this[set] = true // Or put(set, isSelected)
      }
//       Log.d("AddWarmUpSetsScreen", "selectedSets: $selectedSets")
    } else {
//       Log.d("AddWarmUpSetsScreen", "selectSet remove set: $set")
      selectedSets.value = selectedSets.value.toMutableMap().apply {
        this[set] = false // Or put(set, isSelected)
      }
    }
  }*/

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
    //    selectedSets.value = warmUpSets.associateWith { false }
    selectedSets = mutableStateSetOf()
    // TODO Clear set checkbox. Use a wrapper class to hold the set & checkbox state
  }

  fun applySettingsToSelectedSets(modernizedLoadRIR: ModernizedLoadRIR?) {
//     Log.d("AddWarmUpSetsScreen", "applySettingsToSelectedSets called")
//     Log.d("AddWarmUpSetsScreen", "applySettingsToSelectedSets: $modernizedLoadRIR")
//     Log.d("AddWarmUpSetsScreen", "selectedSets: $selectedSets")
    warmUpSetsViewModel.applyModernizedLoadRIRSettingsToSelectedSets(
      sets = selectedSets.toList(), // selectedSets.value.keys.toList(),
      modernizedLoadRIRSetting = modernizedLoadRIR,
    )
//     Log.d("AddWarmUpSetsScreen", "selectedSets cleared")
    clearSelectedSets()
  }

  var currentModernizedLoadRIRState by remember { mutableStateOf<ModernizedLoadRIR?>(null) }

  AddWarmUpSetsContent(
    onNavigateBack = onNavigateBack,
    weightUnit = weightUnit,
    insertWarmUpSets = { warmUpSets ->
      exerciseSets?.let { workoutViewModel.addWarmUpSets(it, warmUpSets) }
      onNavigateBack() // Navigate back after inserting
    },
    replaceWarmUpSets = { warmUpSets ->
      exerciseSets?.let { workoutViewModel.replaceWarmUpSets(it, warmUpSets) }
      onNavigateBack()
    },
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
    //    selectedSets = selectedSets.value,
    onCheckedChange = { set, isChecked ->
      selectSet(set, isChecked)
      // Consider if you still need to clear selectedSets here
      // selectedSets.clear() // This might be too aggressive if you allow multiple selections
      // before applying
    },
  )
}

@Composable
fun AddWarmUpSetsContent(
  warmUpSetsViewModel: WarmUpSetsViewModel = koinViewModel(),
  onNavigateBack: () -> Unit,
  weightUnit: String,
  insertWarmUpSets: (warmUpSets: List<Set>) -> Unit,
  replaceWarmUpSets: (warmUpSets: List<Set>) -> Unit,
  applySettingsToSelectedSetsButton: @Composable () -> Unit,
  onLoadRIRChange: (ModernizedLoadRIR?) -> Unit,
  onCheckedChange: (warmUpSet: Set, isChecked: Boolean) -> Unit,
) {
  val workingSetWeight by warmUpSetsViewModel.workingSetWeight.collectAsState()
  val warmUpSets by warmUpSetsViewModel.warmUpSets.collectAsState()
  var workingSetWeightText by remember { mutableStateOf(doubleFormatted(workingSetWeight)) }
  val modernizedLoadRIRList by warmUpSetsViewModel.modernizedLoadRIRList.collectAsState()
  var currentModernizedLoadRIRState by remember { mutableStateOf<ModernizedLoadRIR?>(null) }

  Scaffold(
    topBar = {
      TopAppBar(
        title = { Text(text = "Add Warm-up Sets") },
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
      verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
      Text(text = "Working Set Weight ($weightUnit)") // TODO USE USER SELECTED WEIGHT

      DialogInput(
        // You might want to rename DialogInput if it's no longer exclusively for dialogs
        text = workingSetWeightText,
        onValueChange = {
          workingSetWeightText =
            when {
              it.isBlank() -> ""
              else -> checkDoubleInput(it.take(7)) ?: workingSetWeightText
            }
          warmUpSetsViewModel.updateWorkingSetWeight(
            checkDoubleInput(workingSetWeightText)?.toDoubleOrNull() ?: 0.0
          )
        },
        onImeAction = {},
      )

      modernizedLoadRIRList
        .takeIf { it.isNotEmpty() }
        ?.let { list ->
          val selectedRir = // TODO Manage Hardcoded Values
            list.firstOrNull { it.repsPerformed == 10 && it.rir == 5.0 } ?: list.first()
          ModernizedLoadRIRHeaderRow(list, selectedRir, onLoadRIRChange = onLoadRIRChange)
        }

      applySettingsToSelectedSetsButton()

      SetsHeaderRow()

      WarmUpSetLazyColumn(
        // Make sure WarmUpSetLazyColumn is adapted for a screen context if necessary
        sets = warmUpSets,
        //        onCheckedChange = onCheckedChange,
        addSet = warmUpSetsViewModel::addSet,
        //        selectedSets = selectedSets,
        warmUpSetItem = { warmUpSet, index, percentageText, repetitionsText, rirText, rpeText ->
          WarmUpSetItem(
            userWeightUnit = weightUnit,
            warmUpSet = warmUpSet,
            index = index,
            percentageText = percentageText,
            repetitionsText = repetitionsText,
            rirText = rirText,
            rpeText = rpeText,
            onCheckedChange = onCheckedChange,
            onPercentageChanged = warmUpSetsViewModel::updateWarmUpSetPercentage,
            onRepetitionsChanged = warmUpSetsViewModel::updateWarmUpSetReps,
            onRirChanged = warmUpSetsViewModel::updateWarmUpSetRIR,
            removeSet = warmUpSetsViewModel::remove,
          )
        },
        actionRow = {
          Column(modifier = Modifier.fillMaxWidth()) {
            Row {
              TextButton(
                onClick = { replaceWarmUpSets(warmUpSets) },
                modifier = Modifier.padding(8.dp),
              ) {
                Text("Update Existing Warm Up Sets")
              }
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
              TextButton(onClick = { onNavigateBack }, modifier = Modifier.padding(8.dp)) {
                Text("Cancel", color = MaterialTheme.colorScheme.error)
              }
              Button(
                modifier = Modifier.padding(8.dp),
                onClick = { insertWarmUpSets(warmUpSets) },
              ) {
                Text("Insert")
              }
            }
          }
        },
      )
    }
  }
}
