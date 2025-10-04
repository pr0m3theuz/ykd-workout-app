@file:OptIn(ExperimentalUuidApi::class, ExperimentalTime::class)

package com.example.myapplication.features.workout.feature.previewWorkout

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.myapplication.common.domain.model.ExerciseSets
import com.example.myapplication.common.domain.model.PreviewExerciseSets
import com.example.myapplication.common.domain.model.PreviewSuperset
import com.example.myapplication.common.domain.model.SetType
import com.example.myapplication.common.domain.model.Superset
import com.example.myapplication.common.domain.model.Workout
import com.example.myapplication.common.domain.model.createWorkoutPreview
import com.example.myapplication.common.domain.model.deepCopy
import com.example.myapplication.common.util.ProgressionOutcome
import com.example.myapplication.common.util.ProgressionResults
import com.example.myapplication.common.util.ProgressionType
import com.example.myapplication.common.util.selectProgression
import kotlin.time.ExperimentalTime
import kotlin.uuid.ExperimentalUuidApi

class WorkoutPreviewViewModel : ViewModel() {

  // The workout plan *before* progression logic ran
  lateinit var originalWorkout: Workout

  // The results of running progression logic on each applicable item
  lateinit var progressionResults: Map<String, ProgressionResults>

  // State for the UI
  val previewItems =
    mutableStateListOf<Any>() // Holds PreviewExerciseSets, PreviewSuperset, or original items
  val overallProgressionSuccess = mutableStateOf(true)
  val progressionMessage = mutableStateOf("")

  @OptIn(ExperimentalUuidApi::class)
  fun setupPreview(
    workoutToPrepare: Workout,
    previousWorkout: Workout // Needed for look-back progression
  ) {
    originalWorkout = workoutToPrepare.deepCopy() // Store a deep copy of the original state

    // --- Simulate Running Progression Logic (Method B: Look Back) ---
    // In a real app, this would call your actual WorkoutProgressorB logic
    val results = mutableMapOf<String, ProgressionResults>()
    workoutToPrepare.workoutItems.forEach { currentWorkoutItem ->
      when (currentWorkoutItem) {
        is ExerciseSets -> {
          val progressionType =
            currentWorkoutItem.sets.firstOrNull { it.setTypeID != SetType.warmUpSet }?.progressionSchemeID
          // Simulate progression call (replace with actual call)
          val previousExerciseSets =
            previousWorkout.workoutItems.find { (it as? ExerciseSets)?.exerciseID == currentWorkoutItem.exerciseID } as? ExerciseSets

          if (progressionType != null) {

            previousExerciseSets?.sets?.filter { it.setCompleted }
              ?.let {
                results[currentWorkoutItem.id.toString()] =
                  selectProgression(progressionType).invoke(it, currentWorkoutItem.sets)
              }
          } else {
            // TODO Replace with user default progression

            previousExerciseSets
              ?.sets
              ?.filter { it.setCompleted }
              ?.let {
                results[currentWorkoutItem.id.toString()] =
                  selectProgression(ProgressionType.DYNAMIC_DOUBLE_PROGRESSION).invoke(it, currentWorkoutItem.sets)
              }

            // No progression for this currentWorkoutItem]
//            results[currentWorkoutItem.id.toString()] = ProgressionResults(
//              // TODO Should this be null if no progression logic applied?
//              // TODO Or should previous workout be shown regardless?
//              completedSets = previousExerciseSets?.sets,
//              currentSets = currentWorkoutItem.sets,
//              outcome = ProgressionOutcome.NULL,
//              message = "No progression logic applied"
//            )
          }
        }

        is Superset -> {
          currentWorkoutItem.exercisesSets.forEach { currentSupersetExerciseSets ->
            val progressionType =
              currentSupersetExerciseSets.sets.firstOrNull { it.setTypeID != SetType.warmUpSet }?.progressionSchemeID
            val previousSupersetExerciseSets =
              previousWorkout.workoutItems.find { (it as? ExerciseSets)?.exerciseID == currentSupersetExerciseSets.exerciseID } as? ExerciseSets

            if (progressionType != null) {
              previousSupersetExerciseSets?.sets?.filter { it.setCompleted }
                ?.let {
                  results[currentSupersetExerciseSets.id.toString()] =
                    selectProgression(progressionType).invoke(it, currentSupersetExerciseSets.sets)
                }
            } else {
              // TODO Replace with user default progression
              previousSupersetExerciseSets?.sets?.filter { it.setCompleted }
                ?.let {
                  results[currentSupersetExerciseSets.id.toString()] =
                    selectProgression(ProgressionType.DYNAMIC_DOUBLE_PROGRESSION).invoke(it, currentSupersetExerciseSets.sets)
                }

              // No progression for this currentWorkoutItem]
//              results[currentSupersetExerciseSets.id.toString()] = ProgressionResults(
//                // TODO Should this be null if no progression logic applied?
//                // TODO Or should previous workout be shown regardless?
//                completedSets = previousSupersetExerciseSets?.sets,
//                currentSets = currentSupersetExerciseSets.sets,
//                outcome = ProgressionOutcome.NULL,
//                message = "No progression logic applied"
//              ) // No progression for this currentWorkoutItem]
            }
          }
        }
      }
    }
    progressionResults = results
    // --- End Simulation ---

    // Create the preview data structure
    previewItems.clear()
    previewItems.addAll(createWorkoutPreview(previousWorkout, originalWorkout, progressionResults))

    // Determine overall status
    val failures = previewItems.count {
      (it is PreviewExerciseSets && it.overallOutcome == ProgressionOutcome.FAILED) ||
              (it is PreviewSuperset && it.previewExerciseSets.any { p -> p.overallOutcome == ProgressionOutcome.FAILED })
    }
    val successes = previewItems.count {
      (it is PreviewExerciseSets && it.overallOutcome == ProgressionOutcome.PROGRESSED) ||
              (it is PreviewSuperset && it.previewExerciseSets.any { p -> p.overallOutcome == ProgressionOutcome.PROGRESSED })
    }

    overallProgressionSuccess.value = failures == 0
    progressionMessage.value = when {
      failures > 0 -> "Progression failed for $failures exercise(s)."
      successes > 0 -> "All $successes progression(s) will be applied."
      else -> "No progressions applied (targets may not have been met)."
    }
  }


  @OptIn(ExperimentalUuidApi::class)
  fun acceptProgressions(): Workout {
    // Create the final workout object by applying successful progressions
    val finalWorkoutItems = originalWorkout.workoutItems.map { originalItem ->
      when (originalItem) {
        is ExerciseSets -> {
          val preview =
            previewItems.find { (it as? PreviewExerciseSets)?.originalItem?.id == originalItem.id } as? PreviewExerciseSets
          if (preview?.overallOutcome == ProgressionOutcome.PROGRESSED || preview?.overallOutcome == ProgressionOutcome.UNCHANGED) {
            // Apply the progressed sets if success or unchanged
            originalItem.copy(sets = preview.previewSets.map { it.progressedSet }.toMutableList())
          } else {
            // Revert to original if failed or rejected implicitly (or handle explicitly)
            originalItem // Return original
          }
        }

        is Superset -> {
          originalItem.copy(exercisesSets = originalItem.exercisesSets.map { exSets ->
            val preview =
              (previewItems.find { (it as? PreviewSuperset)?.originalItem?.id == originalItem.id } as? PreviewSuperset)
                ?.previewExerciseSets?.find { it.originalItem.id == exSets.id }

            if (preview?.overallOutcome == ProgressionOutcome.PROGRESSED || preview?.overallOutcome == ProgressionOutcome.UNCHANGED) {
              exSets.copy(sets = preview.previewSets.map { it.progressedSet }.toMutableList())
            } else {
              exSets
            }
          }.toMutableList())
        }

//        else -> originalItem
      }
    }.toMutableList()
    return originalWorkout.copy(workoutItems = finalWorkoutItems)
  }

  fun rejectProgressions(): Workout {
    // Return the original workout plan without any changes
    return originalWorkout
  }
}
