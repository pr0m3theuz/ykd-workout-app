package com.example.myapplication.common.infrastructure.persistence

import com.example.myapplication.common.domain.model.Set
import com.example.myapplication.common.domain.model.Workout
import kotlinx.coroutines.flow.Flow
import org.bson.types.ObjectId

interface CompletedWorkoutRepository {
  fun getPreviousSetForExercise(exerciseID: ObjectId, setNumber: Int): Set?

  fun getCompletedSetsForExercise(exerciseID: ObjectId): Flow<List<Set>>

  fun getCompletedWorkouts(): List<Workout>?

// TODO fun getCompletedWorkoutsByProgramID(programID: ObjectId): Flow<List<Workout>>

  fun getCompletedWorkoutByID(workoutID: ObjectId): Workout?

  fun getLatestPreviousIdenticalWorkout(currentWorkout: Workout): Workout?

  suspend fun upsertCompletedWorkout(workout: Workout)
}