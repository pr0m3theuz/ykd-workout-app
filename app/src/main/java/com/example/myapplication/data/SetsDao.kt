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
    suspend fun getSetBySetId(setID: Long): SetModel?

    @Query("SELECT * FROM sets WHERE workoutID = :workoutID ORDER BY setNumber ASC")
    fun getSetsForWorkout(workoutID: Long): Flow<List<SetModel>>

    @Query("SELECT * FROM sets WHERE exerciseID = :exerciseID ORDER BY setNumber ASC")
    fun getSetsForExercise(exerciseID: Long): Flow<List<SetModel>>

    @Query("SELECT * FROM sets WHERE workoutID = :workoutID ORDER BY exerciseID ASC, setNumber ASC")
    suspend fun getSetsByWorkoutID(workoutID: Long): List<SetModel>

    @Query(
        "SELECT * FROM sets " +
                "WHERE workoutID = :workoutID AND exerciseID = :exerciseID " +
                "ORDER BY setNumber ASC"
    )
    fun getSetModelsForWorkoutAndExercise(
        workoutID: Long,
        exerciseID: Long
    ): Flow<List<SetModel>>

}