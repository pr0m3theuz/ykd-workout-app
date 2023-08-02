package com.example.myapplication.components.exercise

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.WorkoutViewModel
import com.example.myapplication.components.workoutDialogs.DialogInput
import com.example.myapplication.components.workoutDialogs.SetsHeaderRow
import com.example.myapplication.components.workoutDialogs.equateVolume.EquateVolumeLazyColumn
import com.example.myapplication.components.workoutDialogs.equateVolume.EquateVolumeViewModel
import com.example.myapplication.data.ExerciseSets
import com.example.myapplication.data.Set
import com.example.myapplication.data.totalWorkingSetVolume
import com.example.myapplication.utils.checkDoubleInput
import com.example.myapplication.utils.checkIntInput
import com.example.myapplication.utils.doubleFormatted

@Composable
fun EquateVolumeScreen(
  openDialog: (Boolean) -> Unit,
  workoutViewModel: WorkoutViewModel = viewModel(),
  exerciseSets: ExerciseSets,
  equateVolumeViewModel: EquateVolumeViewModel = hiltViewModel(),
) {
  val totalWorkingSetVolume = exerciseSets.totalWorkingSetVolume()
  val workingSetWeight = workoutViewModel.getWorkingSetWeight(exerciseSets)
  val workingSetRepetitions = workoutViewModel.getWorkingSetRepetitions(exerciseSets)

  equateVolumeViewModel.initialize(exerciseSets)

  EquateVolumeDialog(
    openDialog = { close -> openDialog(close) },
    _workingSetWeight = workingSetWeight,
    _workingSetRepetitions = workingSetRepetitions,
    insertSets = { newSets -> workoutViewModel.addWorkingSets(exerciseSets, newSets) },
    totalWorkingSetVolume = totalWorkingSetVolume,
  )
}

@Composable
fun EquateVolumeDialog(
    equateVolumeViewModel: EquateVolumeViewModel = viewModel(),
    openDialog: (Boolean) -> Unit,
    _workingSetWeight: Double?,
    _workingSetRepetitions: Int?,
    insertSets: (workingSets: List<Set>) -> Unit,
    totalWorkingSetVolume: Double?,
) {
  val sets by equateVolumeViewModel.sets.collectAsState()
  val newTotalWorkingSetVolume by equateVolumeViewModel.newTotalVolume.collectAsState()

  var workingSetWeight by remember { mutableStateOf(_workingSetWeight) }

  var workingSetWeightText by remember { mutableStateOf(doubleFormatted(workingSetWeight)) }

  var workingSetRepetitions: Int by remember { mutableStateOf(_workingSetRepetitions ?: 0) }

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
    title = { Text(text = "Equate Volume") },
    text = {
      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row {
          Text(
            text =
            "Equate Volume of Working Sets based on Weight or Repetitions (***USER DEFAULT WEIGHT UNIT***)"
          ) // TODO USE USER SELECTED WEIGHT
        }

        Row { Text("Total Volume: $totalWorkingSetVolume") }

        Row { Text("New Total Volume: $newTotalWorkingSetVolume") }

        DialogInput(
          text = getWorkingSetWeightOrWorkingSetRepetitions(),
          onValueChange = {
            setWorkingSetWeightOrWorkingSetRepetitionsText(it.take(7))
            setWorkingSetWeightOrWorkingSetRepetitionsNumber()
            calculateNewValues()
          },
        )

        Column() {
          Row(verticalAlignment = Alignment.CenterVertically) {
            RadioButton(
              selected = isByWeightSelected,
              onClick = { selectByWeight() },
            )
            Text(text = "By Weight")
          }

          Row(verticalAlignment = Alignment.CenterVertically) {
            RadioButton(
              selected = isByRepetitionsSelected,
              onClick = { selectByRepetitions() },
            )
            Text(text = "By Repetitions")
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
