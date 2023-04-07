package com.example.myapplication.components.exercise

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
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
import com.example.myapplication.components.SetsHeaderRow
import com.example.myapplication.components.equateVolume.EquateVolumeLazyColumn
import com.example.myapplication.components.equateVolume.EquateVolumeViewModel
import com.example.myapplication.data.ExerciseSetModel
import com.example.myapplication.data.SetModel
import com.example.myapplication.data.totalWorkingSetVolume
import com.example.myapplication.ui.theme.PurpleGrey80
import com.example.myapplication.utils.checkDoubleInput
import com.example.myapplication.utils.checkIntInput
import com.example.myapplication.utils.doubleFormatted

@Composable
fun EquateVolumeScreen(
    openDialog: (Boolean) -> Unit,
    workoutViewModel: WorkoutViewModel = viewModel(),
    exerciseSets: ExerciseSetModel,
    equateVolumeViewModel: EquateVolumeViewModel = viewModel(),
) {
    val totalWorkingSetVolume = exerciseSets.totalWorkingSetVolume()
    val workingSetWeight = workoutViewModel.getWorkingSetWeight(exerciseSets)
    val workingSetRepetitions = workoutViewModel.getWorkingSetRepetitions(exerciseSets)

    equateVolumeViewModel.changeWorkingSets(exerciseSets.sets)
    equateVolumeViewModel.setTotalVolume(totalWorkingSetVolume)

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
    insertSets: (workingSets: List<SetModel>) -> Unit,
    totalWorkingSetVolume: Double?,
) {
    val sets by equateVolumeViewModel.sets.collectAsState()
    val newTotalWorkingSetVolume by equateVolumeViewModel.newTotalVolume.collectAsState()

    var workingSetWeight by remember { mutableStateOf(_workingSetWeight) }

    var workingSetWeightText by remember { mutableStateOf(doubleFormatted(workingSetWeight)) }

    var workingSetRepetitions: Int by remember { mutableStateOf(_workingSetRepetitions ?: 0) }

    var workingSetRepetitionsText by remember {
        mutableStateOf(workingSetRepetitions.toString())
    }

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
        title = {
            Text(text = "Equate Volume")
        },
        text = {
            Column {
                Row {
                    Text(text = "Equate Volume of Working Sets based on Weight or Repetitions (***USER DEFAULT WEIGHT UNIT***)") // TODO USE USER SELECTED WEIGHT
                }

                Row {
                    Text("Total Volume: $totalWorkingSetVolume")
                }

                Row {
                    Text("New Total Volume: $newTotalWorkingSetVolume")
                }

                Box(
                    modifier = Modifier
//                        .padding(4.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(PurpleGrey80)
                        .fillMaxWidth()
                        .wrapContentSize(Alignment.Center),
                    contentAlignment = Alignment.Center,

                ) {
                    BasicTextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(PaddingValues(4.dp)),
                        textStyle = TextStyle(
                            fontSize = 5.em,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onBackground,
                        ),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Next,
                        ),
                        value = getWorkingSetWeightOrWorkingSetRepetitions(),
                        onValueChange = {
                            setWorkingSetWeightOrWorkingSetRepetitionsText(it)
                            setWorkingSetWeightOrWorkingSetRepetitionsNumber()
                            calculateNewValues()
                        },
                    )
                }

                Row() {
                    RadioButton(
                        selected = isByWeightSelected,
                        onClick = { selectByWeight() },
                    )
                    Text(text = "By Weight")
                }

                Row() {
                    RadioButton(
                        selected = isByRepetitionsSelected,
                        onClick = { selectByRepetitions() },
                    )
                    Text(text = "By Repetitions")
                }

                SetsHeaderRow()

                EquateVolumeLazyColumn(
                    workingSets = sets,
//                    workingSetWeight,
                )
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
                onClick = {
                    openDialog(false)
                },
            ) {
                Text("Cancel")
            }
        },
    )
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
