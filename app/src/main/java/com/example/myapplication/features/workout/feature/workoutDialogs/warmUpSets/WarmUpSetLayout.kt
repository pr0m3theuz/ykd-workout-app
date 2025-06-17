@file:OptIn(ExperimentalUuidApi::class)

package com.example.myapplication.features

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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

@Composable
fun WarmUpSetLayout(
  warmUpSetsViewModel: WarmUpSetsViewModel = koinViewModel(),
  setIndex: Int,
  warmUpSet: Set,
//  checked: Boolean,
  onCheckedChange: (warmUpSet: Set, isChecked: Boolean) -> Unit,
) {
  Log.d("WarmUpSetLayout", "warmUpSet ${warmUpSet.setNumber}:\n ${Json.encodeToString(warmUpSet)}")

  val workingWeight by warmUpSetsViewModel.workingSetWeight.collectAsState()

  var warmUpPercentage: Double? by remember { mutableStateOf(warmUpSet.targetPercentage1RM ?: 1.0) }

  var percentageText by remember {
    mutableStateOf(
      getFormattedPercentageText(
        warmUpSet.targetPercentage1RM ?: 1.0
      )
    )
  }

  var repetitionsText by remember { mutableStateOf(warmUpSet.targetRepetitions?.toString() ?: "") }
  var rirText by remember { mutableStateOf(warmUpSet.targetRepetitionsInReserve?.toString() ?: "") }

//  warmUpSetsViewModel.updateWarmUpSetWeight(
//    warmUpSet.uuid,
//    workingWeight.times(warmUpPercentage!!)
//  )

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

  // TODO
  fun setRIRText(rir: String) {
    rirText = checkIntInput(rir.take(3))
    warmUpSetsViewModel.updateWarmUpSetRIR(warmUpSet.uuid, rirText.toDoubleOrNull())
  }

  SetContent(
    setTypeText = setIndex.inc().toString(),
    setTypeColor = Color.White, /* TODO to get from SetType */
    resultsText = calculateNewSetResults(
      warmUpSet.targetWeightPounds,
      warmUpSet.targetPercentage1RM,
      repetitionsText = if (warmUpSet.targetRepetitions != null) warmUpSet.targetRepetitions.toString() else 0.toString(),
      rirText = if (warmUpSet.targetRepetitionsInReserve != null)
        doubleFormatted(warmUpSet.targetRepetitionsInReserve) else "",
      rpeText = warmUpSet.targetRatePerceivedEffort.toString()
    ),
    percentageText = getFormattedPercentageText(warmUpSet.targetPercentage1RM),
    repetitionsText = if (warmUpSet.targetRepetitions != null) warmUpSet.targetRepetitions.toString() else "",
    rirText = if (warmUpSet.targetRepetitionsInReserve != null) doubleFormatted(warmUpSet.targetRepetitionsInReserve) else "",
    // TODO Add RPE
    onPercentageTextChanged = { percentage -> setPercentageText(percentage) },
    onRepetitionsTextChanged = { repetitions -> setRepetitionsText(repetitions) },
    onRIRTextChanged = { rir -> setRIRText(rir) },
//    isChecked = checked,
    onCheckedChange = { isChecked -> onCheckedChange(warmUpSet, isChecked) },
  )
}
