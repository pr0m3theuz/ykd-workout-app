package com.example.myapplication.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface SetsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSetModel(setModel: SetModel)

    @Update
    suspend fun updateSetModel(setModel: SetModel)

    @Delete
    suspend fun deleteSetModel(setModel: SetModel)

    @Query("SELECT * FROM sets WHERE setID = :setID")
    suspend fun getSetModelById(setID: Long): SetModel?

    @Query("SELECT * FROM sets WHERE workoutID = :workoutID ORDER BY setNumber ASC")
    fun getSetModelsForWorkout(workoutID: Long): Flow<List<ExerciseSets>>

    @Query("SELECT * FROM sets WHERE exerciseID = :exerciseID ORDER BY setNumber ASC")
    fun getSetModelsForExercise(exerciseID: Long): Flow<List<ExerciseSets>>

    @Query(
        "SELECT * FROM sets " +
                "WHERE workoutID = :workoutID AND exerciseID = :exerciseID " +
                "ORDER BY setNumber ASC"
    )
    fun getSetModelsForWorkoutAndExercise(
        workoutID: Long,
        exerciseID: Long
    ): Flow<List<ExerciseSets>>

}