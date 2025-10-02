@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.myapplication.features.workout

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
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.myapplication.common.domain.model.Set
import com.example.myapplication.common.domain.model.totalWorkingSetVolume
import com.example.myapplication.common.util.checkDoubleInput
import com.example.myapplication.common.util.checkIntInput
import com.example.myapplication.common.util.doubleFormatted
import com.example.myapplication.features.workout.feature.workoutDialogs.DialogInput
import com.example.myapplication.features.workout.feature.workoutDialogs.ModernizedLoadRIRHeaderRow
import com.example.myapplication.features.workout.feature.workoutDialogs.SetsHeaderRow
import com.example.myapplication.features.workout.feature.workoutDialogs.equateVolume.EquateVolumeLazyColumn
import com.example.myapplication.features.workout.feature.workoutDialogs.equateVolume.EquateVolumeViewModel
import org.bson.types.ObjectId
import org.koin.androidx.compose.koinViewModel
import kotlin.uuid.ExperimentalUuidApi

// TODO Add Target RIR & Target Reps & Use Rep Capacity to determine 1RM%
//  make these optional

@Composable
fun EquateVolumeScreen(
//  openDialog: (Boolean) -> Unit,
  workoutViewModel: WorkoutViewModel,
//  exerciseSets: ExerciseExerciseSets,
  equateVolumeViewModel: EquateVolumeViewModel = koinViewModel(),
  onNavigateBack: () -> Unit,
  exerciseSetsID: ObjectId,
) {
  val _exerciseSets = workoutViewModel.getExerciseSetsByID(exerciseSetsID)
  _exerciseSets?.let {
    equateVolumeViewModel.setExerciseSets(it)
//    equateVolumeViewModel.updateWorkingSetWeight(
//      workoutViewModel.getWorkingSetWeight(it) ?: 0.0
//    )
  }

  val exerciseSets = equateVolumeViewModel.exerciseSets.collectAsState().value

  val weightUnit = equateVolumeViewModel.getWeightUnit()

  val totalWorkingSetVolume = exerciseSets?.totalWorkingSetVolume(weightUnit) ?: 0.0
  val workingSetWeight = exerciseSets?.let { workoutViewModel.getWorkingSetWeight(it) }
  val workingSetRepetitions = exerciseSets?.let { workoutViewModel.getWorkingSetRepetitions(it) }

//  equateVolumeViewModel.initialize(exerciseSets)

  EquateVolumeContent(
    onNavigateBack = { onNavigateBack() },
    _workingSetWeight = workingSetWeight,
    _workingSetRepetitions = workingSetRepetitions,
    insertSets = { newSets -> exerciseSets?.let { workoutViewModel.addWorkingSets(it, newSets) } },
    totalWorkingSetVolume = totalWorkingSetVolume,
    weightUnit = weightUnit,
  )
}

@OptIn(ExperimentalUuidApi::class)
@Composable
fun EquateVolumeContent(
  equateVolumeViewModel: EquateVolumeViewModel = koinViewModel(),
  onNavigateBack: () -> Unit,
  _workingSetWeight: Double?,
  _workingSetRepetitions: Int?,
  insertSets: (workingSets: List<Set>) -> Unit,
  totalWorkingSetVolume: Double?,
  weightUnit: String,
) {
  val sets by equateVolumeViewModel.sets.collectAsState()
  val newTotalWorkingSetVolume by equateVolumeViewModel.newTotalVolume.collectAsState()

  var workingSetWeight by remember { mutableStateOf(_workingSetWeight) }

  var workingSetWeightText by remember { mutableStateOf(doubleFormatted(workingSetWeight)) }

  var workingSetRepetitions: Int by remember { mutableIntStateOf(_workingSetRepetitions ?: 0) }

  var workingSetRepetitionsText by remember { mutableStateOf(workingSetRepetitions.toString()) }

  //    Log.i("EquateVolumeScreen() workingSetRepetitions", "$workingSetRepetitions")

  val modernizedLoadRIRList by equateVolumeViewModel.modernizedLoadRIRList.collectAsState()

  var isByWeightSelected: Boolean by remember { mutableStateOf(true) }
  var isByRepetitionsSelected: Boolean by remember { mutableStateOf(false) }

  fun selectByWeight() {
    isByWeightSelected = true
    isByRepetitionsSelected = false
  }

  fun selectByRepetitions() {
    isByRepetitionsSelected = true
    isByWeightSelected = false
  }

  fun getWorkingSetWeightOrWorkingSetRepetitions(): String {
    return when {
      isByWeightSelected -> workingSetWeightText
      isByRepetitionsSelected -> workingSetRepetitionsText
      else -> ""
    }
  }

  fun setWorkingSetWeightOrWorkingSetRepetitionsText(newValue: String) {
    if (isByWeightSelected) {
      workingSetWeightText = checkDoubleInput(newValue) ?: workingSetWeightText
    } else if (isByRepetitionsSelected) {
      workingSetRepetitionsText = checkIntInput(newValue)
    }
  }

  fun setWorkingSetWeightOrWorkingSetRepetitionsNumber() {
    if (isByWeightSelected) {
      workingSetWeight = workingSetWeightText.toDoubleOrNull()
    } else if (isByRepetitionsSelected) {
      workingSetRepetitions = workingSetRepetitionsText.toIntOrNull() ?: 0
    }
  }

  fun calculateNewValues() {
    if (isByWeightSelected) {
      equateVolumeViewModel.equateVolumeByWeight(workingSetWeight)
    } else if (isByRepetitionsSelected) {
      equateVolumeViewModel.equateVolumeByRepetitions(workingSetRepetitions)
    }
  }

  Scaffold(
    topBar = {
      TopAppBar(
        title = { Text(text = "Equate Volume") },
        navigationIcon = {
          IconButton(onClick = onNavigateBack) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowBack,
              contentDescription = "Navigate Back"
            )
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
      Row {
        Text(text = "Equate volume of working sets based on weight ($weightUnit) or repetitions.")
      }

      Row {
        Text(
          "Total Volume: $totalWorkingSetVolume $weightUnit",
          modifier = Modifier.weight(0.5f, true)
        )
        Text(
          "New Volume: $newTotalWorkingSetVolume $weightUnit",
          modifier = Modifier.weight(0.5f, true)
        )
      }

      DialogInput(
        text = getWorkingSetWeightOrWorkingSetRepetitions(),
        onValueChange = {
          setWorkingSetWeightOrWorkingSetRepetitionsText(it.take(7))
          setWorkingSetWeightOrWorkingSetRepetitionsNumber()
          calculateNewValues()
        },
      )

      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceAround
      ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
          RadioButton(
            selected = isByWeightSelected,
            onClick = { selectByWeight() },
          )
          Text(text = "By Weight ($weightUnit)", style = MaterialTheme.typography.bodySmall)
        }

        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
          RadioButton(
            selected = isByRepetitionsSelected,
            onClick = { selectByRepetitions() },
          )
          Text(text = "By Repetitions", style = MaterialTheme.typography.bodySmall)
        }
      }

      modernizedLoadRIRList.takeIf { it.isNotEmpty() }?.let { list ->
        val selectedRir =
          list.firstOrNull { it.repsPerformed == workingSetRepetitions } ?: list.first()
        ModernizedLoadRIRHeaderRow(
          list,
          selectedRir,
          onLoadRIRChange = { modernizedLoadRIR ->
            modernizedLoadRIR?.let { equateVolumeViewModel.updateAllSets(it) }
          }
        )
      }

      SetsHeaderRow()

      EquateVolumeLazyColumn(
        actionRow = {
          Column(modifier = Modifier.fillMaxWidth()) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
              TextButton(
                onClick = { onNavigateBack },
                modifier = Modifier.padding(8.dp)
              ) {
                Text("Cancel", color = MaterialTheme.colorScheme.error)
              }
              Button(
                modifier = Modifier.padding(8.dp),
                onClick = {
                  onNavigateBack
                  insertSets(sets)
                },
              ) {
                Text("Equate Volume")
              }
            }
          }
        }
      )
    }

  }
}
