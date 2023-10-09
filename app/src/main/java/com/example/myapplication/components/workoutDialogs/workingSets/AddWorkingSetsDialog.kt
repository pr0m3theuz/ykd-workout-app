package com.example.myapplication.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.components.workoutDialogs.DialogInput
import com.example.myapplication.components.workoutDialogs.SetsHeaderRow
import com.example.myapplication.components.workoutDialogs.workingSets.WorkingSetLazyColumn
import com.example.myapplication.components.workoutDialogs.workingSets.WorkingSetViewModel
import com.example.myapplication.data.model.ExerciseExerciseSets
import com.example.myapplication.data.model.Set
import com.example.myapplication.utils.checkDoubleInput
import com.example.myapplication.utils.doubleFormatted
import org.koin.androidx.compose.koinViewModel

// @Preview

@Composable
fun AddWorkingSetsScreen(
  workingSetViewModel: WorkingSetViewModel = koinViewModel(),
  workoutViewModel: WorkoutViewModel = koinViewModel(),
  openDialog: (Boolean) -> Unit,
  exerciseSets: ExerciseExerciseSets,
) {
  workingSetViewModel.getExerciseSets(exerciseSets)
  val weightUnit = exerciseSets.exercise?.weightUnit ?: workingSetViewModel.userDefaultWeightUnit

  /* TODO Get User's One Rep Max or Estimated One Rep Max*/
  workingSetViewModel.changeWorkingSetWeight(
    workoutViewModel.getWorkingSetWeight(exerciseSets) ?: 0.0
  )

  AddWorkingSetsDialog(
    openDialog = { close -> openDialog(close) },
    insertSets = { sets -> workoutViewModel.addWorkingSets(exerciseSets, sets) },
    insertBackOffSets = { sets -> workoutViewModel.addBackOffSets(exerciseSets, sets) },
    weightUnit = weightUnit,
  )
}

@Composable
fun AddWorkingSetsDialog(
  workingSetViewModel: WorkingSetViewModel = viewModel(),
  openDialog: (Boolean) -> Unit,
  insertSets: (workingSets: List<Set>) -> Unit,
  insertBackOffSets: (backOffSets: List<Set>) -> Unit,
  weightUnit: String,
) {
  val sets by workingSetViewModel.sets.collectAsState()

  val workingSetWeight by workingSetViewModel.workingSetWeight.collectAsState()

  var workingSetWeightText by remember { mutableStateOf(doubleFormatted(workingSetWeight)) }

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
            workingSetViewModel.changeWorkingSetWeight(workingSetWeightText.toDoubleOrNull() ?: 0.0)
          }
        )

        SetsHeaderRow()

        WorkingSetLazyColumn()

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.Center,
          verticalAlignment = Alignment.CenterVertically
        ) {
          TextButton(
            onClick = {
              openDialog(false)
              insertBackOffSets(sets)
            }
          ) {
            Text("Insert Back Off Sets")
          }
        }
      }
    },
    confirmButton = {
      TextButton(
        onClick = {
          openDialog(false)
          insertSets(sets)
        }
      ) {
        Text("Insert Working Sets")
      }
    },
    dismissButton = { TextButton(onClick = { openDialog(false) }) { Text("Cancel") } }
  )
}
