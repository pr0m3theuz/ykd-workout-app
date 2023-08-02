package com.example.myapplication.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ExerciseDao {
  @Query("SELECT * FROM exercises ORDER BY name ASC")
  fun getAllExercises(): Flow<List<Exercise>>

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

  @Query("SELECT name FROM muscleGroups WHERE id = :muscleGroupID")
  fun getMuscleGroupNameByID(muscleGroupID: String): Flow<List<String>>

  @Query("SELECT * FROM exercises WHERE name LIKE :exercise ORDER BY name ASC")
  fun searchExercises(exercise: String): Flow<List<Exercise>>

  @Query("SELECT * FROM exercises WHERE exerciseID=:exerciseID")
  fun getExerciseDetails(
    exerciseID: Long,
  ): Flow<List<Exercise>>

  @Query("SELECT * FROM exercises WHERE exerciseID=:exerciseID")
  suspend fun getExercise(
    exerciseID: Long,
  ): List<Exercise>

  //    @Query("SELECT * FROM exercises WHERE name LIKE :exercise ORDER BY name ASC")
  @Query(
    "SELECT * FROM exercises WHERE name LIKE :exercise " +
            "AND muscleGroupID IN (SELECT ALL id FROM muscleGroups WHERE id NOT IN (:muscleGroupIDs)) " +
            "AND equipmentID IN (SELECT ALL id FROM equipment WHERE id NOT IN (:equipmentIDs)) " +
            "ORDER BY name ASC"
  )
  fun filterExercises(
    exercise: String,
    muscleGroupIDs: List<String> = listOf("%"),
    equipmentIDs: List<String> = listOf("%")
    //        movementTypes: List<String> = listOf("%%")
  ): Flow<List<Exercise>>

  @Insert(onConflict = OnConflictStrategy.ABORT, entity = Exercise::class)
  suspend fun insertExercise(exercise: Exercise)

  @Update
  fun updateExercise(exercise: Exercise)

  @Query("UPDATE exercises SET weightUnit=:weightUnit WHERE exerciseID=:exerciseID")
  fun updateWeightUnitsforExercise(exerciseID: Long, weightUnit: String)

  @Query("UPDATE exercises SET workingSetRestInWholeMilliseconds=:restDuration WHERE exerciseID=:exerciseID")
  fun updateWorkingSetRestTimerforExercise(exerciseID: Long, restDuration: Long)

  @Query("UPDATE exercises SET warmUpSetRestInWholeMilliseconds=:restDuration WHERE exerciseID=:exerciseID")
  fun updateWarmUpSetRestTimerforExercise(exerciseID: Long, restDuration: Long)

  @Query("UPDATE exercises SET workingSetRestTimerEnabled=:enable WHERE exerciseID=:exerciseID")
  fun enableWorkingSetRestTimerforExercise(exerciseID: Long, enable: Boolean)

  @Query("UPDATE exercises SET warmUpSetRestTimerEnabled=:enable WHERE exerciseID=:exerciseID")
  fun enableWarmUpSetRestTimerforExercise(exerciseID: Long, enable: Boolean)


  @Delete
  fun deleteExercise(exercise: Exercise)
}
