package com.example.myapplication.data.network

import android.util.Log
import com.couchbase.lite.DataSource
import com.couchbase.lite.Database
import com.couchbase.lite.Expression
import com.couchbase.lite.MutableDocument
import com.couchbase.lite.Ordering
import com.couchbase.lite.QueryBuilder
import com.couchbase.lite.QueryChange
import com.couchbase.lite.SelectResult
import com.couchbase.lite.queryChangeFlow
import com.example.myapplication.data.database.DatabaseManager
import com.example.myapplication.data.model.Workout
import com.example.myapplication.data.model.WorkoutDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.bson.types.ObjectId

interface InProgressWorkoutRepository {
  suspend fun saveInProgressWorkout(workout: Workout): Boolean

  fun getWorkoutByID(workoutID: ObjectId): Workout?

  fun getWorkoutsCount(): Int

  fun getWorkouts(): Flow<List<Workout>>
}

private const val DATABASE_IS_NULL = "In Progress Workout Repository: Database does not exist."

private const val COLLECTION_IS_NULL =
  "In Progress Workout Repository: Workout Collection does not exist in the Database."

class InProgressWorkoutRepositoryImpl(databaseManager: DatabaseManager) :
  InProgressWorkoutRepository {

  val currentActiveWorkoutDatabase: Database? = databaseManager.currentActiveWorkoutDatabase

  // TODO Decide if I should create a repository for user data
  val userDatabase: Database? = databaseManager.userDatabase

  private val withUnknownKeys = Json { ignoreUnknownKeys = true }

  private fun mapQueryChangeToWorkout(queryChange: QueryChange): List<Workout> {
    val workouts = mutableListOf<Workout>() // 1
    queryChange.results?.let { results -> // 2
      results.forEach { result -> // 3
        val json = result.toJSON() // 4
        val workout = withUnknownKeys.decodeFromString<WorkoutDao>(json).Workout // 5
        workouts.add(workout) // 6
      }
    }
    return workouts // 7
  }

  override suspend fun saveInProgressWorkout(workout: Workout): Boolean {
    try {
      requireNotNull(currentActiveWorkoutDatabase) { DATABASE_IS_NULL }
      val collection = currentActiveWorkoutDatabase.getCollection("ActiveWorkout")

      requireNotNull(collection) { COLLECTION_IS_NULL }

      val json = Json.encodeToString(workout)
      val document = MutableDocument(workout.id.toString(), json)

      collection.save(document)

      return true
    } catch (e: Exception) {
      Log.e(e.message, e.stackTraceToString())
    }
    return false
  }

  override fun getWorkoutByID(workoutID: ObjectId): Workout? {
    try {
      requireNotNull(currentActiveWorkoutDatabase) { DATABASE_IS_NULL }
      val collection = currentActiveWorkoutDatabase.getCollection("ActiveWorkout")

      requireNotNull(collection) { COLLECTION_IS_NULL }

      val queryBuilder =
        QueryBuilder.select(SelectResult.all())
          .from(DataSource.collection(collection)) // Replace with your database instance
          .where(Expression.property("id").equalTo(Expression.string(workoutID.toString())))
          .orderBy(Ordering.property("name").ascending())
          .limit(Expression.intValue(1))

      val result = queryBuilder.execute().allResults().firstOrNull()?.toJSON()

      val workout = result?.let { withUnknownKeys.decodeFromString<Workout>(it) }

      return workout
    } catch (e: Exception) {
      Log.e(e.message, e.stackTraceToString())
    }
    return null
  }

  override fun getWorkoutsCount(): Int {
    try {
      requireNotNull(currentActiveWorkoutDatabase) { DATABASE_IS_NULL }

      val collection = currentActiveWorkoutDatabase.getCollection("ActiveWorkout")

      requireNotNull(collection) { COLLECTION_IS_NULL }

      val query = QueryBuilder.select(SelectResult.all()).from(DataSource.collection(collection))

      val results = query.execute().allResults()
      return results[0].getInt("count")
    } catch (e: Exception) {
      Log.e(e.message, e.stackTraceToString())
    }
    return 0
  }

  override fun getWorkouts(): Flow<List<Workout>> {
    val workouts = mutableListOf<Workout>()
    try {
      requireNotNull(currentActiveWorkoutDatabase) { DATABASE_IS_NULL }
      val collection = currentActiveWorkoutDatabase.getCollection("ActiveWorkout")

      requireNotNull(collection) { COLLECTION_IS_NULL }

      val query =
        QueryBuilder.select(SelectResult.all())
          .from(DataSource.collection(collection))

      query.execute().allResults().forEach { item ->
        val json = item.toJSON()
        val workout = withUnknownKeys.decodeFromString<WorkoutDao>(json).Workout
        workouts.add(workout)
      }

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

}