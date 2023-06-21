package com.example.myapplication.data

import androidx.room.*
import androidx.room.Dao
import kotlinx.coroutines.flow.Flow

@Dao
interface Dao {

    @Query("SELECT * FROM exercises_table ORDER BY name ASC")
    fun getAllExercises(): Flow<List<ExerciseModel>>

    @Query("SELECT * FROM exercises_table WHERE name LIKE :exercise ORDER BY name ASC")
    fun searchExercises(exercise: String): Flow<List<ExerciseModel>>

//    @Query("SELECT * FROM exercises_table WHERE name LIKE :exercise AND bodyPart IN(:bodyParts) AND equipment IN(:equipments) ORDER BY name ASC")
    @Query("SELECT * FROM exercises_table WHERE name LIKE :exercise ORDER BY name ASC")
    fun filterExercises(
        exercise: String,
//        bodyParts: List<String> = listOf("%%"),
//        equipments: List<String> = listOf("%%")
//        movementTypes: List<String> = listOf("%%")
    ): Flow<List<ExerciseModel>>

    @Insert(onConflict = OnConflictStrategy.IGNORE, entity = ExerciseModel::class)
    suspend fun insertExercise(exercise: ExerciseModel)

    @Update
    fun updateExercise(exercise: ExerciseModel)

    @Delete
    fun deleteExercise(exercise: ExerciseModel)
}
