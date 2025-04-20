package com.example.myapplication.features.workout

import android.util.Log
import com.example.myapplication.common.domain.model.ExerciseSets
import com.example.myapplication.common.domain.model.Set
import com.example.myapplication.common.domain.model.SetType
import com.example.myapplication.common.domain.model.Superset
import com.example.myapplication.common.domain.model.Workout
import com.example.myapplication.common.infrastructure.persistence.CompletedRepository
import com.example.myapplication.common.infrastructure.persistence.ExerciseNotesRepository
import com.example.myapplication.common.infrastructure.persistence.ExercisesRepository
import com.example.myapplication.common.infrastructure.persistence.InProgressWorkoutRepository
import com.example.myapplication.common.infrastructure.persistence.UserRepository
import com.example.myapplication.common.infrastructure.persistence.WorkoutNotesRepository
import com.example.myapplication.common.infrastructure.persistence.WorkoutRepository
import com.example.myapplication.common.util.ProgressionType
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
  private val completedRepository: CompletedRepository,
  private val userRepository: UserRepository,
) {
  // TODO Look forward in the program to update the next workout
  //  or look backward in the workout history to update the current workout
  @OptIn(ExperimentalUuidApi::class)
  fun applyProgressionToNextWorkout(
    currentWorkout: Workout,
    nextWorkout: Workout
  ) {
    currentWorkout.workoutItems.forEach {
      when (it) {
        is ExerciseSets -> {
          val progressionType: ProgressionType? =
            it.sets.firstOrNull { set -> set.setTypeID != SetType.warmUpSet }?.progressionSchemeID

          val result =
            progressionType?.let { progression -> selectProgression(progression) }?.invoke(
              it.sets,
              it.sets.toMutableList()
            )

          result?.onSuccess { sets ->
            it.sets.clear()
            it.sets.addAll(sets)
          }
        }

        is Superset -> {
          it.exercisesSets.forEach { exerciseSets ->
            val progressionType: ProgressionType? =
              exerciseSets.sets.firstOrNull { set -> set.setTypeID != SetType.warmUpSet }?.progressionSchemeID

            val result =
              progressionType?.let { progression -> selectProgression(progression) }?.invoke(
                exerciseSets.sets,
                exerciseSets.sets.toMutableList()
              )

            result?.onSuccess { sets ->
              exerciseSets.sets.clear()
              exerciseSets.sets.addAll(sets)
            }
          }
        }
      }
    }
  }

  @OptIn(ExperimentalUuidApi::class)
  fun applyProgressionToNextWorkout_GEMINI(
    currentWorkout: Workout, // Contains completed data or template for progression rules
    nextWorkout: Workout     // The workout object to be modified
  ) {
    // Ensure workouts have comparable structures. You might need more robust checks.
    if (currentWorkout.workoutItems.size != nextWorkout.workoutItems.size) {
      Log.w("Progression", "Cannot apply progression: Workout structures (item count) mismatch.")
      return
    }

    currentWorkout.workoutItems.forEachIndexed { index, currentItem ->
      // Get the corresponding item in the next workout
      val nextItem = nextWorkout.workoutItems.getOrNull(index)

      // Safety check: ensure nextItem exists and types match
      if (nextItem == null) {
        Log.w(
          "Progression",
          "Skipping progression for item $index: Corresponding item not found in next workout."
        )
        return@forEachIndexed // continue to next item
      }
      if (currentItem::class != nextItem::class) {
        Log.w(
          "Progression",
          "Skipping progression for item $index: Component type mismatch between workouts (${currentItem::class.simpleName} vs ${nextItem::class.simpleName})."
        )
        return@forEachIndexed // continue to next item
      }


      when (currentItem) {
        is ExerciseSets -> {
          // Cast nextItem safely (already checked type)
          val nextExerciseSets = nextItem as ExerciseSets

          // Find progression scheme from the *current* workout item
          val progressionType: ProgressionType? =
            currentItem.sets.firstOrNull { set -> set.setTypeID != SetType.warmUpSet }?.progressionSchemeID

          // Select the progression function (assuming updated signature)
          val progressionFunction = progressionType?.let { selectProgression(it) }

          // Invoke the function with sets from *both* workouts
          val result: Result<List<Set>>? =
            progressionFunction?.invoke(
              currentItem.sets,         // Sets from the completed/current workout
              nextExerciseSets.sets     // Template sets from the next workout
            )

          // Apply successful result to the *next* workout's item
          result?.onSuccess { newCalculatedSets ->
            Log.d(
              "Progression",
              "Applying progression (${progressionType.name}) to Exercise ${nextExerciseSets.exerciseID}"
            )
            nextExerciseSets.sets.clear()
            nextExerciseSets.sets.addAll(newCalculatedSets)
          }?.onFailure { error ->
            Log.e(
              "Progression",
              "Progression (${progressionType.name}) failed for Exercise ${currentItem.exerciseID}: ${error.message}"
            )
          }
        }

        is Superset -> {
          // Cast nextItem safely
          val nextSuperset = nextItem as Superset

          // Ensure supersets have comparable structures.
          if (currentItem.exercisesSets.size != nextSuperset.exercisesSets.size) {
            Log.w(
              "Progression",
              "Skipping progression for superset at index $index: Exercise count mismatch."
            )
            return@forEachIndexed // continue to next outer item
          }

          currentItem.exercisesSets.forEachIndexed { exerciseIndex, currentExerciseSets ->
            // Get the corresponding exercise sets in the next superset
            val nextExerciseSets = nextSuperset.exercisesSets.getOrNull(exerciseIndex)

            if (nextExerciseSets == null) {
              Log.w(
                "Progression",
                "Skipping progression for superset exercise $exerciseIndex: Corresponding item not found in next workout's superset."
              )
              return@forEachIndexed // continue to next exercise in superset
            }
            // Optional: Check if exercise IDs match if needed
            // if (currentExerciseSets.exerciseID != nextExerciseSets.exerciseID) { ... }


            val progressionType: ProgressionType? =
              currentExerciseSets.sets.firstOrNull { set -> set.setTypeID != SetType.warmUpSet }?.progressionSchemeID

            val progressionFunction = progressionType?.let { selectProgression(it) }

            val result: Result<List<Set>>? = progressionFunction?.invoke(
              currentExerciseSets.sets, // Sets from the completed/current workout's exercise
              nextExerciseSets.sets     // Template sets from the next workout's corresponding exercise
            )

            result?.onSuccess { newCalculatedSets ->
              Log.d(
                "Progression",
                "Applying progression (${progressionType.name}) to Superset Exercise ${nextExerciseSets.exerciseID}"
              )
              nextExerciseSets.sets.clear()
              nextExerciseSets.sets.addAll(newCalculatedSets)
            }?.onFailure { error ->
              Log.e(
                "Progression",
                "Progression (${progressionType.name}) failed for Superset Exercise ${currentExerciseSets.exerciseID}: ${error.message}"
              )
            }
          }
        }
        // Add other WorkoutComponent types if necessary
      }
    }
    Log.i("Progression", "Finished applying progression to next workout.")
    // Note: You might need to explicitly save the modified `nextWorkout` object
    // depending on your persistence mechanism.
  }
}