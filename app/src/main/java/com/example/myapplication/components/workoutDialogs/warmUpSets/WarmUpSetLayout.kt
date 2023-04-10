package com.example.myapplication.components

import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.components.workoutDialogs.SetContent
import com.example.myapplication.components.workoutDialogs.calculateNewSetResults
import com.example.myapplication.components.workoutDialogs.calculatePercentage
import com.example.myapplication.components.workoutDialogs.getFormattedPercentageText
import com.example.myapplication.components.workoutDialogs.warmUpSets.WarmUpSetsViewModel
import com.example.myapplication.data.SetModel
import com.example.myapplication.utils.checkIntInput

@Composable
fun WarmUpSetLayout(
    warmUpSetsViewModel: WarmUpSetsViewModel = viewModel(),
    setIndex: Int,
    warmUpSet: SetModel,
) {
    val workingWeight by warmUpSetsViewModel.workingSetWeight.collectAsState()

    var warmUpPercentage: Double? by remember { mutableStateOf(1.0) }

    var percentageText by remember { mutableStateOf(getFormattedPercentageText(warmUpPercentage)) }

    var repetitionsText by remember { mutableStateOf(warmUpSet.repetitions?.toString() ?: "") }

    warmUpSetsViewModel.updateWarmUpSetWeight(
        warmUpSet.uuid,
        workingWeight.times(warmUpPercentage!!)
    )

    fun setPercentageText(percentage: String) {
        val (text, percent) = calculatePercentage(percentage)
        percentageText = text
        warmUpPercentage = percent
//        newPercentage(warmUpPercentage!!)
    }

    fun setRepetitionsText(repetitions: String) {
        repetitionsText = checkIntInput(repetitions.take(4))
        warmUpSetsViewModel.updateWarmUpSetReps(warmUpSet.uuid, repetitionsText.toIntOrNull() ?: 0)
    }

    SetContent(
        setTypeText = setIndex.inc().toString(),
        setTypeColor = warmUpSet.setType.textColor,
        resultsText = calculateNewSetResults(workingWeight, warmUpPercentage, repetitionsText),
        percentageText = percentageText,
        repetitionsText = repetitionsText,
        onPercentageTextChanged = { percentage -> setPercentageText(percentage) },
        onRepetitionsTextChanged = { repetitions -> setRepetitionsText(repetitions) },
    )
}
