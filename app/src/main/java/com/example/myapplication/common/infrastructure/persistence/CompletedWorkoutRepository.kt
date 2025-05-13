package com.example.myapplication.common.infrastructure.persistence

import com.example.myapplication.common.domain.model.Set
import com.example.myapplication.common.domain.model.Workout
import org.bson.types.ObjectId

interface CompletedWorkoutRepository {
  fun getPreviousSetForExercise(exerciseID: ObjectId, setNumber: Int): Set?

  fun getCompletedWorkouts(): List<Workout>

  fun getCompletedWorkoutByID(workoutID: ObjectId): Workout?

  fun getLatestPreviousIdenticalWorkout(currentWorkout: Workout): Workout?
}