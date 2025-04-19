package com.example.myapplication.data.network

import android.util.Log
import com.couchbase.lite.DataSource
import com.couchbase.lite.Database
import com.couchbase.lite.Expression
import com.couchbase.lite.Ordering
import com.couchbase.lite.QueryBuilder
import com.couchbase.lite.QueryChange
import com.couchbase.lite.SelectResult
import com.example.myapplication.data.database.DatabaseManager
import com.example.myapplication.data.model.Set
import com.example.myapplication.data.model.SetDao
import kotlinx.serialization.json.Json
import org.bson.types.ObjectId

private const val DATABASE_IS_NULL = "Completed Repository: Database does not exist."

private const val COLLECTION_IS_NULL =
  "Completed Repository: Completed * Collection does not exist in the Database."

class CompletedRepositoryImpl(private val databaseManager: DatabaseManager) : CompletedRepository {

  val db: Database? = databaseManager.database

  // TODO Decide if I should create a repository for user data
  val userDatabase: Database? = databaseManager.userDatabase

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
}