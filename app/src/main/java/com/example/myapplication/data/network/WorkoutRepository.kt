@file:OptIn(ExperimentalUuidApi::class)

package com.example.myapplication.data.network

import com.example.myapplication.data.model.ExerciseNote
import com.example.myapplication.data.model.ExerciseSets
import com.example.myapplication.data.model.Set
import com.example.myapplication.data.model.Workout
import com.example.myapplication.data.model.WorkoutNote
import kotlinx.coroutines.flow.Flow
import org.bson.types.ObjectId
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

interface WorkoutRepository {
  fun getWorkoutByID(workoutID: ObjectId): Workout?

  fun getWorkoutsCount(): Int

  /*    suspend fun getWorkoutByMicrocycleUUID(microcycleUUID: UUID): List<Workout> {
      return workoutsDao.getWorkoutsByMicrocycleUUID(microcycleUUID)
  }

  suspend fun getWorkoutByMesocycleUUID(mesocycleUUID: UUID): List<Workout> {
      return workoutsDao.getWorkoutsByMesocycleUUID(mesocycleUUID)
  }

  suspend fun getWorkoutByProgramUUID(programUUID: UUID): List<Workout> {
      return workoutsDao.getWorkoutsByProgramUUID(programUUID)
  }*/

  fun getWorkoutsByMicrocycleID(microcycleID: ObjectId): List<Workout>

  fun getWorkoutsByMesocycleID(mesocycleID: ObjectId): List<Workout>

  fun getWorkoutsByProgramID(programID: ObjectId): Flow<List<Workout>>

  suspend fun insertWorkout(workout: Workout): Boolean

  suspend fun updateWorkout(workout: Workout): Boolean

  suspend fun deleteWorkout(workout: Workout): Boolean

  fun getAllWorkoutTemplates(): Flow<List<Workout>>

  fun getAllCompletedWorkouts(): Flow<List<Workout>>

  fun getPreviousSetForExercise(exerciseID: ObjectId, setNumber: Int): Set?

  fun findNextWorkoutExerciseSets(exerciseSets: ExerciseSets, programID: ObjectId): Workout?

  suspend fun updateSets(sets: MutableList<Set>)

  fun filterWorkouts(
    workout: String,
    programIDs: List<String>,
    startDate: String,
    endDate: String,
    //        movementTypes: List<String>
  ): Flow<List<Workout>>
}

interface CurrentWorkoutRepository {
  fun getWorkoutByID(workoutID: ObjectId): Workout?

  fun getWorkoutsCount(): Int

  fun getExerciseNotes(): Flow<List<ExerciseNote>>

  fun getPinnedExerciseNotes(exerciseID: ObjectId, pinned: Boolean = true): List<ExerciseNote>

  suspend fun insertExerciseNote(exerciseNote: ExerciseNote)

  suspend fun updateExerciseNote(exerciseNote: ExerciseNote)

  fun getPinnedWorkoutNotes(
    workoutUUID: Uuid,
    pinned: Boolean = true,
    programPinned: Boolean = false,
    mesocyclePinned: Boolean = false,
    microcyclePinned: Boolean = false,
  ): List<WorkoutNote>

  suspend fun insertWorkoutNote(workoutNote: WorkoutNote)

  suspend fun updateWorkoutNote(workoutNote: WorkoutNote)

  suspend fun insertWorkout(workout: Workout): Boolean

  suspend fun updateWorkout(workout: Workout): Boolean

  suspend fun deleteWorkout(workout: Workout): Boolean

  fun getAllWorkoutTemplates(): Flow<List<Workout>>

  fun getAllCompletedWorkouts(): Flow<List<Workout>>

  fun getPreviousSetForExercise(exerciseID: ObjectId, setNumber: Int): Set?

  fun findNextWorkoutExerciseSets(exerciseSets: ExerciseSets, programID: ObjectId): Workout?

  suspend fun updateSets(sets: MutableList<Set>)
}
