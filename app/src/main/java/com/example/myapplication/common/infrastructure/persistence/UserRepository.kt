package com.example.myapplication.common.infrastructure.persistence

import android.util.Log
import com.couchbase.lite.Database
import com.couchbase.lite.MutableDocument
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
}

class UserRepositoryImpl(private val databaseManager: DatabaseManager) : UserRepository {
  val db: Database? = databaseManager.database
  private val withUnknownKeys = Json { ignoreUnknownKeys = true }

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
      val document = collection.getDocument(user.id.toHexString())?.toMutable()
      requireNotNull(document) { "User Repository: User does not exist!" }

      document.setString("name", user.name)
      document.setString("email", user.email)
      document.setString("password", user.password)
      document.setString("weightUnit", user.defaultWeightUnit)
      document.setString("intensity", user.intensity)
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
      val document = collection.getDocument(user.id.toHexString())
      requireNotNull(document) { "User Repository: User does not exist!" }

      collection.delete(document)
    } catch (e: Exception) {
      Log.e(e.message, e.stackTraceToString())
    }
  }

}