package com.example.myapplication.data

import androidx.annotation.WorkerThread
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

// Declares the DAO as a private property in the constructor. Pass in the DAO
// instead of the whole database, because you only need access to the DAO
@Singleton
class Repository @Inject constructor(private val dao: Dao) {

    // Room executes all queries on a separate thread.
    // Observed Flow will notify the observer when the data has changed.
    fun getExercises(): Flow<List<ExerciseModel>> = dao.getAllExercises()

    // By default Room runs suspend queries off the main thread, therefore, we don't need to
    // implement anything else to ensure we're not doing long running database work
    // off the main thread.

    @WorkerThread
    suspend fun insertExercise(exercise: ExerciseModel) {
        dao.insertExercise(exercise)
    }

    @WorkerThread
    suspend fun updateExercise(exercise: ExerciseModel) {
        dao.updateExercise(exercise)
    }

    fun filterExercises(
        exercise: String,
        bodyParts: List<String>,
        equipments: List<String>
//        movementTypes: List<String>
    ) {
        dao.filterExercises(exercise, bodyParts, equipments/*, movementTypes*/)
    }
}
