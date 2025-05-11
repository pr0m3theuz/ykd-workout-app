package com.example.myapplication.features

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.example.myapplication.common.domain.model.ExerciseExerciseSets
import com.example.myapplication.common.domain.model.Set
import com.example.myapplication.common.util.checkDoubleInput
import com.example.myapplication.common.util.doubleFormatted
import com.example.myapplication.features.workout.WorkoutViewModel
import com.example.myapplication.features.workout.feature.workoutDialogs.DialogInput
import com.example.myapplication.features.workout.feature.workoutDialogs.ModernizedLoadRIRHeaderRow
import com.example.myapplication.features.workout.feature.workoutDialogs.SetsHeaderRow
import com.example.myapplication.features.workout.feature.workoutDialogs.warmUpSets.WarmUpSetsViewModel
import org.koin.androidx.compose.koinViewModel

// TODO Add Target RIR & Target Reps & Use Rep Capacity to determine 1RM%
//  make these optional

// @Preview
@Composable
fun AddWarmUpSetsScreen(
  workoutViewModel: WorkoutViewModel = koinViewModel(),
  warmUpSetsViewModel: WarmUpSetsViewModel = koinViewModel(),
  openDialog: (Boolean) -> Unit,
  exerciseSets: ExerciseExerciseSets,
) {
  warmUpSetsViewModel.getExerciseSets(exerciseSets)

  warmUpSetsViewModel.changeWorkingSetWeight(
    workoutViewModel.getWorkingSetWeight(exerciseSets) ?: 0.0
  )

  val weightUnit = warmUpSetsViewModel.getWeightUnit(exerciseSets)

  AddWarmUpSetsDialog(
    openDialog = { close -> openDialog(close) },
    weightUnit = weightUnit,
    insertWarmUpSets = { warmUpSets -> workoutViewModel.addWarmUpSets(exerciseSets, warmUpSets) }
  )
}

@Composable
fun AddWarmUpSetsDialog(
  warmUpSetsViewModel: WarmUpSetsViewModel = koinViewModel(),
  openDialog: (Boolean) -> Unit,
  weightUnit: String,
  insertWarmUpSets: (warmUpSets: List<Set>) -> Unit,
) {
  val workingSetWeight by warmUpSetsViewModel.workingSetWeight.collectAsState()

  val warmUpSets by warmUpSetsViewModel.warmUpSets.collectAsState()

  var workingSetWeightText by remember { mutableStateOf(doubleFormatted(workingSetWeight)) }

  val modernizedLoadRIRList by warmUpSetsViewModel.modernizedLoadRIRList.collectAsState()

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
            onLoadRIRChange = {}
          )
        }

        SetsHeaderRow()

        WarmUpSetLazyColumn()
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
