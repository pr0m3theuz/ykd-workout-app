package com.example.myapplication.common.infrastructure.persistence

import com.example.myapplication.common.domain.model.Workout
import kotlinx.coroutines.flow.Flow
import org.bson.types.ObjectId

interface InProgressWorkoutRepository {
  suspend fun saveInProgressWorkout(workout: Workout): Boolean

  // TODO Implement feature to get active workout & ask user if they want to resume workout or delete it
  fun getWorkoutByID(workoutID: ObjectId): Workout?

  fun getWorkoutsCount(): Int

  fun getWorkouts(): Flow<List<Workout>>

  fun deleteActiveWorkout(workoutID: ObjectId): Boolean

  fun deleteAllWorkouts(): Boolean
}
