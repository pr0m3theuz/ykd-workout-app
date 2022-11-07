package com.example.myapplication.components

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
import kotlin.math.roundToInt

@Composable
fun WarmUpSetLayout(
    setIndex: Int,
    warmUpSet: SetModel,
    workingWeight: Double,
    newWeight: (newWeight: Double) -> Unit,
    newReps: (newReps: Int) -> Unit
) {
    var warmUpPercentage: Double? by remember { mutableStateOf(1.0) }

    var percentageText by remember {
        mutableStateOf(
            TextFieldValue(
                if (warmUpPercentage?.minus(warmUpPercentage!!.roundToInt())?.times(100.0) == 0.0) {
                    warmUpPercentage?.roundToInt()?.times(100).toString().plus("%")
                } else {
                    warmUpPercentage?.times(100.0).toString().plus("%")
                }
            )
        )
    }

    var repetitionsText by remember {
        mutableStateOf(
            TextFieldValue(
                if (warmUpSet.repetitions == null) {
                    ""
                } else {
                    warmUpSet.repetitions.toString()
                }
            )
        )
    }

    newWeight(workingWeight.times(warmUpPercentage!!))

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
            ) { Text("W") }
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
                    warmUpPercentage =
                        if (it.text.replace("%", "").isNotEmpty()) {
                            0.0.plus(it.text.replace("%", "").toDouble()).div(100.0)
                        } else {
                            0.0
                        }
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
                    warmUpSet.repetitions = if (it.text.isNotBlank()) it.text.toInt() else null
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
                if (workingWeight.times(warmUpPercentage!!).minus(
                        workingWeight.times(warmUpPercentage!!).roundToInt()
                    ) == 0.0
                ) {
//                    String.format("%", workingWeight?.times(warmUpPercentage!!)!!.roundToInt())
                    String.format(
                        "%d x %s",
                        workingWeight.times(warmUpPercentage!!).roundToInt(),
                        repetitionsText.text
                    )
                } else {
                    val formattedworkingWeight =
                        String.format("%.2f", workingWeight.times(warmUpPercentage!!)).toDouble()

                    if (formattedworkingWeight.minus(formattedworkingWeight.roundToInt()) == 0.0) {
                        String.format(
                            "%d x %s",
                            formattedworkingWeight.roundToInt(),
                            repetitionsText.text
                        )
                    } else {
                        String.format("%.2f x %s", formattedworkingWeight, repetitionsText.text)
                    }
                }
            )
        }
    }
}
