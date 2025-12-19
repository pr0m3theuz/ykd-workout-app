@file:OptIn(ExperimentalUuidApi::class, ExperimentalTime::class)

package com.example.myapplication.common.domain.model

import com.example.myapplication.common.util.ProgressionOutcome
import com.example.myapplication.common.util.ProgressionResults
import kotlin.time.ExperimentalTime
import kotlin.uuid.ExperimentalUuidApi

// Wrapper for a Set to hold its progression state for the preview
data class PreviewSet(
  val previousSet: Set? = null,
  val originalSet: Set,       // The set *before* progression was applied
  val progressedSet: Set,     // The set *after* progression logic ran
  val outcome: ProgressionOutcome = ProgressionOutcome.PENDING // Default or determined by logic
) {
  val wasModified: Boolean
    get() = outcome == ProgressionOutcome.PROGRESSED &&
            (originalSet.targetWeightPounds != progressedSet.targetWeightPounds ||
                    originalSet.targetRepetitions != progressedSet.targetRepetitions)
}

// Wrapper for ExerciseSets to hold its overall outcome and preview sets
data class PreviewExerciseSets @OptIn(ExperimentalUuidApi::class) constructor(
  val originalItem: ExerciseSets, // TODO Change to ExerciseExerciseSets The item before progression
  val previewSets: List<PreviewSet>, // List of sets with before/after state
  val overallOutcome: ProgressionOutcome, // Derived from individual set outcomes
  val isProgressionUniform: Boolean = checkIfProgressionIsUniform(previewSets), // Calculate this on creation
  val progressionResultMessage: String? // TODO
)

// Wrapper for Superset
data class PreviewSuperset(
  val originalItem: Superset, // TODO Change to SupersetExerciseSets
  val previewExerciseSets: List<PreviewExerciseSets> // Nested preview items
)

// Helper function to create Preview structures *after* running progression logic
// If your ExerciseSets.id is a UUID or needs this
fun createWorkoutPreview(
  previousWorkout: Workout,
  originalWorkout: Workout,
  // Key: Corresponds to ExerciseSets.itemId (or exSets.itemId for superset's inner exercises)
  // Value: The result of running progression logic for that specific ExerciseSets
  progressionResultsMap: Map<String, ProgressionResults>
): List<Any> { // Returns List<PreviewExerciseSets | PreviewSuperset | WorkoutItem (for unhandled types)>
  return originalWorkout.workoutItems.map { originalItem ->
    when (originalItem) {
      is ExerciseSets -> {
        val previousExerciseSets =
          previousWorkout.workoutItems.find { (it as? ExerciseSets)?.exerciseID == originalItem.exerciseID } as? ExerciseSets

        // Fetch the ProgressionResults for this specific ExerciseSets item
        val progressionResultForThisItem =
          progressionResultsMap[originalItem.id.toString()] // Use itemId

        val previewSets = mutableListOf<PreviewSet>()
        var overallExerciseOutcome: ProgressionOutcome

        if (progressionResultForThisItem != null) {
          // Use the outcome from ProgressionResults
          overallExerciseOutcome =
            progressionResultForThisItem.outcome ?: ProgressionOutcome.NULL

          val progressedSetsPlan =
            progressionResultForThisItem.currentSets // This is MutableList<Set>?

          if (progressedSetsPlan != null && (overallExerciseOutcome == ProgressionOutcome.PROGRESSED || overallExerciseOutcome == ProgressionOutcome.UNCHANGED)) {
            // Progression was attempted and didn't outright fail (could be PROGRESSED or UNCHANGED)
            originalItem.sets.forEachIndexed { index, originalSet ->
              val progressedSet = progressedSetsPlan.getOrNull(index)
                ?: originalSet // Fallback to original if list sizes differ
              val setOutcome = overallExerciseOutcome

              previewSets.add(
                PreviewSet(
                  previousExerciseSets?.sets?.getOrNull(index),
                  originalSet,
                  progressedSet,
                  setOutcome
                )
              )
            }
            // If overall outcome was UNCHANGED but some sets were still different (e.g. rounding), mark as PROGRESSED
            if (overallExerciseOutcome == ProgressionOutcome.UNCHANGED && previewSets.any { it.wasModified }) {
              overallExerciseOutcome = ProgressionOutcome.PROGRESSED
            } else if (overallExerciseOutcome == ProgressionOutcome.PROGRESSED && previewSets.none { it.wasModified }) {
              // If overall was PROGRESSED but no set actually changed, it's effectively UNCHANGED
              overallExerciseOutcome = ProgressionOutcome.UNCHANGED
            }

          } else {
            // Progression failed, was rejected, or not applicable (currentSets is null), or outcome is FAILED/REJECTED/PENDING
            // So, use original sets for preview, and the outcome from ProgressionResults
            originalItem.sets.forEachIndexed { index, originalSet ->
              previewSets.add(
                PreviewSet(
                  previousExerciseSets?.sets?.getOrNull(index),
                  originalSet,
                  originalSet,
                  overallExerciseOutcome
                )
              )
            }
          }
        } else {
          // No progression result found for this item, treat as PENDING
          overallExerciseOutcome = ProgressionOutcome.NULL
          originalItem.sets.forEachIndexed { index, originalSet ->
            previewSets.add(
              PreviewSet(
                previousExerciseSets?.sets?.getOrNull(index),
                originalSet,
                originalSet,
                overallExerciseOutcome
              )
            )
          }
        }
        val isUniform = checkIfProgressionIsUniform(previewSets)
        PreviewExerciseSets(
          // Pass a copy of original item
          originalItem,
          previewSets,
          overallExerciseOutcome,
          isUniform,
          progressionResultMessage = progressionResultForThisItem?.message
        )
      }

      is Superset -> {
        val previewInnerExercises = originalItem.exercisesSets.map { originalInnerExSets ->
          val previousInnerExSets =
            previousWorkout.workoutItems.find { (it as? ExerciseSets)?.exerciseID == originalInnerExSets.exerciseID } as? ExerciseSets

          val progressionResultForInnerItem =
            progressionResultsMap[originalInnerExSets.id.toString()] // Use itemId
          val innerPreviewSets = mutableListOf<PreviewSet>()
          var innerExerciseOutcome: ProgressionOutcome

          if (progressionResultForInnerItem != null) {
            innerExerciseOutcome =
              progressionResultForInnerItem.outcome ?: ProgressionOutcome.NULL
            val progressedInnerSetsPlan = progressionResultForInnerItem.currentSets

            if (progressedInnerSetsPlan != null && (innerExerciseOutcome == ProgressionOutcome.PROGRESSED || innerExerciseOutcome == ProgressionOutcome.UNCHANGED)) {
              originalInnerExSets.sets.forEachIndexed { index, originalSet ->
                val progressedSet = progressedInnerSetsPlan.getOrNull(index) ?: originalSet
                val setOutcome =
                  determineSetOutcome(originalSet, progressedSet, innerExerciseOutcome)
                innerPreviewSets.add(
                  PreviewSet(
                    previousInnerExSets?.sets?.getOrNull(index),
                    originalSet,
                    progressedSet,
                    setOutcome
                  )
                )
              }
              if (innerExerciseOutcome == ProgressionOutcome.UNCHANGED && innerPreviewSets.any { it.wasModified }) {
                innerExerciseOutcome = ProgressionOutcome.PROGRESSED
              } else if (innerExerciseOutcome == ProgressionOutcome.PROGRESSED && innerPreviewSets.none { it.wasModified }) {
                innerExerciseOutcome = ProgressionOutcome.UNCHANGED
              }
            } else {
              originalInnerExSets.sets.forEachIndexed { index, originalSet ->
                innerPreviewSets.add(
                  PreviewSet(
                    previousInnerExSets?.sets?.getOrNull(index),
                    originalSet,
                    originalSet,
                    innerExerciseOutcome
                  )
                )
              }
            }
          } else {
            innerExerciseOutcome = ProgressionOutcome.NULL
            originalInnerExSets.sets.forEach { originalSet ->
              innerPreviewSets.add(
                PreviewSet(
                  previousInnerExSets?.sets?.getOrNull(0),
                  originalSet,
                  originalSet,
                  innerExerciseOutcome
                )
              )
            }
          }
          val isUniform = checkIfProgressionIsUniform(innerPreviewSets)
          PreviewExerciseSets(
            originalInnerExSets.deepCopy(),
            innerPreviewSets,
            innerExerciseOutcome,
            isUniform,
            progressionResultMessage = progressionResultForInnerItem?.message
          )
        }
        PreviewSuperset(
          originalItem.deepCopy(),
          previewInnerExercises
        ) // Pass a copy of original item
      }
    }
  }
}

// Helper function to determine uniform progression
private fun checkIfProgressionIsUniform(previewSets: List<PreviewSet>): Boolean {
  if (previewSets.isEmpty() || previewSets.none { it.wasModified }) {
    return true // Or false, depending on how you want to treat "no change"
    // If no change, it's technically uniform.
    // If only some changed, it's not uniform.
  }

  val firstModifiedSet = previewSets.firstOrNull { it.wasModified }
    ?: return true // No modified sets means uniform (no change)

  // Calculate the deltas for the first modified set
  val weightPoundsDelta = firstModifiedSet.previousSet?.weightPounds?.let {
    firstModifiedSet.progressedSet.targetWeightPounds?.minus(
      it
    )
  }
  val repsDelta = firstModifiedSet.previousSet?.repetitions?.let {
    firstModifiedSet.progressedSet.targetRepetitions?.minus(
      it
    )
  }
  // TODO You could also check targetRepsMin/Max, RPE, RIR deltas if they are part of your progression

  // Check if all other *modified* sets have the same deltas
  return previewSets.filter { it.wasModified }.all {
    (it.previousSet?.weightPounds?.let { it1 -> it.progressedSet.targetWeightPounds?.minus(it1) }) == weightPoundsDelta &&
            (it.previousSet?.repetitions?.let { it1 -> it.progressedSet.targetRepetitions?.minus(it1) }) == repsDelta
    // TODO other delta checks...
  }
}

/**
 * Helper to determine the outcome for an individual set within a Preview.
 */
private fun determineSetOutcome(
  originalSet: Set,
  progressedSet: Set,
  overallExerciseOutcome: ProgressionOutcome
): ProgressionOutcome {
  return when (overallExerciseOutcome) {
    ProgressionOutcome.FAILED, ProgressionOutcome.REJECTED, ProgressionOutcome.PENDING, ProgressionOutcome.NULL -> overallExerciseOutcome
    ProgressionOutcome.PROGRESSED, ProgressionOutcome.UNCHANGED -> {
      if (originalSet.weightPounds != progressedSet.weightPounds || originalSet.repetitions != progressedSet.repetitions) {
        // Also consider if targetRepsMin/Max changed if those are part of progression
        // For simplicity, just weight and reps for "modified"
        ProgressionOutcome.PROGRESSED
      } else {
        ProgressionOutcome.UNCHANGED
      }
    }
  }
}

// Add deepCopy methods to your data classes if they don't have them,
// or ensure your existing deepCopy() handles them correctly.
// This is important so that the `originalItem` in PreviewExerciseSets/PreviewSuperset
// truly represents the state *before* any modifications for the preview.

fun ExerciseSets.deepCopy(): ExerciseSets {
  return this.copy(
    sets = this.sets.map { it.copy() }.toMutableList()
  )
}

fun Superset.deepCopy(): Superset {
  return this.copy(
    exercisesSets = this.exercisesSets.map { it.deepCopy() }.toMutableList()
  )
}

// Helper for deep copying workout (you might have a better way)
@OptIn(ExperimentalUuidApi::class)
fun Workout.deepCopy(): Workout {
  // Implement proper deep copy based on your data structure details
  // This is a placeholder
  return this.copy(
    workoutItems = this.workoutItems.map { item ->
      when (item) {
        is ExerciseSets -> item.copy(sets = item.sets.map { it.copy() }.toMutableList())
        is Superset -> item.copy(exercisesSets = item.exercisesSets.map { ex ->
          ex.copy(sets = ex.sets.map { it.copy() }.toMutableList())
        }.toMutableList())
      }
    }.toMutableList()
  )
}

