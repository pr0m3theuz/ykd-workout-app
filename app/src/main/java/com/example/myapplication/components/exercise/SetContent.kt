package com.example.myapplication.components

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
import com.example.myapplication.components.exercise.ExerciseWeightState
import com.example.myapplication.components.exercise.ExerciseWeightStateSaver
import com.example.myapplication.components.exercise.TextFieldState
import com.example.myapplication.components.setRestTimer.TimerScreen
import com.example.myapplication.data.ExerciseSetModel
import com.example.myapplication.data.SetModel
import java.text.NumberFormat
import java.util.regex.Pattern
import kotlin.math.roundToInt

// TODO Add ability to rearrange sets
// TODO Behaviour when deleting for Weight String is weird, Fix
// TODO RIR doesn't get updated in viewModel, Fix
@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun SetContent(
    exerciseSets: ExerciseSetModel,
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

    val textFieldColor = MaterialTheme.colorScheme.secondaryContainer

    var expandedSetTypeOptionsDropdownMenu by remember { mutableStateOf(false) }

    val exerciseWeight = getExerciseWeight(set)

    val weightState by rememberSaveable(stateSaver = ExerciseWeightStateSaver) {
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
//        repetitionsText = checkIntInput(reps).toString()
        workoutViewModel.updateSetReps(
            exerciseSets = exerciseSets,
            setUUID = setUUID,
            newReps = checkIntInput(reps)
        )
    }

    fun updateRIR(rir: String) {
        // TODO limit range to 0..10
//        ?repetitionsInReserveText = parseTextFieldInputDouble(checkDoubleInput(rir).toString())
        workoutViewModel.updateSetRIR(
            exerciseSets = exerciseSets,
            setUUID = setUUID,
            newRIR = repetitionsInReserveText.toDoubleOrNull(),
        )
    }

    fun updateWeight(weight: String) {
//        weightState.text = parse-TextFieldInputDouble(checkDoubleInput(weight).toString())
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

    var iconColor: Color = set.setType.textColor

    fun changeIconColor() {
        iconColor = set.setType.textColor
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
        Box(
            modifier = Modifier
                .wrapContentSize(Alignment.Center),
            propagateMinConstraints = true
        ) {
            Button(
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .size(32.dp),
                shape = RoundedCornerShape(size = 8.dp),
                contentPadding = PaddingValues(0.dp),
                onClick = {
                    expandedSetTypeOptionsDropdownMenu = true
                }
            ) {
                Text(text = iconText, color = MaterialTheme.colorScheme.onPrimary)
            }

            SetTypeOptionsMenu(
                // selectedSetType = exerciseSet.setType, TODO Not if this is needed
                expandMenu = { expandedSetTypeOptionsDropdownMenu },
                onDismiss = { expandedSetTypeOptionsDropdownMenu = false },
                updateSetType = { setType ->
                    workoutViewModel.updateSetType(
                        exerciseSets = exerciseSets,
                        setUUID = setUUID,
                        setType = setType
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
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Box(
            modifier = Modifier
                .padding(4.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(textFieldColor)
                .align(Alignment.CenterVertically)
                .width(72.dp)
                .wrapContentSize(Alignment.Center),
            propagateMinConstraints = true,
            contentAlignment = Alignment.Center

        ) {
            ExerciseWeightTextField(
                weightState,
                modifier = Modifier.focusRequester(focusRequester),
                onWeightChange = { weightState.text = checkDoubleInput(it) ?: weightState.text },
                onImeAction = {
                    updateSetData()
                    repsFocusRequester.requestFocus()
                }
            )
        }

        Box(
            modifier = Modifier
                .padding(4.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(textFieldColor)
                .align(Alignment.CenterVertically)
                .width(60.dp)
                .wrapContentSize(Alignment.Center),
            propagateMinConstraints = true,
            contentAlignment = Alignment.Center

        ) {
            ExerciseRepsTextField(
                repetitionsText = repetitionsText,
                modifier = Modifier.focusRequester(repsFocusRequester),
                onRepsChange = { reps -> repetitionsText = checkIntInput(reps).toString() },
                onImeAction = {
                    updateSetData()
                    rirFocusRequester.requestFocus()
                }
            )
        }

        Box(
            modifier = Modifier
                .padding(4.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(textFieldColor)
                .align(Alignment.CenterVertically)
                .width(40.dp)
                .wrapContentSize(Alignment.Center),
            propagateMinConstraints = true,
            contentAlignment = Alignment.Center

        ) {
            RirTextField(
                repetitionsInReserveText,
                modifier = Modifier.focusRequester(rirFocusRequester),
                onRIRChange = { rir ->
//                    val rirDouble = checkDoubleInput(rir)
                    repetitionsInReserveText =
                        checkDoubleInput(rir)?.takeIf { it.toDouble() in 0.0..10.0 }?.toString()
                            ?: repetitionsInReserveText
                    /*if (rirDouble != null && rirDouble.toDouble() in 0.0..10.0) {
                        checkDoubleInput(rir) ?: repetitionsInReserveText
                    } else {
                        repetitionsInReserveText
                    }*/
                },
                onImeAction = { updateSetData() }
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

// TODO Fix
@Composable
private fun RirTextField(
    repetitionsInReserveText: String,
    modifier: Modifier = Modifier,
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
        modifier = modifier
            .padding(PaddingValues(4.dp)),
        textStyle = TextStyle(
            fontSize = 5.em,
            textAlign = TextAlign.Center,
            color = textColor
        ),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Decimal,
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(
            onDone = {
                onImeAction()
            },
        ),
        value = repetitionsInReserveText,
        onValueChange = {
            onRIRChange(it.take(4))
        }
    )
}

@Composable
private fun ExerciseRepsTextField(
    repetitionsText: String,
    modifier: Modifier = Modifier,
    onRepsChange: (newReps: String) -> Unit,
    onImeAction: () -> Unit = {},
) {
    BasicTextField(
        modifier = modifier
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
        keyboardActions = KeyboardActions(
            onDone = {
                onImeAction()
            },
        ),
        value = repetitionsText,
        onValueChange = {
            onRepsChange(it.take(4))
        },
    )
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun ExerciseWeightTextField(
    weightState: TextFieldState = remember { ExerciseWeightState() },
    modifier: Modifier = Modifier,
    onWeightChange: (newWeight: String) -> Unit,
    onImeAction: () -> Unit = {},
) {
    BasicTextField(
        value = weightState.text,
        onValueChange = {
            onWeightChange(it.take(7))
        },
        modifier = modifier
            .padding(PaddingValues(4.dp))
            .onFocusChanged {
                weightState.onFocusChange(it.isFocused)
            },
        textStyle = TextStyle(
            fontSize = 5.em,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onBackground
        ),
//                placeholder = { Text(exerciseSet.exerciseWeight.toString())},
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number,
            imeAction = ImeAction.Next
        ),
        keyboardActions = KeyboardActions(
            onNext = {
                onImeAction()
            },
        ),
    )
}

private const val DOUBLE_VALIDATION_REGEX = "^(-?\\d*)(\\.?)(\\d{0,2})(\\d*)\$"

private fun isDoubleValid(double: String): Boolean {
    return Pattern.matches(DOUBLE_VALIDATION_REGEX, double)
}

/*private*/ fun oldCheckDoubleInput(it: String): String? {
    var double: String?

    var manipulatedInput = it.replace(",", "")

    manipulatedInput = if (manipulatedInput.count { it == '.' } <= 1) {
        val doublePattern = Pattern.compile(DOUBLE_VALIDATION_REGEX)
        val matcher = doublePattern.matcher(manipulatedInput)
        matcher.matches()
        "${matcher.group(1)}${matcher.group(2)}${matcher.group(3)}"
    } else {
        ""
    }

    if (isDoubleValid(manipulatedInput) && manipulatedInput.isNotBlank()) {
        if (manipulatedInput == ".") {
            double = "0." // Add leading zero
        } else if (manipulatedInput.last() == '.') {
            double = manipulatedInput // Prevent early removal of the decimal place
        } else {
            val paddedInput: String =
                NumberFormat.getInstance().parse(manipulatedInput)?.toDouble()
                    .toString() // = it.padStart(16, '0')

            double = if (paddedInput.toDouble() != 0.0) {
                NumberFormat.getInstance().parse(paddedInput)?.toString()
            } else {
                "0"
            }
        }
    } else {
        double = null
    }

    return double
}

private val DOUBLE_PATTERN = Pattern.compile("^(-?\\d*)(\\.?)(\\d{0,2})(\\d*)\$")

fun checkDoubleInput(it: String): String? {
    var manipulatedInput = it.replace(",", "")
    val matcher = DOUBLE_PATTERN.matcher(manipulatedInput)
    manipulatedInput = if (manipulatedInput.count { it == '.' } <= 1 && matcher.matches()) {
        "${matcher.group(1)}${matcher.group(2)}${matcher.group(3)}"
    } else {
        return null
    }

    return if (manipulatedInput.isNotBlank()) {
        when {
            manipulatedInput == "." -> "0." // Add leading zero
            manipulatedInput.last() == '.' -> manipulatedInput // Prevent early removal of the decimal place
            else -> {
                val paddedInput = NumberFormat.getInstance().parse(manipulatedInput)?.toString()
                val parsedDouble = paddedInput?.toDoubleOrNull() ?: return null
                if (parsedDouble == 0.0) "0" else paddedInput
            }
        }
    } else {
        null
    }
}

/**
 * Produced by OpenAI's ChatGPT
 * Prompt "can you optimize the below code written in kotlin?" in referring to the function above.
 * Extracts a valid double-precision floating-point number from the given input string,
 * and returns the result as a string.
 *
 * @param input The input string to process. Must not be null.
 *
 * @return The double-precision floating-point number represented by the input string,
 *         or null if the input string is not a valid representation of a number.
 */
fun chatgptCheckDoubleInput(input: String): String? {
    val inputWithoutCommas = input.replace(",", "")
    val matcher = DOUBLE_PATTERN.matcher(inputWithoutCommas)
    val formattedInput = if (matcher.matches()) {
        "${matcher.group(1)}${matcher.group(2)}${matcher.group(3)}"
    } else {
        return null
    }

    return when {
        formattedInput == "." -> "0."
        formattedInput.last() == '.' -> formattedInput
        else -> {
            val paddedInput = NumberFormat.getInstance().parse(formattedInput)?.toString()
            val parsedDouble = paddedInput?.toDoubleOrNull() ?: return null
            if (parsedDouble == 0.0) "0" else paddedInput
        }
    }
}

/*private */fun checkIntInput(it: String): Int? {
    return if (it.isNotBlank()) {
        val paddedInput = it.padStart(16, '0')
        NumberFormat.getInstance().parse(paddedInput)?.toInt()
    } else {
        null
    }
}

private fun getSetIconText(set: SetModel, setIndex: Int): String =
    if (set.setType.name == "Normal") {
        setIndex.inc().toString()
    } else {
        set.setType.name[0].toString()
    }

private fun getRatePerceivedEffort(set: SetModel): String =
    doubleFormatted(set.ratePerceivedEffort) ?: ""

private fun getRepetitionsInReserve(set: SetModel): String =
    doubleFormatted(set.repetitionsInReserve) ?: ""

private fun getRepetitions(set: SetModel): String =
    set.repetitions.toString() ?: ""

private fun getExerciseWeight(set: SetModel): String =
    doubleFormatted(set.exerciseWeight) ?: ""

/*private*/ fun doubleFormatted(double: Double?) =
    if (double?.minus(double.roundToInt()) == 0.0) {
        double.roundToInt().toString()
    } else {
        double.toString()
    }
