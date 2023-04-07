package com.example.myapplication.components.workingSets

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import com.example.myapplication.data.SetModel
import com.example.myapplication.ui.theme.PurpleGrey80
import java.math.RoundingMode
import java.text.DecimalFormat
import kotlin.math.roundToInt

@Composable
fun WorkingSetLayout(
    setIndex: Int,
    workingSet: SetModel,
    workingWeight: Double,
    newWeight: (newWeight: Double) -> Unit,
    newReps: (newReps: Int) -> Unit,
    newPercentage: (newPercentage: Double) -> Unit,
) {
    var workingSetPercentage: Double? by remember { mutableStateOf(workingSet.percentage1RM) }

    val df = DecimalFormat("#.##")
    df.roundingMode = RoundingMode.HALF_UP

    val reformattedPercentage = df.format(workingSetPercentage?.times(100.0)).toDouble()

    var percentageText by remember {
        mutableStateOf(
            TextFieldValue(
                if (reformattedPercentage.minus(reformattedPercentage.roundToInt()) == 0.0) {
                    reformattedPercentage.roundToInt().toString().plus("%")
                } else {
                    reformattedPercentage.toString().plus("%")
                }
            )
        )
    }

    var repetitionsText by remember {
        mutableStateOf(
            TextFieldValue(
                if (workingSet.repetitions == null) {
                    ""
                } else {
                    workingSet.repetitions.toString()
                }
            )
        )
    }

    newWeight(workingWeight.times(workingSetPercentage!!))

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
                modifier = Modifier.padding(8.dp).size(32.dp),
                shape = RoundedCornerShape(size = 8.dp),
                contentPadding = PaddingValues(0.dp),
                onClick = { }
            ) {
                Text(
                    text = setIndex.inc().toString(),
                    color = workingSet.setType.textColor
                )
            }
        }

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
                    fontSize = 4.em,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onBackground
                ),
//                placeholder = { Text(warmUpSet.exerciseWeight.toString())},
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next
                ),
                value = percentageText,
                onValueChange = {
                    percentageText = it
                    workingSetPercentage =
                        if (it.text.replace("%", "").isNotEmpty()) {
                            it.text.replace("%", "").toDouble().div(100.0)
                        } else {
                            0.0
                        }
                    newPercentage(workingSetPercentage!!)
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
                    fontSize = 4.em,
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
                    workingSet.repetitions = if (it.text.isNotBlank()) it.text.toInt() else null
                    newReps(if (it.text.isNotBlank()) it.text.toInt() else 0)
                }
            )
        }

        Box(
            modifier = Modifier
                .padding(4.dp)
                .align(Alignment.CenterVertically)
                .width(96.dp)
                .wrapContentSize(Alignment.Center),
            contentAlignment = Alignment.Center

        ) {
            Text(
                fontSize = 4.em,
                text =
                if (workingWeight.times(workingSetPercentage!!).minus(
                        workingWeight.times(workingSetPercentage!!).roundToInt()
                    ) == 0.0
                ) {
//                    String.format("%", workingWeight?.times(warmUpPercentage!!)!!.roundToInt())
                    String.format(
                        "%d x %s",
                        workingWeight.times(workingSetPercentage!!).roundToInt(),
                        repetitionsText.text
                    )
                } else {
                    val formattedWorkingWeight =
                        String.format("%.2f", workingWeight.times(workingSetPercentage!!))
                            .toDouble()

                    if (formattedWorkingWeight.minus(formattedWorkingWeight.roundToInt()) == 0.0) {
                        String.format(
                            "%d x %s",
                            formattedWorkingWeight.roundToInt(),
                            repetitionsText.text
                        )
                    } else {
                        String.format("%.2f x %s", formattedWorkingWeight, repetitionsText.text)
                    }
                }
            )
        }
    }
}
