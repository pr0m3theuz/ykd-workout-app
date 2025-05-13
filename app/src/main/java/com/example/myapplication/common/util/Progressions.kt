package com.example.myapplication.common.util

import com.example.myapplication.common.domain.model.Set

// TODO update the next workout
//  add screen that asks the user to confirm the changes/progression applied to the next workout
//  allow user to edit and confirm the next workout. Show the just completed workout next to the
// suggested next workout.

// TODO handle case when the completed sets is less than the sets to be updated in next workout.
//  Repeat that workout's targets for that exercise. Get the original workout before saving the
//  completed workout and use the targets from the original workout. Ask the user to confirm if they
//  want to repeat the original workout or proceed with updating the next workout based on the
//  completed workout

enum class ProgressionType(val progression: String) {
  LINEAR_PROGRESSION("linear"),
  DOUBLE_PROGRESSION("double"),
  DYNAMIC_DOUBLE_PROGRESSION("dynamic double"),
  WAVE_PROGRESSION("wave"),
  AMRAP_PROGRESSION("amrap");

  companion object {
    fun fromProgression(progression: String): ProgressionType? =
      entries.find { it.progression == progression }
  }
}

/**
 * Returns a progression function based on the given [ProgressionType].
 *
 * This function maps each [ProgressionType] to its corresponding progression function. It provides
 * a type-safe way to retrieve the appropriate function for a given progression type.
 *
 * @param progressionType The type of progression for which to retrieve the function.
 * @return A function of type `() -> Unit` representing the progression logic.
 * @throws IllegalArgumentException if an unknown [ProgressionType] is provided.
 * @sample // Direct usage with ProgressionType val linearProgressionFunc =
 *   selectProgression(ProgressionType.LINEAR_PROGRESSION) linearProgressionFunc()
 *
 * // Usage with string-based lookup val progressionType = ProgressionType.fromProgression("linear")
 * progressionType?.let { selectProgression(it) }?.invoke()
 *
 * @see ProgressionType
 * @see ProgressionType.fromProgression
 */
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

/**
 * Implements a linear progression algorithm for weightlifting sets.
 *
 * This function takes a list of completed [Set] objects and a mutable list of [Set] objects to update.
 * It analyzes the completed sets to determine if the lifter met, exceeded, or missed their targets.
 * Based on the outcome, it adjusts the target repetitions and weights for the sets in `setsToUpdate`
 * according to a linear progression strategy.
 *
 * **Progression Logic:**
 * 1. **Target Achieved/Exceeded:** If all sets met or exceeded their target repetitions and weights, the function
 *    increments the target weights (both in pounds and kilograms) for all sets by the specified increment values
 *    (incrementWeightPounds, incrementWeightKilograms). Repetitions are reset to the initial target.
 * 2. **Target Missed:** If any set failed to reach its target repetitions or weights, all sets' target values are reset
 *    to their original target values.
 * 3. **Initial State** If all the current values for repetitions and weights match the target values, the weights will be incremented
 *
 * **Assumptions:**
 * - The input `sets` list contains at least one element.
 * - All sets in `sets` have non-null `repetitions`, `weightPounds`, `weightKilograms`, `incrementWeightPounds`, and `incrementWeightKilograms` properties.
 * - All sets have a corresponding target: `targetRepetitions`, `targetWeightPounds`, and `targetWeightKilograms`
 * - The `incrementWeightPounds` and `incrementWeightKilograms` are consistent across all sets in the list.
 *
 * **Error Handling:**
 * - The function returns a `Result.success` containing the updated `setsToUpdate` list if the progression was successful.
 * -  If the targetMin/MaxRepetitions are not all the same, the function will return success but will not affect the progression of weights.
 *
 * @param previousCompletedSets A list of [Set] objects representing the completed sets. Each set should have
 *             `repetitions`, `weightPounds`, `weightKilograms`, `incrementWeightPounds`,
 *             `incrementWeightKilograms`, `targetMinimumRepetitions`, `targetMaximumRepetitions`,
 *              `targetRepetitions`, */
fun linearProgression(
  previousCompletedSets: List<Set>,
  _setsToUpdate: MutableList<Set>
): Result<MutableList<Set>> {
  val setsToUpdate = _setsToUpdate.toMutableList()

  var maxRepetitionsCompleted =
    previousCompletedSets.maxByOrNull { it.repetitions!! }?.repetitions ?: 0
  var lowestWeightPoundsPerformed =
    previousCompletedSets.minByOrNull { it.weightPounds!! }?.weightPounds ?: 0.0
  var lowestWeightKilogramsPerformed =
    previousCompletedSets.minByOrNull { it.weightKilograms!! }?.weightPounds ?: 0.0

  val incrementWeightPounds = previousCompletedSets.mapNotNull { it.incrementWeightPounds }.first()
  val incrementWeightKilograms =
    previousCompletedSets.mapNotNull { it.incrementWeightKilograms }.first()

  val repetitionsList = previousCompletedSets.mapNotNull { it.repetitions }.toMutableList()
  val weightPoundsList = previousCompletedSets.mapNotNull { it.weightPounds }.toMutableList()
  val weightKilogramsList = previousCompletedSets.mapNotNull { it.weightKilograms }.toMutableList()

  var targetMinimumRepetitionsList =
    previousCompletedSets.mapNotNull { it.targetMinimumRepetitions }
  var targetMaximumRepetitionsList =
    previousCompletedSets.mapNotNull { it.targetMaximumRepetitions }
  val targetRepetitionsList = previousCompletedSets.mapNotNull { it.targetRepetitions?.toInt() }
  val targetWeightPoundsList = previousCompletedSets.mapNotNull { it.targetWeightPounds }
  val targetWeightKilogramsList = previousCompletedSets.mapNotNull { it.targetWeightKilograms }

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
  setsToUpdate.zip(repetitionsList).forEach { (set, rep) -> set.targetRepetitions = rep }
  setsToUpdate.zip(weightPoundsList).forEach { (set, weight) -> set.targetWeightPounds = weight }
  setsToUpdate.zip(weightKilogramsList).forEach { (set, weight) ->
    set.targetWeightKilograms = weight
  }

  return Result.success(setsToUpdate)
}

/**
 * Implements a double progression scheme for weightlifting sets.
 *
 * This function takes a list of `Set` objects representing a completed workout and a mutable list
 * of `Set` objects to be updated for the next workout. It analyzes the performance of the completed
 * workout and adjusts the target repetitions and weights for the next workout based on the double
 * progression rules.
 *
 * **Double Progression Rules:**
 *
 * 1. **Repeat Week:** If any set's actual repetitions are below the target minimum or the actual
 *    weights performed do not equal the target weights, the target repetitions and weights are reset
 *    to their initial values. The workout needs to be repeated this week.
 * 2. **Increment Repetitions (Within Range):** If all sets' actual repetitions are within the target
 *    range (between target minimum and maximum), and actual weights equal target weights, the target
 *    repetition for the first set is incremented by 1.
 * 3. **Increment Target Repetitions:** If at least one set's actual repetitions meet or exceed the
 *    current target repetition, but are below the maximum target repetitions and actual weights equal
 *    target weights,  the target repetition for the first set that satisfies this condition is incremented by one.
 * 4. **Increment Weight (All Sets at Max Reps):** If all sets' actual repetitions reach or exceed the
 *    target maximum, and actual weights equal target weights, the target repetitions are reset to the
 *    target minimum, and the target weights (both pounds and kilograms) are incremented by their
 *    respective increment values.
 * 5. **Repeat Week (Fallback):** If none of the above conditions are met, the target repetitions and
 *    weights are reset to their initial values. This typically happens when there's an unexpected
 *    state.
 *
 * **Error Handling:**
 *
 * - The function returns a `Result.failure` with an `IllegalArgumentException` if the target
 *   minimum repetitions or target maximum repetitions are not the same across all sets.
 *
 * **Assumptions:**
 *
 * - All sets in the input `sets` list have the same target minimum and maximum repetitions.
 * - All sets in the input list have a defined `incrementWeightPounds` and `incrementWeightKilograms` */
fun doubleProgression(
  previousCompletedSets: List<Set>,
  _setsToUpdate: MutableList<Set>
): Result<MutableList<Set>> {
  val setsToUpdate = _setsToUpdate.toMutableList()

  var maxRepetitionsCompleted =
    previousCompletedSets.maxByOrNull { it.repetitions!! }?.repetitions ?: 0
  var lowestWeightPoundsPerformed =
    previousCompletedSets.minByOrNull { it.weightPounds!! }?.weightPounds ?: 0.0
  var lowestWeightKilogramsPerformed =
    previousCompletedSets.minByOrNull { it.weightKilograms!! }?.weightPounds ?: 0.0

  val incrementWeightPounds = previousCompletedSets.map { it.incrementWeightPounds!! }.first()
  val incrementWeightKilograms = previousCompletedSets.map { it.incrementWeightKilograms!! }.first()

  val repetitionsList = previousCompletedSets.map { it.repetitions!! }.toMutableList()
  val weightPoundsList = previousCompletedSets.map { it.weightPounds!! }.toMutableList()
  val weightKilogramsList = previousCompletedSets.map { it.weightKilograms!! }.toMutableList()

  val targetMinimumRepetitionsList = previousCompletedSets.map { it.targetMinimumRepetitions!! }
  val targetMaximumRepetitionsList = previousCompletedSets.map { it.targetMaximumRepetitions!! }
  val targetRepetitionsList = previousCompletedSets.mapNotNull { it.targetRepetitions?.toInt() }
  val targetWeightPoundsList = previousCompletedSets.mapNotNull { it.targetWeightPounds }
  val targetWeightKilogramsList = previousCompletedSets.mapNotNull { it.targetWeightKilograms }

  // Check if all target repetitions are the same
  val targetMinimumRepetitions =
    if (targetMinimumRepetitionsList.all { it == targetMinimumRepetitionsList.first() })
      targetMinimumRepetitionsList.first()
    else return Result.failure(IllegalArgumentException("All target repetitions must be the same"))

  val targetMaximumRepetitions =
    if (targetMaximumRepetitionsList.all { it == targetMaximumRepetitionsList.firstOrNull() })
      targetMaximumRepetitionsList.first()
    else return Result.failure(IllegalArgumentException("All target repetitions must be the same"))

  val actualWeightsPerformedEqualTargetWeight =
    (weightPoundsList == targetWeightPoundsList && weightKilogramsList == targetWeightKilogramsList)

  when {
    repetitionsList.any { it < targetMinimumRepetitions } ||
            !actualWeightsPerformedEqualTargetWeight -> {
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
    }) && actualWeightsPerformedEqualTargetWeight -> {

      for (index in repetitionsList.indices) {
        repetitionsList[index]++
        break
      }
    }
    // If actual repetitions are above target, increment target values
    repetitionsList.zip(targetRepetitionsList).any { (actual, target) ->
      (actual >= target) && (actual < targetMaximumRepetitions)
    } && actualWeightsPerformedEqualTargetWeight -> {

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
    // If all repetitions are at the maximum, reset to target values
    repetitionsList.all { it >= targetMaximumRepetitions } &&
            actualWeightsPerformedEqualTargetWeight -> {

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

  setsToUpdate.zip(repetitionsList).forEach { (set, rep) -> set.targetRepetitions = rep }
  setsToUpdate.zip(weightPoundsList).forEach { (set, weight) -> set.targetWeightPounds = weight }
  setsToUpdate.zip(weightKilogramsList).forEach { (set, weight) ->
    set.targetWeightKilograms = weight
  }

  return Result.success(setsToUpdate)
}

/**
 * Implements a dynamic double progression scheme for weightlifting sets.
 *
 * This function analyzes a list of completed `Set` objects, determines if a progression
 * in weight or repetitions is appropriate, and updates the provided `setsToUpdate` list
 * with the new target values for the next workout. The double progression involves:
 * 1. Increasing repetitions within a set until a maximum is reached.
 * 2. Increasing weight when the maximum repetitions are reached, resetting repetitions to a minimum.
 *
 * **Progression Rules:**
 * - If all sets completed this week have met or exceeded their target repetitions:
 *   - If any set has fewer than the target maximum repetitions, its repetitions are increased by 1.
 *   - If any set has reached the target maximum repetitions, its repetitions are reset to the target minimum,
 *     and its weight (in pounds and kilograms) is increased by the specified increment.
 * - If not all sets completed their target repetitions (at least one set has fewer repetitions than its target):
 *   - The sets are considered to have failed to progress, and the target repetitions and weights for the next week are set back to what they were.
 *
 * **Constraints:**
 * - All sets must have the same target minimum repetitions.
 * - All sets must have the same target maximum repetitions.
 * - `targetRepetitions`, `targetWeightPounds`, and `targetWeightKilograms` must be specified in each set, or this will return an error.
 *
 * @param previousCompletedSets A list of `Set` objects representing the completed sets from the current workout.
 *             Each `Set` must have the following properties populated:
 *             - `repetitions`: The number of repetitions completed in this set.
 *             - `weightPounds`: The weight used in pounds for this set.
 *             - `weightKilograms`: The weight used in kilograms for this set.
 *             - `incrementWeightPounds`: The amount to increase weight in pounds if progression occurs.
 *             - `incrementWeightKilograms`: The amount to increase weight in kilograms if progression occurs.
 *             - `targetMinimumRepetitions`: The minimum number of repetitions for a given set
 *             - `targetMaximumRepetitions`: The maximum number of repetitions for a given set
 *             - `targetRepetitions`: The targeted number of repetitions for */
fun dynamicDoubleProgression(
  previousCompletedSets: List<Set>,
  _setsToUpdate: MutableList<Set>,
): Result<MutableList<Set>> {
  val setsToUpdate = _setsToUpdate.toMutableList()

  var maxRepetitionsCompleted =
    previousCompletedSets.maxByOrNull { it.repetitions!! }?.repetitions ?: 0
  var lowestWeightPoundsPerformed =
    previousCompletedSets.minByOrNull { it.weightPounds!! }?.weightPounds ?: 0.0
  var lowestWeightKilogramsPerformed =
    previousCompletedSets.minByOrNull { it.weightKilograms!! }?.weightPounds ?: 0.0

  val incrementWeightPounds = previousCompletedSets.map { it.incrementWeightPounds!! }.first()
  val incrementWeightKilograms = previousCompletedSets.map { it.incrementWeightKilograms!! }.first()

  var repetitionsList = previousCompletedSets.map { it.repetitions!! }.toMutableList()
  var weightPoundsList = previousCompletedSets.map { it.weightPounds!! }.toMutableList()
  var weightKilogramsList = previousCompletedSets.map { it.weightKilograms!! }.toMutableList()

  val targetMinimumRepetitionsList = previousCompletedSets.map { it.targetMinimumRepetitions!! }
  val targetMaximumRepetitionsList = previousCompletedSets.map { it.targetMaximumRepetitions!! }

  val targetRepetitionsList = previousCompletedSets.mapNotNull { it.targetRepetitions?.toInt() }
  val targetWeightPoundsList = previousCompletedSets.mapNotNull { it.targetWeightPounds }
  val targetWeightKilogramsList = previousCompletedSets.mapNotNull { it.targetWeightKilograms }

  // Check if all target repetitions are the same
  val targetMinimumRepetitions =
    if (targetMinimumRepetitionsList.all { it == targetMinimumRepetitionsList.first() })
      targetMinimumRepetitionsList.first()
    else return Result.failure(IllegalArgumentException("All target repetitions must be the same"))

  val targetMaximumRepetitions =
    if (targetMaximumRepetitionsList.all { it == targetMaximumRepetitionsList.firstOrNull() })
      targetMaximumRepetitionsList.first()
    else return Result.failure(IllegalArgumentException("All target repetitions must be the same"))

  val actualWeightsPerformedEqualTargetWeight =
    (weightPoundsList == targetWeightPoundsList && weightKilogramsList == targetWeightKilogramsList)

  if (repetitionsList.zip(targetRepetitionsList).all { (actual, target) -> (actual >= target) }) {
    repetitionsList.forEachIndexed { index, repetition ->
      if (
        repetition >= targetMinimumRepetitions &&
        weightPoundsList.zip(targetWeightPoundsList).all { (actual, target) -> actual >= target }
      ) {
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
      }
    }
  } else {
    // repeat this week
    repetitionsList = previousCompletedSets.map { it.targetRepetitions!!.toInt() }.toMutableList()
    weightPoundsList = previousCompletedSets.map { it.targetWeightPounds!! }.toMutableList()
    weightKilogramsList = previousCompletedSets.map { it.targetWeightKilograms!! }.toMutableList()
  }

  // Update setsToUpdate with new values
  setsToUpdate.zip(repetitionsList).forEach { (set, rep) -> set.targetRepetitions = rep }
  setsToUpdate.zip(weightPoundsList).forEach { (set, weight) -> set.targetWeightPounds = weight }
  setsToUpdate.zip(weightKilogramsList).forEach { (set, weight) ->
    set.targetWeightKilograms = weight
  }

  return Result.success(setsToUpdate)
}

/**
 * Implements a wave progression scheme for weightlifting sets.
 *
 * This function analyzes a list of completed `Set` objects from a workout and determines the target
 * repetitions and weights for the next workout based on a wave progression pattern. The core idea
 * is to cycle between higher repetitions at lighter weights and lower repetitions at heavier weights,
 * gradually increasing the load.
 *
 * **Wave Progression Logic:**
 *
 * 1. **Repeat Week:** If the completed repetitions are not consistent across all sets, or if any set's
 *    repetitions are outside the target minimum and maximum range, the target repetitions and weights
 *    for the next week are reset to the original target values, and the workout should be repeated.
 * 2. **Increase Weight and Decrease Reps:** If all sets were completed with the same number of repetitions
 *    and that number was greater than the target minimum, the target repetitions for the next week are
 *    decreased by 1, and the target weight (in pounds and kilograms) is increased by the specified
 *    increment.
 * 3. **Decrease Weight and Increase Reps:** If all sets were completed with the same number of repetitions
 *    and that number was equal to the target minimum, the target repetitions for the next week are
 *    increased to the target maximum, and the target weight (in pounds and kilograms) is decreased by
 *    the specified increment.
 * 4. **Repeat Week (Fallback):** If none of the above conditions are met, the target repetitions and
 *    weights are reset to their initial values. This typically happens when there's an unexpected
 *    state.
 *
 * **Error Handling:**
 *
 * - The function returns a `Result.failure` with an `IllegalArgumentException` if the target
 *   minimum repetitions or target maximum repetitions are not the same across all sets.
 *
 * **Assumptions:**
 *
 * - All sets in the input `sets` list have the same target minimum and maximum repetitions.
 * - All sets in the input list have a defined `incrementWeightPounds` and `incrementWeightKilograms`.
 *
 * @param previousCompletedSets A list of [Set] objects representing the completed sets from the current workout.
 *                           Each `Set` must have the following properties populated:
 */
fun waveProgression(
  previousCompletedSets: List<Set>,
  _setsToUpdate: MutableList<Set>
): Result<MutableList<Set>> {
  val setsToUpdate = _setsToUpdate.toMutableList()

  var maxRepetitionsCompleted =
    previousCompletedSets.maxByOrNull { it.repetitions!! }?.repetitions ?: 0
  var lowestWeightPoundsPerformed =
    previousCompletedSets.minByOrNull { it.weightPounds!! }?.weightPounds ?: 0.0
  var lowestWeightKilogramsPerformed =
    previousCompletedSets.minByOrNull { it.weightKilograms!! }?.weightPounds ?: 0.0

  val incrementWeightPounds = previousCompletedSets.map { it.incrementWeightPounds!! }.first()
  val incrementWeightKilograms = previousCompletedSets.map { it.incrementWeightKilograms!! }.first()

  var repetitionsList = previousCompletedSets.map { it.repetitions!! }.toMutableList()
  var weightPoundsList = previousCompletedSets.map { it.weightPounds!! }.toMutableList()
  var weightKilogramsList = previousCompletedSets.map { it.weightKilograms!! }.toMutableList()

  val targetMinimumRepetitionsList = previousCompletedSets.map { it.targetMinimumRepetitions!! }
  val targetMaximumRepetitionsList = previousCompletedSets.map { it.targetMaximumRepetitions!! }

  var targetRepetitionsList = previousCompletedSets.mapNotNull { it.targetRepetitions?.toInt() }
  var targetWeightPoundsList = previousCompletedSets.mapNotNull { it.targetWeightPounds }
  var targetWeightKilogramsList = previousCompletedSets.mapNotNull { it.targetWeightKilograms }

  // Check if all target repetitions are the same
  val targetMinimumRepetitions =
    if (targetMinimumRepetitionsList.all { it == targetMinimumRepetitionsList.first() })
      targetMinimumRepetitionsList.first()
    else return Result.failure(IllegalArgumentException("All target repetitions must be the same"))

  val targetMaximumRepetitions =
    if (targetMaximumRepetitionsList.all { it == targetMaximumRepetitionsList.firstOrNull() })
      targetMaximumRepetitionsList.first()
    else return Result.failure(IllegalArgumentException("All target repetitions must be the same"))

  if (
    !(repetitionsList.all { it == repetitionsList.firstOrNull() }) ||
      repetitionsList.any { it > targetMaximumRepetitions } ||
      repetitionsList.any { it < targetMinimumRepetitions }
  ) {
    // repeat this week
    repetitionsList = previousCompletedSets.map { it.targetRepetitions!!.toInt() }.toMutableList()
    weightPoundsList = previousCompletedSets.map { it.targetWeightPounds!! }.toMutableList()
    weightKilogramsList = previousCompletedSets.map { it.targetWeightKilograms!! }.toMutableList()
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
        repetitionsList =
          previousCompletedSets.map { it.targetRepetitions!!.toInt() }.toMutableList()
        weightPoundsList = previousCompletedSets.map { it.targetWeightPounds!! }.toMutableList()
        weightKilogramsList =
          previousCompletedSets.map { it.targetWeightKilograms!! }.toMutableList()
      }
    }
  }

  // Update setsToUpdate with new values
  setsToUpdate.zip(repetitionsList).forEach { (set, rep) -> set.targetRepetitions = rep }
  setsToUpdate.zip(weightPoundsList).forEach { (set, weight) -> set.targetWeightPounds = weight }
  setsToUpdate.zip(weightKilogramsList).forEach { (set, weight) ->
    set.targetWeightKilograms = weight
  }

  return Result.success(setsToUpdate)
}

/**
 * Calculates and updates the progression of an AMRAP (As Many Reps As Possible) training program.
 *
 * This function takes a list of [Set] objects representing the current week's performance and a mutable list
 * of [Set] objects representing the next week's planned sets. It then analyzes the current week's performance
 * against the defined targets to determine if the weight should be increased for the next week.
 *
 * **Progression Logic:**
 *
 * 1. **Consistency Check:** Verifies that all sets have the same target minimum and maximum repetitions.
 *    Returns a failure `Result` if they are inconsistent.
 * 2. **Target Weight Check:** Confirms that the actual weight performed matches the target weight for each set.
 *    Returns a failure `Result` if the actual weight and target weight are not the same.
 * 3. **Repetition and Weight Adjustment:**
 *    - **If all sets met the minimum target repetitions:**
 *      - **Double Increment:** If any set exceeded the minimum repetitions by double, increment the target weight
 *        by twice the `incrementWeightPounds`.
 *      - **Single Increment:** If any set exceeded the minimum target repetitions but did not double it, increment
 *        the target weight by `incrementWeightPounds`.
 *    - **If not all sets met the minimum target repetitions:**
 *      - Reset the target repetitions and weight to the original target values, signaling a need to repeat the
 *        current week.
 * 4. **Update Sets:** The `setsToUpdate` list is modified in place to reflect the new target repetitions and
 *    weights (pounds and kilograms).
 *
 * **Parameters:**
 * - `sets`: A `List<Set>` representing the completed sets for the current week. Each `Set` object should contain:
 *   - `repetitions`: The actual number of repetitions performed (nullable).
 *   - `weightPounds`: The actual weight used in pounds (nullable).
 *   - `weightKilograms`: The actual weight used in kilograms (nullable).
 *   - `targetWeightPounds`: The target weight for the set in pounds (nullable).
 *   - `targetWeightKilograms`: The target weight for the set in kilograms (nullable).
 *   - `incrementWeightPounds`: The amount to increment the weight by in */
fun amrapProgression(
  previousCompletedSets: List<Set>,
  _setsToUpdate: MutableList<Set>
): Result<MutableList<Set>> {

  val setsToUpdate = _setsToUpdate.toMutableList()

  var maxRepetitionsCompleted =
    previousCompletedSets.maxByOrNull { it.repetitions!! }?.repetitions ?: 0
  var lowestWeightPoundsPerformed =
    previousCompletedSets.minByOrNull { it.weightPounds!! }?.weightPounds ?: 0.0
  var lowestWeightKilogramsPerformed =
    previousCompletedSets.minByOrNull { it.weightKilograms!! }?.weightPounds ?: 0.0

  val targetWeightPounds = previousCompletedSets.map { it.targetWeightPounds!! }.first()
  var targetWeightKilograms = previousCompletedSets.map { it.targetWeightKilograms!! }.first()

  val incrementWeightPounds = previousCompletedSets.map { it.incrementWeightPounds!! }.first()
  val incrementWeightKilograms = previousCompletedSets.map { it.incrementWeightKilograms!! }.first()

  var repetitionsList = previousCompletedSets.map { it.repetitions!! }.toMutableList()
  var weightPoundsList = previousCompletedSets.map { it.weightPounds!! }.toMutableList()
  var weightKilogramsList = previousCompletedSets.map { it.weightKilograms!! }.toMutableList()

  val targetMinimumRepetitionsList = previousCompletedSets.map { it.targetMinimumRepetitions!! }
  val targetMaximumRepetitionsList = previousCompletedSets.map { it.targetMaximumRepetitions!! }

  var targetRepetitionsList = previousCompletedSets.mapNotNull { it.targetRepetitions!! }
  val targetWeightPoundsList = previousCompletedSets.mapNotNull { it.targetWeightPounds!! }
  val targetWeightKilogramsList = previousCompletedSets.mapNotNull { it.targetWeightKilograms!! }

  // Check if all target repetitions are the same
  val targetMinimumRepetitions =
    if (targetMinimumRepetitionsList.all { it == targetMinimumRepetitionsList.first() })
      targetMinimumRepetitionsList.first()
    else return Result.failure(IllegalArgumentException("All target repetitions must be the same"))

  var targetMaximumRepetitions =
    if (targetMaximumRepetitionsList.all { it == targetMaximumRepetitionsList.firstOrNull() })
      targetMaximumRepetitionsList.first()
    else return Result.failure(IllegalArgumentException("All target repetitions must be the same"))

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
    repetitionsList = previousCompletedSets.map { it.targetRepetitions!!.toInt() }.toMutableList()
    weightPoundsList = previousCompletedSets.map { it.targetWeightPounds!! }.toMutableList()
    weightKilogramsList = previousCompletedSets.map { it.targetWeightKilograms!! }.toMutableList()
  }

  // Update setsToUpdate with new values
  setsToUpdate.zip(repetitionsList).forEach { (set, rep) -> set.targetRepetitions = rep }
  setsToUpdate.zip(weightPoundsList).forEach { (set, weight) -> set.targetWeightPounds = weight }
  setsToUpdate.zip(weightKilogramsList).forEach { (set, weight) ->
    set.targetWeightKilograms = weight
  }

  return Result.success(setsToUpdate)
}
