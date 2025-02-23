package com.example.myapplication.data.network

import com.example.myapplication.data.model.Equipment
import com.example.myapplication.data.model.Exercise
import com.example.myapplication.data.model.Force
import com.example.myapplication.data.model.Mechanic
import com.example.myapplication.data.model.MuscleGroup
import com.example.myapplication.data.model.MuscleRoleFunction
import com.example.myapplication.data.model.Utility
import kotlinx.coroutines.flow.Flow
import org.bson.types.ObjectId

// Declares the DAO as a private property in the constructor. Pass in the DAO
// instead of the whole database, because you only need access to the DAO

interface ExercisesRepository {
  // Room executes all queries on a separate thread.
  // Observed Flow will notify the observer when the data has changed.
  fun getAllExercises(): Flow<List<Exercise>>

  fun getAllEquipment(): Flow<List<Equipment>>

  fun getAllMuscleGroups(): Flow<List<MuscleGroup>>

  fun getAllUtilities(): Flow<List<Utility>>

  fun getAllForces(): Flow<List<Force>>

  fun getAllMechanics(): Flow<List<Mechanic>>

  fun getAllMuscleRolesFunctions(): Flow<List<MuscleRoleFunction>>

  fun getMuscleGroupNameByID(muscleGroupID: String): String

  // By default Room runs suspend queries off the main thread, therefore, we don't need to
  // implement anything else to ensure we're not doing long running database work
  // off the main thread.

  // TODO Use function when inserting new exercises
  suspend fun insertExercise(exercise: Exercise)

  fun getExerciseDetails(exerciseID: ObjectId): Exercise?

  fun getExercise(exerciseID: ObjectId?): Exercise?

  suspend fun updateExercise(exercise: Exercise): Boolean

  suspend fun updateWeightUnitsForExercise(exerciseID: ObjectId, weightUnit: String)

  suspend fun updateWorkingSetRestTimerForExercise(exerciseID: ObjectId, restDuration: Long)

  suspend fun updateWarmUpSetRestTimerForExercise(exerciseID: ObjectId, restDuration: Long)

  suspend fun enableWorkingSetRestTimerForExercise(exerciseID: ObjectId, enable: Boolean)

  suspend fun enableWarmUpSetRestTimerForExercise(exerciseID: ObjectId, enable: Boolean)

  fun filterExercises(
    exercise: String,
    muscleGroupIDs: List<String>,
    equipmentIDs: List<String>
    //        movementTypes: List<String>
  ): Flow<List<Exercise>>

  fun getExerciseByName(exerciseName: String): ObjectId?
}
