@file:OptIn(ExperimentalUuidApi::class, ExperimentalTime::class)

package com.example.myapplication.features.workout.feature.workoutDialogs

import android.util.Log
import com.example.myapplication.common.domain.model.MassUnit
import com.example.myapplication.common.domain.model.Set
import com.example.myapplication.common.domain.model.convertKgToPound
import com.example.myapplication.common.domain.model.convertPoundToKg
import com.example.myapplication.common.util.ModernizedLoadRIR
import com.example.myapplication.common.util.roundDownToMultipleOf
import kotlinx.serialization.json.Json
import kotlin.time.ExperimentalTime
import kotlin.uuid.ExperimentalUuidApi

fun applyModernizedLoadRIRSettingsToSets(
  weight: Double,
  exerciseWeightUnit: String?,
  oldSets: List<Set>,
  selectedSets: List<Set>,
  modernizedLoadRIRSetting: ModernizedLoadRIR?,
  roundingPreferencePounds: Double = 5.0,
  roundingPreferenceKilograms: Double = 2.5,
): MutableList<Set> {
  Log.d(
    "applyModernizedLoadRIRSettingsToSets",
    "applySettingsToSelectedSets: $modernizedLoadRIRSetting"
  )
//   Log.d("applyModernizedLoadRIRSettingsToSets", "sets: $selectedSets")

  modernizedLoadRIRSetting?.let { modernizedLoadRIR ->
    val updatedSets = oldSets.map { existingSet ->
      if (selectedSets.any { it.uuid == existingSet.uuid }) {
        Log.d(
          "applyModernizedLoadRIRSettingsToSets",
          "Updating set: ${existingSet.uuid}"
        )
        val relativeLoad = modernizedLoadRIR.relativeLoadPercent / 100

        // TODO Apply Rounding using User Settings/Preferences
        val (newWeightPounds: Double, newWeightKilograms: Double) =
          when (exerciseWeightUnit) {
            MassUnit.POUNDS.abbreviation -> {
              val newPounds: Double =
                weight.times(relativeLoad).roundDownToMultipleOf(roundingPreferencePounds)
              val newKilograms: Double =
                newPounds.convertPoundToKg().roundDownToMultipleOf(roundingPreferenceKilograms)
              Pair(newPounds, newKilograms)
            }

            MassUnit.KILOGRAMS.abbreviation -> {
              val newKilograms =
                weight.times(relativeLoad).roundDownToMultipleOf(roundingPreferenceKilograms)
              val newPounds: Double =
                newKilograms.convertKgToPound().roundDownToMultipleOf(roundingPreferencePounds)
              Pair(newPounds, newKilograms)
            }

            else -> {
              // Assume Pounds is the default if no unit is specified/default
              val newPounds: Double =
                weight * relativeLoad.roundDownToMultipleOf(roundingPreferencePounds)
              val newKilograms: Double =
                newPounds.convertPoundToKg().roundDownToMultipleOf(roundingPreferenceKilograms)
              Pair(newPounds, newKilograms)
            }
          }

        existingSet.copy(
          targetRepetitions = modernizedLoadRIR.repsPerformed,
          targetRepetitionsInReserve = modernizedLoadRIR.rir,
          targetRatePerceivedEffort = modernizedLoadRIR.rpe,
          targetPercentage1RM = relativeLoad,
          percentage1RM = relativeLoad,
          targetWeightPounds = newWeightPounds,
//            weightPounds = newWeightPounds,
          targetWeightKilograms = newWeightKilograms,
//            weightKilograms = newWeightKilograms
        )
      } else {
        existingSet
      }
    }
    updatedSets.forEach {
      Log.d(
        "applyModernizedLoadRIRSettingsToSets",
        "applySettingsToSelectedSets results for set ${it.setNumber}:\n ${
          Json.encodeToString(it)
        }"
      )
    }
    return updatedSets.toMutableList()
  }
  return mutableListOf<Set>()
}