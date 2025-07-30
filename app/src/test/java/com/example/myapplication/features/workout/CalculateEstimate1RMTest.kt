package com.example.myapplication.features.workout

import com.example.myapplication.common.domain.model.Set
import com.example.myapplication.common.util.OneRepMaxEstimateFormulas
import com.example.myapplication.common.util.RepCapacity
import com.example.myapplication.common.util.estimateTargetOneRepMaxModernized
import kotlinx.serialization.json.Json
import net.jqwik.api.Example
import java.io.File
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class CalculateEstimate1RMTest {
  // Define a tolerance for comparing Double values due to potential floating-point inaccuracies
  private val doubleTolerance = 0.0001

  val filename =
    "/mnt/lil_nas/icefoxneoalligator/Documents/AndroidStudioProjects/MyApplication4/app/src/test/java/com/example/myapplication/resources/features/Modernized Load_RIR Chart - Combined Rep Capacity_processed copy.csv"

  val modernizedLoadRIRItems = readCsv(File(filename).inputStream())

  private fun getRelativeLoadPercentage(
    repCapacity: RepCapacity,
    rpe: Double,
    rir: Double?,
    repsPerformed: Int,
  ): Double? {
    println("getRelativeLoadPercentage repCapacity: $repCapacity")
    println("getRelativeLoadPercentage rpe: $rpe")
    println("getRelativeLoadPercentage rir: $rir")
    println("getRelativeLoadPercentage repsPerformed: $repsPerformed")

    var resultList =
      modernizedLoadRIRItems
        .filter { it.repCapacity == repCapacity }
        .filter { it.repsPerformed == repsPerformed }

    println("getRelativeLoadPercentage resultList: $resultList")

    resultList = resultList.filter { it.rir == rir }
    println("getRelativeLoadPercentage resultList: $resultList")

    resultList = resultList.filter { it.rpe == rpe }
    println("getRelativeLoadPercentage resultList: $resultList")

    val result = resultList.firstOrNull()?.relativeLoadPercent

    println("getRelativeLoadPercentage result: $result")

    return result
  }

  private fun calculateEstimated1RMKilograms(set: Set, repCapacity: RepCapacity): Double? {
    return if (
      set.ratePerceivedEffort != null ||
      set.repetitionsInReserve != null &&
      set.repetitions != null
    ) {
      estimateTargetOneRepMaxModernized(
        set.weightKilograms ?: 0.0,
        set.repetitions ?: 0,
        targetReps = 1,
        set.repetitionsInReserve
          ?: 10.0.minus(set.ratePerceivedEffort!!),
        set.ratePerceivedEffort
          ?: 10.0.minus(set.repetitionsInReserve!!),
        repCapacity = repCapacity,
        getRelativeLoadPercentage = ::getRelativeLoadPercentage,
      )
    } else if (set.repetitions != null) {
      OneRepMaxEstimateFormulas.calculateOneRepMax(
        set.weightKilograms ?: 0.0,
        set.repetitions!!,
        OneRepMaxEstimateFormulas.Formula.Brzycki,
      )
    } else {
      null
    }
  }

  private fun calculateEstimated1RMPounds(set: Set, repCapacity: RepCapacity): Double? {
    return if (
      set.ratePerceivedEffort != null ||
      set.repetitionsInReserve != null &&
      set.repetitions != null
    ) {
      estimateTargetOneRepMaxModernized(
        set.weightPounds ?: 0.0,
        set.repetitions ?: 0,
        targetReps = 1,
        set.repetitionsInReserve
          ?: 10.0.minus(set.ratePerceivedEffort!!),
        set.ratePerceivedEffort
          ?: 10.0.minus(set.repetitionsInReserve!!),
        repCapacity = repCapacity,
        getRelativeLoadPercentage = ::getRelativeLoadPercentage,
      )
    } else if (set.repetitions != null) {
      OneRepMaxEstimateFormulas.calculateOneRepMax(
        set.weightPounds ?: 0.0,
        set.repetitions!!,
        OneRepMaxEstimateFormulas.Formula.Brzycki,
      )
    } else {
      null
    }
  }

  val setJson = """
    {
       "id": "6888c124d6bc96e0938311f7",
       "isPublic": false,
       "lazyListItemID": 1058375961199345,
       "type": "Set",
       "uuid": "950d6cdfc9d34c46b5c67721da63331c",
       "dateCreated": "2025-07-29T12:40:04.698455Z",
       "ownerID": "68879e60d6bc96e0938311a2",
       "programID": null,
       "mesocycleID": null,
       "microcycleID": null,
       "exerciseID": "64f0de3de93f8b251df4625f",
       "exerciseSetsID": "6888c124d6bc96e0938311f5",
       "setNumber": 2,
       "workoutID": "6888c124d6bc96e0938311df",
       "weightPounds": 20.0,
       "weightKilograms": 9.071847400000001,
       "repetitions": 12,
       "percentage1RM": null,
       "estimated1RM": null,
       "setCompleted": true,
       "setTypeID": 1,
       "estimated1RMKilograms": null,
       "estimated1RMPounds": null,
       "hashedSetSignature": null,
       "hashedWorkoutSignature": null,
       "incrementWeightKilograms": null,
       "incrementWeightPounds": null,
       "progressionSchemeID": null,
       "ratePerceivedEffort": null,
       "repetitionsInReserve": 2.0,
       "setVolumeInKilograms": 108.8621688,
       "setVolumeInPounds": 240.0,
       "targetMaximumRepetitions": null,
       "targetMinimumRepetitions": null,
       "targetPercentage1RM": null,
       "targetRatePerceivedEffort": 8.0,
       "targetRepetitions": 12,
       "targetRepetitionsInReserve": 2.0,
       "targetWeightKilograms": null,
       "targetWeightPounds": null
    }
  """.trimIndent()

  val set = Json.decodeFromString<Set>(setJson)

  @Example
  fun calculateEstimated1RMPounds() {
    val estimated1RMPounds = calculateEstimated1RMPounds(set, RepCapacity.Average)
    val estimated1RMKilograms = calculateEstimated1RMKilograms(set, RepCapacity.Average)

    assertNotNull(estimated1RMPounds)
    assertNotNull(estimated1RMKilograms)
    assertEquals(27.59381898454753, estimated1RMPounds, doubleTolerance)
    assertEquals(12.5163457505519, estimated1RMKilograms, doubleTolerance)
  }
}
