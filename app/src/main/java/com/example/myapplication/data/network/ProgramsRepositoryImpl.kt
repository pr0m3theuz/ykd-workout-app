@file:OptIn(ExperimentalUuidApi::class)

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
import com.example.myapplication.data.model.Mesocycle
import com.example.myapplication.data.model.MesocycleDao
import com.example.myapplication.data.model.Microcycle
import com.example.myapplication.data.model.MicrocycleDao
import com.example.myapplication.data.model.Program
import com.example.myapplication.data.model.ProgramDao
import com.example.myapplication.data.model.Workout
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

private const val DATABASE_IS_NULL = "Program Repository: Database does not exist."

private const val COLLECTION_IS_NULL =
  "Program Repository: Collection does not exist in the Database."

class ProgramsRepositoryImpl(private val databaseManager: DatabaseManager) : ProgramsRepository {
  // Room executes all queries on a separate thread.
  // Observed Flow will notify the observer when the data has changed.
  val db: Database? = databaseManager.database

  // TODO Decide if I should create a repository for user data
  val userDatabase: Database? = databaseManager.userDatabase

  private val withUnknownKeys = Json { ignoreUnknownKeys = true }

  override fun getAllDistinctProgramTemplates(): Flow<List<Program>> {
    try {
      requireNotNull(db) { DATABASE_IS_NULL }

      val collection = db.getCollection("Program")

      requireNotNull(collection) { COLLECTION_IS_NULL }

      val query =
        QueryBuilder.selectDistinct(SelectResult.all())
          .from(DataSource.collection(collection))
          .where(Expression.all())
          .orderBy(Ordering.property("name").ascending())

      val flow =
        query
          .queryChangeFlow()
          .map { queryChange -> mapQueryChangeToProgram(queryChange) }
          .flowOn(Dispatchers.IO)

      query.execute() // <4>
      return flow // <5>
    } catch (e: Exception) {
      Log.e(e.message, e.stackTraceToString())
    }

    return flow {}
  }

  private fun mapQueryChangeToProgram(queryChange: QueryChange): List<Program> {
    val programs = mutableListOf<Program>() // 1
    queryChange.results?.let { results -> // 2
      results.forEach { result -> // 3
        val json = result.toJSON() // 4
        val program = withUnknownKeys.decodeFromString<ProgramDao>(json).Program // 5
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

  override fun getProgramById(programID: ObjectId): Program? {
    try {
      requireNotNull(db) { DATABASE_IS_NULL }

      val collection = db.getCollection("Program")

      requireNotNull(collection) { COLLECTION_IS_NULL }

      val queryBuilder =
        QueryBuilder.select(SelectResult.all())
          .from(DataSource.collection(collection))
          .where(Expression.property("_id").equalTo(Expression.string(programID.toString())))

      val result = queryBuilder.execute().allResults().firstOrNull()?.toJSON()

      val program =
        result?.let { withUnknownKeys.decodeFromString<ProgramDao>(result.toString()).Program }
      return program
    } catch (e: Exception) {
      Log.e(e.message, e.stackTraceToString())
    }

    return null
  }

  override fun getActivePrograms(): Flow<List<Program>> {
    //    return database.getActivePrograms()

    try {
      requireNotNull(db) { DATABASE_IS_NULL }

      val collection = db.getCollection("Program")

      requireNotNull(collection) { COLLECTION_IS_NULL }

      val query =
        QueryBuilder.select(SelectResult.all())
          .from(DataSource.collection(collection))
          .where(
            Expression.property("active")
              .equalTo(Expression.booleanValue(true))
              .and(Expression.property("ownerID").equalTo(Expression.string("USER ID")))
          )

      val flow =
        query
          .queryChangeFlow()
          .map { queryChange: QueryChange -> mapQueryChangeToProgram(queryChange) }
          .flowOn(Dispatchers.IO)

      query.execute()
      return flow
    } catch (e: Exception) {
      Log.e(e.message, e.stackTraceToString())
    }
    return flow {}
  }

  override fun isProgramActive(programUUID: Uuid): Boolean {
    try {
      requireNotNull(db) { DATABASE_IS_NULL }

      val collection = db.getCollection("Program")

      requireNotNull(collection) { COLLECTION_IS_NULL }

      val query =
        QueryBuilder.select(SelectResult.property("active"))
          .from(DataSource.collection(collection))
          .where(
            Expression.property("uuid")
              .equalTo(Expression.string(programUUID.toString()))
              .and(Expression.property("ownerID").equalTo(Expression.string("USER ID")))
          )

      val result = query.execute().allResults().firstOrNull()?.toJSON()

      return result?.let { withUnknownKeys.decodeFromString<Boolean>(result.toString()) } ?: false
    } catch (e: Exception) {
      Log.e(e.message, e.stackTraceToString())
    }

    return false
  }

  override suspend fun copyProgramFromTemplate(program: Program): Boolean {
//    database.copyProgramFromTemplate(program)
    TODO("Implement copyProgramFromTemplate")
    // TODO copy program to UserDatabase
    return false
  }

  override suspend fun insertProgram(program: Program): Boolean {
    try {
      requireNotNull(db) { DATABASE_IS_NULL }

      val collection = db.getCollection("Program")

      requireNotNull(collection) { COLLECTION_IS_NULL }

      val json = Json.encodeToString(program)
      val document = MutableDocument(program.id.toString(), json)

      collection.save(document)
      return true
    } catch (e: Exception) {
      Log.e(e.message, e.stackTraceToString())
    }
    return false
  }

  override suspend fun updateProgram(program: Program): Boolean {
//    database.updateProgram(program)
    try {
      requireNotNull(db) { DATABASE_IS_NULL }

      val collection = db.getCollection("Program")

      requireNotNull(collection) { COLLECTION_IS_NULL }

      val json = Json.encodeToString(program)
      val document = MutableDocument(program.id.toString(), json)

      collection.save(document)
      return true
    } catch (e: Exception) {
      Log.e(e.message, e.stackTraceToString())
    }
    return false
  }

  override suspend fun deleteProgram(program: Program): Boolean {
//    database.deleteProgram(program)
    try {
      requireNotNull(db) { DATABASE_IS_NULL }

      val collection = db.getCollection("Program")

      requireNotNull(collection) { COLLECTION_IS_NULL }

      val document = collection.getDocument(program.id.toString())
      requireNotNull(document) { "Program Repository: Program does not exist!" }

      collection.delete(document)
      return true
    } catch (e: Exception) {
      Log.e(e.message, e.stackTraceToString())
    }
    return false
  }

  override suspend fun deleteActiveProgram(program: Program): Boolean {
//    database.deleteActiveProgram(program)
    try {
      requireNotNull(db) { DATABASE_IS_NULL }

      val collection = db.getCollection("Program")

      requireNotNull(collection) { COLLECTION_IS_NULL }

      val query =
        QueryBuilder.select(SelectResult.all())
          .from(DataSource.collection(collection))
          .where(
            Expression.property("_id")
              .equalTo(Expression.string(program.id.toString()))
              .and(Expression.property("ownerID").equalTo(Expression.string("USER ID")))
              .and(Expression.property("active").equalTo(Expression.booleanValue(true)))
          ).limit(Expression.intValue(1))

      val result = query.execute().allResults().firstOrNull()?.getString("_id")

      val document = result?.let { collection.getDocument(it) }
      requireNotNull(document) { "Program Repository: Program does not exist!" }

      // TODO delete Mesocycles, Microcycles, Unfinished Workouts based on programID

      collection.delete(document)
      return true
    } catch (e: Exception) {
      Log.e(e.message, e.stackTraceToString())
    }
    return false
  }

  override fun getAllMesocycles(): Flow<List<Mesocycle>> {
//    return database.getAllMesocycles()
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
      Log.e(e.message, e.stackTraceToString())
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
          .where(Expression.property("_id").equalTo(Expression.string(mesocycleID.toString())))

      val result = query.execute().allResults().firstOrNull()?.toJSON()

      return result?.let { withUnknownKeys.decodeFromString<MesocycleDao>(result.toString()).Mesocycle }
    } catch (e: Exception) {
      Log.e(e.message, e.stackTraceToString())
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
      Log.e(e.message, e.stackTraceToString())
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
      Log.e(e.message, e.stackTraceToString())
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
      Log.e(e.message, e.stackTraceToString())
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
      Log.e(e.message, e.stackTraceToString())
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
      Log.e(e.message, e.stackTraceToString())
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
          .where(Expression.property("_id").equalTo(Expression.string(microcycleID.toString())))

      val result = query.execute().allResults().firstOrNull()?.toJSON()

      return result?.let { withUnknownKeys.decodeFromString<MicrocycleDao>(result.toString()).Microcycle }
    } catch (e: Exception) {
      Log.e(e.message, e.stackTraceToString())
    }

    return null
  }

  override fun getMicrocyclesByMesocycle(mesocycleID: ObjectId): Flow<List<Microcycle>> {
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
      Log.e(e.message, e.stackTraceToString())
    }

    return flow {}
  }

  override fun getMicrocyclesByProgram(programID: ObjectId): Flow<List<Microcycle>> {
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
      Log.e(e.message, e.stackTraceToString())
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
      Log.e(e.message, e.stackTraceToString())
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
      Log.e(e.message, e.stackTraceToString())
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
      Log.e(e.message, e.stackTraceToString())
    }
    return false
  }

  override fun getNextWorkoutByProgramID(programID: ObjectId): Workout? {
    try {
      requireNotNull(userDatabase) { DATABASE_IS_NULL }

      val collection = userDatabase.getCollection("Workout")

      requireNotNull(collection) { COLLECTION_IS_NULL }

      val query = QueryBuilder
        .selectDistinct(SelectResult.all())
        .from(DataSource.collection(collection))
        .where(
          Expression.property("programID").equalTo(Expression.string(programID.toString()))
            .and(Expression.property("workoutEnded").isNotValued)
        )
        .limit(Expression.intValue(1))

      val result = query.execute().allResults().firstOrNull()?.toJSON()

      val workout = result?.let { withUnknownKeys.decodeFromString<Workout>(it) }

      return workout
    } catch (e: Exception) {
      Log.e(e.message, e.stackTraceToString())
    }

    return null
  }
}
