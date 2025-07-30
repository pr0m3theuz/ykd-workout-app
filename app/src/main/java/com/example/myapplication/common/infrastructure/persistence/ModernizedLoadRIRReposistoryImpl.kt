package com.example.myapplication.common.infrastructure.persistence

import android.util.Log
import com.couchbase.lite.DataSource
import com.couchbase.lite.Database
import com.couchbase.lite.Expression
import com.couchbase.lite.QueryBuilder
import com.couchbase.lite.QueryChange
import com.couchbase.lite.SelectResult
import com.couchbase.lite.queryChangeFlow
import com.example.myapplication.common.infrastructure.database.DatabaseManager
import com.example.myapplication.common.util.LoadRepCapacity
import com.example.myapplication.common.util.LoadRepCapacityDao
import com.example.myapplication.common.util.ModernizedLoadRIR
import com.example.myapplication.common.util.ModernizedLoadRIRDao
import com.example.myapplication.common.util.RepCapacity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json

private const val DATABASE_IS_NULL = "Modernized Load RIR Repository: Database does not exist."

private const val COLLECTION_IS_NULL =
  "Modernized Load RIR Repository: ModernizedLoadRIR Collection does not exist in the Database."

class ModernizedLoadRIRReposistoryImpl(private val databaseManager: DatabaseManager) :
  ModernizedLoadRIRReposistory {
  val db: Database? = databaseManager.database

  private val withUnknownKeys = Json { ignoreUnknownKeys = true }

  private fun mapQueryChangeToModernizedLoadRIR(queryChange: QueryChange): List<ModernizedLoadRIR> {
    val list = mutableListOf<ModernizedLoadRIR>() // 1
    queryChange.results?.let { results -> // 2
      results.forEach { result -> // 3
        val json = result.toJSON() // 4
        //        Log.i("mapQueryChangeToModernizedLoadRIR.JSON", "mapQueryChangeToModernizedLoadRIR
        // $json")
        val modernizedLoadRIR =
          withUnknownKeys.decodeFromString<ModernizedLoadRIRDao>(json).ModernizedLoadRIR // 5
        list.add(modernizedLoadRIR) // 6
      }
    }
    return list // 7
  }

  private fun mapQueryChangeToRepCapacity(queryChange: QueryChange): List<LoadRepCapacity> {
    val workouts = mutableListOf<LoadRepCapacity>() // 1
    queryChange.results?.let { results -> // 2
      results.forEach { result -> // 3
        val json = result.toJSON() // 4
        val workout =
          withUnknownKeys.decodeFromString<LoadRepCapacityDao>(json).LoadRepCapacity // 5
        workouts.add(workout) // 6
      }
    }
    return workouts // 7
  }

  override fun getRelativeLoadPercentage(
    repCapacity: RepCapacity,
    rir: Double,
    rpe: Double,
    repsPerformed: Int,
  ): ModernizedLoadRIR? {
    try {
      requireNotNull(db) { DATABASE_IS_NULL }
      val collection = db.getCollection("ModernizedLoadRIR")

      requireNotNull(collection) { COLLECTION_IS_NULL }

      val queryBuilder =
        QueryBuilder.select(SelectResult.all())
          .from(DataSource.collection(collection)) // Replace with your database instance
          .where(
            Expression.property("repCapacity")
              .equalTo(Expression.string(repCapacity.name))
              .and(Expression.property("rir").equalTo(Expression.string(rir.toString())))
              .and(
                Expression.property("repsPerformed")
                  .equalTo(Expression.string(repsPerformed.toString()))
              )
              .or(Expression.property("rpe").equalTo(Expression.string(rpe.toString())))
          )
          .limit(Expression.intValue(1))

      val result = queryBuilder.execute().allResults().firstOrNull()?.toJSON()

      return result?.let {
        withUnknownKeys.decodeFromString<ModernizedLoadRIRDao>(it).ModernizedLoadRIR
      }
    } catch (e: Exception) {
      Log.e(e.message, e.stackTraceToString())
    }
    return null
  }

  override fun getRIRRPE(
    repCapacity: RepCapacity,
    repsPerformed: Int,
    relativeLoadPercent: Double,
  ): ModernizedLoadRIR? {
    try {
      requireNotNull(db) { DATABASE_IS_NULL }
      val collection = db.getCollection("ModernizedLoadRIR")

      requireNotNull(collection) { COLLECTION_IS_NULL }

      val queryBuilder =
        QueryBuilder.select(SelectResult.all())
          .from(DataSource.collection(collection)) // Replace with your database instance
          .where(
            Expression.property("repCapacity")
              .equalTo(Expression.string(repCapacity.name))
              .and(
                Expression.property("repsPerformed")
                  .equalTo(Expression.string(repsPerformed.toString()))
              )
              .and(
                Expression.property("relativeLoadPercent")
                  .equalTo(Expression.string(relativeLoadPercent.toString()))
              )
          )
          .limit(Expression.intValue(1))

      val result = queryBuilder.execute().allResults().firstOrNull()?.toJSON()

      return result?.let {
        withUnknownKeys.decodeFromString<ModernizedLoadRIRDao>(it).ModernizedLoadRIR
      }
    } catch (e: Exception) {
      Log.e(e.message, e.stackTraceToString())
    }
    return null
  }

  override fun getRepsPerformed(
    repCapacity: RepCapacity,
    rir: Double,
    rpe: Double,
    relativeLoadPercent: Double,
  ): ModernizedLoadRIR? {
    try {
      requireNotNull(db) { DATABASE_IS_NULL }
      val collection = db.getCollection("ModernizedLoadRIR")

      requireNotNull(collection) { COLLECTION_IS_NULL }

      val queryBuilder =
        QueryBuilder.select(SelectResult.all())
          .from(DataSource.collection(collection)) // Replace with your database instance
          .where(
            Expression.property("repCapacity")
              .equalTo(Expression.string(repCapacity.name))
              .and(Expression.property("rir").equalTo(Expression.string(rir.toString())))
              .and(
                Expression.property("relativeLoadPercent")
                  .equalTo(Expression.string(relativeLoadPercent.toString()))
              )
              .or(Expression.property("rpe").equalTo(Expression.string(rpe.toString())))
          )
          .limit(Expression.intValue(1))

      val result = queryBuilder.execute().allResults().firstOrNull()?.toJSON()

      return result?.let {
        withUnknownKeys.decodeFromString<ModernizedLoadRIRDao>(it).ModernizedLoadRIR
      }
    } catch (e: Exception) {
      Log.e(e.message, e.stackTraceToString())
    }
    return null
  }

  override fun getRepCapacity(estimated1RMPercent: Double): LoadRepCapacity? {
    try {
      requireNotNull(db) { DATABASE_IS_NULL }
      val collection = db.getCollection("RepCapacityLookup")

      requireNotNull(collection) { COLLECTION_IS_NULL }

      val queryBuilder =
        QueryBuilder.select(SelectResult.all())
          .from(DataSource.collection(collection)) // Replace with your database instance
          .where(
            Expression.property("loadPercent")
              .lessThanOrEqualTo(Expression.string(estimated1RMPercent.toString()))
          )

      val result = queryBuilder.execute().allResults().firstOrNull()?.toJSON()

      return result?.let {
        withUnknownKeys.decodeFromString<LoadRepCapacityDao>(it).LoadRepCapacity
      }
    } catch (e: Exception) {
      Log.e(e.message, e.stackTraceToString())
    }
    return null
  }

  override fun getRepCapacityModernizedLoadRIR(
    repCapacity: RepCapacity
  ): Flow<List<ModernizedLoadRIR>> {
    try {
      requireNotNull(db) { DATABASE_IS_NULL }
      val collection = db.getCollection("ModernizedLoadRIR")

      requireNotNull(collection) { COLLECTION_IS_NULL }

      val query =
        QueryBuilder.select(SelectResult.all())
          .from(DataSource.collection(collection))
          .where(Expression.property("repCapacity").equalTo(Expression.string(repCapacity.name)))

      val flow =
        query
          .queryChangeFlow()
          .map { queryChange -> mapQueryChangeToModernizedLoadRIR(queryChange) }
          .flowOn(Dispatchers.IO)

      query.execute() // <4>
      return flow
    } catch (e: Exception) {
      Log.e(e.message, e.stackTraceToString())
    }

    return flow {}
  }

  override fun getAllModernizedLoadRIR(): List<ModernizedLoadRIR> {
    try {
      requireNotNull(db) { DATABASE_IS_NULL }
      val collection = db.getCollection("ModernizedLoadRIR")

      requireNotNull(collection) { COLLECTION_IS_NULL }

      val query = QueryBuilder.select(SelectResult.all()).from(DataSource.collection(collection))

      val result = query.execute().allResults().map {
        val json = it.toJSON()
        withUnknownKeys.decodeFromString<ModernizedLoadRIRDao>(json).ModernizedLoadRIR
      }

      return result
    } catch (e: Exception) {
      Log.e(e.message, e.stackTraceToString())
    }

    return emptyList()
  }

  override fun getModernizedLoadRIR(
    repCapacity: RepCapacity,
    targetPercentage: Double?,
    targetRIR: Double?,
    targetRPE: Double?,
    targetReps: Int?,
  ): List<ModernizedLoadRIR>? {
    try {
      requireNotNull(db) { DATABASE_IS_NULL }
      val collection = db.getCollection("ModernizedLoadRIR")

      requireNotNull(collection) { COLLECTION_IS_NULL }

      val percentageExpression = targetPercentage?.let { "relativeLoadPercent = $it" }

      val targetRIRExpression = targetRIR?.let { "rir = $it" }
      val targetRPEExpression = targetRPE?.let { "rpe = $it" }

      val targetRepsExpression =
        targetReps?.let { reps -> "repsPerformed = $reps".takeIf { reps in 1..30 } }

      val whereClause =
        listOfNotNull(
          percentageExpression,
          targetRIRExpression,
          targetRPEExpression,
          targetRepsExpression,
        )
          .joinToString(separator = " AND ") { it }

      val queryExpression =
        "SELECT * FROM `ModernizedLoadRIR`" +
                "WHERE type = \"ModernizedLoadRIR\"" +
                "AND repCapacity = ${repCapacity.name}" +
                if (whereClause.isNotEmpty()) " AND $whereClause" else ""

      val sqlQeury = db.createQuery(queryExpression)

      val queryResult = sqlQeury.execute().allResults()

      return queryResult.map {
        val json = it.toJSON()
        withUnknownKeys.decodeFromString<ModernizedLoadRIRDao>(json).ModernizedLoadRIR
      }
    } catch (e: Exception) {
      Log.e(e.message, e.stackTraceToString())
    }
    return null
  }
}
