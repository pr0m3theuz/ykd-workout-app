package com.example.myapplication.data.network

import com.example.myapplication.data.database.RealmDatabase
import com.example.myapplication.data.model.Equipment
import com.example.myapplication.data.model.Exercise
import com.example.myapplication.data.model.Force
import com.example.myapplication.data.model.Mechanic
import com.example.myapplication.data.model.MuscleGroup
import com.example.myapplication.data.model.MuscleRoleFunction
import com.example.myapplication.data.model.Utility
import kotlinx.coroutines.flow.Flow
import org.mongodb.kbson.ObjectId

// Declares the DAO as a private property in the constructor. Pass in the DAO
// instead of the whole database, because you only need access to the DAO

class ExercisesRepository(private val database: RealmDatabase) {
  // Room executes all queries on a separate thread.
  // Observed Flow will notify the observer when the data has changed.
  fun getAllExercises(): Flow<List<Exercise>> = database.getAllExercises()

  fun getAllEquipment(): List<Equipment> = database.getAllEquipment()

  fun getAllMuscleGroups(): List<MuscleGroup> = database.getAllMuscleGroups()

  fun getAllUtilities(): Flow<List<Utility>> = database.getAllUtilities()

  fun getAllForces(): Flow<List<Force>> = database.getAllForces()

  fun getAllMechanics(): Flow<List<Mechanic>> = database.getAllMechanics()

  fun getAllMuscleRolesFunctions(): Flow<List<MuscleRoleFunction>> =
    database.getAllMuscleRolesFunctions()

  fun getMuscleGroupNameByID(muscleGroupID: String): String =
    database.getMuscleGroupNameByID(muscleGroupID)

  // By default Room runs suspend queries off the main thread, therefore, we don't need to
  // implement anything else to ensure we're not doing long running database work
  // off the main thread.

  // TODO Use function when inserting new exercises
  suspend fun insertExercise(exercise: Exercise) {
    database.insertExercise(exercise)
  }

  fun getExerciseDetails(exerciseID: ObjectId): Exercise {
    return database.getExerciseDetails(exerciseID)
  }

  fun getExercise(exerciseID: ObjectId?): Exercise? {
    return database.getExercise(exerciseID)
  }

  suspend fun updateExercise(exercise: Exercise) {
    database.updateExercise(exercise)
  }

  suspend fun updateWeightUnitsforExercise(exerciseID: ObjectId, weightUnit: String) {
    database.updateWeightUnitsForExercise(exerciseID, weightUnit)
  }

  suspend fun updateWorkingSetRestTimerforExercise(exerciseID: ObjectId, restDuration: Long) {
    database.updateWorkingSetRestTimerForExercise(exerciseID, restDuration)
  }

  suspend fun updateWarmUpSetRestTimerforExercise(exerciseID: ObjectId, restDuration: Long) {
    database.updateWarmUpSetRestTimerForExercise(exerciseID, restDuration)
  }

  suspend fun enableWorkingSetRestTimerforExercise(exerciseID: ObjectId, enable: Boolean) {
    database.enableWorkingSetRestTimerForExercise(exerciseID, enable)
  }

  suspend fun enableWarmUpSetRestTimerforExercise(exerciseID: ObjectId, enable: Boolean) {
    database.enableWarmUpSetRestTimerForExercise(exerciseID, enable)
  }

  fun filterExercises(
    exercise: String,
    muscleGroupIDs: List<String>,
    equipmentIDs: List<String>
    //        movementTypes: List<String>
  ): Flow<List<Exercise>> {
    return database.filterExercises(exercise, muscleGroupIDs, equipmentIDs /*movementTypes*/)
  }
}
