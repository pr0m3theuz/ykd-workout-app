@file:OptIn(ExperimentalUuidApi::class, ExperimentalUuidApi::class, ExperimentalTime::class)

package com.example.myapplication.common.infrastructure.persistence

import android.util.Log
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
import com.example.myapplication.common.domain.model.CompletedSetsDao
import com.example.myapplication.common.domain.model.CompletedWorkoutsDao
import com.example.myapplication.common.domain.model.ExerciseDao
import com.example.myapplication.common.domain.model.ExerciseSets
import com.example.myapplication.common.domain.model.MuscleDao
import com.example.myapplication.common.domain.model.MuscleExerciseDao
import com.example.myapplication.common.domain.model.MuscleFractionalSets
import com.example.myapplication.common.domain.model.Set
import com.example.myapplication.common.domain.model.SetType
import com.example.myapplication.common.domain.model.Superset
import com.example.myapplication.common.domain.model.TotalWorkoutVolume
import com.example.myapplication.common.domain.model.UserProfileManager
import com.example.myapplication.common.domain.model.Workout
import com.example.myapplication.common.domain.model.WorkoutAnalytics
import com.example.myapplication.common.infrastructure.database.DatabaseManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toInstant
import kotlinx.datetime.toKotlinLocalDateTime
import kotlinx.datetime.toKotlinTimeZone
import kotlinx.serialization.json.Json
import org.bson.types.ObjectId
import org.koin.java.KoinJavaComponent.inject
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters
import kotlin.time.Duration.Companion.days
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlin.time.toDuration
import kotlin.time.toKotlinInstant
import kotlin.uuid.ExperimentalUuidApi

private const val DATABASE_IS_NULL = "Completed Repository: Database does not exist."

private const val COLLECTION_IS_NULL =
  "Completed Repository: Completed * Collection does not exist in the Database."

class CompletedWorkoutRepositoryImpl(databaseManager: DatabaseManager) :
  CompletedWorkoutRepository {
  val userProfileManager: UserProfileManager by inject(UserProfileManager::class.java)

  val db: Database? = databaseManager.database

  // TODO Decide if I should create a repository for user data
  val user = userProfileManager.getCurrentUser()

  private val withUnknownKeys = Json { ignoreUnknownKeys = true }

  private val withDefaults = Json { encodeDefaults = true }

  private fun mapQueryChangeToSet(queryChange: QueryChange): List<Set> {
    val sets = mutableListOf<Set>() // 1
    queryChange.results?.let { results -> // 2
      results.forEach { result -> // 3
        val json = result.toJSON() // 4
        val set = withUnknownKeys.decodeFromString<CompletedSetsDao>(json).CompletedSets // 5
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
        val workout =
          withUnknownKeys.decodeFromString<CompletedWorkoutsDao>(json).CompletedWorkouts // 5
        workouts.add(workout) // 6
      }
    }
    return workouts // 7
  }

  override fun getPreviousSetForExercise(
    exerciseID: ObjectId,
    setNumber: Int,
    setType: SetType
  ): Set? {
    try {

//      TODO Doesn't work as intended. need to use a more sophisticated query to match setType and SetNumber
      requireNotNull(db) { DATABASE_IS_NULL }

      val collection = db.getCollection("CompletedSets")

      requireNotNull(collection) { COLLECTION_IS_NULL }
      // TODO Missing ownerID = user.id.toHexString()
      val queryBuilder =
        QueryBuilder.select(SelectResult.all())
          .from(DataSource.collection(collection)) // Replace with your database instance
          .where(
            Expression.property("exerciseID")
              .equalTo(Expression.string(exerciseID.toString()))
              .and(Expression.property("setNumber").equalTo(Expression.intValue(setNumber)))
              .and(Expression.property("setTypeID").equalTo(Expression.intValue(setType.setTypeID)))
              .and(Expression.property("setCompleted").equalTo(Expression.booleanValue(true)))
          )
          .orderBy(Ordering.property("dateCreated").descending()) // Get latest set
          // TODO order by setNumber
          .limit(Expression.intValue(1))

      // TODO change the limit to be the number of sets to return (max of 10), return 1 list of sets for Warm Up Sets and 1 for Working Sets
      //  zip previous sets to current sets

      val result = queryBuilder.execute().allResults().firstOrNull()?.toJSON()

      return result?.let { withUnknownKeys.decodeFromString<CompletedSetsDao>(it).CompletedSets }
    } catch (e: Exception) {
      Log.e(e.message, e.stackTraceToString())
    }
    return null
  }

  override fun getPreviousSetsForExercise(
    exerciseID: ObjectId,
  ): List<Set> {
    try {

//      TODO Doesn't work as intended. need to use a more sophisticated query to match setType and SetNumber
      requireNotNull(db) { DATABASE_IS_NULL }

      val collection = db.getCollection("CompletedSets")

      requireNotNull(collection) { COLLECTION_IS_NULL }
      // TODO Missing ownerID = user.id.toHexString()
      // TODO change the limit to be the number of sets to return (max of 10), return 1 list of sets for Warm Up Sets and 1 for Working Sets
      //  zip previous sets to current sets

      val warmUpQueryBuilder =
        QueryBuilder.select(SelectResult.all())
          .from(DataSource.collection(collection)) // Replace with your database instance
          .where(
            Expression.property("exerciseID")
              .equalTo(Expression.string(exerciseID.toString()))
              .and(Expression.property("setTypeID").equalTo(Expression.intValue(SetType.warmUpSet.setTypeID)))
              .and(Expression.property("setCompleted").equalTo(Expression.booleanValue(true)))
          )
          .orderBy(Ordering.property("dateCreated").descending()) // Get latest set
          .limit(Expression.intValue(10))
//      , Ordering.property("setNumber").ascending()

      val normalSetQueryBuilder =
        QueryBuilder.select(SelectResult.all())
          .from(DataSource.collection(collection)) // Replace with your database instance
          .where(
            Expression.property("exerciseID")
              .equalTo(Expression.string(exerciseID.toString()))
//              .and(Expression.property("setTypeID").equalTo(Expression.intValue(SetType.normalSet.setTypeID)))
              .and(Expression.property("setCompleted").equalTo(Expression.booleanValue(true)))
          )
          .orderBy(Ordering.property("dateCreated").descending()) // Get latest set
          .limit(Expression.intValue(50))
      //      , Ordering.property("setNumber").ascending()

      val warmUpResult = warmUpQueryBuilder.execute().allResults().map {
        try {
          val json = it.toJSON()
          withUnknownKeys.decodeFromString<CompletedSetsDao>(json).CompletedSets
        } catch (e: Exception) {
          Log.e(e.message, e.stackTraceToString())
          null
        }
      }

      val normalSetResult = normalSetQueryBuilder.execute().allResults().mapNotNull {
        try {
          val json = it.toJSON()
          withUnknownKeys.decodeFromString<CompletedSetsDao>(json).CompletedSets
        } catch (e: Exception) {
          Log.e(e.message, e.stackTraceToString())
          null
        }
      }

      return normalSetResult
    } catch (e: Exception) {
      Log.e(e.message, e.stackTraceToString())
    }
    return emptyList()
  }

  override fun getCompletedWorkoutsCount(): Int {
    try {
      requireNotNull(db) { DATABASE_IS_NULL }
      val collection = db.getCollection("CompletedWorkouts")

      requireNotNull(collection) { COLLECTION_IS_NULL }

      val query = "SELECT Count(*) as count FROM `CompletedWorkouts` WHERE isCompleted = true"
      //        QueryBuilder.select(
      //          SelectResult.expression(Function.count(Expression.string("*"))).`as`("count")
      //        )
      //          .from(DataSource.collection(collection))
      //          .where(
      //            Expression.property("isCompleted")
      //              .equalTo(Expression.booleanValue(true))
      //
      // .and(Expression.property("ownerID").equalTo(Expression.string(user.id.toHexString())))
      //          )

      //      val query =
      // QueryBuilder.select(SelectResult.all()).from(DataSource.collection(collection))

      val workoutCount =
        db.createQuery(query).execute().allResults().firstOrNull()?.getInt("count") ?: 0

      return workoutCount
    } catch (e: Exception) {
      Log.e(e.message, e.stackTraceToString())
    }
    return 0
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
            Expression.property("exerciseID")
              .equalTo(Expression.string(exerciseID.toString()))
              .and(Expression.property("setCompleted").equalTo(Expression.booleanValue(true)))
          )
          .orderBy(Ordering.property("dateCreated").descending()) // Get latest set
          .limit(Expression.intValue(1))

      val flow =
        queryBuilder.queryChangeFlow().map { mapQueryChangeToSet(it) }.flowOn(Dispatchers.IO)

      queryBuilder.execute()

      return flow
    } catch (e: Exception) {
      Log.e(e.message, e.stackTraceToString())
    }

    return emptyFlow()
  }


  // TODO Code Review
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
            Expression.property("isCompleted").equalTo(Expression.booleanValue(true))
            //              .and(
            //
            // Expression.property("ownerID").equalTo(Expression.string(user.id.toHexString()))
            //            )
          )
      //          .orderBy(Ordering.property("workoutEnded").descending())

      val workouts =
        completedWorkoutQuery.execute().allResults().map {
//          Log.d("CompletedWorkoutRepositoryImpl.getCompletedWorkouts()", it.toJSON())
          withUnknownKeys.decodeFromString<CompletedWorkoutsDao>(it.toJSON()).CompletedWorkouts
        }

      val workoutIds = workouts.map { it.id.toString() }

      val workoutIDClause = workoutIds.joinToString(",") { "'$it'" }.let { "[$it]" }

      val setsQuery = "SELECT * FROM `CompletedSets` WHERE workoutID IN $workoutIDClause" // +
      //              " AND ownerID = '${user.id.toHexString()}'"

      val setsResult = db.createQuery(setsQuery).execute().allResults()
      val sets =
        setsResult.mapNotNull {
          try {
            withUnknownKeys.decodeFromString<CompletedSetsDao>(it.toJSON()).CompletedSets
          } catch (e: Exception) {
            Log.e(e.message, e.stackTraceToString())
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
              workoutItem.sets =
                (setsByExerciseSetsID[workoutItem.id] ?: emptyList())
                  .sortedBy { it.setNumber }
                  .toMutableList() // Or toList() if immutability is preferred
            }

            is Superset -> {
              workoutItem.exercisesSets.forEach { exerciseSets ->
                exerciseSets.sets =
                  (setsByExerciseSetsID[exerciseSets.id] ?: emptyList())
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

  // TODO Code Review
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
          .where(Expression.property("id").equalTo(Expression.string(workoutID.toString())))

      val completedSetsQueryBuilder =
        QueryBuilder.select(SelectResult.all())
          .from(DataSource.collection(setCollection)) // Replace with your database instance
          .where(Expression.property("workoutID").equalTo(Expression.string(workoutID.toString())))

      val workoutResult =
        completedWorkoutQueryBuilder.execute().allResults().firstOrNull()?.toJSON()

      val workout =
        workoutResult?.let {
          withUnknownKeys.decodeFromString<CompletedWorkoutsDao>(it).CompletedWorkouts
        }

      val setsResult = completedSetsQueryBuilder.execute().allResults()
      val sets =
        setsResult.map {
          withUnknownKeys.decodeFromString<CompletedSetsDao>(it.toJSON()).CompletedSets
        }

      workout?.let {
        it.workoutItems.forEach { workoutItem ->
          when (workoutItem) {
            is ExerciseSets -> {
              workoutItem.sets =
                sets
                  .filter { set -> set.exerciseSetsID == workoutItem.id }
                  .sortedBy { set -> set.setNumber }
                  .toMutableList()
            }

            is Superset -> {
              workoutItem.exercisesSets.forEach { exerciseSets ->
                exerciseSets.sets =
                  sets
                    .filter { set -> set.exerciseSetsID == exerciseSets.id }
                    .sortedBy { set -> set.setNumber }
                    .toMutableList()
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

      return result?.let {
        withUnknownKeys.decodeFromString<CompletedWorkoutsDao>(it).CompletedWorkouts
      }
    } catch (e: Exception) {
      Log.e(e.message, e.stackTraceToString())
    }

    return null
  }

  // TODO Code Review
  override suspend fun upsertCompletedWorkout(workout: Workout) {
    try {
      requireNotNull(db) { DATABASE_IS_NULL }
      val completedWorkoutsCollection = db.getCollection("CompletedWorkouts")
      requireNotNull(completedWorkoutsCollection) { COLLECTION_IS_NULL }

      val completedSetsCollection = db.getCollection("CompletedSets")
      requireNotNull(completedSetsCollection) { COLLECTION_IS_NULL }

      val completedSets =
        workout.workoutItems.flatMap { workoutItem ->
          when (workoutItem) {
            is ExerciseSets -> workoutItem.sets
            is Superset -> workoutItem.exercisesSets.flatMap { it.sets }
          }
        }

      if (completedSets.isEmpty()) return

      val workoutID = workout.id
      val workoutIDStr = "Workout:${workout.id}"

      val TAG = "CompletedWorkoutRepositoryImpl.upsertCompletedWorkout():"

      require(completedSets.all { set -> set.workoutID == workoutID }) {
        "$TAG All sets workoutIDs does not match Workout.id!"
      }

      db.inBatch(
        UnitOfWork {
          completedSets.forEach { set ->
            require(set.workoutID == workoutID) { "$TAG Set workoutID does not match Workout.id!" }
            val setIdStr = "Set:${set.id}"
            try {
              val setJsonContent = withDefaults.encodeToString(set)
              completedSetsCollection.getDocument(setIdStr)?.toMutable()?.let {
                it.setJSON(setJsonContent)
                Log.i(
                  "CompletedWorkoutRepositoryImpl.upsertCompletedWorkout()",
                  "Updating set $setJsonContent",
                )
                completedSetsCollection.save(it)
              }
                ?: Log.i(
                  "CompletedWorkoutRepositoryImpl.upsertCompletedWorkout()",
                  "Saving set $setJsonContent",
                )
              completedSetsCollection.save(MutableDocument(setIdStr, setJsonContent))
            } catch (e: kotlinx.serialization.SerializationException) {
              Log.e(
                "Failed to serialize set: $setIdStr for workout: $workoutIDStr",
                e.stackTraceToString(),
              )
              //            throw e // Or handle as per your strategy
            } catch (e: Exception) { // Catch generic exception from DB for now
              Log.e(
                "Failed to save set: $setIdStr for workout: $workoutIDStr to DB",
                e.stackTraceToString(),
              )
              //            throw ("Failed to save set $setIdStr", e) // Wrap in custom exception
            }
          }
        }
      )

      // Create a new workout instance with all set data cleared,
      val newWorkout =
        workout.copy(
          workoutItems =
            workout.workoutItems.map { workoutItem ->
              when (workoutItem) {
                is ExerciseSets -> {
                  workoutItem.copy(sets = mutableListOf<Set>())
                }

                is Superset -> {
                  workoutItem.copy(
                    exercisesSets =
                      workoutItem.exercisesSets.map { exerciseSets ->
                        exerciseSets.copy(sets = mutableListOf<Set>())
                      }
                  )
                }
              }
            }
        )

      val workoutJson = withDefaults.encodeToString(newWorkout)
      completedWorkoutsCollection.getDocument(workoutIDStr)?.toMutable()?.let {
        it.setJSON(workoutJson)
        Log.i(
          "CompletedWorkoutRepositoryImpl.upsertCompletedWorkout()",
          "Updating workout $workoutJson",
        )
        completedWorkoutsCollection.save(it)
      }
        ?: Log.i(
          "CompletedWorkoutRepositoryImpl.upsertCompletedWorkout()",
          "Saving workout $workoutJson",
        )
      completedWorkoutsCollection.save(MutableDocument(workoutIDStr, workoutJson))

      // TODO Implement using https://aistudio.google.com/prompts/1jjNZH2UMm80XgZpwu8_qy9Mu1vekKtII

    } catch (e: Exception) {
      Log.e(e.message, e.stackTraceToString())
    }
  }

  // TODO Code Review
  override suspend fun deleteCompletedWorkout(workout: Workout): Boolean {
    try {
      requireNotNull(db) { DATABASE_IS_NULL }
      val completedWorkoutsCollection = db.getCollection("CompletedWorkouts")
      requireNotNull(completedWorkoutsCollection) { COLLECTION_IS_NULL }

      val completedSetsCollection = db.getCollection("CompletedSets")
      requireNotNull(completedSetsCollection) { COLLECTION_IS_NULL }

      val workoutQuery =
        QueryBuilder.select(SelectResult.all())
          .from(DataSource.collection(completedWorkoutsCollection))
          .where(Expression.property("id").equalTo(Expression.string(workout.id.toHexString())))

      val completedSets =
        workout.workoutItems.flatMap { workoutItem ->
          when (workoutItem) {
            is ExerciseSets -> workoutItem.sets
            is Superset -> workoutItem.exercisesSets.flatMap { it.sets }
          }
        }

      completedWorkoutsCollection.getDocument("Workout:${workout.id}")?.let {
        completedWorkoutsCollection.delete(it)
        Log.d(
          "CompletedWorkoutRepositoryImpl.deleteCompletedWorkout()",
          "Deleted workout: Workout:${workout.id}",
        )
      }

      completedSets.forEach { set ->
        completedSetsCollection.getDocument("Set:${set.id}")?.let { setDocument ->
          completedSetsCollection.delete(setDocument)
          Log.d(
            "CompletedWorkoutRepositoryImpl.deleteCompletedWorkout()",
            "Deleted set: Set:${set.id}",
          )
        }
      }

      return true
    } catch (e: Exception) {
      Log.e(e.message, e.stackTraceToString())
    }
    return false
  }

  // TODO Code Review
  override fun getWorkoutAnalyticsForPeriod(
    // TODO Confirm DateTime is adjusted for user timezone
    startDate: LocalDateTime,
    endDate: LocalDateTime,
  ): WorkoutAnalytics? {
    try {
      requireNotNull(db) { DATABASE_IS_NULL }
      val completedWorkoutsCollection = db.getCollection("CompletedWorkouts")
      requireNotNull(completedWorkoutsCollection) { COLLECTION_IS_NULL }

      //      val start: Date = Date(startDate.year, startDate.month.ordinal, startDate.day)
      //      val end: Date = Date(endDate.year, endDate.month.ordinal, endDate.day)

      // TODO Add type if needed

      val query =
        "SELECT Count(*) as count " +
                "FROM `CompletedWorkouts` WHERE dateCreated BETWEEN '$startDate' AND '$endDate'"

      val workoutCount =
        db.createQuery(query).execute().allResults().firstOrNull()?.getInt("count") ?: 0

      //        QueryBuilder.select(
      //            SelectResult.expression(Function.count(Expression.string("*"))).`as`("count")
      //          )
      //          .from(DataSource.collection(completedWorkoutsCollection))
      //          .where(
      //            Expression.property("dateCreated").between(Expression.date(start),
      // Expression.date(end))
      //          ).execute().allResults().firstOrNull()?.getInt("count")

      val totalDuration =
        db
          .createQuery(
            "SELECT SUM(workoutDuration) as duration " +
                    "FROM `CompletedWorkouts` WHERE dateCreated BETWEEN '$startDate' AND '$endDate'"
          )
          .execute()
          .allResults()
          .firstOrNull()
          ?.getInt("duration")
          ?.toDuration(DurationUnit.MILLISECONDS)
          ?.inWholeMinutes ?: 0.toDuration(DurationUnit.MINUTES).inWholeMinutes

      val totalVolumeInPounds =
        db
          .createQuery(
            "SELECT SUM(totalVolumeInPounds) as totalVolumeInPounds " +
                    "FROM `CompletedWorkouts` WHERE dateCreated BETWEEN '$startDate' AND '$endDate'"
          )
          .execute()
          .allResults()
          .firstOrNull()
          ?.getDouble("totalVolumeInPounds") ?: 0.0

      val totalVolumeInKilograms =
        db
          .createQuery(
            "SELECT SUM(totalVolumeInKilograms) as totalVolumeInKilograms  " +
                    "FROM `CompletedWorkouts` WHERE dateCreated BETWEEN '$startDate' AND '$endDate'"
          )
          .execute()
          .allResults()
          .firstOrNull()
          ?.getDouble("totalVolumeInKilograms") ?: 0.0

      return WorkoutAnalytics(
        workoutCount,
        totalDuration,
        totalVolumeInPounds,
        totalVolumeInKilograms,
      )
    } catch (e: Exception) {
      Log.e(e.message, e.stackTraceToString())
    }

    return null
  }

  // TODO Code Review
  override fun getWorkoutAnalyticsForLifetime(): WorkoutAnalytics? {
    try {
      requireNotNull(db) { DATABASE_IS_NULL }
      val completedWorkoutsCollection = db.getCollection("CompletedWorkouts")
      requireNotNull(completedWorkoutsCollection) { COLLECTION_IS_NULL }

      // TODO Add type if needed

      val workoutCount =
        db
          .createQuery("SELECT Count(*) as count " + "FROM `CompletedWorkouts`")
          .execute()
          .allResults()
          .firstOrNull()
          ?.getInt("count") ?: 0

      val totalDuration =
        db
          .createQuery("SELECT SUM(workoutDuration) as duration " + "FROM `CompletedWorkouts`")
          .execute()
          .allResults()
          .firstOrNull()
          ?.getInt("duration")
          ?.toDuration(DurationUnit.MILLISECONDS)
          ?.inWholeMinutes ?: 0.toDuration(DurationUnit.MINUTES).inWholeMinutes

      val totalVolumeInPounds =
        db
          .createQuery(
            "SELECT SUM(totalVolumeInPounds) as totalVolumeInPounds " + "FROM `CompletedWorkouts`"
          )
          .execute()
          .allResults()
          .firstOrNull()
          ?.getDouble("totalVolumeInPounds") ?: 0.0

      val totalVolumeInKilograms =
        db
          .createQuery(
            "SELECT SUM(totalVolumeInKilograms) as totalVolumeInKilograms  " +
                    "FROM `CompletedWorkouts`"
          )
          .execute()
          .allResults()
          .firstOrNull()
          ?.getDouble("totalVolumeInKilograms") ?: 0.0

      return WorkoutAnalytics(
        workoutCount,
        totalDuration,
        totalVolumeInPounds,
        totalVolumeInKilograms,
      )
    } catch (e: Exception) {
      Log.e(e.message, e.stackTraceToString())
    }

    return null
  }

  override fun getTotalVolumeData(): List<TotalWorkoutVolume> {
    try {
      requireNotNull(db) { DATABASE_IS_NULL }
      val completedWorkoutsCollection = db.getCollection("CompletedWorkouts")
      requireNotNull(completedWorkoutsCollection) { COLLECTION_IS_NULL }

      // TODO Add type if needed

      val query =
        "SELECT totalVolumeInPounds, totalVolumeInKilograms, workoutEnded " +
                "FROM `CompletedWorkouts`"

      val totalVolume =
        db
          .createQuery(query)
          .execute()
          .allResults()
          .map { result ->
            val workoutEnded = result.getDate("workoutEnded")?.toInstant()?.toKotlinInstant()
            val totalVolumeInPounds = result.getDouble("totalVolumeInPounds")
            val totalVolumeInKilograms = result.getDouble("totalVolumeInKilograms")

            return@map if (workoutEnded != null) {
              TotalWorkoutVolume(workoutEnded, totalVolumeInPounds, totalVolumeInKilograms)
            } else {
              null
            }
          }
          .filterNotNull()

      if (totalVolume.isEmpty()) {
        return emptyList()
      }

      return totalVolume
    } catch (e: Exception) {
      Log.e(e.message, e.stackTraceToString())
    }

    return emptyList()
  }

  override suspend fun getWeeklyTotalSetsPerMuscle(
    startDate: LocalDateTime,
    endDate: LocalDateTime
  ): List<MuscleFractionalSets> {
    try {
      requireNotNull(db) { DATABASE_IS_NULL }
      val completedSetsCollection = db.getCollection("CompletedSets")
      requireNotNull(completedSetsCollection) { COLLECTION_IS_NULL }

      val muscleExerciseCollection = db.getCollection("MuscleExercise")
      requireNotNull(muscleExerciseCollection) { COLLECTION_IS_NULL }

      val muscleCollection = db.getCollection("Muscle")
      requireNotNull(muscleCollection) { COLLECTION_IS_NULL }

      val setsQuery =
        "SELECT * FROM `CompletedSets` WHERE dateCreated BETWEEN '$startDate' AND '$endDate'"

      Log.i("CompletedWorkoutRepositoryImpl.getWeeklyTotalSetsPerMuscle()", "Query: $setsQuery")

      val sets =
        db.createQuery(setsQuery).execute().allResults().map { result ->
          withUnknownKeys.decodeFromString<CompletedSetsDao>(result.toJSON()).CompletedSets
        }

      val exerciseIDs = sets.mapNotNull { it.exerciseID }

      val exercisesIDClause = exerciseIDs.joinToString(",") { "'$it'" }.let { "[$it]" }

      val exercisesQuery = "SELECT * FROM `Exercise` WHERE id IN $exercisesIDClause"

      val exercises =
        db.createQuery(exercisesQuery).execute().allResults().map { result ->
          withUnknownKeys.decodeFromString<ExerciseDao>(result.toJSON()).Exercise
        }

      val muscleExerciseWithMuscleList = exercises.flatMap { exercise ->

        val muscleExerciseQuery =
          "SELECT * FROM `MuscleExercise` WHERE exerciseID = '${exercise.exrxExerciseID}' and (roleFunction = 'target' or roleFunction = 'synergist')"

        val muscleExerciseResult =
          db.createQuery(muscleExerciseQuery).execute().allResults().mapNotNull {
            try {
              withUnknownKeys.decodeFromString<MuscleExerciseDao>(it.toJSON()).MuscleExercise
            } catch (e: Exception) {
              Log.e(e.message, e.stackTraceToString())
              null
            }
          }

        val list = muscleExerciseResult.mapNotNull { muscleExercise ->
          val muscleQuery =
            QueryBuilder.select(SelectResult.all())
              .from(DataSource.collection(muscleCollection))
              .where(Expression.property("id").equalTo(Expression.string(muscleExercise.muscleID)))

          val result =
            muscleQuery.execute().allResults().firstOrNull()?.toJSON()?.let {
              withUnknownKeys.decodeFromString<MuscleDao>(it).Muscle
            }

          result?.let { muscle ->
            MuscleFractionalSets(muscle, muscleExercise, exercise.name, 0.0)
          }
        }

        list
      }

      val result = muscleExerciseWithMuscleList.map { muscleExercise ->
        val exercise =
          exercises.firstOrNull { exercise -> exercise.exrxExerciseID == muscleExercise.muscleExercise.exerciseID }

        val setsPerformed = sets.count { set -> set.exerciseID == exercise?.id }.toDouble()

        val fractionalSets = when (muscleExercise.muscleExercise.roleFunction) {
          "target" -> setsPerformed
          "synergist" -> setsPerformed.div(2)
          else -> 0.toDouble()
        }

        muscleExercise.copy(fractionalSets = fractionalSets)
      }

      return result
    } catch (e: Exception) {
      Log.e(e.message, e.stackTraceToString())
    }
    return emptyList()
  }


  override fun getWorkoutStreakWeek(): Int? {
    try {
      requireNotNull(db) { DATABASE_IS_NULL }
      val completedWorkoutsCollection = db.getCollection("CompletedWorkouts")
      requireNotNull(completedWorkoutsCollection) { COLLECTION_IS_NULL }

      val timezone = java.util.TimeZone.getDefault().toZoneId().toKotlinTimeZone()

      val startDate =
        LocalDate.now()
          .with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
          .atStartOfDay()
          .toKotlinLocalDateTime()
          .toInstant(timezone)

      val endDate =
        LocalDate.now()
          .with(TemporalAdjusters.next(DayOfWeek.MONDAY))
          .atStartOfDay()
          .minusNanos(1)
          .toKotlinLocalDateTime()
          .toInstant(timezone)

      // TODO Add type if needed

      val workoutEndedDateTimes =
        db
          .createQuery(
            "SELECT workoutEnded as workoutEnded " +
                    "FROM `CompletedWorkouts` ORDER BY workoutEnded DESC"
          )
          .execute()
          .allResults()
          .map { result ->
            result.getDate("workoutEnded")?.toInstant()?.toKotlinInstant()
            //
            // ?.toLocalDateTime(java.util.TimeZone.getDefault().toZoneId().toKotlinTimeZone())
          }

      val workoutWeekStreakMap: MutableMap<ClosedRange<Instant>, Int> = mutableMapOf()

      var dateRange = startDate.minus(7.days)..endDate.minus(7.days)

      while (true) {
        val workoutCountForWeek = workoutEndedDateTimes.filterNotNull().count { it in dateRange }

        if (workoutCountForWeek == 0) break
        workoutWeekStreakMap.put(dateRange, workoutCountForWeek)

        dateRange = dateRange.start.minus(7.days)..dateRange.endInclusive.minus(7.days)
      }

      //      val workoutWeekStreakCount = workoutWeekStreakMap.count()
      //
      //      var workoutWeekStreak = 0
      //      workoutWeekStreakMap.forEach { if (it.value > 0) workoutWeekStreak++ else
      // return@forEach }

      return workoutWeekStreakMap.count()
    } catch (e: Exception) {
      Log.e(e.message, e.stackTraceToString())
    }
    return null
  }
}
