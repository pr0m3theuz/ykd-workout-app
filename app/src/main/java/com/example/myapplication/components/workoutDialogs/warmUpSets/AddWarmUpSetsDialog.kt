package com.example.myapplication.components

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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.WorkoutViewModel
import com.example.myapplication.components.workoutDialogs.DialogInput
import com.example.myapplication.components.workoutDialogs.SetsHeaderRow
import com.example.myapplication.components.workoutDialogs.warmUpSets.WarmUpSetsViewModel
import com.example.myapplication.data.model.ExerciseSets
import com.example.myapplication.data.model.Set
import com.example.myapplication.utils.checkDoubleInput
import com.example.myapplication.utils.doubleFormatted
import org.koin.androidx.compose.koinViewModel

// @Preview
@Composable
fun AddWarmUpSetsScreen(
    workoutViewModel: WorkoutViewModel = koinViewModel(),
    warmUpSetsViewModel: WarmUpSetsViewModel = koinViewModel(),
    openDialog: (Boolean) -> Unit,
    exerciseSets: ExerciseSets,
) {
    warmUpSetsViewModel.getExerciseSets(exerciseSets)

    warmUpSetsViewModel.changeWorkingSetWeight(
        workoutViewModel.getWorkingSetWeight(exerciseSets) ?: 0.0
    )

    AddWarmUpSetsDialog(
        openDialog = { close -> openDialog(close) },
        insertWarmUpSets = { warmUpSets ->
            workoutViewModel.addWarmUpSets(
                exerciseSets,
                warmUpSets
            )
        }
    )
}

@Composable
fun AddWarmUpSetsDialog(
    warmUpSetsViewModel: WarmUpSetsViewModel = viewModel(),
    openDialog: (Boolean) -> Unit,
    insertWarmUpSets: (warmUpSets: List<Set>) -> Unit,
) {
    val workingSetWeight by warmUpSetsViewModel.workingSetWeight.collectAsState()

    val warmUpSets by warmUpSetsViewModel.warmUpSets.collectAsState()

    var workingSetWeightText by remember { mutableStateOf(doubleFormatted(workingSetWeight)) }

    AlertDialog(
        onDismissRequest = {
            // Dismiss the dialog when the user clicks outside the dialog or on the back
            // button. If you want to disable that functionality, simply use an empty
            // onDismissRequest.
            openDialog(false)
        },
        title = {
            Text(text = "Add warm-up sets")
        },
        text = {
            Column {
                Text(text = "Working Set Weight (***USER DEFAULT***)") // TODO USE USER SELECTED WEIGHT

                DialogInput(
                    text = workingSetWeightText,
                    onValueChange = {
                        workingSetWeightText = when {
                            it.isBlank() -> ""
                            else -> checkDoubleInput(it.take(7)) ?: workingSetWeightText
                        }
                        warmUpSetsViewModel.changeWorkingSetWeight(
                            checkDoubleInput(workingSetWeightText)?.toDoubleOrNull() ?: 0.0
                        )
                    },
                    onImeAction = {}
                )

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
        dismissButton = {
            TextButton(
                onClick = {
                    openDialog(false)
                }
            ) {
                Text("Cancel")
            }
        }

    )
}
