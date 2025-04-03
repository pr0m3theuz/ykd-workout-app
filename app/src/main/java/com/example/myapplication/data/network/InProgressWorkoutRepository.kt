package com.example.myapplication.data.network

import com.example.myapplication.data.model.Workout
import kotlinx.coroutines.flow.Flow
import org.bson.types.ObjectId

interface InProgressWorkoutRepository {
  suspend fun saveInProgressWorkout(workout: Workout): Boolean

  fun getWorkoutByID(workoutID: ObjectId): Workout?

  fun getWorkoutsCount(): Int

  fun getWorkouts(): Flow<List<Workout>>

  fun deleteActiveWorkout(workout: Workout): Boolean
}
