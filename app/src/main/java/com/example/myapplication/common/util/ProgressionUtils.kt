@file:OptIn(ExperimentalUuidApi::class)

package com.example.myapplication.common.util

import com.example.myapplication.common.domain.model.ExerciseSets
import kotlin.uuid.ExperimentalUuidApi

// TODO Implement Linear Progression

// TODO Update Implementations
// TODO figure out to implement this. most likely post workout completion,
//  update exercise in the next workout in the program/block that is using the same progression
// scheme
//  Show the updated next workout based on progression scheme,
//  Ask user to review and accept changes, otherwise keep the workout the same

// TODO add way to track failures, i.e. not able to meet target reps and RIR with the target weight
//  propose user deloads after a number of failures
//  Add deload week dynamically, ask user to accept or reject proposed deload weeks

// TODO Refactor so that the completed set is not updated/changed

// Double Progression Feature
fun ExerciseSets.doubleProgression(
/* targetRepetitionRangeMin: Int, targetRepetitionRangeMax: Int, incrementWeight: Int*/ ) {
  val targetRepetitionRangeMin = 12 // TODO replace with set.targetMimumRepetitions
  val targetRepetitionRangeMax = 15 // TODO replace with set.targetMaximumRepetitions
  val incrementWeight = 5.0 // TODO add to pkl
  val incrementPercentage1RM = 0.005 // 0.5% TODO add to pkl

  val maxRepetitionsCompleted = sets.maxByOrNull { it.setCompleted }?.repetitions ?: 0

  val repetitionsList = sets.map { it.repetitions }

  sets.forEach { set ->
    if (
      set.targetWeightPounds == set.weightPounds &&
        set.repetitions in targetRepetitionRangeMin..targetRepetitionRangeMax &&
        sets.all { it.setCompleted }
    ) {
      when {
        set.repetitions!! < maxRepetitionsCompleted -> {
          set.targetRepetitions = set.repetitions?.plus(1)
          return@forEach
        }
        repetitionsList.all {
          it == repetitionsList[0] &&
            it!! < targetRepetitionRangeMax &&
            it == maxRepetitionsCompleted
        } -> {
          sets.first().repetitions?.plus(1)
          return@forEach
        }
        repetitionsList.all { it!! == targetRepetitionRangeMax } -> {
          set.repetitions = targetRepetitionRangeMin
          set.targetWeightPounds = set.weightPounds?.plus(incrementWeight)
        }
      }
    } else if (
      set.targetWeightPounds == set.weightPounds &&
        repetitionsList.all { it!! >= targetRepetitionRangeMax } &&
        sets.all { it.setCompleted }
    ) {
      set.repetitions = targetRepetitionRangeMin
      set.targetWeightPounds = set.weightPounds?.plus(incrementWeight)
    }
    // targetWeight & repetitions does not change
  }
}

// Wave Progression Feature
fun ExerciseSets.waveProgression(
/*int targetRepetitionRangeMin, int targetRepetitionRangeMax, int incrementWeight*/ ) {
  val targetRepetitionRangeMin = 12
  val targetRepetitionRangeMax = 15
  val incrementWeight = 5.0

  val maxRepetitionsCompleted = sets.maxByOrNull { it.setCompleted }?.repetitions ?: 0

  val repetitionsList = sets.map { it.repetitions }

  sets.forEach { set ->
    if (
      set.targetWeightPounds == set.weightPounds &&
        set.repetitions in targetRepetitionRangeMin..targetRepetitionRangeMax &&
        sets.all { it.setCompleted }
    ) {
      when {
        repetitionsList.all { it!! == targetRepetitionRangeMin } -> {
          set.repetitions = targetRepetitionRangeMax
          set.targetWeightPounds = set.weightPounds?.plus(incrementWeight)
        }
        repetitionsList.all { it == repetitionsList[0] && it == maxRepetitionsCompleted } -> {
          set.repetitions?.minus(1)
          set.targetWeightPounds = set.weightPounds?.plus(incrementWeight)
          return@forEach
        }
      }
    }
  }
}

// Dynamic Double Progression Feature
fun ExerciseSets.dynamicDoubleProgression(
/*int targetRepetitionRangeMin, int targetRepetitionRangeMax, int incrementWeight*/ ) {
  val targetRepetitionRangeMin = 12
  val targetRepetitionRangeMax = 15
  val incrementWeight = 5.0

  sets.forEach { set ->
    if (
      set.targetWeightPounds == set.weightPounds &&
        set.repetitions in targetRepetitionRangeMin..targetRepetitionRangeMax &&
        sets.all { it.setCompleted }
    ) {
      when {
        set.repetitions!! < targetRepetitionRangeMax -> {
          set.targetRepetitions = set.repetitions?.plus(1)
        }
        set.repetitions!! >= targetRepetitionRangeMax -> {
          set.repetitions = targetRepetitionRangeMin
          set.targetWeightPounds = set.weightPounds?.plus(incrementWeight)
        }
      }
    }
  }
}

// As Many Repetitions as Possible Progression Feature
fun ExerciseSets.amrapProgression(/*int targetRepetitionRangeMin, int incrementWeight*/ ) {
  val targetRepetitionRangeMin = 5
  val incrementWeight = 5.0

  sets.forEach { set ->
    if (set.targetWeightPounds == set.weightPounds && sets.all { it.setCompleted }) {
      when {
        set.repetitions!! >= targetRepetitionRangeMin.times(2) -> {
          set.targetWeightPounds = set.weightPounds?.plus(incrementWeight.times(2))
        }
        set.repetitions!! in targetRepetitionRangeMin.until(targetRepetitionRangeMin.times(2)) -> {
          set.targetWeightPounds = set.weightPounds?.plus(incrementWeight)
        }
      }
    }
  }
}

// Linear Progression Feature
fun ExerciseSets.linearProgression(/*int targetRepetitionRangeMin, int incrementWeight*/) {
  val targetRepetitionRangeMin = 5
  val incrementWeight = 5.0

  sets.forEach { set ->
    if (set.targetWeightPounds == set.weightPounds && sets.all { it.setCompleted }) {
      when {
        set.repetitions!! >= targetRepetitionRangeMin -> {
          set.targetWeightPounds = set.weightPounds?.plus(incrementWeight)
        }
      }
    }
  }
}