package com.example.myapplication.data

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExerciseSetsRepository @Inject constructor(private val exerciseSetsDao: ExerciseSetsDao) {


    fun getSetModelsForExercise(exerciseID: Long): List<TestExerciseSets> {
        return exerciseSetsDao.getSetModelsForExercise(exerciseID)
    }

    fun getSetsAndExerciseByWorkout(workoutID: Long): List<WorkoutWithSetsAndExercise> {
        return exerciseSetsDao.getSetsAndExerciseByWorkout(workoutID)
    }

    /*    fun getSetModelsForWorkoutAndExercise(
            workoutID: Long,
            exerciseID: Long
        ): Flow<List<TestExerciseSets>> {
            return exerciseSetsDao.getSetModelsForWorkoutAndExercise(workoutID, exerciseID)
        }*/
}