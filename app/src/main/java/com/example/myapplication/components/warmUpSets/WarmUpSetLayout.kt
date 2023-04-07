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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.components.equateVolume.NewSetResultsText
import com.example.myapplication.components.warmUpSets.WarmUpSetsViewModel
import com.example.myapplication.data.SetModel
import com.example.myapplication.ui.theme.PurpleGrey80
import com.example.myapplication.utils.checkDoubleInput
import com.example.myapplication.utils.checkIntInput
import kotlin.math.roundToInt

@Composable
fun WarmUpSetLayout(
    warmUpSetsViewModel: WarmUpSetsViewModel = viewModel(),
    setIndex: Int,
    warmUpSet: SetModel,
    newWeight: (newWeight: Double) -> Unit,
    newReps: (newReps: Int) -> Unit,
) {
    val workingWeight by warmUpSetsViewModel.workingSetWeight.collectAsState()

    var warmUpPercentage: Double? by remember { mutableStateOf(1.0) }

    var percentageText by remember {
        mutableStateOf(
            if (warmUpPercentage?.minus(warmUpPercentage!!.roundToInt())?.times(100.0) == 0.0) {
                warmUpPercentage?.roundToInt()?.times(100).toString().plus("%")
            } else {
                warmUpPercentage?.times(100.0).toString().plus("%")
            }
        )
    }

    var repetitionsText by remember { mutableStateOf(warmUpSet.repetitions?.toString() ?: "") }

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
            ) {
                Text(
                    text = setIndex.inc().toString(),
                    color = warmUpSet.setType.textColor
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
                    warmUpPercentage = formattedPercentage.toDoubleOrNull()?.div(100.0) ?: 0.0
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
            NewSetResultsText(workingWeight, warmUpPercentage, repetitionsText)
        }
    }
}
