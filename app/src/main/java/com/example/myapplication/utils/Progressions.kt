package com.example.myapplication.utils

import com.example.myapplication.data.model.Set

enum class ProgressionType {
  LINEAR_PROGRESSION,
  DOUBLE_PROGRESSION,
  DYNAMIC_DOUBLE_PROGRESSION,
  WAVE_PROGRESSION,
  AMRAP_PROGRESSION,
}

fun selectProgression(
  progressionType: ProgressionType
): (List<Set>, MutableList<Set>) -> MutableList<Set> {
  return when (progressionType) {
    ProgressionType.LINEAR_PROGRESSION -> ::linearProgression
    ProgressionType.DOUBLE_PROGRESSION -> ::doubleProgression
    ProgressionType.DYNAMIC_DOUBLE_PROGRESSION -> ::dynamicDoubleProgression
    ProgressionType.WAVE_PROGRESSION -> ::waveProgression
    ProgressionType.AMRAP_PROGRESSION -> ::amrapProgression
  }
}

/*
 * TODO Each set has a progression scheme id
 *  a when statement is used to determine which progression scheme function to call
 * TODO chock that each set as the same progression scheme id
 * */

/* TODO Write a query to get the next workout with same exercise and progression scheme
     in the current microcycle/mesocycle/program and update the target weight and repetitions
* */

fun linearProgression(sets: List<Set>, setsToUpdate: MutableList<Set>): MutableList<Set> {

  runCatching {
    var maxRepetitionsCompleted = sets.maxByOrNull { it.repetitions!! }?.repetitions ?: 0
    var lowestWeightPoundsPerformed = sets.minByOrNull { it.weightPounds!! }?.weightPounds ?: 0.0
    var lowestWeightKilogramsPerformed =
      sets.minByOrNull { it.weightKilograms!! }?.weightPounds ?: 0.0

    val incrementWeightPounds = sets.mapNotNull { it.incrementWeightPounds!! }.first()
    val incrementWeightKilograms = sets.mapNotNull { it.incrementWeightKilograms!! }.first()

    var repetitionsList = sets.mapNotNull { it.repetitions!! }.toMutableList()
    var weightPoundsList = sets.mapNotNull { it.weightPounds!! }.toMutableList()
    var weightKilogramsList = sets.mapNotNull { it.weightKilograms!! }.toMutableList()

    var targetMinimumRepetitionsList = sets.mapNotNull { it.targetMinimumRepetitions!! }
    var targetMaximumRepetitionsList = sets.mapNotNull { it.targetMaximumRepetitions!! }
    var targetRepetitionsList = sets.mapNotNull { it.targetRepetitions!!.toInt() }
    var targetWeightPoundsList = sets.mapNotNull { it.targetWeightPounds!! }
    var targetWeightKilogramsList = sets.mapNotNull { it.targetWeightKilograms!! }

    var targetMinimumRepetitions =
      if (targetMinimumRepetitionsList.all { it == targetMinimumRepetitionsList.firstOrNull() })
        targetMinimumRepetitionsList.first()
      else return@runCatching
    var targetMaximumRepetitions =
      if (targetMaximumRepetitionsList.all { it == targetMaximumRepetitionsList.firstOrNull() })
        targetMaximumRepetitionsList.first()
      else return@runCatching

    when {
      (repetitionsList == targetRepetitionsList &&
              weightPoundsList == targetWeightPoundsList &&
              weightKilogramsList == targetWeightKilogramsList) -> {
        weightPoundsList.replaceAll { it.plus(incrementWeightPounds) }
        weightKilogramsList.replaceAll { it.plus(incrementWeightKilograms) }
      }

      else -> {
        if (
          repetitionsList.any { it < targetRepetitionsList.minOrNull()!! } ||
          weightPoundsList.any { it < targetWeightPoundsList.minOrNull()!! } ||
          weightKilogramsList.any { it < targetWeightKilogramsList.minOrNull()!! }
        ) {
          // If targets are missed, reset to target values
          // repeat this week
          repetitionsList = sets.map { it.targetRepetitions!!.toInt() }.toMutableList()
          weightPoundsList = sets.map { it.targetWeightPounds!! }.toMutableList()
          weightKilogramsList = sets.map { it.targetWeightKilograms!! }.toMutableList()
        } else if (
          repetitionsList.any { it > targetRepetitionsList.minOrNull()!! } ||
          weightPoundsList.any { it > targetWeightPoundsList.minOrNull()!! } ||
          weightKilogramsList.any { it > targetWeightKilogramsList.minOrNull()!! }
        ) {
          // If targets are exceeded, increment weights
          repetitionsList = sets.map { it.targetRepetitions!!.toInt() }.toMutableList()
          weightPoundsList.replaceAll { it.plus(incrementWeightPounds) }
          weightKilogramsList.replaceAll { it.plus(incrementWeightKilograms) }
        }
      }
    }

    setsToUpdate.forEachIndexed { index, set ->
      set.targetRepetitions = repetitionsList[index].toString()
      set.targetWeightPounds = weightPoundsList[index]
      set.targetWeightKilograms = weightKilogramsList[index]
    }
  }

  return setsToUpdate
}

fun doubleProgression(sets: List<Set>, setsToUpdate: MutableList<Set>): MutableList<Set> {
  runCatching {
    var maxRepetitionsCompleted = sets.maxByOrNull { it.repetitions!! }?.repetitions ?: 0
    var lowestWeightPoundsPerformed = sets.minByOrNull { it.weightPounds!! }?.weightPounds ?: 0.0
    var lowestWeightKilogramsPerformed =
      sets.minByOrNull { it.weightKilograms!! }?.weightPounds ?: 0.0

    val incrementWeightPounds = sets.map { it.incrementWeightPounds!! }.first()
    val incrementWeightKilograms = sets.map { it.incrementWeightKilograms!! }.first()

    var repetitionsList = sets.map { it.repetitions!! }.toMutableList()
    var weightPoundsList = sets.map { it.weightPounds!! }.toMutableList()
    var weightKilogramsList = sets.map { it.weightKilograms!! }.toMutableList()

    var targetMinimumRepetitionsList = sets.map { it.targetMinimumRepetitions!! }
    var targetMaximumRepetitionsList = sets.map { it.targetMaximumRepetitions!! }

    var targetMinimumRepetitions =
      if (targetMinimumRepetitionsList.all { it == targetMinimumRepetitionsList.firstOrNull() })
        targetMinimumRepetitionsList.first()
      else return@runCatching
    var targetMaximumRepetitions =
      if (targetMaximumRepetitionsList.all { it == targetMaximumRepetitionsList.firstOrNull() })
        targetMaximumRepetitionsList.first()
      else return@runCatching

    repetitionsList.forEachIndexed { index, repetition ->
      when {
        repetitionsList.any { it < targetMinimumRepetitions } -> {
          // Reset to target values
          // repeat this week
          repetitionsList = sets.map { it.targetRepetitions!!.toInt() }.toMutableList()
          weightPoundsList = sets.map { it.targetWeightPounds!! }.toMutableList()
          weightKilogramsList = sets.map { it.targetWeightKilograms!! }.toMutableList()
          return@forEachIndexed
        }

        repetition < maxRepetitionsCompleted ||
                (repetitionsList.all {
                  it == maxRepetitionsCompleted && it < targetMaximumRepetitions
                }) -> {
          repetitionsList[index] = repetition.plus(1)
          return@forEachIndexed
        }

        repetitionsList.all { it >= targetMaximumRepetitions } -> {
          repetitionsList.replaceAll { targetMinimumRepetitions }
          weightPoundsList.replaceAll { it.plus(incrementWeightPounds) }
          weightKilogramsList.replaceAll { it.plus(incrementWeightKilograms) }
          return@forEachIndexed
        }
      }
    }

    setsToUpdate.forEachIndexed { index, set ->
      set.targetRepetitions = repetitionsList[index].toString()
      set.targetWeightPounds = weightPoundsList[index]
      set.targetWeightKilograms = weightKilogramsList[index]
    }
  }

  return setsToUpdate
}

fun dynamicDoubleProgression(sets: List<Set>, setsToUpdate: MutableList<Set>): MutableList<Set> {

  runCatching {
    var maxRepetitionsCompleted = sets.maxByOrNull { it.repetitions!! }?.repetitions ?: 0
    var lowestWeightPoundsPerformed = sets.minByOrNull { it.weightPounds!! }?.weightPounds ?: 0.0
    var lowestWeightKilogramsPerformed =
      sets.minByOrNull { it.weightKilograms!! }?.weightPounds ?: 0.0

    val incrementWeightPounds = sets.map { it.incrementWeightPounds!! }.first()
    val incrementWeightKilograms = sets.map { it.incrementWeightKilograms!! }.first()

    var repetitionsList = sets.map { it.repetitions!! }.toMutableList()
    var weightPoundsList = sets.map { it.weightPounds!! }.toMutableList()
    var weightKilogramsList = sets.map { it.weightKilograms!! }.toMutableList()

    var targetMinimumRepetitionsList = sets.map { it.targetMinimumRepetitions!! }
    var targetMaximumRepetitionsList = sets.map { it.targetMaximumRepetitions!! }

    var targetMinimumRepetitions =
      if (targetMinimumRepetitionsList.all { it == targetMinimumRepetitionsList.firstOrNull() })
        targetMinimumRepetitionsList.first()
      else return@runCatching
    var targetMaximumRepetitions =
      if (targetMaximumRepetitionsList.all { it == targetMaximumRepetitionsList.firstOrNull() })
        targetMaximumRepetitionsList.first()
      else return@runCatching

    repetitionsList.forEachIndexed { index, repetition ->
      if (repetition > targetMinimumRepetitions) {
        when {
          repetition < targetMaximumRepetitions -> {
            repetitionsList[index] = repetition.plus(1)
          }

          repetition >= targetMaximumRepetitions -> {
            repetitionsList[index] = targetMinimumRepetitions
            weightPoundsList[index] = weightPoundsList[index].plus(incrementWeightPounds)
            weightKilogramsList[index] = weightKilogramsList[index].plus(incrementWeightKilograms)
          }
        }
      } else {
        println("repeat this week")
        repetitionsList = sets.map { it.targetRepetitions!!.toInt() }.toMutableList()
        weightPoundsList = sets.map { it.targetWeightPounds!! }.toMutableList()
        weightKilogramsList = sets.map { it.targetWeightKilograms!! }.toMutableList()
      }
    }

    setsToUpdate.forEachIndexed { index, set ->
      set.targetRepetitions = repetitionsList[index].toString()
      set.targetWeightPounds = weightPoundsList[index]
      set.targetWeightKilograms = weightKilogramsList[index]
    }
  }

  return setsToUpdate
}

fun waveProgression(sets: List<Set>, setsToUpdate: MutableList<Set>): MutableList<Set> {

  runCatching {
    var maxRepetitionsCompleted = sets.maxByOrNull { it.repetitions!! }?.repetitions ?: 0
    var lowestWeightPoundsPerformed = sets.minByOrNull { it.weightPounds!! }?.weightPounds ?: 0.0
    var lowestWeightKilogramsPerformed =
      sets.minByOrNull { it.weightKilograms!! }?.weightPounds ?: 0.0

    val incrementWeightPounds = sets.map { it.incrementWeightPounds!! }.first()
    val incrementWeightKilograms = sets.map { it.incrementWeightKilograms!! }.first()

    var repetitionsList = sets.map { it.repetitions!! }.toMutableList()
    var weightPoundsList = sets.map { it.weightPounds!! }.toMutableList()
    var weightKilogramsList = sets.map { it.weightKilograms!! }.toMutableList()

    var targetMinimumRepetitionsList = sets.map { it.targetMinimumRepetitions!! }
    var targetMaximumRepetitionsList = sets.map { it.targetMaximumRepetitions!! }

    var targetMinimumRepetitions =
      if (targetMinimumRepetitionsList.all { it == targetMinimumRepetitionsList.firstOrNull() })
        targetMinimumRepetitionsList.first()
      else return@runCatching
    var targetMaximumRepetitions =
      if (targetMaximumRepetitionsList.all { it == targetMaximumRepetitionsList.firstOrNull() })
        targetMaximumRepetitionsList.first()
      else return@runCatching

    if (
      !(repetitionsList.all { it == repetitionsList.firstOrNull() }) ||
      repetitionsList.any { it > targetMaximumRepetitions } ||
      repetitionsList.any { it < targetMinimumRepetitions }
    ) {
      println("repeat this week")
      repetitionsList = sets.map { it.targetRepetitions!!.toInt() }.toMutableList()
      weightPoundsList = sets.map { it.targetWeightPounds!! }.toMutableList()
      weightKilogramsList = sets.map { it.targetWeightKilograms!! }.toMutableList()
    } else if (
      repetitionsList.all {
        it == repetitionsList.firstOrNull() &&
                it in targetMinimumRepetitions..targetMaximumRepetitions
      }
    ) {
      when {
        (repetitionsList.all { it == targetMinimumRepetitions }) -> {
          repetitionsList.replaceAll { targetMaximumRepetitions }
          weightPoundsList.replaceAll { it.minus(incrementWeightPounds) }
          weightKilogramsList.replaceAll { it.minus(incrementWeightKilograms) }
        }

        (repetitionsList.all { it > targetMinimumRepetitions }) -> {
          repetitionsList.replaceAll { it.minus(1) }
          weightPoundsList.replaceAll { it.plus(incrementWeightPounds) }
          weightKilogramsList.replaceAll { it.plus(incrementWeightKilograms) }
        }

        else -> {
          println("repeat this week")
          repetitionsList = sets.map { it.targetRepetitions!!.toInt() }.toMutableList()
          weightPoundsList = sets.map { it.targetWeightPounds!! }.toMutableList()
          weightKilogramsList = sets.map { it.targetWeightKilograms!! }.toMutableList()
        }
      }
    }

    setsToUpdate.forEachIndexed { index, set ->
      set.targetRepetitions = repetitionsList[index].toString()
      set.targetWeightPounds = weightPoundsList[index]
      set.targetWeightKilograms = weightKilogramsList[index]
    }
  }
    .onFailure {}
  return setsToUpdate
}

fun amrapProgression(sets: List<Set>, setsToUpdate: MutableList<Set>): MutableList<Set> {

  runCatching {
    var maxRepetitionsCompleted = sets.maxByOrNull { it.repetitions!! }?.repetitions ?: 0
    var lowestWeightPoundsPerformed = sets.minByOrNull { it.weightPounds!! }?.weightPounds ?: 0.0
    var lowestWeightKilogramsPerformed =
      sets.minByOrNull { it.weightKilograms!! }?.weightPounds ?: 0.0

    var targetWeightPounds = sets.map { it.targetWeightPounds!! }.first()
    var targetWeightKilograms = sets.map { it.targetWeightKilograms!! }.first()

    val incrementWeightPounds = sets.map { it.incrementWeightPounds!! }.first()
    val incrementWeightKilograms = sets.map { it.incrementWeightKilograms!! }.first()

    var repetitionsList = sets.map { it.repetitions!! }.toMutableList()
    var weightPoundsList = sets.map { it.weightPounds!! }.toMutableList()
    var weightKilogramsList = sets.map { it.weightKilograms!! }.toMutableList()

    var targetMinimumRepetitionsList = sets.map { it.targetMinimumRepetitions!! }
    var targetMaximumRepetitionsList = sets.map { it.targetMaximumRepetitions!! }

    var targetMinimumRepetitions =
      if (targetMinimumRepetitionsList.all { it == targetMinimumRepetitionsList.firstOrNull() })
        targetMinimumRepetitionsList.first()
      else return@runCatching
    var targetMaximumRepetitions =
      if (targetMaximumRepetitionsList.all { it == targetMaximumRepetitionsList.firstOrNull() })
        targetMaximumRepetitionsList.first()
      else return@runCatching

    if (
      weightPoundsList.all { it == targetWeightPounds } &&
      repetitionsList.all { it >= targetMinimumRepetitions }
    ) {
      when {
        repetitionsList.any { it >= targetMinimumRepetitions.times(2) } -> {
          weightPoundsList.replaceAll { it.plus(incrementWeightPounds.times(2)) }
        }

        repetitionsList.any {
          it in targetMinimumRepetitions.until(targetMinimumRepetitions.times(2))
        } -> {
          weightPoundsList.replaceAll { it.plus(incrementWeightPounds) }
        }
      }
    } else {
      println("repeat this week")
      repetitionsList = sets.map { it.targetRepetitions!!.toInt() }.toMutableList()
      weightPoundsList = sets.map { it.targetWeightPounds!! }.toMutableList()
      weightKilogramsList = sets.map { it.targetWeightKilograms!! }.toMutableList()
    }

    setsToUpdate.forEachIndexed { index, set ->
      set.targetRepetitions = repetitionsList[index].toString()
      set.targetWeightPounds = weightPoundsList[index]
      set.targetWeightKilograms = weightKilogramsList[index]
    }
  }

  return setsToUpdate
}
