package com.example.myapplication.components.exercise

import net.jqwik.api.Arbitraries
import net.jqwik.api.Example
import net.jqwik.api.Provide
import net.jqwik.api.arbitraries.StringArbitrary
import org.junit.jupiter.api.Assertions.assertEquals


@Deprecated(
  message = "Replace with UpdatedProgressionTests",
  level = DeprecationLevel.WARNING
)
class ProgressionTests {
  @Provide
  fun stringNumber(): StringArbitrary {
    return Arbitraries.strings().withCharRange('0', '9').withChars('.', '-', ',')
  }

  @Example
  //  @Report(Reporting.GENERATED)
  fun `Double Progression Test`(
    targetRepetitionRangeMin: Int = 12,
    targetRepetitionRangeMax: Int = 15,
    incrementWeight: Int = 5,
    //    maxRepetitionsCompleted: Int,
    weight: MutableList<Double> = mutableListOf(100.0, 100.0, 100.0),
    sets: MutableList<Int> = mutableListOf(13, 12, 12),
    targetWeight: Double = 100.0,
  ) {
    var targetRepetitions = 0
    var newTargetWeight = targetWeight

    val maxRepetitionsCompleted = sets.maxOrNull()!!

    run sets@{
      sets.forEachIndexed { index, repetitions ->
        if (
          targetWeight == weight[index] &&
            repetitions in targetRepetitionRangeMin..targetRepetitionRangeMax
        ) {
          when {
            repetitions < maxRepetitionsCompleted -> {
              sets[index] = sets[index].plus(1)
              println(sets[index])
              return@sets
            }
            sets.all {
              it == sets[0] && it < targetRepetitionRangeMax && it == maxRepetitionsCompleted
            } -> {
              sets[index] = sets[index].plus(1)
              return@sets
            }
            sets.all { it == targetRepetitionRangeMax } -> {
              targetRepetitions = targetRepetitionRangeMin
              newTargetWeight = weight[index].plus(incrementWeight)
            }
          }
        } else if (targetWeight == weight[index] && sets.all { it >= targetRepetitionRangeMax }) {
          targetRepetitions = targetRepetitionRangeMin
          newTargetWeight = weight[index].plus(incrementWeight)
        }
      }
    }
    assertEquals(listOf(13, 13, 12), sets)
  }
}
