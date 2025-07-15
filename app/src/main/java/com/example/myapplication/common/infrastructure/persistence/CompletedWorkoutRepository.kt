@file:OptIn(ExperimentalTime::class)

package com.example.myapplication.common.infrastructure.persistence

import com.example.myapplication.common.domain.model.Set
import com.example.myapplication.common.domain.model.Workout
import com.example.myapplication.common.domain.model.WorkoutAnalytics
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDateTime
import org.bson.types.ObjectId
import kotlin.time.ExperimentalTime

interface CompletedWorkoutRepository {
  fun getPreviousSetForExercise(exerciseID: ObjectId, setNumber: Int): Set?

  fun getCompletedWorkoutsCount(): Int

  fun getCompletedSetsForExercise(exerciseID: ObjectId): Flow<List<Set>>

  fun getCompletedWorkouts(): List<Workout>?

// TODO fun getCompletedWorkoutsByProgramID(programID: ObjectId): Flow<List<Workout>>

  fun getCompletedWorkoutByID(workoutID: ObjectId): Workout?

  fun getLatestPreviousIdenticalWorkout(currentWorkout: Workout): Workout?

  suspend fun upsertCompletedWorkout(workout: Workout)

  suspend fun deleteCompletedWorkout(workout: Workout): Boolean

  fun getWorkoutAnalyticsForPeriod(
    startDate: LocalDateTime,
    endDate: LocalDateTime
  ): WorkoutAnalytics?

  fun getWorkoutAnalyticsForLifetime(): WorkoutAnalytics?

  fun getWorkoutStreakWeek(): Int?
}