@file:OptIn(ExperimentalUuidApi::class)

package com.example.myapplication.data.network

import com.example.myapplication.data.database.RealmDatabase
import com.example.myapplication.data.model.ExerciseSets
import com.example.myapplication.data.model.Set
import com.example.myapplication.data.model.Workout
import kotlinx.coroutines.flow.Flow
import org.bson.types.ObjectId
import kotlin.uuid.ExperimentalUuidApi

class WorkoutRepositoryDb(private val database: RealmDatabase) : WorkoutRepository {

  override fun getWorkoutByID(workoutID: ObjectId): Workout? {

  }

  override fun getWorkoutsCount(): Int {

  }

  override fun getWorkoutsByMicrocycleID(microcycleID: ObjectId): List<Workout> {

  }

  override fun getWorkoutsByMesocycleID(mesocycleID: ObjectId): Flow<List<Workout>> {

  }

  override fun getWorkoutsByProgramID(programID: ObjectId): Flow<List<Workout>> {

  }

  override suspend fun insertWorkout(workout: Workout) {
    database.insertWorkout(workout)
  }

  override suspend fun updateWorkout(workout: Workout) {
    database.updateWorkout(workout)
  }

  override suspend fun deleteWorkout(workout: Workout): Boolean {
    database.deleteWorkout(workout)
  }

  override fun getAllWorkoutTemplates(): Flow<List<Workout>> {

  }

  override fun getAllCompletedWorkouts(): Flow<List<Workout>> {

  }

  override fun getPreviousSetForExercise(exerciseID: ObjectId, setNumber: Int): Set? {

  }

  override fun findNextWorkoutExerciseSets(
    exerciseSets: ExerciseSets,
    programID: ObjectId
  ): Workout? {

  }

  override suspend fun updateSets(sets: MutableList<Set>) {
    database.updateSets(sets)
  }

  override fun filterWorkouts(
    workout: String,
    programIDs: List<String>,
    startDate: String,
    endDate: String
  ): Flow<List<Workout>> {

  }
}
