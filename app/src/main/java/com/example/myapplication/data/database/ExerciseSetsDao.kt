package com.example.myapplication.data.database

/*
@Dao
interface ExerciseSetsDao {
  @Query("SELECT * FROM exercises WHERE exerciseID = :exerciseID")
  suspend fun getSetForExercise(exerciseID: Long): List<TestExerciseSets>

  @Transaction
  @Query("SELECT * FROM sets")
  suspend fun getSetsWithExercise(): List<SetsWithExercise>

  @Transaction
  @Query("SELECT * FROM sets WHERE workoutID = :workoutID")
  suspend fun getSetsWithExercise(workoutID: Long): List<SetsWithExercise>

  @Transaction
  @Query("SELECT * FROM workouts WHERE workoutID = :workoutID")
  suspend fun getSetsAndExerciseByWorkout(workoutID: Long): List<WorkoutWithSetsAndExercise>

  @Query(
    "SELECT * FROM sets " +
      "WHERE workoutID = :workoutID AND exerciseID = :exerciseID " +
      "ORDER BY setNumber ASC"
  )
  fun getSetModelsForWorkoutAndExercise(
    workoutID: Long,
    exerciseID: Long
  ): Flow<List<TestExerciseSets>>
}
*/
