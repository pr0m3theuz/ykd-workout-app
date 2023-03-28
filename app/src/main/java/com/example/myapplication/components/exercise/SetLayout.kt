package com.example.myapplication.components

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.WorkoutViewModel
import com.example.myapplication.components.setRestTimer.TimerScreen
import com.example.myapplication.data.ExerciseSetModel
import com.example.myapplication.data.SetModel
import java.text.DecimalFormat
import java.text.NumberFormat
import kotlin.math.roundToInt

// Put in a LazyColumn and had ability to rearrange sets
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetLayout(
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
    val setID = set.id

    val textFieldColor = MaterialTheme.colorScheme.secondaryContainer

    var expandedSetTypeOptionsDropdownMenu by remember { mutableStateOf(false) }

    val exerciseWeight = getExerciseWeight(set)

    var exerciseWeightText by remember { mutableStateOf(exerciseWeight) }

    val repetitions = getRepetitions(set)

    var repetitionsText by remember { mutableStateOf(repetitions) }

    val repetitionsInReserve = getRepetitionsInReserve(set)

    var repetitionsInReserveText by remember { mutableStateOf(repetitionsInReserve) }

    val ratePerceivedEffort = getRatePerceivedEffort(set)

    val ratePerceivedEffortText by remember { mutableStateOf(ratePerceivedEffort) }

    var iconText by remember { mutableStateOf("") }

    fun updateReps(reps: String) {
//        repetitionsText = checkIntInput(reps).toString()
        workoutViewModel.updateSetReps(
            exerciseSets = exerciseSets,
            setID = setID,
            newReps = checkIntInput(reps)
        )
    }

    fun updateRPE(rpe: String) {
//        repetitionsInReserveText = checkDoubleInput(rpe).toString()
        workoutViewModel.updateSetRIR(
            exerciseSets = exerciseSets,
            setID = setID,
            newRIR = checkDoubleInput(rpe)
        )
    }

    fun updateWeight(weight: String) {
//        exerciseWeightText = checkDoubleInput(weight).toString()
        workoutViewModel.updateSetWeight(
            exerciseSets = exerciseSets,
            setID = setID,
            newWeight = checkDoubleInput(weight)
        )
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
                        setID = setID,
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
            ExerciseWeightTextField(exerciseWeightText) { weight -> updateWeight(weight) }
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
            ExerciseRepsTextField(repetitionsText) { reps -> updateReps(reps) }
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
            RpeTextField(repetitionsInReserveText) { rir -> updateRPE(rir) }
        }

        Checkbox(
            checked = checkedState.value,
            onCheckedChange = {
                checkedState.value = it
                workoutViewModel.setComplete(
                    exerciseSets = exerciseSets,
                    setID = setID,
                    setComplete = it
                )
                workoutViewModel.startSetCompletedTimer(
                    exerciseSet = exerciseSets,
                    setID = setID
                )
                if (!workoutViewModel.isTimerRunning) {
                    openTimerScreen(it)
                }
            }
        )
    }
}

@Composable
private fun RpeTextField(
    repetitionsInReserveText: String,
    newRIR: (newRIR: String) -> Unit,
) {
    BasicTextField(
        modifier = Modifier
            .padding(PaddingValues(4.dp)),
        textStyle = TextStyle(
            fontSize = 5.em,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onBackground
        ),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Decimal,
            imeAction = ImeAction.Next
        ),
        value = repetitionsInReserveText,
        onValueChange = {
            newRIR(it.take(4))
        }
    )
}

@Composable
private fun ExerciseRepsTextField(
    repetitionsText: String,
    newReps: (newReps: String) -> Unit,
) {
    BasicTextField(
        modifier = Modifier
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
        value = repetitionsText,
        onValueChange = {
            newReps(it.take(7))
        },
    )
}

@Composable
private fun ExerciseWeightTextField(
    exerciseWeightText: String,
    newWeight: (newWeight: String) -> Unit,
) {
    BasicTextField(
        modifier = Modifier
            .padding(PaddingValues(4.dp)),
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
        value = exerciseWeightText,
        onValueChange = {
            newWeight(it.take(7))
        }
    )
}

private fun parseTextFieldInputDouble(it: String): String {
    return if (it.isNotBlank()) {
        val paddedInput = it.padStart(16, '0')
        DecimalFormat("#0.#")
            .parse(NumberFormat.getInstance().parse(paddedInput)?.toString() ?: "0")
            ?.toDouble().toString()
    } else {
        ""
    }
}

private fun parseTextFieldInputInt(it: String): String {
    return if (it.isNotBlank()) {
        val paddedInput = it.padStart(16, '0')

        DecimalFormat("###0")
            .parse(NumberFormat.getIntegerInstance().parse(paddedInput)?.toString() ?: "0")
            ?.toInt().toString()
    } else {
        ""
    }
}

private fun checkDoubleInput(it: String): Double? {
    return if (it.isNotBlank()) {
        val paddedInput = it.padStart(16, '0')
        doubleFormatted(
            DecimalFormat("#0.##")
                .parse(NumberFormat.getInstance().parse(paddedInput)?.toString() ?: "0")
                ?.toDouble()
        ).toDouble()
    } else {
        null
    }
}

private fun checkIntInput(it: String): Int? {
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
    if (set.ratePerceivedEffort != null) {
        set.ratePerceivedEffort.toString()
    } else {
        ""
    }

private fun getRepetitionsInReserve(set: SetModel): String =
    if (set.repetitionsInReserve != null) {
        set.repetitionsInReserve.toString()
    } else {
        ""
    }

private fun getRepetitions(set: SetModel): String =
    if (set.repetitions != null) {
        set.repetitions.toString()
    } else {
        ""
    }

private fun getExerciseWeight(set: SetModel): String =
    if (set.exerciseWeight != null) {
        doubleFormatted(set.exerciseWeight)
    } else {
        ""
    }

private fun doubleFormatted(double: Double?) =
    if (double?.minus(double.roundToInt()) == 0.0) {
        double.roundToInt().toString()
    } else {
        double.toString()
    }

class AmountVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val originalText = text.text.replace(",", "")
        val formattedText = parseTextFieldInputInt(text.text)

        val offsetMapping = object : OffsetMapping {

            override fun originalToTransformed(offset: Int): Int {
                if (originalText.isNotBlank()) {
                    val commas = formattedText.count { it == ',' }
                    return when {
                        offset <= 1 -> offset
                        offset <= 3 -> if (commas >= 1) offset + 1 else offset
                        offset <= 5 -> if (commas == 2) offset + 2 else offset + 1
                        else -> 8
                    }
                }
                return offset
            }

            override fun transformedToOriginal(offset: Int): Int {
                if (originalText.isNotBlank()) {
                    val commas = formattedText.count { it == ',' }
                    return when (offset) {
                        8, 7 -> offset - 2
                        6 -> if (commas == 1) 5 else 4
                        5 -> if (commas == 1) 4 else if (commas == 2) 3 else offset
                        4, 3 -> if (commas >= 1) offset - 1 else offset
                        2 -> if (commas == 2) 1 else offset
                        else -> offset
                    }
                }
                return offset
            }
        }

        return TransformedText(
            text = AnnotatedString(formattedText),
            offsetMapping = offsetMapping
        )
    }
}
