@file:OptIn(ExperimentalUuidApi::class)

package com.example.myapplication.features.workout.feature.workoutDialogs.equateVolume

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import com.example.myapplication.common.domain.model.Set
import com.example.myapplication.common.util.checkIntInput
import com.example.myapplication.common.util.checkRIRInput
import com.example.myapplication.common.util.doubleFormatted
import com.example.myapplication.features.workout.feature.workoutDialogs.SetContent
import com.example.myapplication.features.workout.feature.workoutDialogs.calculateNewSetResults
import com.example.myapplication.features.workout.feature.workoutDialogs.calculatePercentage
import com.example.myapplication.features.workout.feature.workoutDialogs.getFormattedPercentageText
import org.koin.androidx.compose.koinViewModel
import kotlin.uuid.ExperimentalUuidApi

@Composable
fun EquateVolumeLayout(
    equateVolumeViewModel: EquateVolumeViewModel = koinViewModel(),
    setIndex: Int,
    workingSet: Set,
) {
    var workingSetPercentage: Double? by remember { mutableStateOf(workingSet.percentage1RM) }

    var percentageText by remember { mutableStateOf(getFormattedPercentageText(workingSetPercentage)) }

    var repetitionsText by remember {
        mutableStateOf(
            workingSet.targetRepetitions?.toString() ?: ""
        )
    }

    var rirText by remember { mutableStateOf(workingSet.targetRepetitionsInReserve?.toString()) }

    val workingWeight = equateVolumeViewModel.getWorkoutSetWeight(workingSet)

    fun newWeight(newWeight: Double) {
        equateVolumeViewModel.updateSetWeight(workingSet.uuid, newWeight)
    }

    fun newReps(newReps: Int) {
        equateVolumeViewModel.updateSetReps(workingSet.uuid, newReps)
    }

    fun newPercentage(newPercentage: Double) {
        equateVolumeViewModel.updateSetPercentage(workingSet.uuid, newPercentage)
    }

    fun newRIR(newRIR: Double?) {
        equateVolumeViewModel.updateSetRIR(workingSet.uuid, newRIR)
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

    fun setRIRText(rir: String) {
        rirText = checkRIRInput(rir)?.takeIf { it.toDouble() in 0.0..10.0 }?.toString()
        newRIR(rirText?.toDoubleOrNull())
    }

    var resultsText by remember { mutableStateOf(
        calculateNewSetResults(
            workingSet.targetWeightPounds,
            workingSet.targetPercentage1RM,
            repetitionsText = if (workingSet.targetRepetitions != null) workingSet.targetRepetitions.toString() else 0.toString(),
            rirText = if (workingSet.targetRepetitionsInReserve != null)
                doubleFormatted(workingSet.targetRepetitionsInReserve) else "",
            rpeText = workingSet.targetRatePerceivedEffort.toString()
        ))
    }

    LaunchedEffect(
        percentageText,
        repetitionsText,
        rirText
    ) {
        resultsText = calculateNewSetResults(
            workingSet.targetWeightPounds,
            workingSet.targetPercentage1RM,
            repetitionsText = repetitionsText,
            rirText = rirText,
            rpeText = workingSet.targetRatePerceivedEffort.toString()
        )
    }


    SetContent(
//        setNumber = setIndex.inc(),
        setTypeText = setIndex.inc().toString(),
        setTypeColor = Color.White, /* TODO to get from SetType */
        resultsText = resultsText,
        percentageText = percentageText,
        repetitionsText = repetitionsText,
        rirText = rirText,
        onPercentageTextChanged = { percentage -> setPercentageText(percentage) },
        onRepetitionsTextChanged = { repetitions -> setRepetitionsText(repetitions) },
        onRIRTextChanged = { rir -> setRIRText(rir) },
        isChecked = false,
        onCheckedChange = { checked -> setIndex },
    )
}
