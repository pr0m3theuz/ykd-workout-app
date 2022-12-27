package com.example.myapplication.data

import androidx.room.*
import androidx.room.Dao
import kotlinx.coroutines.flow.Flow

@Dao
interface Dao {

    @Query("SELECT * FROM exercises_table ORDER BY name ASC")
    fun getAllExercises(): Flow<List<ExerciseModel>>

    @Insert(onConflict = OnConflictStrategy.IGNORE, entity = ExerciseModel::class)
    suspend fun insertExercise(exercise: ExerciseModel)

    @Update
    fun updateExercise(exercise: ExerciseModel)
}
