@file:OptIn(ExperimentalUuidApi::class)

package com.example.myapplication.features.previewNextWorkout

import androidx.compose.runtime.Immutable
import com.example.myapplication.common.domain.model.Exercise
import com.example.myapplication.common.domain.model.ExerciseExerciseSets
import com.example.myapplication.common.domain.model.ExerciseNote
import com.example.myapplication.common.domain.model.Set
import com.example.myapplication.common.domain.model.SetType
import com.example.myapplication.common.domain.model.Workout
import com.example.myapplication.common.domain.model.WorkoutComponent
import com.example.myapplication.common.domain.model.WorkoutNote
import org.bson.types.ObjectId
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

/**
 * Represents the complete UI state for the EditNextWorkoutScreen.
 * This immutable data class ensures that our UI reacts to state changes efficiently
 * and that state is consistent.
 */
@Immutable
data class EditNextWorkoutUiState(
  val workout: Workout? = null,
  val workoutComponents: List<WorkoutComponent> = emptyList(),
  val workoutNotes: List<WorkoutNote> = emptyList(),
  val workoutName: String? = null,
  val isLoading: Boolean = true, // Indicate if data is being loaded
  val errorMessage: String? = null, // For displaying errors
  val userDefaultWeightUnit: String = "kg" // Assuming this comes from user preferences
)

/**
 * Represents all possible user interactions on the EditNextWorkoutScreen.
 * This sealed class ensures that we have a clear, exhaustive list of events
 * that the UI can emit to the ViewModel.
 */
sealed class EditNextWorkoutUiEvent {
  data class LoadWorkout(val workoutId: ObjectId) : EditNextWorkoutUiEvent()
  object SaveWorkout : EditNextWorkoutUiEvent()
  object AddWorkoutNote : EditNextWorkoutUiEvent()

  data class PinWorkoutNote(val workoutNote: WorkoutNote, val pinned: Boolean) :
    EditNextWorkoutUiEvent()

  data class UpdateWorkoutNote(val workoutNote: WorkoutNote, val newText: String) :
    EditNextWorkoutUiEvent()

  data class RemoveWorkoutNote(val workoutNote: WorkoutNote) : EditNextWorkoutUiEvent()
  data class RemoveExerciseSets(val exerciseSets: ExerciseExerciseSets) : EditNextWorkoutUiEvent()
  data class AddExerciseNote(val exerciseSets: ExerciseExerciseSets /*, val workoutId: ObjectId*/) :
    EditNextWorkoutUiEvent()

  data class RemoveExerciseNote(val exerciseSets: ExerciseExerciseSets, val note: ExerciseNote) :
    EditNextWorkoutUiEvent()

  data class PinExerciseNote(
    val exerciseSets: ExerciseExerciseSets,
    val note: ExerciseNote,
    val pinned: Boolean
  ) : EditNextWorkoutUiEvent()

  data class UpdateExerciseWeightUnit(val exercise: Exercise, val newUnit: String?) :
    EditNextWorkoutUiEvent()

  data class RemoveSet(val exerciseSets: ExerciseExerciseSets, val set: Set) :
    EditNextWorkoutUiEvent()

  //  data class PopulatePreviousSetField(val exerciseId: ObjectId, val setNumber: Int) : EditNextWorkoutUiEvent()
  data class UpdateSetReps(
    val exerciseSets: ExerciseExerciseSets,
    val setUUID: Uuid,
    val newReps: Int?
  ) : EditNextWorkoutUiEvent()

  data class UpdateSetRIR(
    val exerciseSets: ExerciseExerciseSets,
    val setUUID: Uuid,
    val newRIR: Double?
  ) : EditNextWorkoutUiEvent()

  data class UpdateSetWeight(
    val exerciseSets: ExerciseExerciseSets,
    val setUUID: Uuid,
    val newWeight: Double?
  ) : EditNextWorkoutUiEvent()

  data class UpdateSetType(
    val exerciseSets: ExerciseExerciseSets,
    val setUUID: Uuid,
    val setType: SetType
  ) : EditNextWorkoutUiEvent()

  data class CompleteSet(
    val exerciseSets: ExerciseExerciseSets,
    val setUUID: Uuid,
    val completed: Boolean
  ) : EditNextWorkoutUiEvent()

  data class AddSet(val supersetUuid: Uuid?, val targetExSetsUuid: Uuid) : EditNextWorkoutUiEvent()
  data class GetWeightUnit(val exerciseSets: ExerciseExerciseSets) : EditNextWorkoutUiEvent() {

  }// This might be better as a direct function in ViewModel
}
