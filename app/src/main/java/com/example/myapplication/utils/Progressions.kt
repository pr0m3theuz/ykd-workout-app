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
): (List<Set>, MutableList<Set>) -> Result<MutableList<Set>> {
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

fun linearProgression(sets: List<Set>, setsToUpdate: MutableList<Set>): Result<MutableList<Set>> {

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

    /*    var targetMinimumRepetitions =
          if (targetMinimumRepetitionsList.all { it == targetMinimumRepetitionsList.firstOrNull() })
            targetMinimumRepetitionsList.first()
          else return@runCatching
        var targetMaximumRepetitions =
          if (targetMaximumRepetitionsList.all { it == targetMaximumRepetitionsList.firstOrNull() })
            targetMaximumRepetitionsList.first()
          else return@runCatching
    */

    when {
      repetitionsList == targetRepetitionsList &&
              weightPoundsList == targetWeightPoundsList &&
              weightKilogramsList == targetWeightKilogramsList -> {
        weightPoundsList.replaceAll { it.plus(incrementWeightPounds) }
        weightKilogramsList.replaceAll { it.plus(incrementWeightKilograms) }
      }

      repetitionsList.zip(targetRepetitionsList).any { (actual, target) -> actual < target } ||
              weightPoundsList.zip(targetWeightPoundsList)
                .any { (actual, target) -> actual < target } ||
              weightKilogramsList.zip(targetWeightKilogramsList).any { (actual, target) ->
                actual < target
              } -> {
        // If targets are missed, reset to target values
        repetitionsList.clear()
        repetitionsList.addAll(targetRepetitionsList)
        weightPoundsList.clear()
        weightPoundsList.addAll(targetWeightPoundsList)
        weightKilogramsList.clear()
        weightKilogramsList.addAll(targetWeightKilogramsList)
      }

      else -> {
        // If all targets are exceeded, reset repetitions and increment weights
        repetitionsList.clear()
        repetitionsList.addAll(targetRepetitionsList)
        weightPoundsList.replaceAll { it.plus(incrementWeightPounds) }
        weightKilogramsList.replaceAll { it.plus(incrementWeightKilograms) }
      }
    }

    // Update setsToUpdate with new values
    setsToUpdate.zip(repetitionsList).forEach { (set, rep) ->
      set.targetRepetitions = rep.toString()
    }
    setsToUpdate.zip(weightPoundsList).forEach { (set, weight) -> set.targetWeightPounds = weight }
    setsToUpdate.zip(weightKilogramsList).forEach { (set, weight) ->
      set.targetWeightKilograms = weight
    }
  }

  return Result.success(setsToUpdate)
}

fun doubleProgression(sets: List<Set>, _setsToUpdate: MutableList<Set>): Result<MutableList<Set>> {
  val setsToUpdate = _setsToUpdate.toMutableList()

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
  var targetRepetitionsList = sets.mapNotNull { it.targetRepetitions!!.toInt() }
  var targetWeightPoundsList = sets.mapNotNull { it.targetWeightPounds!! }
  var targetWeightKilogramsList = sets.mapNotNull { it.targetWeightKilograms!! }

  // Check if all target repetitions are the same
  var targetMinimumRepetitions =
    if (targetMinimumRepetitionsList.all { it == targetMinimumRepetitionsList.first() })
      targetMinimumRepetitionsList.first()
    else return Result.failure(IllegalArgumentException("All target repetitions must be the same"))

  var targetMaximumRepetitions =
    if (targetMaximumRepetitionsList.all { it == targetMaximumRepetitionsList.firstOrNull() })
      targetMaximumRepetitionsList.first()
    else return Result.failure(IllegalArgumentException("All target repetitions must be the same"))

  //  if (weightPoundsList != targetWeightPoundsList && weightKilogramsList !=
  // targetWeightKilogramsList)
  //  {
  //    return Result.failure(IllegalArgumentException("Actual weight performed do not match target
  // values"))
  //  }

  when {
    repetitionsList.any { it < targetMinimumRepetitions } ||
            (weightPoundsList != targetWeightPoundsList &&
                    weightKilogramsList != targetWeightKilogramsList) -> {
      // Reset to target values
      // repeat this week
      repetitionsList.clear()
      repetitionsList.addAll(targetRepetitionsList)
      weightPoundsList.clear()
      weightPoundsList.addAll(targetWeightPoundsList)
      weightKilogramsList.clear()
      weightKilogramsList.addAll(targetWeightKilogramsList)
    }
    /*    (repetitionsList == targetRepetitionsList) ||
      // if any repetitions are below the maximum repetitions performed in the workout
      (repetitionsList.any { it < maxRepetitionsCompleted } ||
        // if all repetitions are at the maximum repetitions performed in the workout and below the
        // target maximum repetitions
        (repetitionsList.all { it == maxRepetitionsCompleted && it < targetMaximumRepetitions })) &&*/
    // If repetition is within target range, increment
    (repetitionsList.zip(targetRepetitionsList).all { (actual, target) ->
      (actual == target) && (actual < targetMaximumRepetitions)
    }) &&
            // if weights equal target values
            (weightPoundsList == targetWeightPoundsList &&
                    weightKilogramsList == targetWeightKilogramsList) -> {
      for (index in repetitionsList.indices) {
        repetitionsList[index]++
        break
      }
    }

    // If all repetitions are at the maximum, reset to target values
    repetitionsList.all { it >= targetMaximumRepetitions } &&
            (weightPoundsList == targetWeightPoundsList &&
                    weightKilogramsList == targetWeightKilogramsList) -> {
      repetitionsList.clear()
      repetitionsList.addAll(targetMinimumRepetitionsList)
      weightPoundsList.clear()
      weightPoundsList.addAll(
        targetWeightPoundsList.map { it.plus(incrementWeightPounds) }.toMutableList()
      )
      weightKilogramsList.clear()
      weightKilogramsList.addAll(
        targetWeightKilogramsList.map { it.plus(incrementWeightKilograms) }.toMutableList()
      )
    }
    // If actual repetitions are above target, increment target values
    repetitionsList.zip(targetRepetitionsList).any { (actual, target) ->
      (actual >= target) && (actual < targetMaximumRepetitions)
    } &&
            (weightPoundsList == targetWeightPoundsList &&
                    weightKilogramsList == targetWeightKilogramsList) -> {

      val tempRepetitionsList = targetRepetitionsList.toMutableList()

      val tempPair = repetitionsList.zip(targetRepetitionsList)
      for ((index, pair) in tempPair.withIndex()) {
        val (actual, target) = pair
        if ((actual >= target) && (actual < targetMaximumRepetitions)) {
          tempRepetitionsList[index]++
          break
        }
      }

      repetitionsList.clear()
      repetitionsList.addAll(tempRepetitionsList)
    }

    else -> {
      println("repeat this week")
      repetitionsList.clear()
      repetitionsList.addAll(targetRepetitionsList)
      weightPoundsList.clear()
      weightPoundsList.addAll(targetWeightPoundsList)
      weightKilogramsList.clear()
      weightKilogramsList.addAll(targetWeightKilogramsList)
    }
  }

  setsToUpdate.zip(repetitionsList).forEach { (set, rep) -> set.targetRepetitions = rep.toString() }
  setsToUpdate.zip(weightPoundsList).forEach { (set, weight) -> set.targetWeightPounds = weight }
  setsToUpdate.zip(weightKilogramsList).forEach { (set, weight) ->
    set.targetWeightKilograms = weight
  }

  return Result.success(setsToUpdate)
}

fun dynamicDoubleProgression(
  sets: List<Set>,
  setsToUpdate: MutableList<Set>,
): Result<MutableList<Set>> {

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

    var targetRepetitionsList = sets.mapNotNull { it.targetRepetitions!!.toInt() }
    var targetWeightPoundsList = sets.mapNotNull { it.targetWeightPounds!! }
    var targetWeightKilogramsList = sets.mapNotNull { it.targetWeightKilograms!! }

    // Check if all target repetitions are the same
    var targetMinimumRepetitions =
      if (targetMinimumRepetitionsList.all { it == targetMinimumRepetitionsList.first() })
        targetMinimumRepetitionsList.first()
      else
        return Result.failure(IllegalArgumentException("All target repetitions must be the same"))

    var targetMaximumRepetitions =
      if (targetMaximumRepetitionsList.all { it == targetMaximumRepetitionsList.firstOrNull() })
        targetMaximumRepetitionsList.first()
      else
        return Result.failure(IllegalArgumentException("All target repetitions must be the same"))

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
        // repeat this week
        repetitionsList = sets.map { it.targetRepetitions!!.toInt() }.toMutableList()
        weightPoundsList = sets.map { it.targetWeightPounds!! }.toMutableList()
        weightKilogramsList = sets.map { it.targetWeightKilograms!! }.toMutableList()
      }
    }

    // Update setsToUpdate with new values
    setsToUpdate.zip(repetitionsList).forEach { (set, rep) ->
      set.targetRepetitions = rep.toString()
    }
    setsToUpdate.zip(weightPoundsList).forEach { (set, weight) -> set.targetWeightPounds = weight }
    setsToUpdate.zip(weightKilogramsList).forEach { (set, weight) ->
      set.targetWeightKilograms = weight
    }
  }

  return Result.success(setsToUpdate)
}

fun waveProgression(sets: List<Set>, setsToUpdate: MutableList<Set>): Result<MutableList<Set>> {

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

    var targetRepetitionsList = sets.mapNotNull { it.targetRepetitions!!.toInt() }
    var targetWeightPoundsList = sets.mapNotNull { it.targetWeightPounds!! }
    var targetWeightKilogramsList = sets.mapNotNull { it.targetWeightKilograms!! }

    // Check if all target repetitions are the same
    var targetMinimumRepetitions =
      if (targetMinimumRepetitionsList.all { it == targetMinimumRepetitionsList.first() })
        targetMinimumRepetitionsList.first()
      else
        return Result.failure(IllegalArgumentException("All target repetitions must be the same"))

    var targetMaximumRepetitions =
      if (targetMaximumRepetitionsList.all { it == targetMaximumRepetitionsList.firstOrNull() })
        targetMaximumRepetitionsList.first()
      else
        return Result.failure(IllegalArgumentException("All target repetitions must be the same"))

    if (
      !(repetitionsList.all { it == repetitionsList.firstOrNull() }) ||
      repetitionsList.any { it > targetMaximumRepetitions } ||
      repetitionsList.any { it < targetMinimumRepetitions }
    ) {
      // repeat this week
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
        repetitionsList.all { it == targetMinimumRepetitions } -> {
          repetitionsList.replaceAll { targetMaximumRepetitions }
          weightPoundsList.replaceAll { it.minus(incrementWeightPounds) }
          weightKilogramsList.replaceAll { it.minus(incrementWeightKilograms) }
        }

        repetitionsList.all { it > targetMinimumRepetitions } -> {
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

    // Update setsToUpdate with new values
    setsToUpdate.zip(repetitionsList).forEach { (set, rep) ->
      set.targetRepetitions = rep.toString()
    }
    setsToUpdate.zip(weightPoundsList).forEach { (set, weight) ->
      set.targetWeightPounds = weight
    }
    setsToUpdate.zip(weightKilogramsList).forEach { (set, weight) ->
      set.targetWeightKilograms = weight
    }
  }
    .onFailure {}
  return Result.success(setsToUpdate)
}

fun amrapProgression(sets: List<Set>, setsToUpdate: MutableList<Set>): Result<MutableList<Set>> {

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

    var targetRepetitionsList = sets.mapNotNull { it.targetRepetitions!!.toInt() }
    var targetWeightPoundsList = sets.mapNotNull { it.targetWeightPounds!! }
    var targetWeightKilogramsList = sets.mapNotNull { it.targetWeightKilograms!! }

    // Check if all target repetitions are the same
    var targetMinimumRepetitions =
      if (targetMinimumRepetitionsList.all { it == targetMinimumRepetitionsList.first() })
        targetMinimumRepetitionsList.first()
      else
        return Result.failure(IllegalArgumentException("All target repetitions must be the same"))

    var targetMaximumRepetitions =
      if (targetMaximumRepetitionsList.all { it == targetMaximumRepetitionsList.firstOrNull() })
        targetMaximumRepetitionsList.first()
      else
        return Result.failure(IllegalArgumentException("All target repetitions must be the same"))

    if (
      weightPoundsList != targetWeightPoundsList && weightKilogramsList != targetWeightKilogramsList
    ) {
      return Result.failure(
        IllegalArgumentException("Actual weight performed do not match target values")
      )
    }

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

    // Update setsToUpdate with new values
    setsToUpdate.zip(repetitionsList).forEach { (set, rep) ->
      set.targetRepetitions = rep.toString()
    }
    setsToUpdate.zip(weightPoundsList).forEach { (set, weight) -> set.targetWeightPounds = weight }
    setsToUpdate.zip(weightKilogramsList).forEach { (set, weight) ->
      set.targetWeightKilograms = weight
    }
  }

  return Result.success(setsToUpdate)
}
