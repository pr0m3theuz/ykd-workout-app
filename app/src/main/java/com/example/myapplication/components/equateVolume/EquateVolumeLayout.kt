package com.example.myapplication.components.equateVolume

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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.data.SetModel
import com.example.myapplication.ui.theme.PurpleGrey80
import com.example.myapplication.utils.checkDoubleInput
import com.example.myapplication.utils.checkIntInput
import java.math.RoundingMode
import java.text.DecimalFormat
import kotlin.math.roundToInt

@Composable
fun EquateVolumeLayout(
    equateVolumeViewModel: EquateVolumeViewModel = viewModel(),
    setIndex: Int,
    workingSet: SetModel,
) {
    var workingSetPercentage: Double? by remember { mutableStateOf(workingSet.percentage1RM) }

    val df = DecimalFormat("#.##")
    df.roundingMode = RoundingMode.HALF_UP

    val reformattedPercentage = df.format(workingSetPercentage?.times(100.0)).toDouble()

    var percentageText by remember {
        mutableStateOf(
            if (reformattedPercentage.minus(reformattedPercentage.roundToInt()) == 0.0) {
                reformattedPercentage.roundToInt().toString().plus("%")
            } else {
                reformattedPercentage.toString().plus("%")
            }
        )
    }

    var repetitionsText by remember { mutableStateOf(workingSet.repetitions?.toString() ?: "") }

    val workingWeight = workingSet.exerciseWeight

    fun newWeight(newWeight: Double) {
        equateVolumeViewModel.updateSetWeight(workingSet.id, newWeight)
    }

    fun newReps(newReps: Int) {
        equateVolumeViewModel.updateSetReps(workingSet.id, newReps)
    }

    fun newPercentage(newPercentage: Double) {
        equateVolumeViewModel.updateSetPercentage(workingSet.id, newPercentage)
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
                onValueChange = { percentage ->
                    val formattedPercentage = percentage.replace("%", "").take(6)
                    percentageText = formattedPercentage.takeIf { formattedPercentage.isNotBlank() }
                        ?.let { checkDoubleInput(formattedPercentage) ?: percentageText } ?: ""
                    workingSetPercentage = formattedPercentage.toDoubleOrNull()?.div(100.0) ?: 0.0
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
                    repetitionsText = checkIntInput(it.take(4))
                    newReps(repetitionsText.toIntOrNull() ?: 0)
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
            NewSetResultsText(workingWeight, workingSetPercentage, repetitionsText)
        }
    }
}

@Composable
fun NewSetResultsText(
    workingWeight: Double?,
    percentage: Double?,
    repetitionsText: String,
) {
    val workingWeightXRepetitions = "%d x %s"
    val newWorkingWeight = workingWeight?.times(percentage!!)
    val roundedNewWorkingWeight = workingWeight?.roundToInt()
    val formattedNewWorkingWeight = "%.2f".format(newWorkingWeight).toDouble()
    val roundedFormattedNewWorkingWeight = formattedNewWorkingWeight.roundToInt()

    Text(
        fontSize = 4.em,
        text =
        when {
            newWorkingWeight?.minus(roundedNewWorkingWeight!!) == 0.0 -> {
                workingWeightXRepetitions.format(roundedNewWorkingWeight, repetitionsText)
            }
            formattedNewWorkingWeight.minus(roundedFormattedNewWorkingWeight) == 0.0 -> {
                workingWeightXRepetitions.format(roundedFormattedNewWorkingWeight, repetitionsText)
            }
            else -> {
                workingWeightXRepetitions.format(formattedNewWorkingWeight, repetitionsText)
            }
        }
    )
}
