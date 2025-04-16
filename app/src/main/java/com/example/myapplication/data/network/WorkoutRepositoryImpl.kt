@file:OptIn(ExperimentalUuidApi::class)

package com.example.myapplication.data.network

import android.util.Log
import com.couchbase.lite.DataSource
import com.couchbase.lite.Database
import com.couchbase.lite.Expression
import com.couchbase.lite.Function
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
import com.example.myapplication.data.model.Mesocycle
import com.example.myapplication.data.model.MesocycleDao
import com.example.myapplication.data.model.Microcycle
import com.example.myapplication.data.model.MicrocycleDao
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

      val workoutCollection = db.getCollection("Workout")
      val microcycleCollection = db.getCollection("Microcycle")
      val mesocycleCollection = db.getCollection("Mesocycle")
      val programCollection = db.getCollection("Program")

      requireNotNull(workoutCollection) { COLLECTION_IS_NULL }
      requireNotNull(microcycleCollection) { "Workout Repository: Microcycle Collection does not exist in the Database." }
      requireNotNull(mesocycleCollection) { "Workout Repository: Mesocycle Collection does not exist in the Database." }
      requireNotNull(programCollection) { "Workout Repository: Program Collection does not exist in the Database." }

      "SELECT * FROM `Microcycle` WHERE id = '${completedWorkout.microcycleID}'"
      "SELECT * FROM `Mesocycle` WHERE id = '${completedWorkout.mesocycleID}'"
      "SELECT * FROM `Program` WHERE id = '${completedWorkout.programID}'"


      val workoutCountQuery = QueryBuilder
        .select(
          SelectResult.expression(Function.count(Expression.string("*"))).`as`("count")
        )
        .from(DataSource.collection(workoutCollection))
        .where(
          Expression.property("microcycleID")
            .equalTo(Expression.string(completedWorkout.microcycleID.toString()))
        )

      var workoutCount = 0
      workoutCountQuery.execute().use { rs ->
        rs.allResults().forEach {
          workoutCount = it.getInt("count")
        }
      }

      requireNotNull(completedWorkout.day) { "Workout Repository: Completed Workout Day is null!" }

      when {
        completedWorkout.day!! < workoutCount -> {
          // TODO("Implement logic to get next workout in microcycle")
          val firstWorkoutInMicrcycleQuery = QueryBuilder
            .select(SelectResult.all())
            .from(DataSource.collection(workoutCollection))
            .where(
              Expression.property("microycleID")
                .equalTo(Expression.string(completedWorkout.microcycleID.toString()))
                .and(
                  Expression.property("order")
                    .equalTo(Expression.intValue(completedWorkout.day!!.plus(1)))
                )
            )
          val firstWorkoutInMicrcycleQueryReslt =
            firstWorkoutInMicrcycleQuery.execute().allResults().firstOrNull()?.toJSON()

          val nextWorkout = firstWorkoutInMicrcycleQueryReslt?.let {
            withUnknownKeys.decodeFromString<WorkoutDao>(it).Workout
          }

          return nextWorkout
        }

        completedWorkout.day == workoutCount -> {
          val microcycleQuery = QueryBuilder.select(
            SelectResult.all()
          )
            .from(DataSource.collection(microcycleCollection))
            .where(
              Expression.property("id")
                .equalTo(Expression.string(completedWorkout.microcycleID.toString()))
            )

          var currentMicrocycleOrder = 0
          microcycleQuery.execute().use { rs ->
            rs.allResults().forEach {
              currentMicrocycleOrder = it.getInt("order")
            }
          }
          val microcycleCountQuery = QueryBuilder
            .select(
              SelectResult.expression(Function.count(Expression.string("*"))).`as`("count")
            )
            .from(DataSource.collection(microcycleCollection))
            .where(
              Expression.property("mesocycleID")
                .equalTo(Expression.string(completedWorkout.mesocycleID.toString()))
            )

          var microcycleCount = 0
          microcycleCountQuery.execute().use { rs ->
            rs.allResults().forEach {
              microcycleCount = it.getInt("count")
            }
          }

          when {
            currentMicrocycleOrder < microcycleCount -> {
              // TODO("Implement logic to get next microcycle in mesocycle")
              val nextMicrocycleInCurrentMesocycleQuery = QueryBuilder
                .select(SelectResult.all())
                .from(DataSource.collection(microcycleCollection))
                .where(
                  Expression.property("mesocycleID")
                    .equalTo(Expression.string(completedWorkout.mesocycleID.toString()))
                    .and(
                      Expression.property("order")
                        .equalTo(Expression.intValue(currentMicrocycleOrder.plus(1)))
                    )
                )

              val nextMicrocycle =
                nextMicrocycleInCurrentMesocycleQuery.execute().allResults().firstOrNull()?.toJSON()
                  ?.let {
                    withUnknownKeys.decodeFromString<MicrocycleDao>(it).Microcycle
                  }

              nextMicrocycle?.let {
                // TODO("Implement logic to get next workout in microcycle")
                val firstWorkoutInMicrcycleQuery = QueryBuilder
                  .select(SelectResult.all())
                  .from(DataSource.collection(workoutCollection))
                  .where(
                    Expression.property("microycleID")
                      .equalTo(Expression.string(nextMicrocycle.id.toString()))
                      .and(Expression.property("order").equalTo(Expression.intValue(1)))
                  )
                val firstWorkoutInMicrcycleQueryReslt =
                  firstWorkoutInMicrcycleQuery.execute().allResults().firstOrNull()?.toJSON()

                val nextWorkout = firstWorkoutInMicrcycleQueryReslt?.let {
                  withUnknownKeys.decodeFromString<WorkoutDao>(it).Workout
                }

                return nextWorkout
              }
            }

            currentMicrocycleOrder == microcycleCount -> {
              val mesocycleQuery = QueryBuilder.select(
                SelectResult.all()
              )
                .from(DataSource.collection(mesocycleCollection))
                .where(
                  Expression.property("id")
                    .equalTo(Expression.string(completedWorkout.mesocycleID.toString()))
                )

              var currentMesocycleOrder = 0
              mesocycleQuery.execute().use { rs ->
                rs.allResults().forEach {
                  currentMesocycleOrder = it.getInt("order")
                }
              }

              val mesocycleCountQuery = QueryBuilder
                .select(
                  SelectResult.expression(Function.count(Expression.string("*"))).`as`("count")
                )
                .from(DataSource.collection(mesocycleCollection))
                .where(
                  Expression.property("programID")
                    .equalTo(Expression.string(completedWorkout.programID.toString()))
                )

              var mesocycleCount = 0
              mesocycleCountQuery.execute().use { rs ->
                rs.allResults().forEach {
                  mesocycleCount = it.getInt("count")
                }
              }

              when {
                currentMesocycleOrder >= mesocycleCount -> return null
                currentMesocycleOrder < mesocycleCount -> {
                  // TODO("Implement logic to get next mesocycle in program")
                  val nextMesocycleQuery = QueryBuilder
                    .select(SelectResult.all())
                    .from(DataSource.collection(mesocycleCollection))
                    .where(
                      Expression.property("programID")
                        .equalTo(Expression.string(completedWorkout.programID.toString()))
                        .and(
                          Expression.property("order")
                            .equalTo(Expression.intValue(currentMesocycleOrder.plus(1)))
                        )
                    )

                  val nextMesocycle =
                    nextMesocycleQuery.execute().allResults().firstOrNull()?.toJSON()?.let {
                      withUnknownKeys.decodeFromString<MesocycleDao>(it).Mesocycle
                    }

                  nextMesocycle?.let {
                    // TODO("Implement logic to get next microcycle in mesocycle")
                    val firstMicrocycleInMesocycleQuery = QueryBuilder
                      .select(SelectResult.all())
                      .from(DataSource.collection(microcycleCollection))
                      .where(
                        Expression.property("mesocycleID")
                          .equalTo(Expression.string(nextMesocycle.id.toString()))
                          .and(Expression.property("order").equalTo(Expression.intValue(1)))
                      )

                    val nextMicrocycle =
                      firstMicrocycleInMesocycleQuery.execute().allResults().firstOrNull()?.toJSON()
                        ?.let {
                          withUnknownKeys.decodeFromString<MicrocycleDao>(it).Microcycle
                        }

                    nextMicrocycle?.let {
                      // TODO("Implement logic to get next workout in microcycle")
                      val firstWorkoutInMicrcycleQuery = QueryBuilder
                        .select(SelectResult.all())
                        .from(DataSource.collection(workoutCollection))
                        .where(
                          Expression.property("microycleID")
                            .equalTo(Expression.string(nextMicrocycle.id.toString()))
                            .and(Expression.property("order").equalTo(Expression.intValue(1)))
                        )
                      val firstWorkoutInMicrcycleQueryReslt =
                        firstWorkoutInMicrcycleQuery.execute().allResults().firstOrNull()?.toJSON()

                      val nextWorkout = firstWorkoutInMicrcycleQueryReslt?.let {
                        withUnknownKeys.decodeFromString<WorkoutDao>(it).Workout
                      }

                      return nextWorkout
                    }
                  }
                }
              }
            }

            else -> return null

          }
        }
      }
    } catch (e: Exception) {
      Log.e(e.message, e.stackTraceToString())
    }
    return null
  }
}
