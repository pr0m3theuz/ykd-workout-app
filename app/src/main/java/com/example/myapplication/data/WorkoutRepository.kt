package com.example.myapplication.data

import androidx.annotation.WorkerThread
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WorkoutRepository @Inject constructor(private val workoutsDao: WorkoutsDao) {
    @WorkerThread
    suspend fun getWorkoutById(workoutID: Long): Workout? {
        return workoutsDao.getWorkoutById(workoutID)
    }

/*    suspend fun getWorkoutByMicrocycleUUID(microcycleUUID: UUID): List<Workout> {
        return workoutsDao.getWorkoutsByMicrocycleUUID(microcycleUUID)
    }

    suspend fun getWorkoutByMesocycleUUID(mesocycleUUID: UUID): List<Workout> {
        return workoutsDao.getWorkoutsByMesocycleUUID(mesocycleUUID)
    }

    suspend fun getWorkoutByProgramUUID(programUUID: UUID): List<Workout> {
        return workoutsDao.getWorkoutsByProgramUUID(programUUID)
    }*/

    suspend fun getWorkoutsByMicrocycleID(microcycleID: Long): List<Workout> {
        return workoutsDao.getWorkoutsByMicrocycleID(microcycleID)
    }

    suspend fun getWorkoutByMesocycleID(mesocycleID: Long): List<Workout> {
        return workoutsDao.getWorkoutsByMesocycleID(mesocycleID)
    }

    suspend fun getWorkoutByProgramID(programID: Long): List<Workout> {
        return workoutsDao.getWorkoutsByProgramID(programID)
    }

    @WorkerThread
    suspend fun insertWorkout(workout: Workout) {
        workoutsDao.insertWorkout(workout)
    }

    @WorkerThread
    suspend fun updateWorkout(workout: Workout) {
        workoutsDao.updateWorkout(workout)
    }

    @WorkerThread
    suspend fun deleteWorkout(workout: Workout) {
        workoutsDao.deleteWorkout(workout)
    }

    fun getAllWorkouts(): Flow<List<Workout>> {
        return workoutsDao.getAllWorkouts()
    }
}