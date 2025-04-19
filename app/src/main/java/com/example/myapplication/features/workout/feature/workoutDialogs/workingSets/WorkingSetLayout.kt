@file:OptIn(ExperimentalUuidApi::class)

package com.example.myapplication.features.workout.feature.workoutDialogs.workingSets

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.features.workout.feature.workoutDialogs.SetContent
import com.example.myapplication.features.workout.feature.workoutDialogs.calculateNewSetResults
import com.example.myapplication.features.workout.feature.workoutDialogs.calculatePercentage
import com.example.myapplication.features.workout.feature.workoutDialogs.getFormattedPercentageText
import com.example.myapplication.common.domain.model.Set
import com.example.myapplication.common.util.checkIntInput
import kotlin.uuid.ExperimentalUuidApi

@Composable
fun WorkingSetLayout(
    workingSetViewModel: WorkingSetViewModel = viewModel(),
    setIndex: Int,
    workingSet: Set,
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
        setTypeColor = Color.White /* TODO to get from SetType */,
        resultsText = calculateNewSetResults(workingWeight, workingSetPercentage, repetitionsText),
        percentageText = percentageText,
        repetitionsText = repetitionsText,
        onPercentageTextChanged = { percentage -> setPercentageText(percentage) },
        onRepetitionsTextChanged = { repetitions -> setRepetitionsText(repetitions) },
    )
}
