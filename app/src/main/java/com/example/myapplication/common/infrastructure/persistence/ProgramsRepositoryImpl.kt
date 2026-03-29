@file:OptIn(ExperimentalUuidApi::class, ExperimentalTime::class)

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
import com.example.myapplication.common.domain.model.CompletedProgramsDao
import com.example.myapplication.common.domain.model.CompletedSetsDao
import com.example.myapplication.common.domain.model.CompletedWorkoutsDao
import com.example.myapplication.common.domain.model.ExerciseSets
import com.example.myapplication.common.domain.model.Mesocycle
import com.example.myapplication.common.domain.model.MesocycleDao
import com.example.myapplication.common.domain.model.Microcycle
import com.example.myapplication.common.domain.model.MicrocycleDao
import com.example.myapplication.common.domain.model.Program
import com.example.myapplication.common.domain.model.ProgramDao
import com.example.myapplication.common.domain.model.Set
import com.example.myapplication.common.domain.model.Superset
import com.example.myapplication.common.domain.model.UserProfileManager
import com.example.myapplication.common.domain.model.Workout
import com.example.myapplication.common.domain.model.WorkoutDao
import com.example.myapplication.common.infrastructure.database.DatabaseManager
import com.example.myapplication.common.util.duplicateProgramStructure
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import org.bson.types.ObjectId
import kotlin.time.ExperimentalTime
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

private const val DATABASE_IS_NULL = "Program Repository: Database does not exist."

private const val COLLECTION_IS_NULL =
  "Program Repository: %s Collection does not exist in the Database."

private const val COLLECTION_IS_NULL_TEMPLATE =
  "Program Repository: %s Collection does not exist in the Database."

class ProgramsRepositoryImpl(
  private val databaseManager: DatabaseManager,
  private val userProfileManager: UserProfileManager,
) : ProgramsRepository {
  // Room executes all queries on a separate thread.
  // Observed Flow will notify the observer when the data has changed.
  val db: Database? = databaseManager.database

  val user = userProfileManager.getCurrentUser()

  // TODO Decide if I should create a repository for user data
  //  val userDatabase: Database? = databaseManager.userDatabase

  private val withUnknownKeys = Json {
    encodeDefaults = true
    ignoreUnknownKeys = true
  }

  // TODO Empty programs causing crashing caused by Kotlin Serialization Library
  override fun getAllDistinctProgramTemplates(): Flow<List<Program>> {
    try {
      requireNotNull(db) { DATABASE_IS_NULL }

      val collection = db.getCollection("Program")

      requireNotNull(collection) { COLLECTION_IS_NULL }

      val query =
        QueryBuilder.selectDistinct(SelectResult.all())
          .from(DataSource.collection(collection))
          .orderBy(Ordering.property("name").ascending())

      val flow =
        query
          .queryChangeFlow()
          .map { queryChange -> mapQueryChangeToProgram(queryChange) }
          .flowOn(Dispatchers.IO)

      query.execute()
      return flow
    } catch (e: Exception) {
//       Log.e(e.message, e.stackTraceToString())
    }

    return flow {}
  }

  private fun mapQueryChangeToProgram(queryChange: QueryChange): List<Program> {
    val programs = mutableListOf<Program>() // 1
    queryChange.results?.let { results -> // 2
      try {
        results.forEachIndexed { indexed, result -> // 3
          val json = result.toJSON() // 4
          //          Log.d("ProgramRepositoryImpl.mapQueryChangeToProgram()", "program $indexed:
          // $json")
          val program = withUnknownKeys.decodeFromString<ProgramDao>(json).Program // 5
          programs.add(program) // 6
        }
      } catch (e: Exception) {
//         Log.e(e.message, e.stackTraceToString())
        return programs
      }
    }
    return programs
  }

  private fun mapQueryChangeToCompletedPrograms(queryChange: QueryChange): List<Program> {
    val programs = mutableListOf<Program>() // 1
    queryChange.results?.let { results -> // 2
      results.forEach { result -> // 3
        val json = result.toJSON() // 4
        val program =
          withUnknownKeys.decodeFromString<CompletedProgramsDao>(json).CompletedPrograms // 5
        programs.add(program) // 6
      }
    }
    return programs // 7
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

  override fun getProgramByID(programID: ObjectId): Program? {
    try {
      requireNotNull(db) { DATABASE_IS_NULL }

      val programCollection = db.getCollection("Program")
      requireNotNull(programCollection) { String.format(COLLECTION_IS_NULL) }

      val mesocycleCollection = db.getCollection("Mesocycle")
      requireNotNull(mesocycleCollection) { String.format(COLLECTION_IS_NULL) }

      val microcycleCollection = db.getCollection("Microcycle")
      requireNotNull(microcycleCollection) { String.format(COLLECTION_IS_NULL) }

      val workoutCollection = db.getCollection("Workout")
      requireNotNull(workoutCollection) { String.format(COLLECTION_IS_NULL) }

      val program =
        QueryBuilder.select(SelectResult.all())
          .from(DataSource.collection(programCollection))
          .where(Expression.property("id").equalTo(Expression.string(programID.toString())))
          .execute()
          .allResults()
          .firstOrNull()
          ?.toJSON()
          ?.let { withUnknownKeys.decodeFromString<ProgramDao>(it.toString()).Program }

      val mesocyclesResult =
        QueryBuilder.select(SelectResult.all())
          .from(DataSource.collection(mesocycleCollection))
          .where(Expression.property("programID").equalTo(Expression.string(programID.toString())))
          .orderBy(Ordering.property("order").ascending())
          .execute()
          .allResults()

      val mesocycles =
        mesocyclesResult.map { result ->
          val json = result.toJSON()
          withUnknownKeys.decodeFromString<MesocycleDao>(json).Mesocycle
        }

      val microcyclesResult =
        QueryBuilder.select(SelectResult.all())
          .from(DataSource.collection(microcycleCollection))
          .where(Expression.property("programID").equalTo(Expression.string(programID.toString())))
          .orderBy(Ordering.property("order").ascending())
          .execute()
          .allResults()

      val microcycles =
        microcyclesResult.map { result ->
          val json = result.toJSON()
          withUnknownKeys.decodeFromString<MicrocycleDao>(json).Microcycle
        }

      val workoutsResult =
        QueryBuilder.select(SelectResult.all())
          .from(DataSource.collection(workoutCollection))
          .where(Expression.property("programID").equalTo(Expression.string(programID.toString())))
          .orderBy(Ordering.property("day").ascending())
          .execute()
          .allResults()

      val workouts =
        workoutsResult.map { result ->
          val json = result.toJSON()
          withUnknownKeys.decodeFromString<WorkoutDao>(json).Workout
        }

      microcycles.forEach { microcycle ->
        microcycle.workouts =
          workouts.filter { it.microcycleID == microcycle.id }.sortedBy { it.day }.toMutableList()
      }

      mesocycles.forEach { mesocycle ->
        mesocycle.microcycles =
          microcycles
            .filter { it.mesocycleID == mesocycle.id }
            .sortedBy { it.order }
            .toMutableList()
      }

      program?.mesocycles = mesocycles

      //      Log.i("ProgramRepositoryImpl.getProgramByID()", "program: $program")

      return program
    } catch (e: Exception) {
//       Log.e(e.message, e.stackTraceToString())
    }

    return null
  }

  override fun getActiveProgramByID(programID: ObjectId): Program? {
    try {
      requireNotNull(db) { DATABASE_IS_NULL }

      val programCollection = db.getCollection("CompletedPrograms")
      requireNotNull(programCollection) { String.format(COLLECTION_IS_NULL) }

      val workoutCollection = db.getCollection("CompletedWorkouts")
      requireNotNull(workoutCollection) { String.format(COLLECTION_IS_NULL) }

      val setCollection = db.getCollection("CompletedSets")
      requireNotNull(setCollection) { String.format(COLLECTION_IS_NULL) }

      val program =
        QueryBuilder.select(SelectResult.all())
          .from(DataSource.collection(programCollection))
          .where(Expression.property("id").equalTo(Expression.string(programID.toString())))
          .execute()
          .allResults()
          .firstOrNull()
          ?.toJSON()
          ?.let {
            withUnknownKeys.decodeFromString<CompletedProgramsDao>(it.toString()).CompletedPrograms
          }

      val mesocycles = program?.mesocycles ?: emptyList()

      val microcycles = mesocycles.map { result -> result.microcycles }.flatten().toMutableList()

      val workoutsResult =
        QueryBuilder.select(SelectResult.all())
          .from(DataSource.collection(workoutCollection))
          .where(Expression.property("programID").equalTo(Expression.string(programID.toString())))
          .orderBy(Ordering.property("day").ascending())
          .execute()
          .allResults()

      val workouts =
        workoutsResult.map { result ->
          val json = result.toJSON()
          withUnknownKeys.decodeFromString<CompletedWorkoutsDao>(json).CompletedWorkouts
        }

      val setsResult =
        QueryBuilder.select(SelectResult.all())
          .from(DataSource.collection(setCollection))
          .where(Expression.property("programID").equalTo(Expression.string(programID.toString())))
          .execute()
          .allResults()

      val sets =
        setsResult.map {
          withUnknownKeys.decodeFromString<CompletedSetsDao>(it.toJSON()).CompletedSets
        }

      workouts.forEach {
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

      microcycles.forEach { microcycle ->
        microcycle.workouts =
          workouts.filter { it.microcycleID == microcycle.id }.sortedBy { it.day }.toMutableList()
      }

      mesocycles.forEach { mesocycle ->
        mesocycle.microcycles =
          microcycles
            .filter { it.mesocycleID == mesocycle.id }
            .sortedBy { it.order }
            .toMutableList()
      }

      program?.mesocycles = mesocycles

      //      Log.i("ProgramRepositoryImpl.getProgramByID()", "program: $program")

      return program
    } catch (e: Exception) {
//       Log.e(e.message, e.stackTraceToString())
    }
    return null
  }

  override fun getActivePrograms(): Flow<List<Program>> {
    //    return database.getActivePrograms()

    try {
      requireNotNull(db) { DATABASE_IS_NULL }

      val collection = db.getCollection("CompletedPrograms")

      requireNotNull(collection) { String.format(COLLECTION_IS_NULL, "CompletedPrograms") }

      val query = QueryBuilder.select(SelectResult.all()).from(DataSource.collection(collection))
      //          .where(
      //            Expression.property("isActive")
      //              .equalTo(Expression.booleanValue(true))
      //
      // .and(Expression.property("ownerID").equalTo(Expression.string(user.id.toHexString())))
      //          )

      val flow =
        query
          .queryChangeFlow()
          .map { queryChange: QueryChange -> mapQueryChangeToCompletedPrograms(queryChange) }
          .flowOn(Dispatchers.IO)

      query.execute()
      return flow
    } catch (e: Exception) {
//       Log.e(e.message, e.stackTraceToString())
    }
    return flow {}
  }

  override fun isProgramActive(programUUID: Uuid): Boolean {
    try {
      requireNotNull(db) { DATABASE_IS_NULL }

      val collection = db.getCollection("CompletedPrograms")

      requireNotNull(collection) { COLLECTION_IS_NULL }

      val query =
        QueryBuilder.select(SelectResult.property("isActive"))
          .from(DataSource.collection(collection))
          .where(
            Expression.property("uuid")
              .equalTo(Expression.string(programUUID.toString()))
              .and(Expression.property("ownerID").equalTo(Expression.string(user.id.toHexString())))
          )

      val result = query.execute().allResults().firstOrNull()?.toJSON()

      return result?.let { withUnknownKeys.decodeFromString<Boolean>(result.toString()) } ?: false
    } catch (e: Exception) {
//       Log.e(e.message, e.stackTraceToString())
    }

    return false
  }

  override fun isProgramActiveByID(programID: String): Boolean {
    try {
      requireNotNull(db) { DATABASE_IS_NULL }

      val collection = db.getCollection("CompletedPrograms")

      requireNotNull(collection) { COLLECTION_IS_NULL }

      val query =
        QueryBuilder.select(SelectResult.property("isActive"))
          .from(DataSource.collection(collection))
          .where(
            Expression.property("id").equalTo(Expression.string(programID))
            //
            // .and(Expression.property("ownerID").equalTo(Expression.string(user.id.toHexString())))
          )

      val result = query.execute().allResults().firstOrNull()?.toJSON()

      return result?.let { withUnknownKeys.decodeFromString<Boolean>(result.toString()) } ?: false
    } catch (e: Exception) {
//       Log.e(e.message, e.stackTraceToString())
    }

    return false
  }

  override suspend fun createActiveProgram(program: Program): Boolean {
    //    database.copyProgramFromTemplate(program)
    //    TODO("Implement copyProgramFromTemplate")
    // TODO copy program to UserDatabase
    // TODO Copy programs workouts to CompletedWorkouts
    // TODO
    try {
      requireNotNull(db) { DATABASE_IS_NULL }

      val completedProgramsCollection = db.getCollection("CompletedPrograms")
      requireNotNull(completedProgramsCollection) {
        String.format(COLLECTION_IS_NULL_TEMPLATE, "CompletedPrograms")
      }

      val completedWorkoutsCollection = db.getCollection("CompletedWorkouts")
      requireNotNull(completedWorkoutsCollection) {
        String.format(COLLECTION_IS_NULL_TEMPLATE, "CompletedWorkouts")
      }

      val completedSetsCollection = db.getCollection("CompletedSets")
      requireNotNull(completedSetsCollection) {
        String.format(COLLECTION_IS_NULL_TEMPLATE, "CompletedSets")
      }

      // TODO Refactor using https://aistudio.google.com/prompts/1jjNZH2UMm80XgZpwu8_qy9Mu1vekKtII
      val (newProgram: Program, newProgramWorkouts: List<Workout>, newProgramSets: List<Set>) =
        duplicateProgramStructure(program, user)

      val groupedSets = newProgramSets.groupBy { it.workoutID }
      val workoutIDs = newProgramWorkouts.map { it.id }

      require(workoutIDs == groupedSets.keys.toList()) {
        "Sets' workoutIDs do not match new Workouts' ids"
      }
      require(newProgramSets.all { it.workoutID in workoutIDs }) {
        "Sets' workoutIDs do not match new Workouts' ids"
      }

      val newProgramJson = Json.encodeToString(newProgram)
      // TODO Review document id
      val newProgramDocument = MutableDocument("Program:${newProgram.id}", newProgramJson)
      //      Log.i("ProgramRepositoryImpl.createActiveProgram()", "newProgramJson:
      // $newProgramJson")
      //      Log.i(
      //        "ProgramRepositoryImpl.createActiveProgram()",
      //        "newProgramDocument: $newProgramDocument",
      //      )
      completedProgramsCollection.save(newProgramDocument)

      db.inBatch(
        UnitOfWork {
          newProgramWorkouts.forEach { workout ->
            val workoutJson = Json.encodeToString(workout)
            val newProgramWorkoutsDocument = MutableDocument("Workout:${workout.id}", workoutJson)
            //            Log.i(
            //              "ProgramRepositoryImpl.createActiveProgram()",
            //              "newProgramWorkoutsDocument: $newProgramWorkoutsDocument",
            //            )
            completedWorkoutsCollection.save(newProgramWorkoutsDocument)
          }
        }
      )

      groupedSets.forEach { (workoutID, sets) ->
        db.inBatch(
          UnitOfWork {
            sets.forEach { set ->
              //            Log.i("ProgramRepositoryImpl.createActiveProgram()", "set: $set")

              val setJson = Json.encodeToString(set)
              //            Log.i("ProgramRepositoryImpl.createActiveProgram()", "setJson:
              // $setJson")

              val newProgramSetsDocument = MutableDocument("Set:${set.id}", setJson)
              //              Log.i(
              //                "ProgramRepositoryImpl.createActiveProgram()",
              //                "newProgramSetsDocument: $newProgramSetsDocument",
              //              )
              completedSetsCollection.save(newProgramSetsDocument)
            }
          }
        )
      }

      val programQuery =
        QueryBuilder.selectDistinct(SelectResult.all())
          .from(DataSource.collection(completedProgramsCollection))
          .where(Expression.property("id").equalTo(Expression.string(newProgram.id.toString())))

      val programResult = programQuery.execute().allResults()
      val completedProgram =
        withUnknownKeys.decodeFromString<ProgramDao>(programResult.first().toJSON()).Program

      val workoutQuery =
        QueryBuilder.selectDistinct(SelectResult.all())
          .from(DataSource.collection(completedWorkoutsCollection))
          .where(
            Expression.property("programID").equalTo(Expression.string(newProgram.id.toString()))
          )

      val workoutResult = workoutQuery.execute().allResults()

      val workouts =
        workoutResult.map {
          withUnknownKeys.decodeFromString<CompletedWorkoutsDao>(it.toJSON()).CompletedWorkouts
        }

      val completedSetsQueryBuilder =
        QueryBuilder.select(SelectResult.all())
          .from(DataSource.collection(completedSetsCollection))
          .where(
            //
            // Expression.property("workoutID").equalTo(Expression.string(workout?.id.toString()))
            Expression.property("programID").equalTo(Expression.string(newProgram.id.toString()))
          )

      val setsResult = completedSetsQueryBuilder.execute().allResults()
      val sets =
        setsResult.map {
          withUnknownKeys.decodeFromString<CompletedSetsDao>(it.toJSON()).CompletedSets
        }

      require(newProgram.id == completedProgram.id) { "Program ID's do not match" }
      require(completedProgram.isActive) { "Program is not active" }

      require(newProgramSets.size == sets.size) {
        "ProgramRepositoryImpl.createActiveProgram(): Sets do not match"
      }
      require(newProgramWorkouts.size == workouts.size) {
        "ProgramRepositoryImpl.createActiveProgram(): Workouts do not match"
      }

      Log.i(
        "ProgramRepositoryImpl.createActiveProgram()",
        "prgrams have been written to database is active: ${completedProgram.isActive}",
      )
      Log.i(
        "ProgramRepositoryImpl.createActiveProgram()",
        "all sets have been written to database: ${newProgramSets.size == sets.size}",
      )
      Log.i(
        "ProgramRepositoryImpl.createActiveProgram()",
        "all workouts have been written to database: ${newProgramWorkouts.size == workouts.size}",
      )

      return true
    } catch (e: Exception) {
//       Log.e(e.message, e.stackTraceToString())
    }
    return false
  }

  override suspend fun insertProgram(program: Program): Boolean {
    //    TODO("What calls insertProgram")
    try {
      requireNotNull(db) { DATABASE_IS_NULL }

      val collection = db.getCollection("CompletedPrograms")

      requireNotNull(collection) { COLLECTION_IS_NULL }

      val json = Json.encodeToString(program.copy(ownerID = user.id.toHexString() ?: "MISSING_ID"))
      val document = MutableDocument("Program:${program.id}", json)

      //      collection.save(document)
      return true
    } catch (e: Exception) {
//       Log.e(e.message, e.stackTraceToString())
    }
    return false
  }

  override suspend fun updateProgram(program: Program): Boolean {
    //    database.updateProgram(program)
    //    TODO("What calls updateProgram")
    try {
      requireNotNull(db) { DATABASE_IS_NULL }

      val collection = db.getCollection("Program")

      requireNotNull(collection) { COLLECTION_IS_NULL }
      var document = collection.getDocument(program.id.toHexString())?.toMutable()
      requireNotNull(document) { "Program Repository: Program does not exist!" }

      if (document.getString("id") == program.id.toHexString()) {
        val json = Json.encodeToString(program)
        document = MutableDocument(program.id.toHexString(), json)

        collection.save(document)
        return true
      }
    } catch (e: Exception) {
//       Log.e(e.message, e.stackTraceToString())
    }
    return false
  }

  override suspend fun deleteProgram(program: Program): Boolean {
    //    database.deleteProgram(program)
    //    TODO("What calls deleteProgram")
    try {
      requireNotNull(db) { DATABASE_IS_NULL }

      val collection = db.getCollection("Program")

      requireNotNull(collection) { COLLECTION_IS_NULL }

      val document = collection.getDocument(program.id.toHexString())
      requireNotNull(document) { "Program Repository: Program does not exist!" }

      collection.delete(document)
      return true
    } catch (e: Exception) {
//       Log.e(e.message, e.stackTraceToString())
    }
    return false
  }

  override suspend fun deleteActiveProgram(program: Program): Boolean {
    //    database.deleteActiveProgram(program)
    try {
      requireNotNull(db) { DATABASE_IS_NULL }

      val completedProgramsCollection = db.getCollection("CompletedPrograms")
      requireNotNull(completedProgramsCollection) { COLLECTION_IS_NULL.format("CompletedPrograms") }

      val completedWorkoutsCollection = db.getCollection("CompletedWorkouts")
      requireNotNull(completedWorkoutsCollection) { COLLECTION_IS_NULL.format("CompletedWorkouts") }

      val completedSetsCollection = db.getCollection("CompletedSets")
      requireNotNull(completedSetsCollection) { COLLECTION_IS_NULL.format("CompletedSets") }

      val completedProgramQuery =
        QueryBuilder.select(SelectResult.all())
          .from(DataSource.collection(completedProgramsCollection))
          .where(
            Expression.property("id").equalTo(Expression.string(program.id.toHexString()))
            //
            // .and(Expression.property("ownerID").equalTo(Expression.string(user.id.toHexString())))
            //
            // .and(Expression.property("active").equalTo(Expression.booleanValue(true)))
          )
      //          .limit(Expression.intValue(1))

      val result =
        completedProgramQuery
          .execute()
          .allResults()
          .firstNotNullOf {
            val json = it.toJSON()
            withUnknownKeys.decodeFromString<CompletedProgramsDao>(json).CompletedPrograms
          }
          .id

      db.inBatch(
        UnitOfWork {
          // TODO Review document id
//          val document = completedProgramsCollection.getDocument("CompletedProgram:${program.id}")
          val document = completedProgramsCollection.getDocument("Program:${program.id}")
          requireNotNull(document) { "Program Repository: Program does not exist!" }
//           Log.i("ProgramRepositoryImpl.deleteActiveProgram()", "program: $program")
//           Log.i("ProgramRepositoryImpl.deleteActiveProgram()", "deleted document: $document")
          completedProgramsCollection.delete(document)
        }
      )

      val completedWorkoutsQuery =
        QueryBuilder.select(SelectResult.property("id"))
          .from(DataSource.collection(completedWorkoutsCollection))
          .where(
            Expression.property("programID").equalTo(Expression.string(program.id.toHexString()))
            // .and(Expression.property("ownerID").equalTo(Expression.string(user.id.toHexString())))
          )

      val completedWorkoutsResult =
        completedWorkoutsQuery.execute().allResults().mapNotNull { it.getString("id") }

      db.inBatch(
        UnitOfWork {
          completedWorkoutsResult.forEach { workoutID ->
            val workoutDocument = completedWorkoutsCollection.getDocument("Workout:$workoutID")
            requireNotNull(workoutDocument) { "Program Repository: Workout does not exist!" }
            Log.i(
              "ProgramRepositoryImpl.deleteActiveProgram()",
              "deleted workoutDocument: $workoutDocument",
            )
            completedWorkoutsCollection.delete(workoutDocument)
          }
        }
      )

      val completedSetsQuery =
        QueryBuilder.select(SelectResult.property("id"))
          .from(DataSource.collection(completedSetsCollection))
          .where(
            Expression.property("programID").equalTo(Expression.string(program.id.toHexString()))
            // .and(Expression.property("ownerID").equalTo(Expression.string(user.id.toHexString())))
          )

      val completedSetsResult =
        completedSetsQuery.execute().allResults().mapNotNull { it.getString("id") }

      db.inBatch(
        UnitOfWork {
          completedSetsResult.forEach { setID ->
            val setDocument = completedSetsCollection.getDocument("Set:$setID")
            requireNotNull(setDocument) { "Program Repository: Set does not exist!" }
            Log.i(
              "ProgramRepositoryImpl.deleteActiveProgram()",
              "deleted setDocument: $setDocument",
            )
            completedSetsCollection.delete(setDocument)
          }
        }
      )

      return true
    } catch (e: Exception) {
//       Log.e(e.message, e.stackTraceToString())
    }
    return false
  }

  override fun getAllMesocycles(): Flow<List<Mesocycle>> {
    //    return database.getAllMesocycles()
    TODO("Not yet implemented")
    try {
      requireNotNull(db) { DATABASE_IS_NULL }

      val collection = db.getCollection("Mesocycle")

      requireNotNull(collection) { COLLECTION_IS_NULL }

      val query =
        QueryBuilder.selectDistinct(SelectResult.all())
          .from(DataSource.collection(collection))
          .where(Expression.all())
          .orderBy(Ordering.property("name").ascending())

      val flow =
        query
          .queryChangeFlow()
          .map { queryChange -> mapQueryChangeToMesocycle(queryChange) }
          .flowOn(Dispatchers.IO)

      query.execute() // <4>
      return flow // <5>
    } catch (e: Exception) {
//       Log.e(e.message, e.stackTraceToString())
    }

    return flow {}
  }

  override fun getMesocycleById(mesocycleID: ObjectId): Mesocycle? {
    //    return database.getMesocycleByID(mesocycleID)

    try {
      requireNotNull(db) { DATABASE_IS_NULL }

      val collection = db.getCollection("Mesocycle")

      requireNotNull(collection) { COLLECTION_IS_NULL }

      val query =
        QueryBuilder.selectDistinct(SelectResult.all())
          .from(DataSource.collection(collection))
          .where(Expression.property("id").equalTo(Expression.string(mesocycleID.toString())))

      val result = query.execute().allResults().firstOrNull()?.toJSON()

      return result?.let {
        withUnknownKeys.decodeFromString<MesocycleDao>(result.toString()).Mesocycle
      }
    } catch (e: Exception) {
//       Log.e(e.message, e.stackTraceToString())
    }

    return null
  }

  override fun getMesocyclesByProgram(programID: ObjectId): Flow<List<Mesocycle>> {
    //    return database.getMesocyclesByProgramID(programID)

    try {
      requireNotNull(db) { DATABASE_IS_NULL }

      val collection = db.getCollection("Mesocycle")

      requireNotNull(collection) { COLLECTION_IS_NULL }

      val query =
        QueryBuilder.selectDistinct(SelectResult.all())
          .from(DataSource.collection(collection))
          .where(Expression.property("programID").equalTo(Expression.string(programID.toString())))
          .orderBy(Ordering.property("order").ascending())

      val flow =
        query
          .queryChangeFlow()
          .map { queryChange -> mapQueryChangeToMesocycle(queryChange) }
          .flowOn(Dispatchers.IO)

      query.execute() // <4>
      return flow // <5>
    } catch (e: Exception) {
//       Log.e(e.message, e.stackTraceToString())
    }

    return flow {}
  }

  override suspend fun insertMesocycle(mesocycle: Mesocycle): Boolean {
    //    database.insertMesocycle(mesocycle)
    try {
      requireNotNull(db) { DATABASE_IS_NULL }

      val collection = db.getCollection("Mesocycle")

      requireNotNull(collection) { COLLECTION_IS_NULL }

      val json = Json.encodeToString(mesocycle)
      val document = MutableDocument(mesocycle.id.toString(), json)

      collection.save(document)
      return true
    } catch (e: Exception) {
//       Log.e(e.message, e.stackTraceToString())
    }
    return false
  }

  override suspend fun updateMesocycle(mesocycle: Mesocycle): Boolean {
    try {
      requireNotNull(db) { DATABASE_IS_NULL }

      val collection = db.getCollection("Mesocycle")

      requireNotNull(collection) { COLLECTION_IS_NULL }

      val json = Json.encodeToString(mesocycle)
      val document = MutableDocument(mesocycle.id.toString(), json)

      collection.save(document)
      return true
    } catch (e: Exception) {
//       Log.e(e.message, e.stackTraceToString())
    }
    return false
  }

  override suspend fun deleteMesocycle(mesocycle: Mesocycle): Boolean {
    try {
      requireNotNull(db) { DATABASE_IS_NULL }

      val collection = db.getCollection("Mesocycle")

      requireNotNull(collection) { COLLECTION_IS_NULL }
      val document = collection.getDocument(mesocycle.id.toString())
      requireNotNull(document) { "Workout Repository: Workout does not exist!" }

      collection.delete(document)
      return true
    } catch (e: Exception) {
//       Log.e(e.message, e.stackTraceToString())
    }
    return false
  }

  override fun getAllMicrocycles(): Flow<List<Microcycle>> {
    try {
      requireNotNull(db) { DATABASE_IS_NULL }

      val collection = db.getCollection("Microcycle")

      requireNotNull(collection) { COLLECTION_IS_NULL }

      val query =
        QueryBuilder.selectDistinct(SelectResult.all())
          .from(DataSource.collection(collection))
          .where(Expression.all())
          .orderBy(Ordering.property("name").ascending())

      val flow =
        query
          .queryChangeFlow()
          .map { queryChange -> mapQueryChangeToMicrocycle(queryChange) }
          .flowOn(Dispatchers.IO)

      query.execute() // <4>
      return flow // <5>
    } catch (e: Exception) {
//       Log.e(e.message, e.stackTraceToString())
    }

    return flow {}
  }

  override suspend fun getMicrocycleByID(microcycleID: ObjectId): Microcycle? {
    try {
      requireNotNull(db) { DATABASE_IS_NULL }

      val collection = db.getCollection("Mircocycle")

      requireNotNull(collection) { COLLECTION_IS_NULL }

      val query =
        QueryBuilder.selectDistinct(SelectResult.all())
          .from(DataSource.collection(collection))
          .where(Expression.property("id").equalTo(Expression.string(microcycleID.toString())))

      val result = query.execute().allResults().firstOrNull()?.toJSON()

      return result?.let {
        withUnknownKeys.decodeFromString<MicrocycleDao>(result.toString()).Microcycle
      }
    } catch (e: Exception) {
//       Log.e(e.message, e.stackTraceToString())
    }

    return null
  }

  override fun getMicrocyclesByMesocycle(mesocycleID: ObjectId): Flow<List<Microcycle>> {
    TODO("Implement getMicrocyclesByMesocycle")
    try {
      requireNotNull(db) { DATABASE_IS_NULL }

      val collection = db.getCollection("Microcycle")

      requireNotNull(collection) { COLLECTION_IS_NULL }

      val query =
        QueryBuilder.selectDistinct(SelectResult.all())
          .from(DataSource.collection(collection))
          .where(
            Expression.property("mesocycleID").equalTo(Expression.string(mesocycleID.toString()))
          )
          .orderBy(Ordering.property("order").ascending())

      val flow =
        query
          .queryChangeFlow()
          .map { queryChange -> mapQueryChangeToMicrocycle(queryChange) }
          .flowOn(Dispatchers.IO)

      query.execute() // <4>
      return flow // <5>
    } catch (e: Exception) {
//       Log.e(e.message, e.stackTraceToString())
    }

    return flow {}
  }

  override fun getMicrocyclesByProgram(programID: ObjectId): Flow<List<Microcycle>> {
    TODO("Implement getMicrocyclesByProgram")
    try {
      requireNotNull(db) { DATABASE_IS_NULL }

      val collection = db.getCollection("Microcycle")

      requireNotNull(collection) { COLLECTION_IS_NULL }

      val query =
        QueryBuilder.selectDistinct(SelectResult.all())
          .from(DataSource.collection(collection))
          .where(Expression.property("programID").equalTo(Expression.string(programID.toString())))
          .orderBy(Ordering.property("order").ascending())

      val flow =
        query
          .queryChangeFlow()
          .map { queryChange -> mapQueryChangeToMicrocycle(queryChange) }
          .flowOn(Dispatchers.IO)

      query.execute() // <4>
      return flow // <5>
    } catch (e: Exception) {
//       Log.e(e.message, e.stackTraceToString())
    }

    return flow {}
  }

  //    suspend fun getMicrocyclesByProgramSorted(programID: Long): List<Microcycle> {
  //        return database.getMicrocycleByProgramSorted(programID)
  //    }

  override suspend fun insertMicrocycle(microcycle: Microcycle): Boolean {
    try {
      requireNotNull(db) { DATABASE_IS_NULL }

      val collection = db.getCollection("Mesocycle")

      requireNotNull(collection) { COLLECTION_IS_NULL }

      val json = Json.encodeToString(microcycle)
      val document = MutableDocument(microcycle.id.toString(), json)

      collection.save(document)
      return true
    } catch (e: Exception) {
//       Log.e(e.message, e.stackTraceToString())
    }
    return false
  }

  override suspend fun updateMicrocycle(microcycle: Microcycle): Boolean {
    try {
      requireNotNull(db) { DATABASE_IS_NULL }

      val collection = db.getCollection("Mesocycle")

      requireNotNull(collection) { COLLECTION_IS_NULL }

      val json = Json.encodeToString(microcycle)
      val document = MutableDocument(microcycle.id.toString(), json)

      collection.save(document)
      return true
    } catch (e: Exception) {
//       Log.e(e.message, e.stackTraceToString())
    }
    return false
  }

  override suspend fun deleteMicrocycle(microcycle: Microcycle): Boolean {
    try {
      requireNotNull(db) { DATABASE_IS_NULL }

      val collection = db.getCollection("Microcycle")

      requireNotNull(collection) { COLLECTION_IS_NULL }
      val document = collection.getDocument(microcycle.id.toString())
      requireNotNull(document) { "Workout Repository: Workout does not exist!" }

      collection.delete(document)
      return true
    } catch (e: Exception) {
//       Log.e(e.message, e.stackTraceToString())
    }
    return false
  }

  // TODO Current Implementation is inadequate for getting next workout
  //  @Deprecated("Current Implementation is inadequate for getting next workout", replaceWith =
  // ReplaceWith("WorkoutRepository.getNextWorkoutInProgram()")
  override fun getNextWorkoutByProgramID(programID: ObjectId): Workout? {
    try {
      requireNotNull(db) { DATABASE_IS_NULL }

      val workoutCollection = db.getCollection("CompletedWorkouts")
      requireNotNull(workoutCollection) { COLLECTION_IS_NULL.format("CompletedWorkouts") }

      val setCollection = db.getCollection("CompletedSets")
      requireNotNull(setCollection) { COLLECTION_IS_NULL }

      val query =
        QueryBuilder.selectDistinct(SelectResult.all())
          .from(DataSource.collection(workoutCollection))
          .where(
            Expression.property("programID")
              .equalTo(Expression.string(programID.toString()))
              .and(Expression.property("workoutEnded").isNotValued)
            //
            // .and(Expression.property("ownerID").equalTo(Expression.string(user.id.toHexString())))
          )
          .limit(Expression.intValue(1))

      val workoutResult = query.execute().allResults().firstOrNull()?.toJSON()

      val workout =
        workoutResult?.let {
          withUnknownKeys.decodeFromString<CompletedWorkoutsDao>(it).CompletedWorkouts
        }

      val completedSetsQueryBuilder =
        QueryBuilder.select(SelectResult.all())
          .from(DataSource.collection(setCollection))
          .where(
            //
            Expression.property("workoutID").equalTo(Expression.string(workout?.id.toString()))
            //
            // Expression.property("programID").equalTo(Expression.string(programID.toString()))
          )

      val setsResult = completedSetsQueryBuilder.execute().allResults()
      val sets =
        setsResult.map {
          withUnknownKeys.decodeFromString<CompletedSetsDao>(it.toJSON()).CompletedSets
        }

      //      val filteredSets = sets.filter { it.workoutID == workout?.id }

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
      Log.d(
        "ProgramRepositoryImpl.getNextWorkoutByProgramID()",
        "ownerID & user.id are identical: ${workout?.ownerID == user.id.toHexString()}",
      )
      return workout
    } catch (e: Exception) {
//       Log.e(e.message, e.stackTraceToString())
    }

    return null
  }
}
