@file:OptIn(ExperimentalUuidApi::class)

package com.example.myapplication.common.infrastructure.persistence

import android.util.Log
import com.couchbase.lite.Collection
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
import com.example.myapplication.common.infrastructure.database.DatabaseManager
import com.example.myapplication.common.domain.model.ExerciseNote
import com.example.myapplication.common.domain.model.ExerciseSets
import com.example.myapplication.common.domain.model.Mesocycle
import com.example.myapplication.common.domain.model.MesocycleDao
import com.example.myapplication.common.domain.model.Microcycle
import com.example.myapplication.common.domain.model.MicrocycleDao
import com.example.myapplication.common.domain.model.Set
import com.example.myapplication.common.domain.model.Workout
import com.example.myapplication.common.domain.model.WorkoutDao
import com.example.myapplication.common.domain.model.WorkoutNote
import com.example.myapplication.common.domain.model.WorkoutNoteDao
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

const val COLLECTION_IS_NULL_TEMPLATE =
  "Workout Repository: %s Collection does not exist in the Database."
const val WORKOUT_DAY_IS_NULL = "Workout Repository: Completed Workout Day is null!"
const val TAG = "WorkoutRepository" // For Logging

class WorkoutRepositoryImpl(private val databaseManager: DatabaseManager) : WorkoutRepository {

  val db: Database? = databaseManager.database

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

  override fun getWorkoutByID(workoutID: ObjectId): Workout? {
    try {
      requireNotNull(db) { DATABASE_IS_NULL }
      val collection = db.getCollection("Workout")

      requireNotNull(collection) { COLLECTION_IS_NULL }

      val queryBuilder =
        QueryBuilder.select(SelectResult.all())
          .from(DataSource.collection(collection)) // Replace with your database instance
          .where(Expression.property("id").equalTo(Expression.string(workoutID.toString())))

      val result = queryBuilder.execute().allResults().firstOrNull()?.toJSON()

      val workout = result?.let { withUnknownKeys.decodeFromString<WorkoutDao>(it).Workout }

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

  override fun getWorkoutsByMicrocycleID(
    microcycle: Microcycle,
    microcycleID: ObjectId
  ): List<Workout> {
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
        val workout = withUnknownKeys.decodeFromString<WorkoutDao>(json).Workout
        workouts.add(workout)
      }

//      val countWorkoutsQuery =
//        QueryBuilder.select(SelectResult.expression(Function.count(Expression.all())))
//          .from(DataSource.collection(collection)).where(
//            Expression.property("microcycleID").equalTo(Expression.string(microcycleID.toString()))
//          )
//      countWorkoutsQuery.execute().use { resultSets ->
//        resultSets.forEach {
//          Log.i(
//            "WorkoutRepositoryImpl.getWorkoutsByMicrocycleID()",
//            "Microcycle ID: ${microcycle.name}, Workout Count: ${it.getInt("count")}"
//          )
//        }
//      }

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

  // TODO Save Completed Workouts and Completed Sets to CompletedWorkout & CompletedSet Collections
  // TODO CompletedWorkout & CompletedSet Collections to Sync Gateway & Database
  // TODO Create update functions for Completed Workouts & Workout Templates
  // TODO Do the same for Programs
  override suspend fun insertWorkout(workout: Workout): Boolean {
    try {
      requireNotNull(db) { DATABASE_IS_NULL }
      val collection = db.getCollection("Workout")

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

  override suspend fun updateWorkout(workout: Workout): Boolean {
    try {
      requireNotNull(db) { DATABASE_IS_NULL }
      val collection = db.getCollection("Workout")

      requireNotNull(collection) { COLLECTION_IS_NULL }

      val mutableDoc = collection.getDocument(workout.id.toString())?.toMutable() // Not Necessary
      requireNotNull(mutableDoc) { "Workout Repository: Workout does not exist!" }

      val json = Json.encodeToString(workout)
      val document = MutableDocument(workout.id.toString(), json)

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

      val document = collection.getDocument(workout.id.toString())
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
        val note = withUnknownKeys.decodeFromString<WorkoutNoteDao>(json).item // 5
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

  override fun findNextWorkoutExerciseSets(
    exerciseSets: ExerciseSets,
    programID: ObjectId,
  ): Workout? {
    // Implement logic to find the next workout containing the given ExerciseSets within the program
    try {
      requireNotNull(db) { DATABASE_IS_NULL }

      val workoutCollection = db.getCollection("Workout")

      requireNotNull(workoutCollection) { COLLECTION_IS_NULL }

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
          .from(DataSource.collection(workoutCollection).`as`("workout"))
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
      // TODO return results
    } catch (e: Exception) {
      Log.e(e.message, e.stackTraceToString())
    }
    return null
  }

  override suspend fun updateSets(sets: MutableList<Set>) {
    // TODO Implement logic to update multiple sets
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

  private fun mapQueryChangeToMesocycle(queryChange: QueryChange): List<Mesocycle> {
    val mesocycles = mutableListOf<Mesocycle>() // 1
    queryChange.results?.let { results -> // 2
      results.forEach { result -> // 3
        val json = result.toJSON() // 4
        val mesocycle = withUnknownKeys.decodeFromString<MesocycleDao>(json).Mesocycle // 5
        mesocycles.add(mesocycle) // 6
      }
    }
    return mesocycles // 7
  }

  private fun mapQueryChangeToMicrocycle(queryChange: QueryChange): List<Microcycle> {
    val microcycles = mutableListOf<Microcycle>() // 1
    queryChange.results?.let { results -> // 2
      results.forEach { result -> // 3
        val json = result.toJSON() // 4
        val microcycle = withUnknownKeys.decodeFromString<MicrocycleDao>(json).Microcycle // 5
        microcycles.add(microcycle) // 6
      }
    }
    return microcycles // 7
  }

  override fun getNextWorkoutInProgram(completedWorkout: Workout): Workout? {
    try {
      // write query to check if this is the last workout in the microcycle.
      // if so, get the next microcycle then get the next workout in the microcycle
      // write query to check if this microcycle is the last microcycle in current microcycle's mesocycle.
      // if so, get the next mesocycle then get the next workout in the next microcycle

      requireNotNull(db) { DATABASE_IS_NULL }

      val currentWorkoutDay = requireNotNull(completedWorkout.day) { WORKOUT_DAY_IS_NULL }
      val workoutCollection = db.getCollection("Workout")
      val microcycleCollection = db.getCollection("Microcycle")
      val mesocycleCollection = db.getCollection("Mesocycle")
      val programCollection = db.getCollection("Program")

      requireNotNull(workoutCollection) { COLLECTION_IS_NULL }
      requireNotNull(microcycleCollection) { "Workout Repository: Microcycle Collection does not exist in the Database." }
      requireNotNull(mesocycleCollection) { "Workout Repository: Mesocycle Collection does not exist in the Database." }
      requireNotNull(programCollection) { "Workout Repository: Program Collection does not exist in the Database." }

      // 1. Try to find the next workout in the *current* microcycle
      // if currentWorkoutDay.plus(1) does not exist, then there are no more workouts in the microcycle
      val nextWorkoutInMicrocycle = findWorkoutByOrder(
        workoutCollection,
        completedWorkout.microcycleID.toString(),
        currentWorkoutDay.plus(1)
      )

      // if nextWorkoutInMicrocycle is null, then no workout exists
      if (nextWorkoutInMicrocycle != null) {
        Log.d(TAG, "Found next workout in same microcycle: ${nextWorkoutInMicrocycle.id}")
        return nextWorkoutInMicrocycle
      }

      // 2. If no next workout in microcycle, find the *current* microcycle to get its order
      Log.d(
        TAG,
        "No more workouts in microcycle ${completedWorkout.microcycleID}. Checking for next microcycle."
      )
      val currentMicrocycle =
        findMicrocycleById(microcycleCollection, completedWorkout.microcycleID.toString())
          ?: run {
            Log.e(
              TAG,
              "Could not find current microcycle with ID: ${completedWorkout.microcycleID}"
            )
            return null // Or throw an exception for data inconsistency
          }

      // 3. Try to find the next microcycle in the *current* mesocycle
      val nextMicrocycle = findMicrocycleByOrder(
        microcycleCollection,
        completedWorkout.mesocycleID.toString(),
        currentMicrocycle.order + 1
      )

      if (nextMicrocycle != null) {
        // 4. If next microcycle found, get its *first* workout
        Log.d(TAG, "Found next microcycle ${nextMicrocycle.id}. Getting its first workout.")
        val firstWorkoutOfNextMicrocycle =
          findWorkoutByOrder(workoutCollection, nextMicrocycle.id.toString(), 1)

        if (firstWorkoutOfNextMicrocycle != null) {
          Log.d(TAG, "Found first workout of next microcycle: ${firstWorkoutOfNextMicrocycle.id}")
          return firstWorkoutOfNextMicrocycle
        } else {
          Log.w(
            TAG,
            "Next microcycle ${nextMicrocycle.id} found, but it has no workouts with order 1."
          )
          // Potentially continue searching or return null depending on requirements
          return null
        }
      }

      // 5. If no next microcycle, find the *current* mesocycle to get its order
      Log.d(
        TAG,
        "No more microcycles in mesocycle ${completedWorkout.mesocycleID}. Checking for next mesocycle."
      )

      val currentMesocycle =
        findMesocycleById(mesocycleCollection, completedWorkout.mesocycleID.toString())
          ?: run {
            Log.e(TAG, "Could not find current mesocycle with ID: ${completedWorkout.mesocycleID}")
            return null // Or throw an exception for data inconsistency
          }

      // 6. Try to find the next mesocycle in the *program*
      val nextMesocycle = findMesocycleByOrder(
        mesocycleCollection,
        completedWorkout.programID.toString(),
        currentMesocycle.order + 1
      )

      if (nextMesocycle != null) {
        // 7. If next mesocycle found, find its *first* microcycle
        Log.d(TAG, "Found next mesocycle ${nextMesocycle.id}. Getting its first microcycle.")
        val firstMicrocycleOfNextMeso =
          findMicrocycleByOrder(microcycleCollection, nextMesocycle.id.toString(), 1)

        if (firstMicrocycleOfNextMeso != null) {
          // 8. If first microcycle found, get its *first* workout
          Log.d(
            TAG,
            "Found first microcycle of next mesocycle: ${firstMicrocycleOfNextMeso.id}. Getting its first workout."
          )

          val firstWorkoutOfFirstMicro =
            findWorkoutByOrder(workoutCollection, firstMicrocycleOfNextMeso.id.toString(), 1)

          if (firstWorkoutOfFirstMicro != null) {
            Log.d(TAG, "Found first workout of first microcycle: ${firstWorkoutOfFirstMicro.id}")
            return firstWorkoutOfFirstMicro
          } else {
            Log.w(
              TAG,
              "First microcycle ${firstMicrocycleOfNextMeso.id} of next mesocycle found, but it has no workouts with order 1."
            )
            return null
          }
        } else {
          Log.w(
            TAG,
            "Next mesocycle ${nextMesocycle.id} found, but it has no microcycles with order 1."
          )
          return null
        }
      }

      // 9. If no next mesocycle found, we are at the end of the program
      Log.d(
        TAG,
        "No more mesocycles in program ${completedWorkout.programID}. End of program reached."
      )

      return null

    } catch (e: Exception) {
      Log.e(e.message, e.stackTraceToString())
    } catch (e: IllegalStateException) {
      // Catch specific exceptions from requireNotNull or collection checks
      Log.e(TAG, "Precondition failed: ${e.message}", e)
      return null
    } catch (cblExc: com.couchbase.lite.CouchbaseLiteException) {
      // Catch Couchbase Lite specific errors
      Log.e(TAG, "Couchbase Lite error during query: ${cblExc.message}", cblExc)
      return null
    } catch (e: Exception) {
      // General catch block
      Log.e(TAG, "An unexpected error occurred: ${e.message}", e)
      return null
    }

    return null
  }

  private fun findWorkoutByOrder(
    collection: Collection,
    microcycleId: String,
    order: Int
  ): Workout? {
    val query = QueryBuilder
      .select(SelectResult.all())
      .from(DataSource.collection(collection))
      .where(
        Expression.property("microcycleID")
          .equalTo(Expression.string(microcycleId)) // Corrected typo
          .and(
            Expression.property("day").equalTo(Expression.intValue(order))
          ) // Assuming 'day' is the order property for workouts
      )
      .limit(Expression.intValue(1)) // Optimization: We only need one

    query.execute().use { rs ->
      return rs.firstOrNull()?.let { result ->
        // Adapt this parsing logic based on how your Workout objects are stored/retrieved
        // Using kotlinx.serialization as an example
        try {
          result.toJSON().let { jsonString ->
            withUnknownKeys.decodeFromString<WorkoutDao>(jsonString).Workout // Or directly decode<Workout> if not nested
          }
        } catch (e: Exception) {
          Log.e(TAG, "Failed to parse Workout JSON: ${e.message}")
          null
        }
      }
    }
  }

  private fun findMicrocycleById(collection: Collection, microcycleId: String): Microcycle? {
    val query = QueryBuilder
      .select(SelectResult.all())
      .from(DataSource.collection(collection))
      .where(Expression.property("id").equalTo(Expression.string(microcycleId)))
      .limit(Expression.intValue(1))

    query.execute().use { rs ->
      return rs.firstOrNull()?.let { result ->
        try {
          result.toJSON().let { jsonString ->
            withUnknownKeys.decodeFromString<MicrocycleDao>(jsonString).Microcycle
          }
        } catch (e: Exception) {
          Log.e(TAG, "Failed to parse Microcycle JSON: ${e.message}")
          null
        }
      }
    }
  }

  private fun findMicrocycleByOrder(
    collection: Collection,
    mesocycleId: String,
    order: Int
  ): Microcycle? {
    val query = QueryBuilder
      .select(SelectResult.all())
      .from(DataSource.collection(collection))
      .where(
        Expression.property("mesocycleID").equalTo(Expression.string(mesocycleId))
          .and(Expression.property("order").equalTo(Expression.intValue(order)))
      )
      .orderBy(
        Ordering.property("order").ascending()
      ) // Ensure correct order if multiple exist (though shouldn't by design)
      .limit(Expression.intValue(1))

    query.execute().use { rs ->
      return rs.firstOrNull()?.let { result ->
        try {
          result.toJSON().let { jsonString ->
            withUnknownKeys.decodeFromString<MicrocycleDao>(jsonString).Microcycle
          }
        } catch (e: Exception) {
          Log.e(TAG, "Failed to parse Microcycle JSON: ${e.message}")
          null
        }
      }
    }
  }

  private fun findMesocycleById(collection: Collection, mesocycleId: String): Mesocycle? {
    val query = QueryBuilder
      .select(SelectResult.all())
      .from(DataSource.collection(collection))
      .where(Expression.property("id").equalTo(Expression.string(mesocycleId)))
      .limit(Expression.intValue(1))

    query.execute().use { rs ->
      return rs.firstOrNull()?.let { result ->
        try {
          result.toJSON().let { jsonString ->
            withUnknownKeys.decodeFromString<MesocycleDao>(jsonString).Mesocycle
          }
        } catch (e: Exception) {
          Log.e(TAG, "Failed to parse Mesocycle JSON: ${e.message}")
          null
        }
      }
    }
  }

  private fun findMesocycleByOrder(
    collection: Collection,
    programId: String,
    order: Int
  ): Mesocycle? {
    val query = QueryBuilder
      .select(SelectResult.all())
      .from(DataSource.collection(collection))
      .where(
        Expression.property("programID").equalTo(Expression.string(programId))
          .and(Expression.property("order").equalTo(Expression.intValue(order)))
      )
      .orderBy(Ordering.property("order").ascending()) // Ensure correct order
      .limit(Expression.intValue(1))

    query.execute().use { rs ->
      return rs.firstOrNull()?.let { result ->
        try {
          result.toJSON().let { jsonString ->
            withUnknownKeys.decodeFromString<MesocycleDao>(jsonString).Mesocycle
          }
        } catch (e: Exception) {
          Log.e(TAG, "Failed to parse Mesocycle JSON: ${e.message}")
          null
        }
      }
    }
  }
}
