package com.example.myapplication.data

import androidx.annotation.WorkerThread
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

// Declares the DAO as a private property in the constructor. Pass in the DAO
// instead of the whole database, because you only need access to the DAO
@Singleton
class ExercisesRepository @Inject constructor(private val exerciseDao: ExerciseDao) {

  // Room executes all queries on a separate thread.
  // Observed Flow will notify the observer when the data has changed.
  fun getExercises(): Flow<List<ExerciseModel>> = exerciseDao.getAllExercises()

  fun getAllEquipment(): Flow<List<Equipment>> = exerciseDao.getAllEquipment()

  @WorkerThread
  suspend fun getAllMuscleGroups(): Flow<List<MuscleGroup>> = exerciseDao.getAllMuscleGroups()

  fun getAllUtilities(): Flow<List<Utility>> = exerciseDao.getAllUtilities()

  fun getAllForces(): Flow<List<Force>> = exerciseDao.getAllForces()

  fun getAllMechanics(): Flow<List<Mechanic>> = exerciseDao.getAllMechanics()

  fun getAllMuscleRolesFunctions(): Flow<List<MuscleRoleFunction>> =
    exerciseDao.getAllMuscleRolesFunctions()

  fun getMuscleGroupNameByID(muscleGroupID: String): Flow<List<String>> =
    exerciseDao.getMuscleGroupNameByID(muscleGroupID)

  // By default Room runs suspend queries off the main thread, therefore, we don't need to
  // implement anything else to ensure we're not doing long running database work
  // off the main thread.

  @WorkerThread
  suspend fun insertExercise(exercise: ExerciseModel) {
    exerciseDao.insertExercise(exercise)
  }

  fun getExerciseDetails(exercisePK: Long): Flow<List<ExerciseModel>> {
    return exerciseDao.getExerciseDetails(exercisePK)
  }

  @WorkerThread
  fun updateExercise(exercise: ExerciseModel) {
    exerciseDao.updateExercise(exercise)
  }

  @WorkerThread
  fun updateWeightUnitsforExercise(exerciseID: Long, weightUnit: String) {
    exerciseDao.updateWeightUnitsforExercise(exerciseID, weightUnit)
  }

  @WorkerThread
  fun updateWorkingSetRestTimerforExercise(exerciseID: Long, restDuration: Long) {
    exerciseDao.updateWorkingSetRestTimerforExercise(exerciseID, restDuration)
  }

  @WorkerThread
  fun updateWarmUpSetRestTimerforExercise(exerciseID: Long, restDuration: Long) {
    exerciseDao.updateWarmUpSetRestTimerforExercise(exerciseID, restDuration)
  }

  @WorkerThread
  fun enableWorkingSetRestTimerforExercise(exerciseID: Long, enable: Boolean) {
    exerciseDao.enableWorkingSetRestTimerforExercise(exerciseID, enable)
  }

  @WorkerThread
  fun enableWarmUpSetRestTimerforExercise(exerciseID: Long, enable: Boolean) {
    exerciseDao.enableWarmUpSetRestTimerforExercise(exerciseID, enable)
  }


  fun filterExercises(
    exercise: String,
    muscleGroupIDs: List<String>,
    equipmentIDs: List<String>
    //        movementTypes: List<String>
  ): Flow<List<ExerciseModel>> {
    return exerciseDao.filterExercises(exercise, muscleGroupIDs, equipmentIDs /*movementTypes*/)
  }
}
