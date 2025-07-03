package com.example.myapplication.common.infrastructure.persistence

import android.util.Log
import com.couchbase.lite.DataSource
import com.couchbase.lite.Database
import com.couchbase.lite.Expression
import com.couchbase.lite.MutableDocument
import com.couchbase.lite.QueryBuilder
import com.couchbase.lite.SelectResult
import com.example.myapplication.common.domain.model.UserProfile
import com.example.myapplication.common.infrastructure.database.DatabaseManager
import kotlinx.serialization.json.Json

private const val DATABASE_IS_NULL = "User Repository: Database does not exist."

private const val COLLECTION_IS_NULL =
  "User Repository: User Collection does not exist in the Database."

interface UserRepository {
  fun createUser(user: UserProfile)

  fun updateCurrentUser(user: UserProfile)

  fun deleteCurrentUser(user: UserProfile)

  fun getUser(user: UserProfile): UserProfile?
}

class UserRepositoryImpl(private val databaseManager: DatabaseManager) : UserRepository {
  val db: Database? = databaseManager.database
  private val withUnknownKeys = Json {
    encodeDefaults = true
    ignoreUnknownKeys = true
  }

  override fun createUser(user: UserProfile) {
    try {
      requireNotNull(db) { DATABASE_IS_NULL }

      val collection = db.getCollection("Users")
      requireNotNull(collection) { COLLECTION_IS_NULL }

      val userIDStr = "User:${user.id}"
      val userJson = withUnknownKeys.encodeToString(user)
      val document = MutableDocument(userIDStr, userJson)

      collection.save(document)
    } catch (e: Exception) {
      Log.e(e.message, e.stackTraceToString())
    }
  }

  override fun updateCurrentUser(user: UserProfile) {
    try {
      requireNotNull(db) { DATABASE_IS_NULL }

      val collection = db.getCollection("Users")
      requireNotNull(collection) { COLLECTION_IS_NULL }

      val userIDStr = "User:${user.id}"
      val document = user.id.let { collection.getDocument(userIDStr)?.toMutable() }
      requireNotNull(document) { "User Repository: User does not exist!" }

      val userJson = withUnknownKeys.encodeToString(user)
      document.setJSON(userJson)

      collection.save(document)
    } catch (e: Exception) {
      Log.e(e.message, e.stackTraceToString())
    }
  }

  override fun deleteCurrentUser(user: UserProfile) {
    try {
      requireNotNull(db) { DATABASE_IS_NULL }
      val collection = db.getCollection("Users")
      requireNotNull(collection) { COLLECTION_IS_NULL }
      val document = collection.getDocument(user.id.toHexString() ?: "MISSING_VALUE")
      requireNotNull(document) { "User Repository: User does not exist!" }

      collection.delete(document)
    } catch (e: Exception) {
      Log.e(e.message, e.stackTraceToString())
    }
  }

  override fun getUser(user: UserProfile): UserProfile? {
    try {
      requireNotNull(db) { DATABASE_IS_NULL }
      val collection = db.getCollection("Users")
      requireNotNull(collection) { COLLECTION_IS_NULL }

      val queryBuilder =
        QueryBuilder.select(SelectResult.all())
          .from(DataSource.collection(collection))
          .where(Expression.property("id").equalTo(Expression.string(user.id.toHexString())))
          .limit(Expression.intValue(1))

      val result = queryBuilder.execute().allResults().firstOrNull()?.toJSON()

      val dbUser = withUnknownKeys.decodeFromString<UserProfile>(result.toString())

      return if (dbUser.id == user.id) {
        dbUser
      } else {
        null
      }
    } catch (e: Exception) {
      Log.e(e.message, e.stackTraceToString())
    }
    return null
  }
}