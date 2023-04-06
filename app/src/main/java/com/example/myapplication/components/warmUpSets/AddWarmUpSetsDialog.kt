package com.example.myapplication.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.WorkoutViewModel
import com.example.myapplication.components.warmUpSets.WarmUpSetsViewModel
import com.example.myapplication.data.ExerciseSetModel
import com.example.myapplication.data.SetModel
import com.example.myapplication.ui.theme.PurpleGrey80
import com.example.myapplication.utils.checkDoubleInput
import com.example.myapplication.utils.doubleFormatted

// @Preview
@Composable
fun AddWarmUpSetsScreen(
    workoutViewModel: WorkoutViewModel = viewModel(),
    warmUpSetsViewModel: WarmUpSetsViewModel = viewModel(),
    openDialog: (Boolean) -> Unit,
    exerciseSets: ExerciseSetModel,
) {
    warmUpSetsViewModel.changeWorkingSetWeight(
        workoutViewModel.getWorkingSetWeight(exerciseSets) ?: 0.0
    )

    AddWarmUpSetsDialog(
        openDialog = { close -> openDialog(close) },
        onWorkingSetWeightChange = { newWorkingWeight ->
            warmUpSetsViewModel.changeWorkingSetWeight(newWorkingWeight)
        },
        updateWarmUpSetWeight = { id: Long, newWeight: Double ->
            warmUpSetsViewModel.updateWarmUpSetWeight(
                id,
                newWeight
            )
        },
        updateWarmUpSetReps = { id: Long, newReps: Int ->
            warmUpSetsViewModel.updateWarmUpSetReps(
                id,
                newReps
            )
        },
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
    onWorkingSetWeightChange: (Double) -> Unit,
    updateWarmUpSetWeight: (id: Long, newWeight: Double) -> Unit,
    updateWarmUpSetReps: (id: Long, newReps: Int) -> Unit,
    insertWarmUpSets: (warmUpSets: List<SetModel>) -> Unit,
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
                Box(
                    modifier = Modifier
//                        .padding(4.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(PurpleGrey80)
                        .fillMaxWidth()
                        .wrapContentSize(Alignment.Center),
                    contentAlignment = Alignment.Center

                ) {
                    WorkingWeightTextField(
                        workingSetWeightText = workingSetWeightText,
                        onWorkingSetWeightChange = {
                            workingSetWeightText = when {
                                it.isBlank() -> ""
                                else -> checkDoubleInput(it) ?: workingSetWeightText
                            }
                            onWorkingSetWeightChange(
                                checkDoubleInput(workingSetWeightText)?.toDoubleOrNull() ?: 0.0
                            )
                        },
                        onImeAction = {
                        }
                    )
                }
                SetsHeaderRow()

                WarmUpSetLazyColumn(
                    warmUpSets = warmUpSets,
                    updateWarmUpSetWeight = { id: Long, newWeight: Double ->
                        updateWarmUpSetWeight(
                            id,
                            newWeight
                        )
                    },
                    updateWarmUpSetReps = { id: Long, newReps: Int ->
                        updateWarmUpSetReps(
                            id,
                            newReps
                        )
                    },
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    openDialog(false)
                    run { insertWarmUpSets(warmUpSets) }
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

@Composable
fun SetsHeaderRow() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .padding(0.dp)
                .width(48.dp),
            contentAlignment = Alignment.Center

        ) {
            Text("Set")
        }

        Box(
            modifier = Modifier
                .padding(PaddingValues(horizontal = 4.dp)),
            contentAlignment = Alignment.Center
        ) { Text("Percent") }

        Box(
            modifier = Modifier
                .padding(PaddingValues(horizontal = 4.dp))
                .width(50.dp),
            contentAlignment = Alignment.Center
        ) { Text("Reps") }

        Box(
            modifier = Modifier
                .padding(PaddingValues(horizontal = 4.dp))
                .width(60.dp),
            contentAlignment = Alignment.Center
        ) { /* TODO Put appropriate text here */ }
    }
}

@Composable
private fun WorkingWeightTextField(
    workingSetWeightText: String,
    onWorkingSetWeightChange: (newWeight: String) -> Unit,
    onImeAction: () -> Unit = {},
) {
    BasicTextField(
        modifier = Modifier
            .fillMaxWidth()
            .padding(PaddingValues(4.dp)),
        textStyle = TextStyle(
            fontSize = 5.em,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onBackground
        ),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number,
            imeAction = ImeAction.Done
        ),
        value = workingSetWeightText,
        onValueChange = {
            onWorkingSetWeightChange(it.take(7))
        },
        keyboardActions = KeyboardActions(
            onDone = {
                onImeAction()
            },
        ),
    )
}
