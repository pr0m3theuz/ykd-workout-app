package com.example.myapplication.features.workout

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.window.DialogProperties
import com.example.myapplication.common.domain.model.ExerciseExerciseSets
import com.example.myapplication.common.domain.model.Set
import com.example.myapplication.common.domain.model.totalWorkingSetVolume
import com.example.myapplication.common.util.checkDoubleInput
import com.example.myapplication.common.util.checkIntInput
import com.example.myapplication.common.util.doubleFormatted
import com.example.myapplication.features.workout.feature.workoutDialogs.DialogInput
import com.example.myapplication.features.workout.feature.workoutDialogs.SetsHeaderRow
import com.example.myapplication.features.workout.feature.workoutDialogs.equateVolume.EquateVolumeLazyColumn
import com.example.myapplication.features.workout.feature.workoutDialogs.equateVolume.EquateVolumeViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun EquateVolumeScreen(
  openDialog: (Boolean) -> Unit,
  workoutViewModel: WorkoutViewModel,
  exerciseSets: ExerciseExerciseSets,
  equateVolumeViewModel: EquateVolumeViewModel = koinViewModel(),
) {
  val weightUnit = exerciseSets.exercise.weightUnit ?: equateVolumeViewModel.userDefaultWeightUnit

  val totalWorkingSetVolume = exerciseSets.totalWorkingSetVolume(weightUnit)
  val workingSetWeight = workoutViewModel.getWorkingSetWeight(exerciseSets)
  val workingSetRepetitions = workoutViewModel.getWorkingSetRepetitions(exerciseSets)

  equateVolumeViewModel.initialize(exerciseSets)

  EquateVolumeDialog(
    openDialog = { close -> openDialog(close) },
    _workingSetWeight = workingSetWeight,
    _workingSetRepetitions = workingSetRepetitions,
    insertSets = { newSets -> workoutViewModel.addWorkingSets(exerciseSets, newSets) },
    totalWorkingSetVolume = totalWorkingSetVolume,
    weightUnit = weightUnit,
  )
}

@Composable
fun EquateVolumeDialog(
  equateVolumeViewModel: EquateVolumeViewModel = koinViewModel(),
  openDialog: (Boolean) -> Unit,
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

  AlertDialog(
    onDismissRequest = {
      // Dismiss the dialog when the user clicks outside the dialog or on the back
      // button. If you want to disable that functionality, simply use an empty
      // onDismissRequest.
      openDialog(false)
    },
    modifier = Modifier
      .fillMaxSize()
      .imePadding(),
    properties = DialogProperties(usePlatformDefaultWidth = false),
    shape = RoundedCornerShape(0),
    title = {
      Row(verticalAlignment = Alignment.CenterVertically) {
        IconButton(onClick = { openDialog(false) }) {
          Icon(
            Icons.AutoMirrored.Outlined.ArrowBack,
            contentDescription = "Localized description",
            modifier = Modifier.size(22.dp)
          )
        }
        Text(text = "Equate Volume")
      }
    },
    text = {
      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row {
          Text(text = "Equate Volume of Working Sets based on Weight ($weightUnit) or Repetitions")
        }

        Row { Text("Total Volume: $totalWorkingSetVolume $weightUnit") }

        Row { Text("New Total Volume: $newTotalWorkingSetVolume $weightUnit") }

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

        SetsHeaderRow()

        EquateVolumeLazyColumn()
      }
    },
    confirmButton = {
      TextButton(
        onClick = {
          openDialog(false)
          insertSets(sets)
        },
      ) {
        Text("Equate Volume")
      }
    },
    dismissButton = {
      TextButton(
        onClick = { openDialog(false) },
      ) {
        Text("Cancel")
      }
    },
  )
}
