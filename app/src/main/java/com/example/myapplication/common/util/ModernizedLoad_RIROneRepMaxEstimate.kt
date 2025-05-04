package com.example.myapplication.common.util

import com.example.myapplication.common.domain.model.Exercise
import java.math.RoundingMode
import kotlin.math.max
import kotlin.math.min

// Enum to represent the determined rep capacity category.
// Enum to represent different rep capacities based on the provided charts.
enum class RepCapacity {
  AVERAGE, HIGH, LOW
}

data class LoadRepCapacityDao(var LoadRepCapacity: LoadRepCapacity)

data class LoadRepCapacity(
  val loadPercent: Double, // Relative Load as a percentage (e.g., 80.0)
  val lowRepsRounded: Int,  // Rounded lower bound of the expected rep range
  val highRepsRounded: Int // Rounded upper bound of the expected rep range
)

// Data structure to hold the percentage of 1RM for different rep capacities.
// This data is assumed to be loaded and available from your database/storage.
// The structure is a Map where the key is the RepCapacity enum, and the value
// is another Map. This inner Map has the RIR (Reps in Reserve) as the key
// and a Map of Reps Performed to the corresponding Percentage of 1RM (as a Double).
data class ModernizedLoadRIRDao(var ModernizedLoadRIR: ModernizedLoadRIR)

data class ModernizedLoadRIR(
  val repCapacity: RepCapacity,
  val rir: Double, // Relative Load as a percentage (e.g., 80.0)
  val rpe: Double, // Relative Load as a percentage (e.g., 80.0)
  val repsPerformed: Int, // Relative Load as a percentage (e.g., 80.0)
  val relativeLoadPercent: Double, // Relative Load as a percentage (e.g., 80.0)
)

/**
 * Estimates the 1 Rep Max (1RM) based on weight lifted, repetitions, RIR, and rep capacity.
 *
 * This function uses data from the "Modernized Load/RIR Chart" from Data Driven Strength
 * and Nuzzo, et al. (2023) to find the estimated percentage of 1RM for the given reps and RIR,
 * and then calculates the 1RM.
 * It includes linear interpolation for reps and RIR values that fall between the chart data points.
 *
 * @param weight The weight lifted (e.g., in kg or lbs).
 * @param reps The number of repetitions performed.
 * @param rir The Reps in Reserve (0 for failure, 1 for 1 rep left, etc.).
 * @param repCapacity The user's rep capacity type (AVERAGE, HIGH, or LOW). Defaults to AVERAGE.
 * @return The estimated 1RM as a Double, or null if the estimation is not possible
 * (e.g., invalid input or chart data not found).
 */
fun estimateOneRepMaxModernized(
  weight: Double,
  reps: Int,
  rir: Double,
  rpe: Double,
  repCapacity: RepCapacity = RepCapacity.AVERAGE,
  getRelativeLoadPercentage: (RepCapacity, Double, Double, Int) -> Double?,
): Double? {
  // Get the percentage of 1RM from the selected chart with interpolation
  val percentage = getRelativeLoadPercentage(
    repCapacity,
    rir,
    rpe,
    reps
  )// ?.relativeLoadPercent

  // Calculate estimated 1RM if percentage is found and is greater than 0
  return if (percentage != null && percentage > 0) {
    weight.div(percentage)
  } else {
    null // Unable to estimate 1RM
  }
}

/**
 * Helper function to convert RPE (Rating of Perceived Exertion) to RIR (Reps in Reserve).
 *
 * The RPE scale used in the provided charts is typically 10 - RIR.
 *
 * @param rpe The RPE value (typically from 0 to 10).
 * @return The corresponding RIR value. Returns 0 for RPE >= 10, and 10 for RPE <= 0.
 */
fun rpeToRIR(rpe: Double): Double {
  return max(0.0, min(10.0, 10.0 - rpe)) // Ensure RIR is within a reasonable range [0, 10]
}

/**
 * Helper function to convert RIR (Reps in Reserve) to RPE (Rating of Perceived Exertion).
 *
 * The RIR scale used in the provided charts is typically 10 - RIR.
 *
 * @param rir The RIR value (typically from 0 to 10).
 * @return The corresponding RIR value. Returns 0 for RIR >= 10, and 10 for RIR <= 0.
 */
fun rirToRPE(rir: Double): Double {
  return max(0.0, min(10.0, 10.0 - rir)) // Ensure RPE is within a reasonable range [0, 10]
}

/**
 * Determines the rep capacity (Low, Average, High) based on the relative load and actual reps performed to failure.
 *
 * Uses data from the parsed Setup CSV table to find the expected rep range for a given
 * relative load and compares the actual reps performed to this range.
 * This function assumes the `repCapacityLookupTable` is already loaded and sorted by loadPercent.
 *
 * @param relativeLoadPercent The percentage of 1RM lifted (e.g., 80.0 for 80%). Must be between 0 and 100.
 * @param actualRepsPerformed The actual number of repetitions performed to failure. Must be non-negative.
 * @param getRepCapacity A function to retrieve the rep capacity lookup data for a given relative load.
 * @return The RepCapacityCategory (LOW_CAPACITY, AVERAGE_CAPACITY, HIGH_CAPACITY),
 * or null if the input is invalid or lookup fails (e.g., empty lookup table, load outside effective table range).
 */
fun determineRepCapacity(
  relativeLoadPercent: Double,
  actualRepsPerformed: Int,
  getRepCapacity: (Double) -> LoadRepCapacity?
): RepCapacity? {
  // Validate inputs
  if (relativeLoadPercent < 15 || relativeLoadPercent > 100 || actualRepsPerformed < 0) {
    println("Error: Invalid input for rep capacity determination.")
    return null // Invalid input or empty lookup table
  }

  // Find the entry in the table with the largest loadPercent less than or equal to the relativeLoadPercent.
  val relevantEntry: LoadRepCapacity? = getRepCapacity(
    relativeLoadPercent.toBigDecimal().setScale(2, RoundingMode.HALF_UP).toDouble()
  )

  // If no entry is found (shouldn't happen if loadPercent is >= min table load), return null.
  if (relevantEntry == null) {
    println("Warning: Relative load ($relativeLoadPercent%) is below the minimum table load. Using data for minimum load.")
    return null // Use the first entry's range
  }

  // Get the expected rounded rep range from the found entry
  val expectedLowReps = relevantEntry.lowRepsRounded
  val expectedHighReps = relevantEntry.highRepsRounded

  // Determine rep capacity based on actual reps performed and the expected range
  return when {
    actualRepsPerformed < expectedLowReps -> RepCapacity.LOW
    actualRepsPerformed > expectedHighReps -> RepCapacity.HIGH
    else -> RepCapacity.AVERAGE
  }
}

/**
 * Performs a simulated 1RM test based on an estimated 1RM and a test set to determine
 * and update the exercise's rep capacity.
 *
 * This function calculates the relative load of the test set and uses the rep capacity
 * lookup table to determine the exercise's rep capacity category. It then updates
 * the provided Exercise object.
 *
 * @param exercise The Exercise object to update.
 * @param estimated1RM The user's estimated 1RM for this exercise. Must be greater than 0.
 * @param testWeight The weight lifted during the test set to failure.
 * @param testRepsToFailure The number of repetitions performed to failure during the test set.
 * @param getRepCapacity A function to retrieve the rep capacity lookup data for a given relative load.
 * @return true if the rep capacity was successfully determined and updated, false otherwise.
 */
fun performRepCapacityTest(
  exercise: Exercise,
  estimated1RM: Double,
  testWeight: Double,
  testRepsToFailure: Int,
  getRepCapacity: (Double) -> LoadRepCapacity?
): Boolean {
  // Validate estimated 1RM to avoid division by zero
  if (estimated1RM <= 0) {
    println("Error: Estimated 1RM must be greater than 0 to perform a rep capacity test.")
    return false
  }

  // Calculate the relative load percentage of the test set
  val relativeLoadPercent = (testWeight / estimated1RM) * 100.0

  // Determine the rep capacity category
  val determinedCapacity = determineRepCapacity(
    relativeLoadPercent = relativeLoadPercent,
    actualRepsPerformed = testRepsToFailure,
    getRepCapacity = getRepCapacity
  )

  // Update the exercise's rep capacity if determined
  return if (determinedCapacity != null) {
    exercise.repCapacity = determinedCapacity
    println("Rep capacity for ${exercise.name} updated to ${exercise.repCapacity}.")
    // In a real application, you would save the updated exercise object to your database here.
    // saveExerciseToDatabase(exercise) // Example database call
    true
  } else {
    println("Failed to determine rep capacity for ${exercise.name}. Exercise data not updated.")
    false
  }
}
