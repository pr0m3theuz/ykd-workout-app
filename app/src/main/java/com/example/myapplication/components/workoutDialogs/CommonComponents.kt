package com.example.myapplication.components.workoutDialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import java.math.RoundingMode
import java.text.DecimalFormat
import kotlin.math.roundToInt

fun getFormattedPercentageText(percentage: Double?): String {
    val df = DecimalFormat("#.##")
    df.roundingMode = RoundingMode.HALF_UP

    val reformattedPercentage = df.format(percentage?.times(100.0)).toDouble()

    return if (reformattedPercentage.minus(reformattedPercentage.roundToInt()) == 0.0) {
        reformattedPercentage.roundToInt().toString().plus("%")
    } else {
        reformattedPercentage.toString().plus("%")
    }
}

fun calculatePercentage(percentage: String): Pair<String, Double> {
    val formattedPercentage = percentage.replace("%", "").take(6)
    val percentageText = formattedPercentage.takeIf { formattedPercentage.isNotBlank() } ?: ""
    val workingSetPercentage = formattedPercentage.toDoubleOrNull()?.div(100.0) ?: 0.0
    return Pair(percentageText, workingSetPercentage)
}

/**
 * Calculates and returns a formatted string representing new set results based on the given parameters.
 *
 * @param weight The working weight for the set. Can be null.
 * @param percentage The percentage to apply to the working weight. Can be null.
 * @param repetitionsText The text representing the repetitions for the set.
 * @return A formatted string representing the new set results.
 *
 * from ChatGPT
 */
fun calculateNewSetResults(
    weight: Double?,
    percentage: Double?,
    repetitionsText: String,
): String {
    // Constants for formatting the string
    val workingWeightXRepetitions = "%s x %s"

    // Calculate new working weight and round to nearest integer
    val newWorkingWeight = weight?.times(percentage!!)
    val roundedNewWorkingWeight = weight?.roundToInt()

    // Format new working weight as double with 2 decimal places and round to nearest integer
    val formattedNewWorkingWeight = "%.2f".format(newWorkingWeight).toDouble()
    val roundedFormattedNewWorkingWeight = formattedNewWorkingWeight.roundToInt()

    // Choose appropriate values for formatting the string based on calculated results
    return when {
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
}

@Composable
fun RoundedBox(
    modifier: Modifier,
    content: @Composable () -> Unit,
) {
    Box(
        modifier = modifier,
        propagateMinConstraints = true,
        contentAlignment = Alignment.Center

    ) {
        content()
    }
}

@Composable
fun Textfield(
    text: String,
    onValueChange: (String) -> Unit,
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
        value = text,
        onValueChange = {
            onValueChange(it)
        }
    )
}

@Composable
fun SetTypeButton(
    setTypeText: String,
    setTypeColor: Color,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .wrapContentSize(Alignment.Center),
        propagateMinConstraints = true
    ) {
        Button(
            modifier = Modifier
                .padding(8.dp)
                .size(32.dp),
            shape = RoundedCornerShape(size = 8.dp),
            contentPadding = PaddingValues(0.dp),
            onClick = onClick
        ) {
            Text(
                text = setTypeText,
                color = setTypeColor
            )
        }
    }
}

@Composable
fun SetContent(
    setTypeText: String,
    setTypeColor: Color,
    resultsText: String,
    percentageText: String,
    repetitionsText: String,
    onPercentageTextChanged: (String) -> Unit,
    onRepetitionsTextChanged: (String) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        val textfieldModifier = Modifier
            .padding(4.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.secondaryContainer)
            .align(Alignment.CenterVertically)
            .wrapContentSize(Alignment.Center)

        val resultsModifier = Modifier
            .padding(4.dp)
            .align(Alignment.CenterVertically)
            .width(96.dp)
            .wrapContentSize(Alignment.Center)

        SetTypeButton(setTypeText, setTypeColor) {}

        RoundedBox(
            textfieldModifier.width(72.dp)
        ) {
            Textfield(
                text = percentageText,
                onValueChange = onPercentageTextChanged
            )
        }

        RoundedBox(
            textfieldModifier.width(60.dp)
        ) {
            Textfield(
                text = repetitionsText,
                onValueChange = onRepetitionsTextChanged
            )
        }

        Box(
            modifier = resultsModifier,
            contentAlignment = Alignment.Center
        ) {
            Text(
                fontSize = 4.em,
                text = resultsText
            )
        }
    }
}

@Composable
fun SetsHeaderRow() {
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

        // Blank Space
        Box(
            modifier = Modifier
                .padding(PaddingValues(horizontal = 4.dp))
                .width(60.dp),
            contentAlignment = Alignment.Center
        ) { /* TODO Put appropriate text here */ }
    }
}

@Composable
fun DialogInput(
    text: String,
    onValueChange: (String) -> Unit,
    onImeAction: () -> Unit = {},
) {
    Box(
        modifier = Modifier
//                        .padding(4.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.secondaryContainer)
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
                color = MaterialTheme.colorScheme.onSecondaryContainer
            ),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    onImeAction()
                },
            ),
            value = text,
            onValueChange = onValueChange
        )
    }
}

@Composable
fun AddSetText() {
    androidx.compose.material.Text("Add Set", color = MaterialTheme.colorScheme.primary)
}
