package com.example.myapplication.components.workoutDialogs.workingSets

import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.components.workoutDialogs.SetContent
import com.example.myapplication.components.workoutDialogs.calculateNewSetResults
import com.example.myapplication.components.workoutDialogs.calculatePercentage
import com.example.myapplication.components.workoutDialogs.getFormattedPercentageText
import com.example.myapplication.data.SetModel
import com.example.myapplication.utils.checkIntInput

@Composable
fun WorkingSetLayout(
    workingSetViewModel: WorkingSetViewModel = viewModel(),
    setIndex: Int,
    workingSet: SetModel,
) {
    val workingWeight by workingSetViewModel.workingSetWeight.collectAsState()

    var workingSetPercentage: Double? by remember { mutableStateOf(workingSet.percentage1RM) }

    var percentageText by remember { mutableStateOf(getFormattedPercentageText(workingSetPercentage)) }

    var repetitionsText by remember { mutableStateOf(workingSet.repetitions?.toString() ?: "") }

    workingSetViewModel.updateSetWeight(
        workingSet.uuid,
        workingWeight.times(workingSetPercentage!!)
    )

    fun setPercentageText(percentage: String) {
        val (text, percent) = calculatePercentage(percentage)
        percentageText = text
        workingSetPercentage = percent
        workingSetViewModel.updateSetPercentage(workingSet.uuid, workingSetPercentage!!)
    }

    fun setRepetitionsText(repetitions: String) {
        repetitionsText = checkIntInput(repetitions.take(4))
        workingSetViewModel.updateSetReps(workingSet.uuid, repetitionsText.toIntOrNull() ?: 0)
    }

    SetContent(
        setTypeText = setIndex.inc().toString(),
        setTypeColor = workingSet.setType.textColor,
        resultsText = calculateNewSetResults(workingWeight, workingSetPercentage, repetitionsText),
        percentageText = percentageText,
        repetitionsText = repetitionsText,
        onPercentageTextChanged = { percentage -> setPercentageText(percentage) },
        onRepetitionsTextChanged = { repetitions -> setRepetitionsText(repetitions) },
    )
}
