@file:OptIn(ExperimentalUuidApi::class, ExperimentalUuidApi::class)

package com.example.myapplication.common.infrastructure.persistence

import android.util.Log
import com.couchbase.lite.Collection
import com.couchbase.lite.DataSource
import com.couchbase.lite.Database
import com.couchbase.lite.Expression
import com.couchbase.lite.MutableDocument
import com.couchbase.lite.Ordering
import com.couchbase.lite.QueryBuilder
import com.couchbase.lite.QueryChange
import com.couchbase.lite.SelectResult
import com.couchbase.lite.UnitOfWork
import com.couchbase.lite.queryChangeFlow
import com.example.myapplication.common.domain.model.ExerciseSets
import com.example.myapplication.common.domain.model.Set
import com.example.myapplication.common.domain.model.SetDao
import com.example.myapplication.common.domain.model.Superset
import com.example.myapplication.common.domain.model.UserProfileManager
import com.example.myapplication.common.domain.model.Workout
import com.example.myapplication.common.domain.model.WorkoutDao
import com.example.myapplication.common.infrastructure.database.DatabaseManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import org.bson.types.ObjectId
import kotlin.uuid.ExperimentalUuidApi

private const val DATABASE_IS_NULL = "Completed Repository: Database does not exist."

private const val COLLECTION_IS_NULL =
  "Completed Repository: Completed * Collection does not exist in the Database."

class CompletedWorkoutRepositoryImpl(
  private val databaseManager: DatabaseManager,
  private val userProfileManager: UserProfileManager
) :
  CompletedWorkoutRepository {

  val db: Database? = databaseManager.database

  // TODO Decide if I should create a repository for user data
  val user = userProfileManager.getCurrentUser()

  private val withUnknownKeys = Json { ignoreUnknownKeys = true }

  private fun mapQueryChangeToSet(queryChange: QueryChange): List<Set> {
    val sets = mutableListOf<Set>() // 1
    queryChange.results?.let { results -> // 2
      results.forEach { result -> // 3
        val json = result.toJSON() // 4
        val set = withUnknownKeys.decodeFromString<SetDao>(json).Set // 5
        sets.add(set) // 6
      }
    }
    return sets // 7
  }

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

  // TODO is this best practice?
  private fun mapQueryChangeToWorkoutCompletedSets(
    queryChange: QueryChange,
    setCollection: Collection
  ): List<Workout> {
    val workouts = mutableListOf<Workout>() // 1
    queryChange.results?.let { results -> // 2
      results.forEach { result -> // 3
        val json = result.toJSON() // 4
        val workout = withUnknownKeys.decodeFromString<WorkoutDao>(json).Workout
        workouts.add(workout) // 6
      }
    }

    val workoutIds = workouts.map { it.id.toString() }

    val workoutIDClause = workoutIds.joinToString(",") { "'$it'" }.let { "[$it]" }





    return workouts // 7
  }

  override fun getPreviousSetForExercise(exerciseID: ObjectId, setNumber: Int): Set? {
    try {
      requireNotNull(db) { DATABASE_IS_NULL }

      val collection = db.getCollection("CompletedSets")

      requireNotNull(collection) { COLLECTION_IS_NULL }

      val queryBuilder =
        QueryBuilder.select(SelectResult.all())
          .from(DataSource.collection(collection)) // Replace with your database instance
          .where(
            Expression.property("exerciseID").equalTo(Expression.string(exerciseID.toString()))
              .and(Expression.property("setNumber").equalTo(Expression.intValue(setNumber)))
              .and(Expression.property("setCompleted").equalTo(Expression.booleanValue(true)))
          )
          .orderBy(Ordering.property("dateCreated").descending()) // Get latest set
          .limit(Expression.intValue(1))

      val result = queryBuilder.execute().allResults().firstOrNull()?.toJSON()

      return result?.let { withUnknownKeys.decodeFromString<SetDao>(it).Set }
    } catch (e: Exception) {
      Log.e(e.message, e.stackTraceToString())
    }
    return null
  }

  override fun getCompletedSetsForExercise(exerciseID: ObjectId): Flow<List<Set>> {
    try {
      requireNotNull(db) { DATABASE_IS_NULL }

      val collection = db.getCollection("CompletedSets")

      requireNotNull(collection) { COLLECTION_IS_NULL }

      val queryBuilder =
        QueryBuilder.select(SelectResult.all())
          .from(DataSource.collection(collection)) // Replace with your database instance
          .where(
            Expression.property("exerciseID").equalTo(Expression.string(exerciseID.toString()))
              .and(Expression.property("setCompleted").equalTo(Expression.booleanValue(true)))
          )
          .orderBy(Ordering.property("dateCreated").descending()) // Get latest set
          .limit(Expression.intValue(1))

      val flow = queryBuilder.queryChangeFlow().map {
        mapQueryChangeToSet(it)
      }.flowOn(Dispatchers.IO)

      queryBuilder.execute()

      return flow
    } catch (e: Exception) {
      Log.e(e.message, e.stackTraceToString())
    }

    return emptyFlow()
  }

  override fun getCompletedWorkouts(): List<Workout>? {
    try {
      requireNotNull(db) { DATABASE_IS_NULL }

      val workoutCollection = db.getCollection("CompletedWorkouts")
      requireNotNull(workoutCollection) { COLLECTION_IS_NULL }

      val setCollection = db.getCollection("CompletedSets")
      requireNotNull(setCollection) { COLLECTION_IS_NULL }

      val completedWorkoutQuery =
        QueryBuilder.select(SelectResult.all())
          .from(DataSource.collection(workoutCollection)) // Replace with your database instance
          .where(
            Expression.property("isCompleted").equalTo(Expression.booleanValue(true)).and(
              Expression.property("isPublic").equalTo(Expression.booleanValue(false))
            ).and(
              Expression.property("ownerID").equalTo(Expression.string(user.id.toHexString()))
            )
          )
          .orderBy(Ordering.property("dateCreated").descending())

      val workouts = completedWorkoutQuery.execute().allResults()
        .map {
          withUnknownKeys.decodeFromString<WorkoutDao>(it.toJSON()).Workout
        }

      val workoutIds = workouts.map { it.id.toString() }

      val workoutIDClause = workoutIds.joinToString(",") { "'$it'" }.let { "[$it]" }

      val setsQuery = "SELECT * FROM `CompletedSets` " +
              "WHERE workoutID IN $workoutIDClause" +
              " AND isPublic = false" +
              " AND ownerID = '${user.id.toHexString()}'"

      val setsResult = db.createQuery(setsQuery).execute().allResults()
      val sets = setsResult.mapNotNull {
        try {
          withUnknownKeys.decodeFromString<SetDao>(it.toJSON()).Set
        } catch (e: Exception) {
          null
        }
      }

      val setsByWorkoutID = sets.groupBy { it.workoutID }

      workouts.forEach { workout ->
        val relevantSetsForWorkout = setsByWorkoutID[workout.id] ?: emptyList()
        // Further group by exerciseSetsID for efficiency if needed, as discussed before
        val setsByExerciseSetsID = relevantSetsForWorkout.groupBy { it.exerciseSetsID }

        workout.workoutItems.forEach { workoutItem ->
          when (workoutItem) {
            is ExerciseSets -> {
              workoutItem.sets = (setsByExerciseSetsID[workoutItem.id] ?: emptyList())
                .sortedBy { it.setNumber }
                .toMutableList() // Or toList() if immutability is preferred
            }

            is Superset -> {
              workoutItem.exercisesSets.forEach { exerciseSets ->
                exerciseSets.sets = (setsByExerciseSetsID[exerciseSets.id] ?: emptyList())
                  .sortedBy { it.setNumber }
                  .toMutableList() // Or toList()
              }
            }
          }
        }
      }

      return workouts
    } catch (e: Exception) {
      Log.e(e.message, e.stackTraceToString())
    }
    return null
  }

  override fun getCompletedWorkoutByID(workoutID: ObjectId): Workout? {
    try {
      requireNotNull(db) { DATABASE_IS_NULL }

      val workoutCollection = db.getCollection("CompletedWorkouts")
      requireNotNull(workoutCollection) { COLLECTION_IS_NULL }

      val setCollection = db.getCollection("CompletedSets")
      requireNotNull(setCollection) { COLLECTION_IS_NULL }

      val completedWorkoutQueryBuilder =
        QueryBuilder.select(SelectResult.all())
          .from(DataSource.collection(workoutCollection)) // Replace with your database instance
          .where(
            Expression.property("id").equalTo(Expression.string(workoutID.toString()))
          )

      val completedSetsQueryBuilder =
        QueryBuilder.select(SelectResult.all())
          .from(DataSource.collection(setCollection)) // Replace with your database instance
          .where(
            Expression.property("workoutID").equalTo(Expression.string(workoutID.toString()))
          )

      val workoutResult =
        completedWorkoutQueryBuilder.execute().allResults().firstOrNull()?.toJSON()

      val workout = workoutResult?.let { withUnknownKeys.decodeFromString<WorkoutDao>(it).Workout }

      val setsResult = completedSetsQueryBuilder.execute().allResults()
      val sets = setsResult.map { withUnknownKeys.decodeFromString<SetDao>(it.toJSON()).Set }

      workout?.let {
        it.workoutItems.forEach { workoutItem ->
          when (workoutItem) {
            is ExerciseSets -> {
              workoutItem.sets = sets.filter { set -> set.exerciseSetsID == workoutItem.id }
                .sortedBy { set -> set.setNumber }.toMutableList()
            }

            is Superset -> {
              workoutItem.exercisesSets.forEach { exerciseSets ->
                exerciseSets.sets = sets.filter { set -> set.exerciseSetsID == exerciseSets.id }
                  .sortedBy { set -> set.setNumber }.toMutableList()
              }
            }
          }
        }
      }

      return workout
    } catch (e: Exception) {
      Log.e(e.message, e.stackTraceToString())
    }

    return null
  }

  override fun getLatestPreviousIdenticalWorkout(currentWorkout: Workout): Workout? {
    try {
      requireNotNull(db) { DATABASE_IS_NULL }

      val collection = db.getCollection("CompletedWorkouts")

      requireNotNull(collection) { COLLECTION_IS_NULL }

      val queryBuilder =
        QueryBuilder.select(SelectResult.all())
          .from(DataSource.collection(collection)) // Replace with your database instance
          .where(
            Expression.property("hashedSignature")
              .equalTo(Expression.string(currentWorkout.hashedSignature))
              .and(Expression.property("isCompleted").equalTo(Expression.booleanValue(true)))
              .and(
                Expression.property("programID")
                  .notEqualTo(Expression.string(currentWorkout.programID.toString()))
              )
          )
          .orderBy(Ordering.property("dateCreated").descending()) // Get latest set
          .limit(Expression.intValue(1))

      val result = queryBuilder.execute().allResults().firstOrNull()?.toJSON()

      return result?.let { withUnknownKeys.decodeFromString<WorkoutDao>(it).Workout }
    } catch (e: Exception) {
      Log.e(e.message, e.stackTraceToString())
    }

    return null
  }

  override suspend fun upsertCompletedWorkout(workout: Workout) {
    try {
      requireNotNull(db) { DATABASE_IS_NULL }
      val completedWorkoutsCollection = db.getCollection("CompletedWorkouts")
      requireNotNull(completedWorkoutsCollection) { COLLECTION_IS_NULL }

      val completedSetsCollection = db.getCollection("CompletedSets")
      requireNotNull(completedSetsCollection) { COLLECTION_IS_NULL }

      val completedSets = workout.workoutItems.flatMap { workoutItem ->
        when (workoutItem) {
          is ExerciseSets -> workoutItem.sets
          is Superset -> workoutItem.exercisesSets.flatMap { it.sets }
        }
      }

      if (completedSets.isEmpty()) return

      db.inBatch(UnitOfWork {
        completedSets.forEach { set ->
          val setIdStr = set.id.toHexString()
          try {
            val setJsonContent = Json.encodeToString(set)
            completedSetsCollection.getDocument(set.id.toHexString())?.toMutable()?.let {
              it.setJSON(setJsonContent)
              completedSetsCollection.save(it)
            } ?: completedSetsCollection.save(MutableDocument(set.id.toHexString(), setJsonContent))
          } catch (e: kotlinx.serialization.SerializationException) {
            Log.e(
              "Failed to serialize set $setIdStr for workout ${workout.id}",
              e.stackTraceToString()
            )
//            throw e // Or handle as per your strategy
          } catch (e: Exception) { // Catch generic exception from DB for now
            Log.e(
              "Failed to save set $setIdStr for workout ${workout.id} to DB",
              e.stackTraceToString()
            )
//            throw ("Failed to save set $setIdStr", e) // Wrap in custom exception
          }

        }
      })

      // Create a new workout instance with all set data cleared,
      val newWorkout = workout.copy(workoutItems = workout.workoutItems.map { workoutItem ->
        when (workoutItem) {
          is ExerciseSets -> {
            workoutItem.copy(sets = mutableListOf<Set>())
          }

          is Superset -> {
            workoutItem.copy(exercisesSets = workoutItem.exercisesSets.map { exerciseSets ->
              exerciseSets.copy(sets = mutableListOf<Set>())
            })
          }
        }
      })

      val workoutJson = Json.encodeToString(newWorkout)
      completedWorkoutsCollection.getDocument(workout.id.toHexString())?.toMutable()?.let {
        it.setJSON(workoutJson)
        completedWorkoutsCollection.save(it)
      } ?: completedWorkoutsCollection.save(MutableDocument(workout.id.toHexString(), workoutJson))


      // TODO Implement using https://aistudio.google.com/prompts/1jjNZH2UMm80XgZpwu8_qy9Mu1vekKtII

//      collection.save(document)

    } catch (e: Exception) {
      Log.e(e.message, e.stackTraceToString())
    }

  }
}

