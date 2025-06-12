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
import kotlinx.serialization.encodeToString
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
  private val json = Json { ignoreUnknownKeys = true; prettyPrint = true; encodeDefaults = true } // Added encodeDefaults for empty lists

  override fun createUser(user: UserProfile) {
    try {
      requireNotNull(db) { DATABASE_IS_NULL }

      val collection = db.getCollection("Users")
      requireNotNull(collection) { COLLECTION_IS_NULL }

      val document = MutableDocument()
      document.setString("id", user.id.toHexString())
      document.setString("name", user.name)
      document.setString("email", user.email)
      document.setString("password", user.password)
      document.setString("weightUnit", user.defaultWeightUnit)
      document.setString("intensity", user.intensity)
      document.setString("type", "User")
      document.setString("authToken", user.authToken)
      document.setString("plates", json.encodeToString(user.plates))
      document.setString("barbells", json.encodeToString(user.barbells))
      document.setString("collars", json.encodeToString(user.collars))
//      document.setString("lengthUnit", user.defaultLengthUnit)

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
      val document = user.id.let { collection.getDocument(it.toHexString())?.toMutable() }
      requireNotNull(document) { "User Repository: User does not exist!" }

      document.setString("name", user.name)
      document.setString("email", user.email)
      document.setString("password", user.password)
      document.setString("weightUnit", user.defaultWeightUnit)
      document.setString("intensity", user.intensity)
      document.setString("authToken", user.authToken)
      document.setString("plates", json.encodeToString(user.plates))
      document.setString("barbells", json.encodeToString(user.barbells))
      document.setString("collars", json.encodeToString(user.collars))
//      document.setString("lengthUnit", user.defaultLengthUnit)

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
          .where(
            Expression.property("id").equalTo(Expression.string(user.id.toHexString()))
          )
          .limit(Expression.intValue(1))

      val result = queryBuilder.execute().allResults().firstOrNull()?.getDictionary(collection.name)

      return result?.let { doc ->
        UserProfile(
          id = org.bson.types.ObjectId(doc.getString("id")),
          name = doc.getString("name"),
          email = doc.getString("email"),
          password = doc.getString("password"),
          defaultWeightUnit = doc.getString("weightUnit") ?: "lb", // Provide default if missing
          intensity = doc.getString("intensity") ?: "RIR", // Provide default if missing
          authToken = doc.getString("authToken"),
          isLoggedIn = doc.getBoolean("isLoggedIn"), // Assuming this field exists or will be added
          plates = doc.getString("plates")?.let { json.decodeFromString<List<Plate>>(it) } ?: emptyList(),
          barbells = doc.getString("barbells")?.let { json.decodeFromString<List<Barbell>>(it) } ?: emptyList(),
          collars = doc.getString("collars")?.let { json.decodeFromString<List<Collar>>(it) } ?: emptyList()
        )
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
        json.decodeFromString<MutableList<Plate>>(platesJson)
      } else {
        mutableListOf()
      }
      platesList.add(plate)
      document.setString("plates", json.encodeToString(platesList))
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
        val platesList = json.decodeFromString<MutableList<Plate>>(platesJson)
        platesList.removeAll { it.id == plateId }
        document.setString("plates", json.encodeToString(platesList))
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
        json.decodeFromString<MutableList<Barbell>>(barbellsJson)
      } else {
        mutableListOf()
      }
      barbellsList.add(barbell)
      document.setString("barbells", json.encodeToString(barbellsList))
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
        val barbellsList = json.decodeFromString<MutableList<Barbell>>(barbellsJson)
        barbellsList.removeAll { it.id == barbellId }
        document.setString("barbells", json.encodeToString(barbellsList))
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
        json.decodeFromString<MutableList<Collar>>(collarsJson)
      } else {
        mutableListOf()
      }
      collarsList.add(collar)
      document.setString("collars", json.encodeToString(collarsList))
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
        val collarsList = json.decodeFromString<MutableList<Collar>>(collarsJson)
        collarsList.removeAll { it.id == collarId }
        document.setString("collars", json.encodeToString(collarsList))
        collection.save(document)
      }
    } catch (e: Exception) {
      Log.e(e.message, e.stackTraceToString())
    }
  }
}