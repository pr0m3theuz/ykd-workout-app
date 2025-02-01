@file:OptIn(ExperimentalUuidApi::class)

package com.example.myapplication.data.network

import com.couchbase.lite.Database
import com.example.myapplication.data.database.DatabaseManager
import com.example.myapplication.data.model.ExerciseNote
import com.example.myapplication.data.model.WorkoutNote
import org.bson.types.ObjectId
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

private const val DATABASE_IS_NULL = "Notes Repository: Database does not exist."

private const val COLLECTION_IS_NULL =
  "Notes Repository: Notes Collection does not exist in the Database."

class ExerciseNotesRepositoryImpl(private val databaseManager: DatabaseManager) :
  ExerciseNotesRepository {
  val db: Database? = databaseManager.database

  // TODO Decide if I should create a repository for user data
  val userDatabase: Database? = databaseManager.userDatabase

  override fun getPinnedExerciseNotes(exercise: ObjectId): List<ExerciseNote> {
    return emptyList<ExerciseNote>()
  }

  override suspend fun insertExerciseNote(exerciseNote: ExerciseNote) {

  }
}

class WorkoutNotesRepositoryImpl(private val databaseManager: DatabaseManager) :
  WorkoutNotesRepository {
  val db: Database? = databaseManager.database

  // TODO Decide if I should create a repository for user data
  val userDatabase: Database? = databaseManager.userDatabase

  override fun getPinnedWorkoutNotes(workoutUUID: Uuid): List<WorkoutNote> {
    return emptyList<WorkoutNote>()
  }
}