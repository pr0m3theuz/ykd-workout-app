package com.example.myapplication.data

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExerciseSetsRepository @Inject constructor(private val exerciseSetsDao: ExerciseSetsDao) {
//    suspend fun getSetForExercise(exerciseID: Long): List<TestExerciseSets> {
//        return exerciseSetsDao.getSetForExercise(exerciseID)
//    }

    suspend fun getSetsWithExercise(workoutID: Long): List<SetsWithExercise> {
        return exerciseSetsDao.getSetsWithExercise(workoutID)
    }

    suspend fun getSetsAndExerciseByWorkout(workoutID: Long): List<WorkoutWithSetsAndExercise> {
        return exerciseSetsDao.getSetsAndExerciseByWorkout(workoutID)
    }

    /*    fun getSetModelsForWorkoutAndExercise(
            workoutID: Long,
            exerciseID: Long
        ): Flow<List<TestExerciseSets>> {
            return exerciseSetsDao.getSetModelsForWorkoutAndExercise(workoutID, exerciseID)
        }*/
}