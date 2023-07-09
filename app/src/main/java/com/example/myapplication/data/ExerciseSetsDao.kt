package com.example.myapplication.data

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction

@Dao
interface ExerciseSetsDao {
    @Query("SELECT * FROM exercises WHERE exerciseID = :exerciseID")
    fun getSetModelsForExercise(exerciseID: Long): List<TestExerciseSets>

    @Transaction
    @Query("SELECT * FROM sets")
    fun getSetsWithExercise(): List<SetsWithExercise>

    @Transaction
    @Query("SELECT * FROM workouts WHERE workoutID = :workoutID")
    fun getSetsAndExerciseByWorkout(workoutID: Long): List<WorkoutWithSetsAndExercise>

    /*    @Query(
            "SELECT * FROM sets " +
                    "WHERE workoutID = :workoutID AND exerciseID = :exerciseID " +
                    "ORDER BY setNumber ASC"
        )
        fun getSetModelsForWorkoutAndExercise(
            workoutID: Long,
            exerciseID: Long
        ): Flow<List<TestExerciseSets>>*/
}