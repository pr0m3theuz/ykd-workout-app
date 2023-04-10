package com.example.myapplication.components.workoutDialogs.equateVolume

import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.components.workoutDialogs.SetContent
import com.example.myapplication.components.workoutDialogs.calculateNewSetResults
import com.example.myapplication.components.workoutDialogs.calculatePercentage
import com.example.myapplication.components.workoutDialogs.getFormattedPercentageText
import com.example.myapplication.data.SetModel
import com.example.myapplication.utils.checkIntInput

@Composable
fun EquateVolumeLayout(
    equateVolumeViewModel: EquateVolumeViewModel = viewModel(),
    setIndex: Int,
    workingSet: SetModel,
) {
    var workingSetPercentage: Double? by remember { mutableStateOf(workingSet.percentage1RM) }

    var percentageText by remember { mutableStateOf(getFormattedPercentageText(workingSetPercentage)) }

    var repetitionsText by remember { mutableStateOf(workingSet.repetitions?.toString() ?: "") }

    val workingWeight = workingSet.exerciseWeight

    fun newWeight(newWeight: Double) {
        equateVolumeViewModel.updateSetWeight(workingSet.uuid, newWeight)
    }

    fun newReps(newReps: Int) {
        equateVolumeViewModel.updateSetReps(workingSet.uuid, newReps)
    }

    fun newPercentage(newPercentage: Double) {
        equateVolumeViewModel.updateSetPercentage(workingSet.uuid, newPercentage)
    }

    fun setPercentageText(percentage: String) {
        val (text, percent) = calculatePercentage(percentage)
        percentageText = text
        workingSetPercentage = percent
        newPercentage(workingSetPercentage!!)
    }

    fun setRepetitionsText(repetitions: String) {
        repetitionsText = checkIntInput(repetitions.take(4))
        newReps(repetitionsText.toIntOrNull() ?: 0)
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
