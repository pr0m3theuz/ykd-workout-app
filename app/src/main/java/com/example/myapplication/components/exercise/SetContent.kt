package com.example.myapplication.components.exercise

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.FocusState.*
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.WorkoutViewModel
import com.example.myapplication.components.SetTypeOptionsMenu
import com.example.myapplication.components.setRestTimer.TimerScreen
import com.example.myapplication.data.ExerciseSets
import com.example.myapplication.data.SetModel
import com.example.myapplication.utils.checkDoubleInput
import com.example.myapplication.utils.checkIntInput
import com.example.myapplication.utils.doubleFormatted

// TODO Add ability to rearrange sets
// TODO Add placeholder text for target weight, reps, & rpe/rir
// TODO FocusRequester.requestFocus() to tab to next field doesn't work
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SetContent(
    exerciseSets: ExerciseSets,
    setIndex: Int,
    set: SetModel,
    //    isSetCompleted: (Boolean) -> Unit,
    //    newWeight: (newWeight: Double?) -> Unit,
    //    newReps: (newReps: Int?) -> Unit,
    //    newRIR: (newRIR: Double?) -> Unit,
    //    newRPE: (newRPE: Double?) -> Unit,
    //    updateSetType: (setType: SetTypeModel) -> Unit,
    workoutViewModel: WorkoutViewModel = viewModel(),
) {
    val setUUID = set.uuid

    //    val sets = workoutViewModel.allSets.collectAsState()
    //
    //    val set = sets.value.first { it.uuid == setUUID }

    val showKeyboard = remember { mutableStateOf(true) }
    val focusRequester = remember { FocusRequester() }
    val keyboard = LocalSoftwareKeyboardController.current

    val repsFocusRequester = remember { FocusRequester() }
    val rirFocusRequester = remember { FocusRequester() }

    val textFieldBackgroundColor = MaterialTheme.colorScheme.secondaryContainer
    val textColor = MaterialTheme.colorScheme.onSecondaryContainer

    var expandedSetTypeOptionsDropdownMenu by remember { mutableStateOf(false) }

    val exerciseWeight = getExerciseWeight(set)

    val weightState by
    rememberSaveable(stateSaver = ExerciseWeightStateSaver) {
        mutableStateOf(ExerciseWeightState(exerciseWeight))
    }

    var weightFocusState by remember { mutableStateOf(weightState.isFocusedDirty) }

    weightState.isFocusedDirty = weightFocusState

    var exerciseWeightText by remember { mutableStateOf(exerciseWeight) }

    var exerciseWeightFocused by remember { mutableStateOf(false) }

    val repetitions = getRepetitions(set)

    var repetitionsText by remember { mutableStateOf(repetitions) }

    val repetitionsInReserve = getRepetitionsInReserve(set)

    var repetitionsInReserveText by remember { mutableStateOf(repetitionsInReserve) }

    val ratePerceivedEffort = getRatePerceivedEffort(set)

    var ratePerceivedEffortText by remember { mutableStateOf(ratePerceivedEffort) }

    var iconText by remember { mutableStateOf("") }

    fun updateReps(reps: String) {
        workoutViewModel.updateSetReps(
            exerciseSets = exerciseSets,
            setUUID = setUUID,
            newReps = repetitionsText.toIntOrNull()
        )
    }

    fun updateRIR(rir: String) {
        workoutViewModel.updateSetRIR(
            exerciseSets = exerciseSets,
            setUUID = setUUID,
            newRIR = rir.toDoubleOrNull(),
        )
    }

    fun updateWeight(weight: String) {
        workoutViewModel.updateSetWeight(
            exerciseSets = exerciseSets,
            setUUID = setUUID,
            newWeight = weightState.text.toDoubleOrNull()
        )
        weightFocusState = true
        weightState.isFocusedDirty = weightFocusState
    }

    fun updateSetData() {
        updateWeight(exerciseWeightText)
        updateReps(repetitionsText)
        updateRIR(repetitionsInReserveText)
    }

    iconText = getSetIconText(set, setIndex)

    fun changeIconText() {
        iconText = getSetIconText(set, setIndex)
    }

    fun getSetTypeColor(setTypeID: Int) {}

    var iconColor: Color = Color.White

    fun changeIconColor() {
        iconColor = Color.White
    }

    // Set Completed
    val checkedState = remember { mutableStateOf(set.setCompleted) }

    var openTimerScreen by remember { mutableStateOf(false) }

    fun openTimerScreen(open: Boolean) {
        openTimerScreen = open
    }

    if (openTimerScreen && workoutViewModel.isTimerRunning) {
        TimerScreen(openDialog = { open -> openTimerScreen(open) })
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier.wrapContentSize(Alignment.Center), propagateMinConstraints = true) {
            Button(
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .size(32.dp),
                shape = RoundedCornerShape(size = 8.dp),
                contentPadding = PaddingValues(0.dp),
                onClick = { expandedSetTypeOptionsDropdownMenu = true }
            ) {
                Text(text = iconText, color = MaterialTheme.colorScheme.onPrimary)
            }

            SetTypeOptionsMenu(
                // selectedSetType = exerciseSet.setType, TODO Not if this is needed
                expandMenu = { expandedSetTypeOptionsDropdownMenu },
                onDismiss = { expandedSetTypeOptionsDropdownMenu = false },
                updateSetType = { setTypeID ->
                    workoutViewModel.updateSetType(
                        exerciseSets = exerciseSets,
                        setUUID = setUUID,
                        setTypeID = setTypeID
                    )
                },
                changeIconText = { changeIconText() },
                changeIconColor = { changeIconColor() }
            )
        }

        // TODO Get Previous Set Data by Exercise, Workout, Block, Plan
        // TODO If Previous RIR is null, text = "110 x 10" or "110 x 10 @ 1RM%"
        Text(
            "110 x 10 @ RIR 0",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodySmall
        )

        Box(
            modifier =
            Modifier
                .padding(4.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(textFieldBackgroundColor)
                .align(Alignment.CenterVertically)
                .width(72.dp)
                .wrapContentSize(Alignment.Center),
            propagateMinConstraints = true,
            contentAlignment = Alignment.Center
        ) {
            ExerciseWeightTextField(
                modifier = Modifier.focusRequester(focusRequester),
                weightState = weightState,
                onWeightChange = {
                    weightState.text =
                        when {
                            it.isBlank() -> ""
                            else -> checkDoubleInput(it) ?: weightState.text
                        }
                },
                onImeAction = {
                    updateSetData()
                    repsFocusRequester.requestFocus()
                }
            )
        }

        Box(
            modifier =
            Modifier
                .padding(4.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(textFieldBackgroundColor)
                .align(Alignment.CenterVertically)
                .width(60.dp)
                .wrapContentSize(Alignment.Center),
            propagateMinConstraints = true,
            contentAlignment = Alignment.Center
        ) {
            ExerciseRepsTextField(
                modifier = Modifier.focusRequester(repsFocusRequester),
                repetitionsText = repetitionsText,
                onRepsChange = { reps -> repetitionsText = checkIntInput(reps) },
                onImeAction = {
                    updateSetData()
                    rirFocusRequester.requestFocus()
                }
            )
        }

        Box(
            modifier =
            Modifier
                .padding(4.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(textFieldBackgroundColor)
                .align(Alignment.CenterVertically)
                .width(40.dp)
                .wrapContentSize(Alignment.Center),
            propagateMinConstraints = true,
            contentAlignment = Alignment.Center
        ) {
            RirTextField(
                modifier = Modifier.focusRequester(rirFocusRequester),
                repetitionsInReserveText = repetitionsInReserveText,
                onRIRChange = { rir ->
                    repetitionsInReserveText =
                        when {
                            rir.isBlank() -> ""
                            else -> checkDoubleInput(rir)?.takeIf { it.toDouble() in 0.0..10.0 }
                                ?.toString()
                                ?: repetitionsInReserveText
                        }
                },
                onImeAction = { updateRIR(repetitionsInReserveText) }
            )
        }

        Checkbox(
            checked = checkedState.value,
            onCheckedChange = {
                checkedState.value = it
                workoutViewModel.setComplete(
                    exerciseSets = exerciseSets,
                    setUUID = setUUID,
                    setComplete = it
                )
                workoutViewModel.startSetCompletedTimer(
                    exerciseSet = exerciseSets,
                    setUUID = setUUID
                )
                if (!workoutViewModel.isTimerRunning) {
                    openTimerScreen(it)
                }
            }
        )
    }
}

@Composable
private fun RirTextField(
    modifier: Modifier = Modifier,
    repetitionsInReserveText: String,
    onRIRChange: (newRIR: String) -> Unit,
    onImeAction: () -> Unit = {},
) {
    val uneditedTextColor = MaterialTheme.colorScheme.onSecondaryContainer.copy()
    var textColor by remember { mutableStateOf(uneditedTextColor) }

    var changeTextColor by remember { mutableStateOf(false) }

    fun changeTextColor() {
        changeTextColor = true
    }

    if (changeTextColor) {
        textColor = MaterialTheme.colorScheme.onBackground
    }

    BasicTextField(
        modifier = modifier.padding(PaddingValues(4.dp)),
        textStyle = TextStyle(fontSize = 5.em, textAlign = TextAlign.Center, color = textColor),
        keyboardOptions =
        KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Done),
        keyboardActions =
        KeyboardActions(
            onDone = { onImeAction() },
        ),
        value = repetitionsInReserveText,
        onValueChange = { onRIRChange(it.take(4)) }
    )
}

@Composable
private fun ExerciseRepsTextField(
    modifier: Modifier = Modifier,
    repetitionsText: String,
    onRepsChange: (newReps: String) -> Unit,
    onImeAction: () -> Unit = {},
) {
    BasicTextField(
        modifier = modifier.padding(PaddingValues(4.dp)),
        textStyle =
        TextStyle(
            fontSize = 5.em,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onBackground
        ),
        keyboardOptions =
        KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
        keyboardActions =
        KeyboardActions(
            onDone = { onImeAction() },
        ),
        value = repetitionsText,
        onValueChange = { onRepsChange(it.take(4)) },
    )
}

@Composable
private fun ExerciseWeightTextField(
    modifier: Modifier = Modifier,
    weightState: TextFieldState = remember { ExerciseWeightState() },
    onWeightChange: (newWeight: String) -> Unit,
    onImeAction: () -> Unit = {},
) {
    BasicTextField(
        value = weightState.text,
        onValueChange = { onWeightChange(it.take(7)) },
        modifier =
        modifier
            .padding(PaddingValues(4.dp))
            .onFocusChanged {
                weightState.onFocusChange(it.isFocused)
            },
        textStyle =
        TextStyle(
            fontSize = 5.em,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onBackground
        ),
        //                placeholder = { Text(exerciseSet.exerciseWeight.toString())},
        keyboardOptions =
        KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
        keyboardActions =
        KeyboardActions(
            onNext = { onImeAction() },
        ),
    )
}

private fun getSetIconText(set: SetModel, setIndex: Int): String =
    if (set.setTypeID == 1) {
        setIndex.inc().toString()
    } else {
        "TODO"
    }

private fun getRatePerceivedEffort(set: SetModel): String = doubleFormatted(set.ratePerceivedEffort)

private fun getRepetitionsInReserve(set: SetModel): String =
    doubleFormatted(set.repetitionsInReserve)

private fun getRepetitions(set: SetModel): String =
    when (set.repetitions) {
        null -> ""
        else -> set.repetitions.toString()
    }

private fun getExerciseWeight(set: SetModel): String = doubleFormatted(set.exerciseWeight)
