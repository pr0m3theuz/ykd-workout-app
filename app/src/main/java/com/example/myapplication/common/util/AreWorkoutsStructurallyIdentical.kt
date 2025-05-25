@file:OptIn(ExperimentalUuidApi::class)

package com.example.myapplication.common.util

import com.example.myapplication.common.domain.model.ExerciseSets
import com.example.myapplication.common.domain.model.Superset
import com.example.myapplication.common.domain.model.Workout
import kotlin.uuid.ExperimentalUuidApi

/**
 * Compares two Workout objects for structural identity.
 *
 * Checks if they have the same sequence of exercises (or supersets),
 * with the same number of sets of the same type and progression scheme within each exercise.
 * Ignores specific target values (weight, reps, RIR/RPE) and actual performance values.
 *
 * @param w1 The first Workout object.
 * @param w2 The second Workout object.
 * @return True if the workouts are structurally identical, false otherwise.
 */
fun areWorkoutsStructurallyIdentical(w1: Workout, w2: Workout): Boolean {
  // 1. Compare the number of top-level items (Exercises or Supersets)
  if (w1.workoutItems.size != w2.workoutItems.size) {
    return false
  }

  // 2. Compare each item in sequence
  for (i in w1.workoutItems.indices) {
    val item1 = w1.workoutItems[i]
    val item2 = w2.workoutItems[i]

    // Check if items at the same position are of the same type
    if (item1::class != item2::class) {
      return false
    }

    // Compare based on type
    val structurallyIdentical = when (item1) {
      is ExerciseSets -> {
        // Ensure item2 is also ExerciseSets (already checked by class comparison)
        // and then compare their structure
        areExerciseSetsStructurallyIdentical(item1, item2 as ExerciseSets)
      }

      is Superset -> {
        // Ensure item2 is also Superset and compare their structure
        areSupersetsStructurallyIdentical(item1, item2 as Superset)
      }
    }

    // If any pair of items is not structurally identical, the workouts are not
    if (!structurallyIdentical) {
      return false
    }
  }

  // If all items match structurally
  return true
}

/**
 * Helper function to compare two ExerciseSets objects structurally.
 */
private fun areExerciseSetsStructurallyIdentical(es1: ExerciseSets, es2: ExerciseSets): Boolean {
  // 1. Compare Exercise ID
  if (es1.exerciseID != es2.exerciseID) {
    return false
  }

  // 2. Compare number of sets
  if (es1.sets.size != es2.sets.size) {
    return false
  }

  // 3. Compare each set's structural properties
  for (i in es1.sets.indices) {
    val set1 = es1.sets[i]
    val set2 = es2.sets[i]

    // Compare Set Type ID
    if (set1.setTypeID != set2.setTypeID) {
      return false
    }

    // Compare Progression Scheme ID (optional, include if it defines structure)
    if (set1.progressionSchemeID != set2.progressionSchemeID) {
      return false
    }

    // --- IGNORE specific targets/performance: ---
    // weightPounds, weightKilograms, repetitions, ratePerceivedEffort,
    // repetitionsInReserve, targetWeightPounds, targetWeightKilograms,
    // targetRepetitions, targetRatePerceivedEffort, targetRepetitionsInReserve,
    // percentage1RM, setCompleted, etc.
  }

  // If all sets match structurally
  return true
}

/**
 * Helper function to compare two Superset objects structurally.
 */
private fun areSupersetsStructurallyIdentical(s1: Superset, s2: Superset): Boolean {
  // 1. Compare number of exercises within the superset
  if (s1.exercisesSets.size != s2.exercisesSets.size) {
    return false
  }

  // 2. Compare each ExerciseSets within the superset structurally
  for (i in s1.exercisesSets.indices) {
    val innerEs1 = s1.exercisesSets[i]
    val innerEs2 = s2.exercisesSets[i]

    // Recursively call the structural comparison for ExerciseSets
    if (!areExerciseSetsStructurallyIdentical(innerEs1, innerEs2)) {
      return false
    }
  }

  // If all inner ExerciseSets match structurally
  return true
}