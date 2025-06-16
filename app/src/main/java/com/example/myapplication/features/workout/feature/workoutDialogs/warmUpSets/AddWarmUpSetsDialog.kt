@file:OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)

package com.example.myapplication.features

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
import androidx.compose.material3.ButtonShapes
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.window.DialogProperties
import com.example.myapplication.common.domain.model.Set
import com.example.myapplication.common.util.ModernizedLoadRIR
import com.example.myapplication.common.util.checkDoubleInput
import com.example.myapplication.common.util.doubleFormatted
import com.example.myapplication.features.workout.WorkoutViewModel
import com.example.myapplication.features.workout.feature.workoutDialogs.DialogInput
import com.example.myapplication.features.workout.feature.workoutDialogs.ModernizedLoadRIRHeaderRow
import com.example.myapplication.features.workout.feature.workoutDialogs.SetsHeaderRow
import com.example.myapplication.features.workout.feature.workoutDialogs.warmUpSets.WarmUpSetsViewModel
import org.bson.types.ObjectId
import org.koin.androidx.compose.koinViewModel

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
  val exerciseSets = workoutViewModel.getExerciseSetsByID(exerciseSetsID)

  if (exerciseSets == null) {
    Log.d("AddWarmUpSetsScreen", "ExerciseSets ${exerciseSetsID.toHexString()} could not be found.")
    onNavigateBack()
  }

  requireNotNull(exerciseSets)
  warmUpSetsViewModel.setExerciseSets(exerciseSets)

  warmUpSetsViewModel.changeWorkingSetWeight(
    workoutViewModel.getWorkingSetWeight(exerciseSets) ?: 0.0
  )

  val weightUnit = warmUpSetsViewModel.getWeightUnit(exerciseSets)

  /*  val a = warmUpSets.associateWith { false }.toMutableMap()
    val selectedSets = remember { mutableStateSetOf(warmUpSets.associateWith { false }) }

    fun selectSet(set: Set, isSelected: Boolean) {
      if (isSelected) {
        Log.d("AddWarmUpSetsScreen", "selectSet add set: $set")
        selectedSets.value = selectedSets.value.toMutableMap().apply {
          this[set] = true // Or put(set, isSelected)
        }
        Log.d("AddWarmUpSetsScreen", "selectedSets: $selectedSets")
      } else {
        Log.d("AddWarmUpSetsScreen", "selectSet remove set: $set")
        selectedSets.value = selectedSets.value.toMutableMap().apply {
          this[set] = false // Or put(set, isSelected)
        }
      }
    }*/

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
//    selectedSets.value = warmUpSets.associateWith { false }
    selectedSets = mutableStateSetOf()
  }

  fun applySettingsToSelectedSets(modernizedLoadRIR: ModernizedLoadRIR?) {
    Log.d("AddWarmUpSetsScreen", "applySettingsToSelectedSets called")
    Log.d("AddWarmUpSetsScreen", "applySettingsToSelectedSets: $modernizedLoadRIR")
    Log.d("AddWarmUpSetsScreen", "selectedSets: $selectedSets")
    warmUpSetsViewModel.applySettingsToSelectedSets(
      sets = selectedSets.toList(), // selectedSets.value.keys.toList(),
      modernizedLoadRIRSetting = modernizedLoadRIR
    )
    Log.d("AddWarmUpSetsScreen", "selectedSets cleared")
    clearSelectedSets()
  }

  /*  AddWarmUpSetsDialog(
      openDialog = { close -> openDialog(close) },
      weightUnit = weightUnit,
      insertWarmUpSets = { warmUpSets -> workoutViewModel.addWarmUpSets(exerciseSets, warmUpSets) },
      applySettingsToSelectedSets = { modernizedLoadRIR ->
        applySettingsToSelectedSets(modernizedLoadRIR)
      },
      onCheckedChange = { set, isChecked ->
        selectSet(set, isChecked)
        selectedSets.clear()
      }
    )*/

  AddWarmUpSetsContent(
    warmUpSetsViewModel = warmUpSetsViewModel,
    onNavigateBack = onNavigateBack,
    weightUnit = weightUnit,
    insertWarmUpSets = { warmUpSets ->
      workoutViewModel.addWarmUpSets(exerciseSets, warmUpSets)
      onNavigateBack() // Navigate back after inserting
    },
    applySettingsToSelectedSets = { modernizedLoadRIR ->
      applySettingsToSelectedSets(modernizedLoadRIR)
    },
//    selectedSets = selectedSets.value,
    onCheckedChange = { set, isChecked ->
      selectSet(set, isChecked)
      // Consider if you still need to clear selectedSets here
      // selectedSets.clear() // This might be too aggressive if you allow multiple selections before applying
    }
  )
}

@Composable
fun AddWarmUpSetsDialog(
  warmUpSetsViewModel: WarmUpSetsViewModel = koinViewModel(),
  openDialog: (Boolean) -> Unit,
  weightUnit: String,
  insertWarmUpSets: (warmUpSets: List<Set>) -> Unit,
  applySettingsToSelectedSets: (modernizedLoadRIRSetting: ModernizedLoadRIR?) -> Unit = {},
//  selectedSets: Map<Set, Boolean>,
  onCheckedChange: (warmUpSet: Set, isChecked: Boolean) -> Unit,
) {
  val workingSetWeight by warmUpSetsViewModel.workingSetWeight.collectAsState()

  val warmUpSets by warmUpSetsViewModel.warmUpSets.collectAsState()

  var workingSetWeightText by remember { mutableStateOf(doubleFormatted(workingSetWeight)) }

  val modernizedLoadRIRList by warmUpSetsViewModel.modernizedLoadRIRList.collectAsState()
  var curentModernizedLoadRIRState by remember { mutableStateOf<ModernizedLoadRIR?>(null) }

  AlertDialog(
    properties = DialogProperties(usePlatformDefaultWidth = false),
    onDismissRequest = {
      // Dismiss the dialog when the user clicks outside the dialog or on the back
      // button. If you want to disable that functionality, simply use an empty
      // onDismissRequest.
      openDialog(false)
    },
    title = { Text(text = "Add warm-up sets") },
    text = {
      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(text = "Working Set Weight ($weightUnit)") // TODO USE USER SELECTED WEIGHT

        DialogInput(
          text = workingSetWeightText,
          onValueChange = {
            workingSetWeightText =
              when {
                it.isBlank() -> ""
                else -> checkDoubleInput(it.take(7)) ?: workingSetWeightText
              }
            warmUpSetsViewModel.changeWorkingSetWeight(
              checkDoubleInput(workingSetWeightText)?.toDoubleOrNull() ?: 0.0
            )
          },
          onImeAction = {}
        )

        modernizedLoadRIRList.takeIf { it.isNotEmpty() }?.let { list ->
          val selectedRir = // TODO Manage Hardcoded Values
            list.firstOrNull { it.repsPerformed == 10 && it.rir == 5.0 } ?: list.first()
          ModernizedLoadRIRHeaderRow(
            list,
            selectedRir,
            onLoadRIRChange = { curentModernizedLoadRIRState = it }
          )
        }

        TextButton(onClick = {
          curentModernizedLoadRIRState?.let {
            applySettingsToSelectedSets(curentModernizedLoadRIRState)
          }
        }) {
          Text("Apply Settings to Selected Sets")
        }

        SetsHeaderRow()
        HorizontalDivider()

        WarmUpSetLazyColumn(
          onCheckedChange = onCheckedChange,
//          selectedSets = selectedSets
        )
      }
    },
    confirmButton = {
      TextButton(
        onClick = {
          openDialog(false)
          insertWarmUpSets(warmUpSets)
        }
      ) {
        Text("Insert")
      }
    },
    dismissButton = { TextButton(onClick = { openDialog(false) }) { Text("Cancel") } }
  )
}

@ExperimentalMaterial3ExpressiveApi
@Composable
fun AddWarmUpSetsContent(
  // Renamed from AddWarmUpSetsDialog
  warmUpSetsViewModel: WarmUpSetsViewModel, // Removed koinViewModel default as it's passed from AddWarmUpSetsScreen
  onNavigateBack: () -> Unit,
  weightUnit: String,
  insertWarmUpSets: (warmUpSets: List<Set>) -> Unit,
  applySettingsToSelectedSets: (modernizedLoadRIRSetting: ModernizedLoadRIR?) -> Unit = {},
//  selectedSets: Map<Set, Boolean>,
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
            Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowBack,
              contentDescription = "Back"
            )
          }
        },
        actions = {}
      )
    }
  ) { paddingValues ->
    Column(
      modifier = Modifier
        .padding(paddingValues)
        .padding(16.dp) // Add some padding around the content
        .fillMaxSize(),
      verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      Text(text = "Working Set Weight ($weightUnit)") // TODO USE USER SELECTED WEIGHT

      DialogInput( // You might want to rename DialogInput if it's no longer exclusively for dialogs
        text = workingSetWeightText,
        onValueChange = {
          workingSetWeightText =
            when {
              it.isBlank() -> ""
              else -> checkDoubleInput(it.take(7)) ?: workingSetWeightText
            }
          warmUpSetsViewModel.changeWorkingSetWeight(
            checkDoubleInput(workingSetWeightText)?.toDoubleOrNull() ?: 0.0
          )
        },
        onImeAction = {}
      )

      modernizedLoadRIRList.takeIf { it.isNotEmpty() }?.let { list ->
        val selectedRir = // TODO Manage Hardcoded Values
          list.firstOrNull { it.repsPerformed == 10 && it.rir == 5.0 } ?: list.first()
        ModernizedLoadRIRHeaderRow(
          list,
          selectedRir,
          onLoadRIRChange = { currentModernizedLoadRIRState = it }
        )
      }

      Button(
        onClick = {
          currentModernizedLoadRIRState?.let {
            applySettingsToSelectedSets(currentModernizedLoadRIRState)
          }
        },
        shapes = ButtonShapes(
          shape = MaterialTheme.shapes.small,
          pressedShape = MaterialTheme.shapes.small
        )
      ) {
        Text("Apply Settings to Selected Sets")
      }

      SetsHeaderRow()

      WarmUpSetLazyColumn(
        // Make sure WarmUpSetLazyColumn is adapted for a screen context if necessary
        onCheckedChange = onCheckedChange,
//        selectedSets = selectedSets,
      )

      Row(
        modifier = Modifier
          .fillMaxWidth(),
        horizontalArrangement = Arrangement.End,
      ) {
        TextButton(
          onClick = { onNavigateBack },
          modifier = Modifier.padding(8.dp)
        ) { Text("Cancel", color = MaterialTheme.colorScheme.error) }
        Button(
          modifier = Modifier.padding(8.dp),
          onClick = {
            insertWarmUpSets(warmUpSets)
//            onNavigateBack()
          }
        ) {
          Text("Insert")
        }

      }
    }
  }
}