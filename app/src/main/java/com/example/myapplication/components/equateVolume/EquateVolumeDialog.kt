package com.example.myapplication.components.exercise

import android.util.Log
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
import com.example.myapplication.components.equateVolume.EquateVolumeLazyColumn
import com.example.myapplication.components.equateVolume.EquateVolumeViewModel
import com.example.myapplication.data.ExerciseSetModel
import com.example.myapplication.data.SetModel
import com.example.myapplication.data.totalWorkingSetVolume
import com.example.myapplication.ui.theme.PurpleGrey80
import kotlin.math.roundToInt

@Composable
fun EquateVolumeScreen(
    openDialog: (Boolean) -> Unit,
    workoutViewModel: WorkoutViewModel = viewModel(),
    exerciseSets: ExerciseSetModel,
    equateVolumeViewModel: EquateVolumeViewModel = viewModel()
) {
    val totalWorkingSetVolume = exerciseSets.totalWorkingSetVolume()
    val workingSetWeight = workoutViewModel.getWorkingSetWeight(exerciseSets)
    val workingSetRepetitions = workoutViewModel.getWorkingSetRepetitions(exerciseSets)
    val sets = exerciseSets.sets

//    Log.i("EquateVolumeScreen() totalWorkingSetVolume", "$totalWorkingSetVolume")
//    Log.i("EquateVolumeScreen() exerciseSets", "$exerciseSets")

    equateVolumeViewModel.changeWorkingSets(sets)
    equateVolumeViewModel.setTotalVolume(totalWorkingSetVolume)
//
//    val sets by equateVolumeViewModel.sets.collectAsState()
//
//    val newTotalWorkingSetVolume by equateVolumeViewModel.newTotalVolume.collectAsState()

    EquateVolumeInterface(
        openDialog = { close -> openDialog(close) },
        _workingSetWeight = workingSetWeight,
        _workingSetRepetitions = workingSetRepetitions,
        addWorkingSets = { newSets -> workoutViewModel.addWorkingSets(exerciseSets, newSets) },
        totalWorkingSetVolume = totalWorkingSetVolume,
        _sets = sets
    )

/*
    EquateVolumeDialog(
        openDialog = { close -> openDialog(close) },
        _workingSetWeight = workoutViewModel.getWorkingSetWeight(exerciseSets),
        _workingSetRepetitions = workoutViewModel.getWorkingSetRepetitions(exerciseSets),
        changeWorkingSetWeight = { newWorkingWeight ->
            equateVolumeViewModel.changeWorkingSetWeight(
                newWorkingWeight
            )
        },
        sets = sets,
        addSet = { equateVolumeViewModel.addSet() },
        removeSet = { id: Long -> equateVolumeViewModel.remove(id) },
        updateSetWeight = { id: Long, newWeight: Double ->
            equateVolumeViewModel.updateSetWeight(
                id,
                newWeight
            )
        },
        updateSetReps = { id: Long, newReps: Int ->
            equateVolumeViewModel.updateSetReps(
                id,
                newReps
            )
        },
        updateSetPercentage = { id: Long, newPercentage: Float ->
            equateVolumeViewModel.updateSetPercentage(
                id,
                newPercentage
            )
        },
        insertSets = { newSets -> workoutViewModel.addWorkingSets(exerciseSets, newSets) },
        totalWorkingSetVolume = totalWorkingSetVolume,
        newTotalWorkingSetVolume = newTotalWorkingSetVolume,
        equateVolumeByWeight = { newWeight: Double? ->
            equateVolumeViewModel.equateVolumeByWeight(
                newWeight
            )
        },
        equateVolumeByRepetitions = { newRepetitions: Int? ->
            equateVolumeViewModel.equateVolumeByRepetitions(
                newRepetitions
            )
        }
    )
*/
}

@Composable
fun EquateVolumeInterface(
    equateVolumeViewModel: EquateVolumeViewModel = viewModel(),
    openDialog: (Boolean) -> Unit,
    _workingSetWeight: Double?,
    _workingSetRepetitions: Int?,
    addWorkingSets: (workingSets: List<SetModel>) -> Unit,
    totalWorkingSetVolume: Double,
    _sets: List<SetModel>
) {
//    Log.i("EquateVolumeInterface() totalWorkingSetVolume", "$totalWorkingSetVolume")
//    Log.i("EquateVolumeInterface() exerciseSets", "$_sets")

//    equateVolumeViewModel.changeWorkingSets(_sets)
//    equateVolumeViewModel.setTotalVolume(totalWorkingSetVolume)

    val sets by equateVolumeViewModel.sets.collectAsState()
    val newTotalWorkingSetVolume by equateVolumeViewModel.newTotalVolume.collectAsState()

    Log.i("EquateVolumeInterface() sets", "$sets")

    EquateVolumeDialog(
        openDialog = { close -> openDialog(close) },
        _workingSetWeight = _workingSetWeight,
        _workingSetRepetitions = _workingSetRepetitions,
        changeWorkingSetWeight = { newWorkingWeight ->
            equateVolumeViewModel.changeWorkingSetWeight(
                newWorkingWeight
            )
        },
        sets = sets,
        addSet = { equateVolumeViewModel.addSet() },
        removeSet = { id: Long -> equateVolumeViewModel.remove(id) },
        updateSetWeight = { id: Long, newWeight: Double ->
            equateVolumeViewModel.updateSetWeight(
                id,
                newWeight
            )
        },
        updateSetReps = { id: Long, newReps: Int ->
            equateVolumeViewModel.updateSetReps(
                id,
                newReps
            )
        },
        updateSetPercentage = { id: Long, newPercentage: Float ->
            equateVolumeViewModel.updateSetPercentage(
                id,
                newPercentage
            )
        },
        insertSets = { newSets -> addWorkingSets(newSets) },
        totalWorkingSetVolume = totalWorkingSetVolume,
        newTotalWorkingSetVolume = newTotalWorkingSetVolume,
        equateVolumeByWeight = { newWeight: Double? ->
            equateVolumeViewModel.equateVolumeByWeight(
                newWeight
            )
        },
        equateVolumeByRepetitions = { newRepetitions: Int? ->
            equateVolumeViewModel.equateVolumeByRepetitions(
                newRepetitions
            )
        }
    )
}

@Composable
fun EquateVolumeDialog(
    openDialog: (Boolean) -> Unit,
    _workingSetWeight: Double?,
    _workingSetRepetitions: Int?,
    changeWorkingSetWeight: (Double) -> Unit,
    sets: List<SetModel>,
    addSet: () -> Unit,
    removeSet: (id: Long) -> Unit,
    updateSetWeight: (id: Long, newWeight: Double) -> Unit,
    updateSetReps: (id: Long, newReps: Int) -> Unit,
    updateSetPercentage: (id: Long, newPercentage: Float) -> Unit,
    insertSets: (workingSets: List<SetModel>) -> Unit,
    totalWorkingSetVolume: Double?,
    newTotalWorkingSetVolume: Double?,
    equateVolumeByWeight: (newWeight: Double?) -> Unit,
    equateVolumeByRepetitions: (newRepetitions: Int?) -> Unit
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

    var workingSetRepetitions: Int by remember {
        if (_workingSetRepetitions != null) {
            mutableStateOf(_workingSetRepetitions)
        } else {
            mutableStateOf(0)
        }
    }

    var workingSetRepetitionsText by remember {
        mutableStateOf(
            TextFieldValue(
                if (workingSetRepetitions == 0) {
                    ""
                } else {
                    workingSetRepetitions.toString()
                }
            )
        )
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

    fun getWorkingSetWeightOrWorkingSetRepetitions(): TextFieldValue {
        var textFieldValue: TextFieldValue = TextFieldValue("")

        if (isByWeightSelected) {
            textFieldValue = workingSetWeightText
        } else if (isByRepetitionsSelected) {
            textFieldValue = workingSetRepetitionsText
        }
        return textFieldValue
    }

    fun setWorkingSetWeightOrWorkingSetRepetitionsText(newValue: TextFieldValue) {
        if (isByWeightSelected) {
            workingSetWeightText = newValue
        } else if (isByRepetitionsSelected) {
            workingSetRepetitionsText = newValue
        }
    }

    fun setWorkingSetWeightOrWorkingSetRepetitionsNumber(newValue: TextFieldValue) {
        if (isByWeightSelected) {
            workingSetWeight = if (newValue.text.isNotBlank()) {
                newValue.text.toDouble()
            } else {
                0.0
            }
        } else if (isByRepetitionsSelected) {
            workingSetRepetitions = if (newValue.text.isNotBlank()) {
                newValue.text.toInt()
            } else {
                0
            }
        }
    }

    fun calculateNewValues() {
        if (isByWeightSelected) {
            equateVolumeByWeight(workingSetWeight)
        } else if (isByRepetitionsSelected) {
            equateVolumeByRepetitions(workingSetRepetitions)
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
                        value = getWorkingSetWeightOrWorkingSetRepetitions(),
                        onValueChange = {
                            setWorkingSetWeightOrWorkingSetRepetitionsText(it)
                            setWorkingSetWeightOrWorkingSetRepetitionsNumber(it)
                            calculateNewValues()
                            /* TODO */
/*                            changeWorkingSetWeight(
                                if (it.text.isNotBlank()) {
                                    it.text.toDouble()
                                } else {
                                    0.0
                                }
                            )*/
                        }
                    )
                }

                Row() {
                    RadioButton(
                        selected = isByWeightSelected,
                        onClick = { selectByWeight() }
                    )
                    Text(text = "By Weight")
                }

                Row() {
                    RadioButton(
                        selected = isByRepetitionsSelected,
                        onClick = { selectByRepetitions() }
                    )
                    Text(text = "By Repetitions")
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
                    ) { Text("Weight") }

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

                EquateVolumeLazyColumn(
                    sets,
//                    workingSetWeight,
                    { id: Long -> removeSet(id) },
                    { id: Long, newWeight: Double -> updateSetWeight(id, newWeight) },
                    { id: Long, newReps: Int -> updateSetReps(id, newReps) },
                    { id: Long, newPercentage: Float -> updateSetPercentage(id, newPercentage) },
                    { addSet() }
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    openDialog(false)
                    insertSets(sets)
                }
            ) {
                Text("Equate Volume")
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
