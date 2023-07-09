package com.example.myapplication.data

import androidx.annotation.WorkerThread
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SetsRepository @Inject constructor(private val setModelDao: SetsDao) {
    @WorkerThread
    suspend fun insertSetModel(setModel: SetModel) {
        setModelDao.insertSetModel(setModel)
    }

    @WorkerThread
    suspend fun updateSetModel(setModel: SetModel) {
        setModelDao.updateSetModel(setModel)
    }

    @WorkerThread
    suspend fun deleteSetModel(setModel: SetModel) {
        setModelDao.deleteSetModel(setModel)
    }

    @WorkerThread
    suspend fun getSetModelById(setID: Long): SetModel? {
        return setModelDao.getSetModelById(setID)
    }

    fun getSetModelsForWorkout(workoutID: Long): Flow<List<ExerciseSets>> {
        return setModelDao.getSetModelsForWorkout(workoutID)
    }

    fun getSetModelsForExercise(exerciseID: Long): Flow<List<ExerciseSets>> {
        return setModelDao.getSetModelsForExercise(exerciseID)
    }

    fun getSetModelsForWorkoutAndExercise(
        workoutID: Long,
        exerciseID: Long
    ): Flow<List<ExerciseSets>> {
        return setModelDao.getSetModelsForWorkoutAndExercise(workoutID, exerciseID)
    }
}