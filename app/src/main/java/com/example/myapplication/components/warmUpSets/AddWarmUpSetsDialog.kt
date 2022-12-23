package com.example.myapplication.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.WorkoutViewModel
import com.example.myapplication.components.warmUpSets.WarmUpViewModel
import com.example.myapplication.data.ExerciseSetModel
import com.example.myapplication.data.SetModel
import com.example.myapplication.ui.theme.PurpleGrey80
import kotlin.math.roundToInt

// @Preview
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AddWarmUpSetsScreen(
    workoutViewModel: WorkoutViewModel = viewModel(),
    warmUpViewModel: WarmUpViewModel = viewModel(),
    openDialog: (Boolean) -> Unit,
    exerciseSets: ExerciseSetModel
) {
    AddWarmUpSetsDialog(
        openDialog = { close -> openDialog(close) },
        _workingSetWeight = workoutViewModel.getWorkingSetWeight(exerciseSets),
        changeWorkingSetWeight = { newWorkingWeight ->
            warmUpViewModel.changeWorkingSetWeight(
                newWorkingWeight
            )
        },
        warmUpSets = warmUpViewModel.warmUpSets,
        addSet = { warmUpViewModel.addSet() },
        removeSet = { id: Long -> warmUpViewModel.remove(id) },
        updateWarmUpSetWeight = { id: Long, newWeight: Double ->
            warmUpViewModel.updateWarmUpSetWeight(
                id,
                newWeight
            )
        },
        updateWarmUpSetReps = { id: Long, newReps: Int ->
            warmUpViewModel.updateWarmUpSetReps(
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
    openDialog: (Boolean) -> Unit,
    _workingSetWeight: Double?,
    changeWorkingSetWeight: (Double) -> Unit,
    warmUpSets: List<SetModel>,
    addSet: () -> Unit,
    removeSet: (id: Long) -> Unit,
    updateWarmUpSetWeight: (id: Long, newWeight: Double) -> Unit,
    updateWarmUpSetReps: (id: Long, newReps: Int) -> Unit,
    insertWarmUpSets: (warmUpSets: List<SetModel>) -> Unit
) {
    var workingSetWeight: Double by remember {
        if (_workingSetWeight != null) {
            mutableStateOf(_workingSetWeight)
        } else {
            mutableStateOf(0.0)
        }
    }

    var workingSetWeightText by remember {
        mutableStateOf(
            TextFieldValue(
                if (workingSetWeight == 0.0) {
                    ""
                } else {
                    if (workingSetWeight.minus(workingSetWeight.roundToInt()) == 0.0) {
                        workingSetWeight.roundToInt().toString()
                    } else {
                        workingSetWeight.toString()
                    }
                }
            )
        )
    }

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
                Text(text = "Work set (***USER DEFAULT***)") // TODO USE USER SELECTED WEIGHT
                Box(
                    modifier = Modifier
//                        .padding(4.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(PurpleGrey80)
                        .fillMaxWidth()
                        .wrapContentSize(Alignment.Center),
                    contentAlignment = Alignment.Center

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
                            imeAction = ImeAction.Next
                        ),
                        value = workingSetWeightText,
                        onValueChange = {
                            workingSetWeightText = it
                            workingSetWeight = if (it.text.isNotBlank()) {
                                it.text.toDouble()
                            } else {
                                0.0
                            }
                            changeWorkingSetWeight(
                                if (it.text.isNotBlank()) {
                                    it.text.toDouble()
                                } else {
                                    0.0
                                }
                            )
                        }
                    )
                }
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

                WarmUpSetLazyColumn(
                    warmUpSets,
                    workingSetWeight,
                    { id: Long -> removeSet(id) },
                    { id: Long, newWeight: Double -> updateWarmUpSetWeight(id, newWeight) },
                    { id: Long, newReps: Int -> updateWarmUpSetReps(id, newReps) },
                    { addSet() }
                )

/*                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = { addSet() }) {
                        Text("Add Set")
                    }
                }*/
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    openDialog(false)
                    run { insertWarmUpSets(warmUpSets) }
//                    TODO Add warm up sets to the beginning of current exercise sets
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
