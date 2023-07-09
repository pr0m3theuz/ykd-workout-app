package com.example.myapplication.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ExerciseDao {
    @Query("SELECT * FROM exercises ORDER BY name ASC")
    fun getAllExercises(): Flow<List<ExerciseModel>>

    @Query("SELECT * FROM equipment ORDER BY name ASC")
    fun getAllEquipment(): Flow<List<Equipment>>

    @Query("SELECT * FROM muscleGroups ORDER BY name ASC")
    fun getAllMuscleGroups(): Flow<List<MuscleGroup>>

    @Query("SELECT * FROM utility ORDER BY name ASC")
    fun getAllUtilities(): Flow<List<Utility>>

    @Query("SELECT * FROM forces ORDER BY name ASC")
    fun getAllForces(): Flow<List<Force>>

    @Query("SELECT * FROM mechanics ORDER BY name ASC")
    fun getAllMechanics(): Flow<List<Mechanic>>

    @Query("SELECT * FROM muscleRolesFunctions ORDER BY roleFunction ASC")
    fun getAllMuscleRolesFunctions(): Flow<List<MuscleRoleFunction>>

    @Query("SELECT * FROM exercises WHERE name LIKE :exercise ORDER BY name ASC")
    fun searchExercises(exercise: String): Flow<List<ExerciseModel>>

    //    @Query("SELECT * FROM exercises_table WHERE name LIKE :exercise AND bodyPart IN(:bodyParts) AND equipment IN(:equipments) ORDER BY name ASC")
    @Query("SELECT * FROM exercises WHERE name LIKE :exercise ORDER BY name ASC")
    fun filterExercises(
        exercise: String,
//        bodyParts: List<String> = listOf("%%"),
//        equipments: List<String> = listOf("%%")
//        movementTypes: List<String> = listOf("%%")
    ): Flow<List<ExerciseModel>>

    @Query("SELECT * FROM exercises WHERE exerciseID=:exerciseID")
    fun getExerciseDetails(
        exerciseID: Long,
    ): Flow<List<ExerciseModel>>

    @Insert(onConflict = OnConflictStrategy.ABORT, entity = ExerciseModel::class)
    suspend fun insertExercise(exercise: ExerciseModel)

    @Update
    fun updateExercise(exercise: ExerciseModel)

    @Delete
    fun deleteExercise(exercise: ExerciseModel)
}
