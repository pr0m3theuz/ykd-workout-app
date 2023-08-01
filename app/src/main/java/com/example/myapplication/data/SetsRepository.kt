
package com.example.myapplication.data

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SetsRepository @Inject constructor(private val setsDao: SetsDao) {
    suspend fun insertSetModel(setModel: SetModel) {
        setsDao.insertSetModel(setModel)
    }

    suspend fun updateSetModel(setModel: SetModel) {
        setsDao.updateSetModel(setModel)
    }

    suspend fun deleteSetModel(setModel: SetModel) {
        setsDao.deleteSetModel(setModel)
    }

    suspend fun getSetBySetId(setID: Long): SetModel? {
        return setsDao.getSetBySetId(setID)
    }

    suspend fun getSetsByWorkout(workoutID: Long): List<SetModel> {
        return setsDao.getSetsByWorkoutID(workoutID)
    }
}