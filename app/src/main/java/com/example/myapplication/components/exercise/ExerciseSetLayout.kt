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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import com.example.myapplication.data.SetModel
import com.example.myapplication.data.SetTypeModel
import com.example.myapplication.ui.theme.PurpleGrey80
import kotlin.math.roundToInt

// TODO Create Set Data Type
// Put in a LazyColumn and had ability to rearrange sets
// TODO exerciseSetLayout() takes in Set Data to populate row
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun exerciseSetLayout(
    setIndex: Int,
    exerciseSet: SetModel,
    isSetCompleted: (Boolean) -> Unit,
    newWeight: (newWeight: Double?) -> Unit,
    newReps: (newReps: Int?) -> Unit,
    newRIR: (newRIR: Double?) -> Unit,
    newRPE: (newRPE: Double?) -> Unit,
    updateSetType: (setType: SetTypeModel) -> Unit
) {
    var expandedSetTypeOptionsDropdownMenu by remember { mutableStateOf(false) }
    var exerciseWeightText by remember {
        mutableStateOf(
            TextFieldValue(
                if (exerciseSet.exerciseWeight == null) {
                    ""
                } else {
                    if (exerciseSet.exerciseWeight!!.minus(exerciseSet.exerciseWeight!!.roundToInt()) == 0.0) {
                        exerciseSet.exerciseWeight?.roundToInt().toString()
                    } else {
                        exerciseSet.exerciseWeight.toString()
                    }
                }
            )
        )
    }

    var repetitionsText by remember {
        mutableStateOf(
            TextFieldValue(
                if (exerciseSet.repetitions == null) {
                    ""
                } else {
                    exerciseSet.repetitions.toString()
                }
            )
        )
    }
    var repetitionsInReserveText by remember {
        mutableStateOf(
            TextFieldValue(
                if (exerciseSet.repetitionsInReserve == null) {
                    ""
                } else {
                    exerciseSet.repetitionsInReserve.toString()
                }
            )
        )
    }
    var ratePerceivedEffortText by remember {
        mutableStateOf(
            TextFieldValue(
                if (exerciseSet.ratePerceivedEffort == null) {
                    ""
                } else {
                    exerciseSet.ratePerceivedEffort.toString()
                }
            )
        )
    }

    var iconText by remember { mutableStateOf("") }

    iconText = if (exerciseSet.setType.name == "Normal") {
        setIndex.inc().toString()
    } else {
        exerciseSet.setType.name[0].toString()
    }

    fun changeIconText() {
        iconText = if (exerciseSet.setType.name == "Normal") {
            setIndex.inc().toString()
        } else {
            exerciseSet.setType.name[0].toString()
        }
    }

    var iconColor: Color = exerciseSet.setType.textColor

    fun changeIconColor() {
        iconColor = exerciseSet.setType.textColor
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
                modifier = Modifier.padding(horizontal = 16.dp).size(32.dp),
                shape = RoundedCornerShape(size = 8.dp),
                contentPadding = PaddingValues(0.dp),
                onClick = {
                    expandedSetTypeOptionsDropdownMenu = true
                }
            ) {
                Text(text = iconText, color = iconColor)
            }

            SetTypeOptionsMenu(
                // selectedSetType = exerciseSet.setType, TODO Not if this is needed
                expandMenu = { expandedSetTypeOptionsDropdownMenu },
                onDismiss = { expandedSetTypeOptionsDropdownMenu = false },
                updateSetType = { setType -> updateSetType(setType) },
                changeIconText = { changeIconText() },
                changeIconColor = { changeIconColor() }
            )
        }

        // TODO Get Previous Set Data by Exercise, Workout, Block, Plan
        // TODO If Previous RIR is null, text = "110 x 10" or "110 x 10 @ 1RM%"
        Text(
            "110 x 10 @ RIR 0",
            color = Color.Gray
        )

        Box(
            modifier = Modifier
                .padding(4.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(PurpleGrey80)
                .align(Alignment.CenterVertically)
                .width(72.dp)
                .wrapContentSize(Alignment.Center),

            propagateMinConstraints = true,
            contentAlignment = Alignment.Center

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
                    exerciseWeightText = it
                    newWeight(
                        if (it.text.isNotBlank()) {
                            it.text.toDouble()
                        } else {
                            null
                        }
                    )
//                    exerciseSet.exerciseWeight =
//                        if (it.text.isNotBlank()) {
//                            it.text.toDouble()
//                        } else {
//                            null
//                        }
                }
            )
        }

        Box(
            modifier = Modifier
                .padding(4.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(PurpleGrey80)
                .align(Alignment.CenterVertically)
                .width(60.dp)
                .wrapContentSize(Alignment.Center),

            propagateMinConstraints = true,
            contentAlignment = Alignment.Center

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
                    repetitionsText = it
                    exerciseSet.repetitions =
                        if (it.text.isNotBlank()) it.text.toInt() else null
                    newReps(if (it.text.isNotBlank()) it.text.toInt() else null)
                }
            )
        }

        Box(
            modifier = Modifier
                .padding(4.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(PurpleGrey80)
                .align(Alignment.CenterVertically)
                .width(40.dp)
                .wrapContentSize(Alignment.Center),

            propagateMinConstraints = true,
            contentAlignment = Alignment.Center

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
                    repetitionsInReserveText = it
                    exerciseSet.repetitionsInReserve =
                        if (it.text.isNotBlank()) 0.0.plus(it.text.toDouble()) else null
                    newRIR(if (it.text.isNotBlank()) 0.0.plus(it.text.toDouble()) else null)
                }
            )
        }

        // Set Completed
        val checkedState = remember { mutableStateOf(false) }
        Checkbox(
            checked = checkedState.value,
            onCheckedChange = {
                checkedState.value = it
                isSetCompleted(it)
            }
        )
    }
}
