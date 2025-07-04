package com.example.myapplication.common.infrastructure.persistence

import android.util.Log
import com.couchbase.lite.DataSource
import com.couchbase.lite.Database
import com.couchbase.lite.Expression
import com.couchbase.lite.MutableDocument
import com.couchbase.lite.QueryBuilder
import com.couchbase.lite.SelectResult
import com.example.myapplication.common.domain.model.Barbell
import com.example.myapplication.common.domain.model.Collar
import com.example.myapplication.common.domain.model.Plate
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

  fun addPlate(userId: String, plate: Plate)
  fun removePlate(userId: String, plateId: String)
  fun addBarbell(userId: String, barbell: Barbell)
  fun removeBarbell(userId: String, barbellId: String)
  fun addCollar(userId: String, collar: Collar)
  fun removeCollar(userId: String, collarId: String)
}

class UserRepositoryImpl(private val databaseManager: DatabaseManager) : UserRepository {
  val db: Database? = databaseManager.database
  private val withUnknownKeys = Json {
    encodeDefaults = true
    ignoreUnknownKeys = true
  } // Added encodeDefaults for empty lists

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

      val result = queryBuilder.execute().allResults().firstOrNull()

      return result?.let { doc ->
        withUnknownKeys.decodeFromString<UserProfile>(doc.toJSON())
      }
    } catch (e: Exception) {
      Log.e(e.message, e.stackTraceToString())
      return null // Return null on error
    }
  }

  // Helper function to get user document
  private fun getUserDocument(userId: String, collection: com.couchbase.lite.Collection): MutableDocument? {
    return collection.getDocument(userId)?.toMutable()
  }

  override fun addPlate(userId: String, plate: Plate) {
    try {
      requireNotNull(db) { DATABASE_IS_NULL }
      val collection = db.getCollection("Users")
      requireNotNull(collection) { COLLECTION_IS_NULL }
      val document = getUserDocument(userId, collection)
      requireNotNull(document) { "User Repository: User with ID $userId does not exist!" }

      val platesJson = document.getString("plates")
      val platesList = if (platesJson != null) {
        withUnknownKeys.decodeFromString<MutableList<Plate>>(platesJson)
      } else {
        mutableListOf()
      }
      platesList.add(plate)
      document.setString("plates", withUnknownKeys.encodeToString(platesList))
      collection.save(document)
    } catch (e: Exception) {
      Log.e(e.message, e.stackTraceToString())
    }
  }

  override fun removePlate(userId: String, plateId: String) {
    try {
      requireNotNull(db) { DATABASE_IS_NULL }
      val collection = db.getCollection("Users")
      requireNotNull(collection) { COLLECTION_IS_NULL }
      val document = getUserDocument(userId, collection)
      requireNotNull(document) { "User Repository: User with ID $userId does not exist!" }

      val platesJson = document.getString("plates")
      if (platesJson != null) {
        val platesList = withUnknownKeys.decodeFromString<MutableList<Plate>>(platesJson)
        platesList.removeAll { it.id == plateId }
        document.setString("plates", withUnknownKeys.encodeToString(platesList))
        collection.save(document)
      }
    } catch (e: Exception) {
      Log.e(e.message, e.stackTraceToString())
    }
  }

  override fun addBarbell(userId: String, barbell: Barbell) {
    try {
      requireNotNull(db) { DATABASE_IS_NULL }
      val collection = db.getCollection("Users")
      requireNotNull(collection) { COLLECTION_IS_NULL }
      val document = getUserDocument(userId, collection)
      requireNotNull(document) { "User Repository: User with ID $userId does not exist!" }

      val barbellsJson = document.getString("barbells")
      val barbellsList = if (barbellsJson != null) {
        withUnknownKeys.decodeFromString<MutableList<Barbell>>(barbellsJson)
      } else {
        mutableListOf()
      }
      barbellsList.add(barbell)
      document.setString("barbells", withUnknownKeys.encodeToString(barbellsList))
      collection.save(document)
    } catch (e: Exception) {
      Log.e(e.message, e.stackTraceToString())
    }
  }

  override fun removeBarbell(userId: String, barbellId: String) {
    try {
      requireNotNull(db) { DATABASE_IS_NULL }
      val collection = db.getCollection("Users")
      requireNotNull(collection) { COLLECTION_IS_NULL }
      val document = getUserDocument(userId, collection)
      requireNotNull(document) { "User Repository: User with ID $userId does not exist!" }

      val barbellsJson = document.getString("barbells")
      if (barbellsJson != null) {
        val barbellsList = withUnknownKeys.decodeFromString<MutableList<Barbell>>(barbellsJson)
        barbellsList.removeAll { it.id == barbellId }
        document.setString("barbells", withUnknownKeys.encodeToString(barbellsList))
        collection.save(document)
      }
    } catch (e: Exception) {
      Log.e(e.message, e.stackTraceToString())
    }
  }

  override fun addCollar(userId: String, collar: Collar) {
    try {
      requireNotNull(db) { DATABASE_IS_NULL }
      val collection = db.getCollection("Users")
      requireNotNull(collection) { COLLECTION_IS_NULL }
      val document = getUserDocument(userId, collection)
      requireNotNull(document) { "User Repository: User with ID $userId does not exist!" }

      val collarsJson = document.getString("collars")
      val collarsList = if (collarsJson != null) {
        withUnknownKeys.decodeFromString<MutableList<Collar>>(collarsJson)
      } else {
        mutableListOf()
      }
      collarsList.add(collar)
      document.setString("collars", withUnknownKeys.encodeToString(collarsList))
      collection.save(document)
    } catch (e: Exception) {
      Log.e(e.message, e.stackTraceToString())
    }
  }

  override fun removeCollar(userId: String, collarId: String) {
    try {
      requireNotNull(db) { DATABASE_IS_NULL }
      val collection = db.getCollection("Users")
      requireNotNull(collection) { COLLECTION_IS_NULL }
      val document = getUserDocument(userId, collection)
      requireNotNull(document) { "User Repository: User with ID $userId does not exist!" }

      val collarsJson = document.getString("collars")
      if (collarsJson != null) {
        val collarsList = withUnknownKeys.decodeFromString<MutableList<Collar>>(collarsJson)
        collarsList.removeAll { it.id == collarId }
        document.setString("collars", withUnknownKeys.encodeToString(collarsList))
        collection.save(document)
      }
    } catch (e: Exception) {
      Log.e(e.message, e.stackTraceToString())
    }
  }
}