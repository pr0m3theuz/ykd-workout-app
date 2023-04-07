package com.example.myapplication.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
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
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.WorkoutViewModel
import com.example.myapplication.components.workingSets.WorkingSetLazyColumn
import com.example.myapplication.components.workingSets.WorkingSetViewModel
import com.example.myapplication.data.ExerciseSetModel
import com.example.myapplication.data.SetModel
import com.example.myapplication.ui.theme.PurpleGrey80
import kotlin.math.roundToInt

// @Preview

@Composable
fun AddWorkingSetsScreen(
    workingSetViewModel: WorkingSetViewModel = viewModel(),
    workoutViewModel: WorkoutViewModel = viewModel(),
    openDialog: (Boolean) -> Unit,
    exerciseSets: ExerciseSetModel,
) {
    AddWorkingSetsDialog(
        openDialog = { close -> openDialog(close) },
        _oneRepMax = workoutViewModel.getWorkingSetWeight(exerciseSets), /* TODO Get User's One Rep Max or Estimated One Rep Max*/
        changeWorkingSetWeight = { newWorkingWeight ->
            workingSetViewModel.changeWorkingSetWeight(
                newWorkingWeight
            )
        },
        sets = workingSetViewModel.sets,
        addSet = { workingSetViewModel.addSet() },
        removeSet = { id: Long -> workingSetViewModel.remove(id) },
        updateSetWeight = { id: Long, newWeight: Double ->
            workingSetViewModel.updateSetWeight(
                id,
                newWeight
            )
        },
        updateSetReps = { id: Long, newReps: Int ->
            workingSetViewModel.updateSetReps(
                id,
                newReps
            )
        },
        updateSetPercentage = { id: Long, newPercentage: Double ->
            workingSetViewModel.updateSetPercentage(
                id,
                newPercentage
            )
        },
        insertSets = { sets -> workoutViewModel.addWorkingSets(exerciseSets, sets) },
        insertBackOffSets = { sets -> workoutViewModel.addBackOffSets(exerciseSets, sets) }
    )
}

@Composable
fun AddWorkingSetsDialog(
    openDialog: (Boolean) -> Unit,
    _oneRepMax: Double?,
    changeWorkingSetWeight: (Double) -> Unit,
    sets: List<SetModel>,
    addSet: () -> Unit,
    removeSet: (id: Long) -> Unit,
    updateSetWeight: (id: Long, newWeight: Double) -> Unit,
    updateSetReps: (id: Long, newReps: Int) -> Unit,
    updateSetPercentage: (id: Long, newPercentage: Double) -> Unit,
    insertSets: (workingSets: List<SetModel>) -> Unit,
    insertBackOffSets: (backOffSets: List<SetModel>) -> Unit,
) {
    var workingSetWeight: Double by remember {
        if (_oneRepMax != null) {
            mutableStateOf(_oneRepMax)
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
            Text(text = "Add working sets")
        },
        text = {
            Column {
                Text(text = "1RM or Estimated 1RM (***USER DEFAULT WEIGHT UNIT***)") // TODO USE USER SELECTED WEIGHT
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

/*                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = { addSet() }) {
                        Text("Add Set")
                    }
                }*/

                WorkingSetLazyColumn(
                    sets,
                    workingSetWeight,
                    { id: Long -> removeSet(id) },
                    { id: Long, newWeight: Double -> updateSetWeight(id, newWeight) },
                    { id: Long, newReps: Int -> updateSetReps(id, newReps) },
                    { id: Long, newPercentage: Double -> updateSetPercentage(id, newPercentage) },
                    { addSet() }
                )

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
