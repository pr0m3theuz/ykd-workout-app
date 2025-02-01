@file:OptIn(ExperimentalUuidApi::class)

package com.example.myapplication.data.network

import android.util.Log
import com.couchbase.lite.DataSource
import com.couchbase.lite.Database
import com.couchbase.lite.Expression
import com.couchbase.lite.Join
import com.couchbase.lite.MutableDocument
import com.couchbase.lite.Ordering
import com.couchbase.lite.QueryBuilder
import com.couchbase.lite.QueryChange
import com.couchbase.lite.SelectResult
import com.couchbase.lite.queryChangeFlow
import com.example.myapplication.data.database.DatabaseManager
import com.example.myapplication.data.model.ExerciseNote
import com.example.myapplication.data.model.ExerciseSets
import com.example.myapplication.data.model.Set
import com.example.myapplication.data.model.Workout
import com.example.myapplication.data.model.WorkoutDao
import com.example.myapplication.data.model.WorkoutNote
import com.example.myapplication.data.model.WorkoutNoteDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.bson.types.ObjectId
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

private const val DATABASE_IS_NULL = "Workout Repository: Database does not exist."

private const val COLLECTION_IS_NULL =
  "Workout Repository: Workout Collection does not exist in the Database."

class WorkoutRepositoryImpl(private val databaseManager: DatabaseManager) : WorkoutRepository {

  val db: Database? = databaseManager.database

  // TODO Decide if I should create a repository for user data
  val userDatabase: Database? = databaseManager.userDatabase

  private fun mapQueryChangeToWorkout(queryChange: QueryChange): List<Workout> {
    val workouts = mutableListOf<Workout>() // 1
    queryChange.results?.let { results -> // 2
      results.forEach { result -> // 3
        val json = result.toJSON() // 4
        val workout = Json.decodeFromString<WorkoutDao>(json).item // 5
        workouts.add(workout) // 6
      }
    }
    return workouts // 7
  }

  override fun getWorkoutByID(workoutID: ObjectId): Workout? {
    try {
      requireNotNull(db) { DATABASE_IS_NULL }
      val collection = db.getCollection("Workout")

      requireNotNull(collection) { COLLECTION_IS_NULL }

      val queryBuilder =
        QueryBuilder.select(SelectResult.all())
          .from(DataSource.collection(collection)) // Replace with your database instance
          .where(Expression.property("_id").equalTo(Expression.string(workoutID.toString())))
          .orderBy(Ordering.property("name").ascending())
          .limit(Expression.intValue(1))

      val result = queryBuilder.execute().allResults().firstOrNull()?.toJSON()

      val workout = result?.let { Json.decodeFromString<Workout>(it) }

      return workout
    } catch (e: Exception) {
      Log.e(e.message, e.stackTraceToString())
    }
    return null
  }

  override fun getWorkoutsCount(): Int {
    try {
      requireNotNull(db) { DATABASE_IS_NULL }
      val collection = db.getCollection("Workout")

      requireNotNull(collection) { COLLECTION_IS_NULL }

      /*      val query =
      QueryBuilder.select(SelectResult.expression(Function.count(Expression.string("*"))).`as`("mycount"))
        .from(DataSource.collection(collection))*/

      val query = QueryBuilder.select(SelectResult.all()).from(DataSource.collection(collection))

      val results = query.execute().allResults()
      return results[0].getInt("count")
    } catch (e: Exception) {
      Log.e(e.message, e.stackTraceToString())
    }
    return 0
  }

  override fun getWorkoutsByMicrocycleID(microcycleID: ObjectId): List<Workout> {
    val workouts = mutableListOf<Workout>()
    try {
      requireNotNull(db) { DATABASE_IS_NULL }
      val collection = db.getCollection("Workout")

      requireNotNull(collection) { COLLECTION_IS_NULL }

      val query =
        QueryBuilder.select(SelectResult.all())
          .from(DataSource.collection(collection))
          .where(
            Expression.property("microcycleID").equalTo(Expression.string(microcycleID.toString()))
          )

      query.execute().allResults().forEach { item ->
        val json = item.toJSON()
        val workout = Json.decodeFromString<WorkoutDao>(json).item
        workouts.add(workout)
      }
    } catch (e: Exception) {
      Log.e(e.message, e.stackTraceToString())
    }
    return workouts.toList()
  }

  override fun getWorkoutsByMesocycleID(mesocycleID: ObjectId): List<Workout> {
    val workouts = mutableListOf<Workout>()
    try {
      requireNotNull(db) { DATABASE_IS_NULL }
      val collection = db.getCollection("Workout")

      requireNotNull(collection) { COLLECTION_IS_NULL }

      val query =
        QueryBuilder.select(SelectResult.all())
          .from(DataSource.collection(collection))
          .where(
            Expression.property("mesocycleID").equalTo(Expression.string(mesocycleID.toString()))
          )

      // TODO

    } catch (e: Exception) {
      Log.e(e.message, e.stackTraceToString())
    }
    return workouts.toList()
  }

  override fun getWorkoutsByProgramID(programID: ObjectId): Flow<List<Workout>> = flow {
    // Similar to getWorkoutByMesocycleID, but using programID
  }

  override suspend fun insertWorkout(workout: Workout): Boolean {
    try {
      requireNotNull(db) { DATABASE_IS_NULL }
      val collection = db.getCollection("Workout")

      requireNotNull(collection) { COLLECTION_IS_NULL }

      val json = Json.encodeToString(workout)
      val document = MutableDocument(workout._id.toString(), json)

      collection.save(document)
      return true
    } catch (e: Exception) {
      Log.e(e.message, e.stackTraceToString())
    }
    return false
  }

  override suspend fun updateWorkout(workout: Workout): Boolean {
    try {
      requireNotNull(db) { DATABASE_IS_NULL }
      val collection = db.getCollection("Workout")

      requireNotNull(collection) { COLLECTION_IS_NULL }

      val mutableDoc = collection.getDocument(workout._id.toString())?.toMutable() // Not Necessary
      requireNotNull(mutableDoc) { "Workout Repository: Workout does not exist!" }

      val json = Json.encodeToString(workout)
      val document = MutableDocument(workout._id.toString(), json)

      collection.save(document)

      return true
    } catch (e: Exception) {
      Log.e(e.message, e.stackTraceToString())
    }
    return false
  }

  override suspend fun deleteWorkout(workout: Workout): Boolean {
    try {
      requireNotNull(db) { DATABASE_IS_NULL }
      val collection = db.getCollection("Workout")

      requireNotNull(collection) { COLLECTION_IS_NULL }

      val document = collection.getDocument(workout._id.toString())
      requireNotNull(document) { "Workout Repository: Workout does not exist!" }

      collection.delete(document)
      return true
    } catch (e: Exception) {
      Log.e(e.message, e.stackTraceToString())
    }
    return false
  }

  override fun getAllWorkoutTemplates(): Flow<List<Workout>> {

    try {
      requireNotNull(db) { DATABASE_IS_NULL }

      val collection = db.getCollection("Workout")

      requireNotNull(collection) { COLLECTION_IS_NULL }

      val query =
        QueryBuilder.select(SelectResult.all())
          .from(DataSource.collection(collection))
          .where(Expression.all())
          .orderBy(Ordering.property("dateCreated").descending())

      val flow =
        query
          .queryChangeFlow()
          .map { queryChange -> mapQueryChangeToWorkout(queryChange) }
          .flowOn(Dispatchers.IO)

      query.execute()
      return flow
    } catch (e: Exception) {
      Log.e(e.message, e.stackTraceToString())
    }

    return flow {}
  }

  override suspend fun insertWorkoutNote(workoutNote: WorkoutNote) {
    TODO("Not yet implemented")
  }

  override suspend fun updateWorkoutNote(workoutNote: WorkoutNote) {
    TODO("Not yet implemented")
  }

  override fun getPinnedWorkoutNotes(
    workoutUUID: Uuid,
    pinned: Boolean,
    programPinned: Boolean,
    mesocyclePinned: Boolean,
    microcyclePinned: Boolean
  ): Flow<List<WorkoutNote>> {
    try {
      requireNotNull(db) { DATABASE_IS_NULL }

      val collection = db.getCollection("Workout")

      requireNotNull(collection) { COLLECTION_IS_NULL }

      // TODO add ownerID, get current user
      val query =
        QueryBuilder.select(SelectResult.property("notes"))
          .from(DataSource.collection(collection))
          .where(
            Expression.property("uuid").equalTo(Expression.string(workoutUUID.toString()))
              .and(Expression.property("notes.pinned").equalTo(Expression.booleanValue(pinned)))
          )
          .orderBy(Ordering.property("dateCreated").descending())

      val flow =
        query
          .queryChangeFlow()
          .map { queryChange -> mapQueryChangeToWorkoutNote(queryChange) }
          .flowOn(Dispatchers.IO)

      query.execute()
      return flow
    } catch (e: Exception) {
      Log.e(e.message, e.stackTraceToString())
    }

    return flow {}
  }

  private fun mapQueryChangeToWorkoutNote(queryChange: QueryChange): List<WorkoutNote> {
    val notes = mutableListOf<WorkoutNote>() // 1
    queryChange.results?.let { results -> // 2
      results.forEach { result -> // 3
        val json = result.toJSON() // 4
        val note = Json.decodeFromString<WorkoutNoteDao>(json).item // 5
        notes.add(note) // 6
      }
    }
    return notes // 7
  }

  override suspend fun insertExerciseNote(exerciseNote: ExerciseNote) {
    TODO("Not yet implemented")
  }

  override suspend fun updateExerciseNote(exerciseNote: ExerciseNote) {
    TODO("Not yet implemented")
  }

  override fun getPinnedExerciseNotes(exerciseID: ObjectId, pinned: Boolean): List<ExerciseNote> {
    TODO("Not yet implemented")
  }

  override fun getExerciseNotes(): Flow<List<ExerciseNote>> {
    TODO("Not yet implemented")
  }

  override fun getAllCompletedWorkouts(): Flow<List<Workout>> {

    try {
      requireNotNull(db) { DATABASE_IS_NULL }

      val collection = db.getCollection("Workout")

      requireNotNull(collection) { COLLECTION_IS_NULL }

      // TODO add ownerID, get current user
      val query =
        QueryBuilder.select(SelectResult.all())
          .from(DataSource.collection(collection))
          .where(Expression.property("workoutEnded").isValued())
          .orderBy(Ordering.property("dateCreated").descending())

      val flow =
        query
          .queryChangeFlow()
          .map { queryChange -> mapQueryChangeToWorkout(queryChange) }
          .flowOn(Dispatchers.IO)

      query.execute()
      return flow
    } catch (e: Exception) {
      Log.e(e.message, e.stackTraceToString())
    }

    return flow {}
  }

  override fun getPreviousSetForExercise(exerciseID: ObjectId, setNumber: Int): Set? {
    // Implement logic to retrieve previous set based on exerciseID and setNumber
    // This might require querying Sets and filtering
    TODO("Implement logic to retrieve previous set")
    return null
  }

  override fun findNextWorkoutExerciseSets(
    exerciseSets: ExerciseSets,
    programID: ObjectId,
  ): Workout? {
    // Implement logic to find the next workout containing the given ExerciseSets within the program
    try {
      requireNotNull(db) { DATABASE_IS_NULL }
      val collection = db.getCollection("Workout")

      requireNotNull(collection) { COLLECTION_IS_NULL }

      val exerciseSetsCollection = db.getCollection("ExerciseSets")

      requireNotNull(exerciseSetsCollection) { COLLECTION_IS_NULL }

      val exerciseSetsQuery =
        QueryBuilder.select(SelectResult.all())
          .from(DataSource.collection(exerciseSetsCollection))
          .where(
            Expression.property("exerciseID")
              .equalTo(Expression.string(exerciseSets.exerciseID.toString()))
              .and(
                Expression.property("ownerID")
                  .equalTo(Expression.string(exerciseSets.ownerID.toString()))
              )

              // Join with Workout to check program ID and workout status

              .and(
                Expression.property("programID")
                  .equalTo(Expression.string(programID.toString()))
                  .and(Expression.property("workoutEnded").isNotValued())
              )
          )

      val query =
        QueryBuilder.select(SelectResult.all())
          .from(DataSource.collection(collection).`as`("workout"))
          .join(Join.join(DataSource.collection(exerciseSetsCollection).`as`("exerciseSets")))
          .where(
            Expression.property("programID")
              .from("workout")
              .equalTo(
                Expression.string(programID.toString())
                  .and(Expression.property("workoutEnded").from("workout").isNotValued())
              )
              .and(
                Expression.property("exerciseID")
                  .from("exerciseSets")
                  .equalTo(Expression.string(exerciseSets.exerciseID.toString()))
              )
          )

      // Execute first query to get potential matches
      val results = query.execute().allResults()
    } catch (e: Exception) {
      Log.e(e.message, e.stackTraceToString())
    }
    return null
  }

  override suspend fun updateSets(sets: MutableList<Set>) {
    // Implement logic to update multiple sets
    // This might involve looping through the sets and updating their corresponding documents

  }

  override fun filterWorkouts(
    workout: String,
    programIDs: List<String>,
    startDate: String,
    endDate: String,
  ): Flow<List<Workout>> = flow {
    // Implement complex filtering logic using QueryBuilder
  }
}
