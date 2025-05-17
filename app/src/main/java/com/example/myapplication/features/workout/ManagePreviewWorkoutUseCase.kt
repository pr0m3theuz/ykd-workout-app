@file:OptIn(ExperimentalUuidApi::class)

package com.example.myapplication.features.workout

import android.util.Log
import com.example.myapplication.common.domain.model.ExerciseExerciseSets
import com.example.myapplication.common.domain.model.ExerciseSets
import com.example.myapplication.common.domain.model.SetType
import com.example.myapplication.common.domain.model.Superset
import com.example.myapplication.common.domain.model.SupersetExercisesSets
import com.example.myapplication.common.domain.model.Workout
import com.example.myapplication.common.domain.model.WorkoutComponent
import com.example.myapplication.common.infrastructure.persistence.CompletedWorkoutRepository
import com.example.myapplication.common.infrastructure.persistence.ExerciseNotesRepository
import com.example.myapplication.common.infrastructure.persistence.ExercisesRepository
import com.example.myapplication.common.infrastructure.persistence.InProgressWorkoutRepository
import com.example.myapplication.common.infrastructure.persistence.UserRepository
import com.example.myapplication.common.infrastructure.persistence.WorkoutNotesRepository
import com.example.myapplication.common.infrastructure.persistence.WorkoutRepository
import com.example.myapplication.common.util.ProgressionOutcome
import com.example.myapplication.common.util.selectProgression
import kotlin.uuid.ExperimentalUuidApi

// TODO Write tests using deserialization
// TODO Tests multiple workouts

class ManagePreviewWorkoutUseCase(
  private val workoutRepository: WorkoutRepository,
  //  private val setsRepository: SetsRepository,
  //  private val exerciseSetsRepository: ExerciseSetsRepository,
  private val exercisesRepository: ExercisesRepository,
  private val workoutNotesRepository: WorkoutNotesRepository,
  private val exerciseNotesRepository: ExerciseNotesRepository,
  private val inProgressWorkoutRepository: InProgressWorkoutRepository,
  private val completedWorkoutRepository: CompletedWorkoutRepository,
  private val userRepository: UserRepository,
) {
  // TODO Look forward in the program to update the next workout
  //  or look backward in the workout history to update the current workout

  /**
   * Applies progression to `currentWorkoutToPrepare` based on the performance of the latest completed
   * workout with the same exercises. This function modifies the `currentWorkoutWorkoutComponents`
   * in place by adjusting sets (e.g., reps, weight) according to the defined progression schemes.
   *
   * The progression logic is applied by comparing the completed sets from the
   * `latestCompletedIdenticalWorkout` with the template sets in `currentWorkoutWorkoutComponents`.
   *
   * If no latest completed identical workout is found, or if an exercise in the current workout
   * has no corresponding performance history, the sets for that exercise will remain unchanged.
   *
   * @param currentWorkoutToPrepare The [Workout] object representing the template or plan for the
   *   current session. This is used to identify the corresponding last completed workout.
   * @param currentWorkoutWorkoutComponents A mutable list of [WorkoutComponent] objects that
   *   represent the exercises and their sets for the current workout. These components will be
   *   modified directly if progression is applicable.
   * @return A [Result] containing the (potentially modified) `currentWorkoutWorkoutComponents` if
   *   progression was attempted (even if no changes were made due to lack of history or other
   *   conditions). Returns a [Result.failure] if a `lastCompletedIdenticalWorkout` could not be
   *   retrieved, indicating a prerequisite for progression was not met.
   */
  fun prepareCurrentWorkoutWithProgression(
    currentWorkoutToPrepare: Workout,
    currentWorkoutWorkoutComponents: MutableList<WorkoutComponent>,
  ): Result<MutableList<WorkoutComponent>> {
    // TODO Move this out. Doesn't respect Referential Transparency.
    val latestCompletedIdenticalWorkout =
      completedWorkoutRepository.getLatestPreviousIdenticalWorkout(currentWorkout = currentWorkoutToPrepare)

    val results = mutableListOf<Result<WorkoutComponent>>()

    latestCompletedIdenticalWorkout?.let { previousWorkout ->
      currentWorkoutWorkoutComponents.forEach { currentItemToPrepare ->
//        val originalCurrentItemToPrepare = currentItemToPrepare.copy()
        when (currentItemToPrepare) {
          is ExerciseExerciseSets -> {
            // Find the performance data for this exercise from the history
            val lastPerformance = previousWorkout.workoutItems.filterIsInstance<ExerciseSets>()
              .firstOrNull { it.exerciseID == currentItemToPrepare.exercise.id }

            val lastPerformanceData =
              lastPerformance?.sets?.filter { it.setCompleted } ?: emptyList() // Get completed sets


            if (lastPerformanceData.isNotEmpty()) { // Only progress if there's history
              val progressionType = currentItemToPrepare.sets // Use progression from current plan
                .firstOrNull { it.setTypeID != SetType.warmUpSet }?.progressionSchemeID

              val progressionResults = progressionType?.let { progression ->
                selectProgression(progression).invoke(
                  lastPerformanceData,
                  currentItemToPrepare.sets // Pass the current plan to be modified
                )
              }

              when (progressionResults?.outcome) {
                ProgressionOutcome.PROGRESSED -> {
                  progressionResults.currentSets?.let {
                    currentItemToPrepare.sets.clear()
                    currentItemToPrepare.sets.addAll(it)
                    Log.i(
                      "prepareCurrentWorkoutWithProgression",
                      "Progression applied to ${currentItemToPrepare.exercise.name} in ${currentWorkoutToPrepare.name}: ${progressionResults.message}"
                    )
                  }
                }

                ProgressionOutcome.UNCHANGED -> {
                  progressionResults.currentSets?.let {
                    currentItemToPrepare.sets.clear()
                    currentItemToPrepare.sets.addAll(it)
                    Log.i(
                      "prepareCurrentWorkoutWithProgression",
                      "${currentItemToPrepare.exercise.name} in ${currentWorkoutToPrepare.name}: ${progressionResults.message}"
                    )
                  }
                }

                ProgressionOutcome.FAILED -> {
                  Log.i(
                    "prepareCurrentWorkoutWithProgression",
                    "Progression failed for ${currentItemToPrepare.exercise.name}: ${progressionResults.message}"
                  )
                }

                ProgressionOutcome.PENDING -> {
                  Log.i(
                    "prepareCurrentWorkoutWithProgression",
                    "No progression to apply for ${currentItemToPrepare.exercise.name} in ${currentWorkoutToPrepare.name}"
                  )
                }

                else -> {
                  Log.i(
                    "prepareCurrentWorkoutWithProgression",
                    "Progression failed for ${currentItemToPrepare.exercise.name}: ${progressionResults?.message}"
                  )
                }
              }
            } else {
              Log.i(
                "prepareCurrentWorkoutWithProgression",
                "No history found for ${currentItemToPrepare.exercise.name} to apply backward progression."
              )
            }
          }

          is SupersetExercisesSets -> {
            currentItemToPrepare.exercisesSets.forEach { exerciseInSupersetToPrepare ->
              val lastPerformanceData =
                previousWorkout.workoutItems.filterIsInstance<Superset>().flatMap {
                  it.exercisesSets
                }
                  .firstOrNull { it.exerciseID == exerciseInSupersetToPrepare.exercise.id }
                  ?.sets?.filter { it.setCompleted } ?: emptyList()

              if (lastPerformanceData.isNotEmpty()) {
                val progressionType = exerciseInSupersetToPrepare.sets
                  .firstOrNull { it.setTypeID != SetType.warmUpSet }?.progressionSchemeID

                val progressionResults = progressionType?.let { progression ->
                  selectProgression(progression).invoke(
                    lastPerformanceData,
                    exerciseInSupersetToPrepare.sets
                  )
                }

                when (progressionResults?.outcome) {
                  ProgressionOutcome.PROGRESSED -> {
                    progressionResults.currentSets?.let {
                      exerciseInSupersetToPrepare.sets.clear()
                      exerciseInSupersetToPrepare.sets.addAll(it)
                      Log.i(
                        "prepareCurrentWorkoutWithProgression",
                        "Progression applied to ${exerciseInSupersetToPrepare.exercise.name} in ${currentWorkoutToPrepare.name}: ${progressionResults.message}"
                      )
                    }
                  }

                  ProgressionOutcome.UNCHANGED -> {
                    progressionResults.currentSets?.let {
                      exerciseInSupersetToPrepare.sets.clear()
                      exerciseInSupersetToPrepare.sets.addAll(it)
                      Log.i(
                        "prepareCurrentWorkoutWithProgression",
                        "${exerciseInSupersetToPrepare.exercise.name} in ${currentWorkoutToPrepare.name}: ${progressionResults.message}"
                      )
                    }
                  }

                  ProgressionOutcome.FAILED -> {
                    Log.i(
                      "prepareCurrentWorkoutWithProgression",
                      "Progression failed for ${exerciseInSupersetToPrepare.exercise.name}: ${progressionResults.message}"
                    )
                  }

                  ProgressionOutcome.PENDING -> {
                    Log.i(
                      "prepareCurrentWorkoutWithProgression",
                      "No progression to apply for ${exerciseInSupersetToPrepare.exercise.name} in ${currentWorkoutToPrepare.name}"
                    )
                  }

                  else -> {
                    Log.i(
                      "prepareCurrentWorkoutWithProgression",
                      "Progression failed for ${exerciseInSupersetToPrepare.exercise.name}: ${progressionResults?.message}"
                    )
                  }
                }


              } else {
                Log.i(
                  "prepareCurrentWorkoutWithProgression",
                  "No history found for superset exercise ${exerciseInSupersetToPrepare.exercise.name} to apply backward progression."
                )
              }
            }
          }
        }
      }

      return Result.success(currentWorkoutWorkoutComponents)
    }
    return Result.failure(IllegalArgumentException("No previous workout found."))
  }
}