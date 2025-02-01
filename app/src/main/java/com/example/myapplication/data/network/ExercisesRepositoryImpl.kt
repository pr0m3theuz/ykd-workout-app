package com.example.myapplication.data.network

import android.util.Log
import com.couchbase.lite.DataSource
import com.couchbase.lite.Database
import com.couchbase.lite.Expression
import com.couchbase.lite.Function
import com.couchbase.lite.MutableDocument
import com.couchbase.lite.Ordering
import com.couchbase.lite.QueryBuilder
import com.couchbase.lite.QueryChange
import com.couchbase.lite.SelectResult
import com.couchbase.lite.queryChangeFlow
import com.example.myapplication.data.database.DatabaseManager
import com.example.myapplication.data.model.Equipment
import com.example.myapplication.data.model.EquipmentDao
import com.example.myapplication.data.model.Exercise
import com.example.myapplication.data.model.ExerciseDao
import com.example.myapplication.data.model.Force
import com.example.myapplication.data.model.ForceDao
import com.example.myapplication.data.model.Mechanic
import com.example.myapplication.data.model.MechanicDao
import com.example.myapplication.data.model.MuscleGroup
import com.example.myapplication.data.model.MuscleGroupDao
import com.example.myapplication.data.model.MuscleRoleFunction
import com.example.myapplication.data.model.MuscleRoleFunctionDao
import com.example.myapplication.data.model.Utility
import com.example.myapplication.data.model.UtilityDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.bson.types.ObjectId

// Declares the DAO as a private property in the constructor. Pass in the DAO
// instead of the whole database, because you only need access to the DAO

private const val DATABASE_IS_NULL = "Exercise Repository: Database does not exist."

private const val COLLECTION_IS_NULL =
  "Exercise Repository: Exercise Collection does not exist in the Database."

class ExercisesRepositoryImpl(private val databaseManager: DatabaseManager) : ExercisesRepository {
  // Room executes all queries on a separate thread.
  // Observed Flow will notify the observer when the data has changed.
  val db: Database? = databaseManager.database

  override fun getAllExercises(): Flow<List<Exercise>> {
    try {
      requireNotNull(db) { DATABASE_IS_NULL }

      val collection = db.getCollection("Exercise")

      requireNotNull(collection) { COLLECTION_IS_NULL }

      val query =
        QueryBuilder.select(SelectResult.all())
          .from(DataSource.collection(collection))
          .where(Expression.all())
          .orderBy(Ordering.property("name").ascending())

      val flow =
        query
          .queryChangeFlow()
          .map { queryChange -> mapQueryChangeToExercise(queryChange) }
          .flowOn(Dispatchers.IO)

      query.execute() // <4>
      return flow // <5>
    } catch (e: Exception) {
      Log.e(e.message, e.stackTraceToString())
    }

    return flow {}
  }

  private fun mapQueryChangeToExercise(queryChange: QueryChange): List<Exercise> {
    val exercises = mutableListOf<Exercise>() // 1
    queryChange.results?.let { results -> // 2
      results.forEach { result -> // 3
        val json = result.toJSON() // 4
        val exercise = Json.decodeFromString<ExerciseDao>(json).item // 5
        exercises.add(exercise) // 6
      }
    }
    return exercises // 7
  }

  override fun getAllEquipment(): List<Equipment> {
    val equipmentList = mutableListOf<Equipment>()
    try {
      requireNotNull(db) { DATABASE_IS_NULL }

      val collection = db.getCollection("Equipment")

      requireNotNull(collection) { COLLECTION_IS_NULL }

      val query =
        QueryBuilder.select(SelectResult.all())
          .from(DataSource.collection(collection))
          .where(Expression.all())
          .orderBy(Ordering.property("name").ascending())

      // loop through results and add to list
      query.execute().allResults().forEach { item -> // <5>
        val json = item.toJSON()
        val equipment = Json.decodeFromString<EquipmentDao>(json).item
        equipmentList.add(equipment)
      }
    } catch (e: Exception) {
      Log.e(e.message, e.stackTraceToString())
    }

    return equipmentList.toList()
  }

  override fun getAllMuscleGroups(): List<MuscleGroup> {
    val muscleGroups = mutableListOf<MuscleGroup>()
    try {
      requireNotNull(db) { DATABASE_IS_NULL }

      val collection = db.getCollection("MuscleGroup")

      requireNotNull(collection) { COLLECTION_IS_NULL }

      val query =
        QueryBuilder.select(SelectResult.all())
          .from(DataSource.collection(collection))
          .where(Expression.all())
          .orderBy(Ordering.property("name").ascending())

      // loop through results and add to list
      query.execute().allResults().forEach { item -> // <5>
        val json = item.toJSON()
        val muscleGroup = Json.decodeFromString<MuscleGroupDao>(json).item
        muscleGroups.add(muscleGroup)
      }
    } catch (e: Exception) {
      Log.e(e.message, e.stackTraceToString())
    }

    return muscleGroups.toList()
  }

  override fun getAllUtilities(): List<Utility> {
    val utilities = mutableListOf<Utility>()
    try {
      db?.let { database ->
        val collection = database.getCollection("Utility")
        collection?.let {
          val query =
            QueryBuilder.select(SelectResult.all())
              .from(DataSource.collection(it))
              .where(Expression.all())
              .orderBy(Ordering.property("name").ascending())

          // loop through results and add to list
          query.execute().allResults().forEach { item -> // <5>
            val json = item.toJSON()
            val utility = Json.decodeFromString<UtilityDao>(json).item
            utilities.add(utility)
          }
        }
      }
    } catch (e: Exception) {
      Log.e(e.message, e.stackTraceToString())
    }

    return utilities.toList()
  }

  override fun getAllForces(): List<Force> {
    val forces = mutableListOf<Force>()
    try {
      db?.let { database ->
        val collection = database.getCollection("Force")
        collection?.let {
          val query =
            QueryBuilder.select(SelectResult.all())
              .from(DataSource.collection(it))
              .where(Expression.all())
              .orderBy(Ordering.property("name").ascending())

          // loop through results and add to list
          query.execute().allResults().forEach { item -> // <5>
            val json = item.toJSON()
            val force = Json.decodeFromString<ForceDao>(json).item
            forces.add(force)
          }
        }
      }
    } catch (e: Exception) {
      Log.e(e.message, e.stackTraceToString())
    }

    return forces.toList()
  }

  override fun getAllMechanics(): List<Mechanic> {
    val mechanics = mutableListOf<Mechanic>()
    try {
      db?.let { database ->
        val collection = database.getCollection("Mechanic")
        collection?.let {
          val query =
            QueryBuilder.select(SelectResult.all())
              .from(DataSource.collection(it))
              .where(Expression.all())
              .orderBy(Ordering.property("name").ascending())

          // loop through results and add to list
          query.execute().allResults().forEach { item -> // <5>
            val json = item.toJSON()
            val mechanic = Json.decodeFromString<MechanicDao>(json).item
            mechanics.add(mechanic)
          }
        }
      }
    } catch (e: Exception) {
      Log.e(e.message, e.stackTraceToString())
    }

    return mechanics.toList()
  }

  override fun getAllMuscleRolesFunctions(): List<MuscleRoleFunction> {
    val muscleRoleFunctions = mutableListOf<MuscleRoleFunction>()
    try {
      db?.let { database ->
        val collection = database.getCollection("Mechanic")
        collection?.let {
          val query =
            QueryBuilder.select(SelectResult.all())
              .from(DataSource.collection(it))
              .where(Expression.all())
              .orderBy(Ordering.property("name").ascending())

          // loop through results and add to list
          query.execute().allResults().forEach { item -> // <5>
            val json = item.toJSON()
            val muscleRoleFunction = Json.decodeFromString<MuscleRoleFunctionDao>(json).item
            muscleRoleFunctions.add(muscleRoleFunction)
          }
        }
      }
    } catch (e: Exception) {
      Log.e(e.message, e.stackTraceToString())
    }

    return muscleRoleFunctions.toList()
  }

  override fun getMuscleGroupNameByID(muscleGroupID: String): String {
    var result = ""
    try {
      db?.let { database ->
        val collection = database.getCollection("MuscleGroup")
        collection?.let {
          val queryBuilder =
            QueryBuilder.select(SelectResult.property("name"))
              .from(DataSource.collection(it)) // Replace with your database instance
              .where(Expression.property("muscleGroupID").equalTo(Expression.string(muscleGroupID)))
              .orderBy(Ordering.property("name").ascending())
              .limit(Expression.intValue(1))

          result =
            queryBuilder.execute().allResults().firstOrNull()?.getString("name")
              ?: "Muscle Group not found."
        }
      }
    } catch (e: Exception) {
      Log.e(e.message, e.stackTraceToString())
    }

    return result
  }

  // By default Room runs suspend queries off the main thread, therefore, we don't need to
  // implement anything else to ensure we're not doing long running database work
  // off the main thread.

  // TODO Use function when inserting new exercises
  override suspend fun insertExercise(exercise: Exercise) {
    try {

      val collection = db?.getCollection("Exercise")

      requireNotNull(collection) { COLLECTION_IS_NULL }

      val mutableDoc = MutableDocument()

      mutableDoc.setString("lazyListItemID", exercise.lazyListItemID.toString())
      mutableDoc.setString("name", exercise.name)
      mutableDoc.setString("exrxExerciseID", exercise.exrxExerciseID)
      mutableDoc.setString("equipmentID", exercise.equipmentID)
      mutableDoc.setString("primaryMuscleID", exercise.primaryMuscleID)
      mutableDoc.setString("muscleGroupID", exercise.muscleGroupID)
      mutableDoc.setString("utilityID", exercise.utilityID)
      mutableDoc.setString("mechanicID", exercise.mechanicsID)
      mutableDoc.setString("force", exercise.force)
      mutableDoc.setString("functionID", exercise.functionID)
      mutableDoc.setString("intensityID", exercise.intensityID)
      mutableDoc.setString("weightUnit", exercise.weightUnit)
      mutableDoc.setNumber(
        "workingSetRestInWholeMilliseconds",
        exercise.workingSetRestInWholeMilliseconds,
      )
      mutableDoc.setNumber(
        "warmUpSetRestInWholeMilliseconds",
        exercise.warmUpSetRestInWholeMilliseconds,
      )
      mutableDoc.setBoolean("workingSetRestTimerEnabled", exercise.workingSetRestTimerEnabled)
      mutableDoc.setBoolean("warmUpSetRestTimerEnabled", exercise.warmUpSetRestTimerEnabled)
      mutableDoc.setString("mediaURI", exercise.mediaURI)
      mutableDoc.setString("pageURI", exercise.pageURI)
      mutableDoc.setString("ownerID", exercise.ownerID)
      mutableDoc.setBoolean("isPublic", exercise.isPublic)

      collection.save(mutableDoc)
    } catch (e: Exception) {
      Log.e(e.message, e.stackTraceToString())
    }
  }

  override fun getExerciseDetails(exerciseID: ObjectId): Exercise? {
    try {
      db?.let { database ->
        val collection = database.getCollection("Exercise")

        requireNotNull(collection) { COLLECTION_IS_NULL }

        collection.let {
          val queryBuilder =
            QueryBuilder.select(SelectResult.all())
              .from(DataSource.collection(it)) // Replace with your database instance
              .where(Expression.property("_id").equalTo(Expression.string(exerciseID.toString())))
              .orderBy(Ordering.property("name").ascending())
              .limit(Expression.intValue(1))

          val result = queryBuilder.execute().allResults().firstOrNull()?.toJSON()

          val exercise = result?.let { Json.decodeFromString<ExerciseDao>(it).item }
          return exercise
        }
      }
    } catch (e: Exception) {
      Log.e(e.message, e.stackTraceToString())
    }

    return null
  }

  override fun getExercise(exerciseID: ObjectId?): Exercise? {
    try {
      db?.let { database ->
        val collection = database.getCollection("Exercise")

        requireNotNull(collection) { COLLECTION_IS_NULL }

        collection.let {
          val queryBuilder =
            QueryBuilder.select(SelectResult.all())
              .from(DataSource.collection(it)) // Replace with your database instance
              .where(Expression.property("_id").equalTo(Expression.string(exerciseID.toString())))
              .orderBy(Ordering.property("name").ascending())
              .limit(Expression.intValue(1))

          val result = queryBuilder.execute().allResults().firstOrNull()?.toJSON()

          val exercise = result?.let { Json.decodeFromString<ExerciseDao>(it).item }
          return exercise
        }
      }
    } catch (e: Exception) {
      Log.e(e.message, e.stackTraceToString())
    }

    return null
  }

  override suspend fun updateExercise(exercise: Exercise): Boolean {
    try {
      requireNotNull(db) { DATABASE_IS_NULL }
      val collection = db.getCollection("Exercise")

      requireNotNull(collection) { COLLECTION_IS_NULL }

      /*collection.getDocument(exercise._id.toString())?.toMutable()?.let {
        it.setString("lazyListItemID", exercise.lazyListItemID.toString())
        it.setString("name", exercise.name)
        it.setString("exrxExerciseID", exercise.exrxExerciseID)
        it.setString("equipmentID", exercise.equipmentID)
        it.setString("primaryMuscleID", exercise.primaryMuscleID)
        it.setString("muscleGroupID", exercise.muscleGroupID)
        it.setString("utilityID", exercise.utilityID)
        it.setString("mechanicID", exercise.mechanicsID)
        it.setString("force", exercise.force)
        it.setString("functionID", exercise.functionID)
        it.setString("intensityID", exercise.intensityID)
        it.setString("weightUnit", exercise.weightUnit)
        it.setNumber(
          "workingSetRestInWholeMilliseconds",
          exercise.workingSetRestInWholeMilliseconds,
        )
        it.setNumber("warmUpSetRestInWholeMilliseconds", exercise.warmUpSetRestInWholeMilliseconds)
        it.setBoolean("workingSetRestTimerEnabled", exercise.workingSetRestTimerEnabled)
        it.setBoolean("warmUpSetRestTimerEnabled", exercise.warmUpSetRestTimerEnabled)
        it.setString("mediaURI", exercise.mediaURI)
        it.setString("pageURI", exercise.pageURI)
        it.setString("ownerID", exercise.ownerID)
        it.setBoolean("isPublic", exercise.isPublic)

        collection.save(it)
      }*/

      val json = Json.encodeToString(exercise)
      val document = MutableDocument(exercise._id.toString(), json)

      collection.save(document)

      return true
    } catch (e: Exception) {
      Log.e(e.message, e.stackTraceToString())
    }
    return false
  }

  override suspend fun updateWeightUnitsForExercise(exerciseID: ObjectId, weightUnit: String) {
    try {
      val collection =
        db?.getCollection("Exercise") ?: throw IllegalStateException("collection not found")

      collection.getDocument(exerciseID.toString())?.toMutable()?.let {
        it.setString("weightUnit", weightUnit)

        collection.save(it)
      }
    } catch (e: Exception) {
      Log.e(e.message, e.stackTraceToString())
    }
  }

  override suspend fun updateWorkingSetRestTimerForExercise(
    exerciseID: ObjectId,
    restDuration: Long,
  ) {
    try {
      val collection =
        db?.getCollection("Exercise") ?: throw IllegalStateException("collection not found")

      collection.getDocument(exerciseID.toString())?.toMutable()?.let {
        it.setNumber("workingSetRestInWholeMilliseconds", restDuration)

        collection.save(it)
      }
    } catch (e: Exception) {
      Log.e(e.message, e.stackTraceToString())
    }
  }

  override suspend fun updateWarmUpSetRestTimerForExercise(
    exerciseID: ObjectId,
    restDuration: Long,
  ) {
    try {
      val collection =
        db?.getCollection("Exercise") ?: throw IllegalStateException("collection not found")

      collection.getDocument(exerciseID.toString())?.toMutable()?.let {
        it.setNumber("warmUpSetRestInWholeMilliseconds", restDuration)

        collection.save(it)
      }
    } catch (e: Exception) {
      Log.e(e.message, e.stackTraceToString())
    }
  }

  override suspend fun enableWorkingSetRestTimerForExercise(exerciseID: ObjectId, enable: Boolean) {
    try {
      val collection =
        db?.getCollection("Exercise") ?: throw IllegalStateException("collection not found")

      collection.getDocument(exerciseID.toString())?.toMutable()?.let {
        it.setBoolean("workingSetRestTimerEnabled", enable)

        collection.save(it)
      }
    } catch (e: Exception) {
      Log.e(e.message, e.stackTraceToString())
    }
  }

  override suspend fun enableWarmUpSetRestTimerForExercise(exerciseID: ObjectId, enable: Boolean) {
    try {
      val collection =
        db?.getCollection("Exercise") ?: throw IllegalStateException("collection not found")

      collection.getDocument(exerciseID.toString())?.toMutable()?.let {
        it.setBoolean("warmUpSetRestTimerEnabled", enable)

        collection.save(it)
      }
    } catch (e: Exception) {
      Log.e(e.message, e.stackTraceToString())
    }
  }

  override fun filterExercises(
    exercise: String,
    muscleGroupIDs: List<String>,
    equipmentIDs: List<String>,
    //        movementTypes: List<String>
  ): Flow<List<Exercise>> {

    try {
      val collection =
        db?.getCollection("Exercise") ?: throw IllegalStateException("collection not found")

      var whereQueryExpression =
        Expression.property("isPublic")
          .equalTo(Expression.booleanValue(true))
          .and(
            Function.lower(Expression.property("name"))
              .like(Expression.string("%" + exercise.lowercase() + "%"))
          )

      // Add muscle group filter if provided
      if (muscleGroupIDs.isNotEmpty()) {
        val muscleGroupExpression =
          Expression.property("muscleGroupID").`in`(Expression.list(muscleGroupIDs))

        whereQueryExpression = whereQueryExpression.and(muscleGroupExpression)
      }

      // Add equipment filter if provided
      if (equipmentIDs.isNotEmpty()) {
        val equipmentExpression =
          Expression.property("equipmentID").`in`(Expression.list(equipmentIDs))

        whereQueryExpression = whereQueryExpression.and(equipmentExpression)
      }

      val query =
        QueryBuilder.select(SelectResult.all())
          .from(DataSource.collection(collection))
          .where(whereQueryExpression)
          .orderBy(Ordering.property("name").ascending())

      val flow =
        query
          .queryChangeFlow()
          .map { queryChange -> mapQueryChangeToExercise(queryChange) }
          .flowOn(Dispatchers.IO)

      query.execute()
      return flow
    } catch (e: Exception) {
      Log.e(e.message, e.stackTraceToString())
    }

    return flow {}
  }

  override fun getExerciseByName(exerciseName: String): ObjectId? {
    try {
      db?.let { database ->
        val collection = database.getCollection("Exercise")
        collection?.let {
          val queryBuilder =
            QueryBuilder.select(SelectResult.all())
              .from(DataSource.collection(it)) // Replace with your database instance
              .where(
                Function.lower(Expression.property("name"))
                  .like(Expression.string(exerciseName.lowercase()))
              )
              .orderBy(Ordering.property("name").ascending())
              .limit(Expression.intValue(1))

          val result = queryBuilder.execute().allResults().firstOrNull()?.toJSON()

          val exercise = result?.let { Json.decodeFromString<ExerciseDao>(it).item }
          return exercise?._id
        }
      }
    } catch (e: Exception) {
      Log.e(e.message, e.stackTraceToString())
    }

    return null
  }
}
