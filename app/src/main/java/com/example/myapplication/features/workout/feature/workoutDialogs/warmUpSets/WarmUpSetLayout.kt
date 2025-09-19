@file:OptIn(ExperimentalUuidApi::class)

package com.example.myapplication.features

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import com.example.myapplication.common.domain.model.Set
import com.example.myapplication.common.util.checkIntInput
import com.example.myapplication.common.util.doubleFormatted
import com.example.myapplication.features.workout.feature.workoutDialogs.SetContent
import com.example.myapplication.features.workout.feature.workoutDialogs.calculateNewSetResults
import com.example.myapplication.features.workout.feature.workoutDialogs.calculatePercentage
import com.example.myapplication.features.workout.feature.workoutDialogs.getFormattedPercentageText
import com.example.myapplication.features.workout.feature.workoutDialogs.warmUpSets.WarmUpSetsViewModel
import kotlinx.serialization.json.Json
import org.koin.androidx.compose.koinViewModel
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid


@Composable
fun WarmUpSetLayout(
  warmUpSet: Set,
  setIndex: Int,
  percentageText: String,
  repetitionsText: String,
  rirText: String,
  onCheckedChange: (warmUpSet: Set, isChecked: Boolean) -> Unit,
  onPercentageChanged: (uuid: Uuid, percentage: Double) -> Unit,
  onRepetitionsChanged: (uuid: Uuid, reps: Int) -> Unit,
  onRirChanged: (uuid: Uuid, rir: Double?) -> Unit,
) {
  Log.d("WarmUpSetLayout", "warmUpSet ${warmUpSet.setNumber}:\n ${Json.encodeToString(warmUpSet)}")
  var currentPercentageText by remember { mutableStateOf(percentageText) }
  var currentRepetitionsText by remember { mutableStateOf(repetitionsText) }
  var currentRirText by remember { mutableStateOf(rirText) }

  // Key to the solution: Update local state when the incoming prop changes
  // This handles updates originating from the ViewModel (e.g., after applySettingsToSelectedSets)
  LaunchedEffect(percentageText) {
    Log.d(
      "WarmUpSetLayout",
      "LaunchedEffect: percentageText changed to $percentageText. Updating local state from $currentPercentageText."
    )
    if (currentPercentageText != percentageText) { // Only update if actually different
      currentPercentageText = percentageText
    }
  }
  LaunchedEffect(repetitionsText) {
    Log.d(
      "WarmUpSetLayout",
      "LaunchedEffect: repetitionsText changed to $repetitionsText. Updating local state from $currentRepetitionsText."
    )
    if (currentRepetitionsText != repetitionsText) {
      currentRepetitionsText = repetitionsText
    }
  }
  LaunchedEffect(rirText) {
    Log.d(
      "WarmUpSetLayout",
      "LaunchedEffect: rirText changed to $rirText. Updating local state from $currentRirText."
    )
    if (currentRirText != rirText) {
      currentRirText = rirText
    }
  }

  val handlePercentageChange = { percentage: String ->
    val (text, percent) = calculatePercentage(percentage)
    currentPercentageText = text
    onPercentageChanged(warmUpSet.uuid, percent)
  }

  val handleRepetitionsChange = { repetitions: String ->
    currentRepetitionsText = checkIntInput(repetitions.take(4))
    onRepetitionsChanged(warmUpSet.uuid, repetitionsText.toIntOrNull() ?: 0)
  }

  val handleRirChange = { rir: String ->
    currentRirText = checkIntInput(rir.take(3))
    onRirChanged(warmUpSet.uuid, rirText.toDoubleOrNull())
  }

  var resultsText by remember { mutableStateOf(
    calculateNewSetResults(
      warmUpSet.targetWeightPounds,
      warmUpSet.targetPercentage1RM,
      repetitionsText = if (warmUpSet.targetRepetitions != null) warmUpSet.targetRepetitions.toString() else 0.toString(),
      rirText = if (warmUpSet.targetRepetitionsInReserve != null)
        doubleFormatted(warmUpSet.targetRepetitionsInReserve) else "",
      rpeText = warmUpSet.targetRatePerceivedEffort.toString()
    ))
  }

  LaunchedEffect(
    currentPercentageText,
    currentRepetitionsText,
    currentRirText
  ) {
    resultsText = calculateNewSetResults(
      warmUpSet.targetWeightPounds,
      warmUpSet.targetPercentage1RM,
      repetitionsText = currentRepetitionsText,
      rirText = currentRirText,
      rpeText = warmUpSet.targetRatePerceivedEffort.toString()
    )
  }

  SetContent(
    setTypeText = setIndex.inc().toString(),
    setTypeColor = Color.White, /* TODO to get from SetType */
    resultsText = resultsText,
    percentageText = currentPercentageText,
    repetitionsText = currentRepetitionsText,
    rirText = currentRirText,
    // TODO Add RPE
    onPercentageTextChanged = handlePercentageChange,
    onRepetitionsTextChanged = handleRepetitionsChange,
    onRIRTextChanged = handleRirChange,
    onCheckedChange = { isChecked -> onCheckedChange(warmUpSet, isChecked) },
  )
}

@Composable
fun WarmUpSetLayoutContainer(
  warmUpSetsViewModel: WarmUpSetsViewModel = koinViewModel(),
  setIndex: Int,
  warmUpSet: Set,
//  checked: Boolean,
  onCheckedChange: (warmUpSet: Set, isChecked: Boolean) -> Unit,
) {
  Log.d("WarmUpSetLayout", "warmUpSet ${warmUpSet.setNumber}:\n ${Json.encodeToString(warmUpSet)}")
  val percentageText by remember {
    mutableStateOf(
      getFormattedPercentageText(
        warmUpSet.targetPercentage1RM ?: 1.0
      )
    )
  }
  val repetitionsText by remember { mutableStateOf(warmUpSet.targetRepetitions?.toString() ?: "") }
  val rirText by remember { mutableStateOf(warmUpSet.targetRepetitionsInReserve?.toString() ?: "") }
  WarmUpSetLayout(
    warmUpSet = warmUpSet,
    setIndex = setIndex,
    percentageText = percentageText,
    repetitionsText = repetitionsText,
    rirText = rirText,
    onCheckedChange = onCheckedChange,
    onPercentageChanged = { uuid, percentage ->
      warmUpSetsViewModel.updateWarmUpSetPercentage(uuid, percentage)
    },
    onRepetitionsChanged = { uuid, reps ->
      warmUpSetsViewModel.updateWarmUpSetReps(uuid, reps)
    },
    onRirChanged = { uuid, rir ->
      warmUpSetsViewModel.updateWarmUpSetRIR(uuid, rir)
    }
  )
}

/*
val setContent: @Composable (
  onPercentageTextChanged: (String) -> Unit,
  onRepetitionsTextChanged: (String) -> Unit,
  onRIRTextChanged: (String) -> Unit,
  onCheckedChange: (Boolean) -> Unit,
) -> Unit = { onPercentageTextChanged, onRepetitionsTextChanged, onRIRTextChanged, onCheckedChange ->
  SetContent(
    setTypeText = setIndex.inc().toString(),
    setTypeColor = Color.White, */
/* TODO to get from SetType *//*

    resultsText = calculateNewSetResults(
      warmUpSet.targetWeightPounds,
      warmUpSet.targetPercentage1RM,
      repetitionsText = if (warmUpSet.targetRepetitions != null) warmUpSet.targetRepetitions.toString() else 0.toString(),
      rirText = if (warmUpSet.targetRepetitionsInReserve != null)
        doubleFormatted(warmUpSet.targetRepetitionsInReserve) else "",
      rpeText = warmUpSet.targetRatePerceivedEffort.toString()
    ),
    percentageText = percentageText,
    repetitionsText = repetitionsText,
    rirText = rirText,
    onPercentageTextChanged = onPercentageTextChanged,
    onRepetitionsTextChanged = onRepetitionsTextChanged,
    onRIRTextChanged = onRIRTextChanged,
    onCheckedChange = onCheckedChange,
  )
}

setContent(
onPercentageTextChanged = handlePercentageChange,
onRepetitionsTextChanged = handleRepetitionsChange,
onRIRTextChanged = handleRirChange,
onCheckedChange = { isChecked -> onCheckedChange(warmUpSet, isChecked) },
)*/
