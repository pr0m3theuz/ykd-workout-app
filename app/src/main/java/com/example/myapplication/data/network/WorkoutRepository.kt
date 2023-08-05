package com.example.myapplication.data.network

import com.example.myapplication.data.database.RealmDatabase
import com.example.myapplication.data.model.Workout
import kotlinx.coroutines.flow.Flow
import org.mongodb.kbson.ObjectId


class WorkoutRepository constructor(private val database: RealmDatabase) {
    suspend fun getWorkoutByID(workoutID: ObjectId): Workout? {
        return database.getWorkoutByID(workoutID)
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

    fun getWorkoutsByMicrocycleID(microcycleID: ObjectId): Flow<List<Workout>> {
        return database.getWorkoutsByMicrocycleID(microcycleID)
    }

    fun getWorkoutByMesocycleID(mesocycleID: ObjectId): Flow<List<Workout>> {
        return database.getWorkoutsByMesocycleID(mesocycleID)
    }

    fun getWorkoutByProgramID(programID: ObjectId): Flow<List<Workout>> {
        return database.getWorkoutsByProgramID(programID)
    }


    suspend fun insertWorkout(workout: Workout) {
        database.insertWorkout(workout)
    }


    suspend fun updateWorkout(workout: Workout) {
        database.updateWorkout(workout)
    }


    suspend fun deleteWorkout(workout: Workout) {
        database.deleteWorkout(workout)
    }

    fun getAllWorkouts(): Flow<List<Workout>> {
        return database.getAllWorkouts()
    }
}