@file:OptIn(ExperimentalUuidApi::class)

package com.example.myapplication.features

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import com.example.myapplication.features.workout.feature.workoutDialogs.SetContent
import com.example.myapplication.features.workout.feature.workoutDialogs.calculateNewSetResults
import com.example.myapplication.features.workout.feature.workoutDialogs.calculatePercentage
import com.example.myapplication.features.workout.feature.workoutDialogs.getFormattedPercentageText
import com.example.myapplication.features.workout.feature.workoutDialogs.warmUpSets.WarmUpSetsViewModel
import com.example.myapplication.common.domain.model.Set
import com.example.myapplication.common.util.checkIntInput
import org.koin.androidx.compose.koinViewModel
import kotlin.uuid.ExperimentalUuidApi

@Composable
fun WarmUpSetLayout(
    warmUpSetsViewModel: WarmUpSetsViewModel = koinViewModel(),
    setIndex: Int,
    warmUpSet: Set,
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
        setTypeColor = Color.White /* TODO to get from SetType */,
        resultsText = calculateNewSetResults(workingWeight, warmUpPercentage, repetitionsText),
        percentageText = percentageText,
        repetitionsText = repetitionsText,
        onPercentageTextChanged = { percentage -> setPercentageText(percentage) },
        onRepetitionsTextChanged = { repetitions -> setRepetitionsText(repetitions) },
    )
}
