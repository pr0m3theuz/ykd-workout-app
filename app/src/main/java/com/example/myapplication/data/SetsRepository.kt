
package com.example.myapplication.data

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SetsRepository @Inject constructor(private val setsDao: SetsDao) {
    suspend fun insertSetModel(set: Set) {
        setsDao.insertSetModel(set)
    }

    suspend fun updateSetModel(set: Set) {
        setsDao.updateSetModel(set)
    }

    suspend fun deleteSetModel(set: Set) {
        setsDao.deleteSetModel(set)
    }

    suspend fun getSetBySetId(setID: Long): Set? {
        return setsDao.getSetBySetId(setID)
    }

    suspend fun getSetsByWorkout(workoutID: Long): List<Set> {
        return setsDao.getSetsByWorkoutID(workoutID)
    }
}