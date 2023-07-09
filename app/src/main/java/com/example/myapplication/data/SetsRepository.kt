package com.example.myapplication.data

import androidx.annotation.WorkerThread
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
}