package com.example.myapplication.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import java.util.UUID

@Dao
interface WorkoutsDao {
    @Query("SELECT * FROM workouts ORDER BY dateCreated DESC")
    fun getAllWorkouts(): Flow<List<Workout>>

    @Query("SELECT * FROM workouts WHERE workoutID = :workoutID")
    suspend fun getWorkoutById(workoutID: Long): Workout?

    @Query("SELECT * FROM workouts WHERE microcycleUUID = :microcycleUUID")
    suspend fun getWorkoutsByMicrocycleUUID(microcycleUUID: UUID): List<Workout>

    @Query("SELECT * FROM workouts WHERE mesocycleUUID = :mesocycleUUID")
    suspend fun getWorkoutsByMesocycleUUID(mesocycleUUID: UUID): List<Workout>

    @Query("SELECT * FROM workouts WHERE programUUID = :programUUID")
    suspend fun getWorkoutsByProgramUUID(programUUID: UUID): List<Workout>

    @Query("SELECT * FROM workouts WHERE microcycleID = :microcycleID")
    suspend fun getWorkoutsByMicrocycleID(microcycleID: Long): List<Workout>

    @Query("SELECT * FROM workouts WHERE mesocycleID = :mesocycleID")
    suspend fun getWorkoutsByMesocycleID(mesocycleID: Long): List<Workout>

    @Query("SELECT * FROM workouts WHERE programID = :programID")
    suspend fun getWorkoutsByProgramID(programID: Long): List<Workout>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkout(workout: Workout)

    @Update
    suspend fun updateWorkout(workout: Workout)

    @Delete
    suspend fun deleteWorkout(workout: Workout)
}