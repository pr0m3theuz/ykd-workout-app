package com.example.myapplication.common.infrastructure.persistence

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
import com.example.myapplication.common.domain.model.CompletedProgramsDao
import com.example.myapplication.common.domain.model.CompletedSetsDao
import com.example.myapplication.common.domain.model.CompletedWorkoutsDao
import com.example.myapplication.common.domain.model.Equipment
import com.example.myapplication.common.domain.model.EquipmentDao
import com.example.myapplication.common.domain.model.Exercise
import com.example.myapplication.common.domain.model.ExerciseDao
import com.example.myapplication.common.domain.model.Force
import com.example.myapplication.common.domain.model.ForceDao
import com.example.myapplication.common.domain.model.Mechanic
import com.example.myapplication.common.domain.model.MechanicDao
import com.example.myapplication.common.domain.model.MuscleGroup
import com.example.myapplication.common.domain.model.MuscleGroupDao
import com.example.myapplication.common.domain.model.MuscleRoleFunction
import com.example.myapplication.common.domain.model.MuscleRoleFunctionDao
import com.example.myapplication.common.domain.model.Program
import com.example.myapplication.common.domain.model.Set
import com.example.myapplication.common.domain.model.SetDao
import com.example.myapplication.common.domain.model.UserProfile
import com.example.myapplication.common.domain.model.UserProfileManager
import com.example.myapplication.common.domain.model.Utility
import com.example.myapplication.common.domain.model.UtilityDao
import com.example.myapplication.common.domain.model.Workout
import com.example.myapplication.common.infrastructure.database.DatabaseManager
import com.example.myapplication.common.util.newLazyListItemID
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import org.bson.types.ObjectId

// Declares the DAO as a private property in the constructor. Pass in the DAO
// instead of the whole database, because you only need access to the DAO

private const val DATABASE_IS_NULL = "Exercise Repository: Database does not exist."

private const val COLLECTION_IS_NULL =
  "Exercise Repository: Exercise Collection does not exist in the Database."

class ExercisesRepositoryImpl(
  private val databaseManager: DatabaseManager,
  userProfileManager: UserProfileManager,
) : ExercisesRepository {
  // Room executes all queries on a separate thread.
  // Observed Flow will notify the observer when the data has changed.
  val db: Database? = databaseManager.database
  val user: UserProfile = userProfileManager.getCurrentUser()
  private val withUnknownKeys = Json { ignoreUnknownKeys = true }

  override fun getAllExercises(): Flow<List<Exercise>> {
    try {
      requireNotNull(db) { DATABASE_IS_NULL }

      val collection = db.getCollection("Exercise")

      requireNotNull(collection) { COLLECTION_IS_NULL }

      val query =
        QueryBuilder.select(SelectResult.all())
          .from(DataSource.collection(collection))
          //          .where(Expression.all())
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
        val exercise = withUnknownKeys.decodeFromString<ExerciseDao>(json).Exercise // 5
        exercises.add(exercise) // 6
      }
    }
    Log.i("ExercisesRepositoryImpl.mapQueryChangeToExercise()", exercises.firstOrNull().toString())
    return exercises // 7
  }

  private fun mapQueryChangeToEquipment(queryChange: QueryChange): List<Equipment> {
    val equipments = mutableListOf<Equipment>()

    queryChange.results?.let { results ->
      results.forEach { result ->
        val json = result.toJSON()
        val equipment = withUnknownKeys.decodeFromString<EquipmentDao>(json).Equipment
        equipments.add(equipment)
      }
    }
    //    Log.i("ExercisesRepositoryImpl.mapQueryChangeToEquipment()", equipments.toString())
    return equipments
  }

  private fun mapQueryChangeToMuscleGroups(queryChange: QueryChange): List<MuscleGroup> {
    val muscleGroups = mutableListOf<MuscleGroup>()

    queryChange.results?.let { results ->
      results.forEach { result ->
        val json = result.toJSON()
        val muscleGroup = withUnknownKeys.decodeFromString<MuscleGroupDao>(json).MuscleGroup
        muscleGroups.add(muscleGroup)
      }
    }
    //    Log.i("ExercisesRepositoryImpl.mapQueryChangeToMuscleGroups()", muscleGroups.toString())
    return muscleGroups
  }

  // Version for Utilities
  private fun mapQueryChangeToUtilities(queryChange: QueryChange): List<Utility> {
    val utilities = mutableListOf<Utility>()

    queryChange.results?.let { results ->
      results.forEach { result ->
        val json = result.toJSON()
        val utility = withUnknownKeys.decodeFromString<UtilityDao>(json).Utility
        utilities.add(utility)
      }
    }
    return utilities
  }

  // Version for Forces
  private fun mapQueryChangeToForces(queryChange: QueryChange): List<Force> {
    val forces = mutableListOf<Force>()

    queryChange.results?.let { results ->
      results.forEach { result ->
        val json = result.toJSON()
        val force = withUnknownKeys.decodeFromString<ForceDao>(json).Force
        forces.add(force)
      }
    }
    return forces
  }

  // Version for Mechanics
  private fun mapQueryChangeToMechanics(queryChange: QueryChange): List<Mechanic> {
    val mechanics = mutableListOf<Mechanic>()

    queryChange.results?.let { results ->
      results.forEach { result ->
        val json = result.toJSON()
        val mechanic = withUnknownKeys.decodeFromString<MechanicDao>(json).Mechanic
        mechanics.add(mechanic)
      }
    }
    return mechanics
  }

  // Version for MuscleRoleFunctions
  private fun mapQueryChangeToMuscleRoleFunctions(
    queryChange: QueryChange
  ): List<MuscleRoleFunction> {
    val muscleRoleFunctions = mutableListOf<MuscleRoleFunction>()

    queryChange.results?.let { results ->
      results.forEach { result ->
        val json = result.toJSON()
        val muscleRoleFunction =
          withUnknownKeys.decodeFromString<MuscleRoleFunctionDao>(json).MuscleRoleFunction
        muscleRoleFunctions.add(muscleRoleFunction)
      }
    }
    return muscleRoleFunctions
  }

  override fun getAllEquipment(): Flow<List<Equipment>> {
    val equipmentList = mutableListOf<Equipment>()
    try {
      requireNotNull(db) { DATABASE_IS_NULL }

      val collection = db.getCollection("Equipment")

      requireNotNull(collection) { COLLECTION_IS_NULL }

      val query =
        QueryBuilder.select(SelectResult.all())
          .from(DataSource.collection(collection))
          //          .where(Expression.all())
          .orderBy(Ordering.property("name").ascending())

      // loop through results and add to list
      val flow =
        query
          .queryChangeFlow()
          .map { queryChange -> mapQueryChangeToEquipment(queryChange) }
          .flowOn(Dispatchers.IO)

      query.execute() // <4>
      return flow // <5>
    } catch (e: Exception) {
      Log.e(e.message, e.stackTraceToString())
    }

    return flow {}
  }

  override fun getAllMuscleGroups(): Flow<List<MuscleGroup>> {
    val muscleGroups = mutableListOf<MuscleGroup>()
    try {
      requireNotNull(db) { DATABASE_IS_NULL }

      val collection = db.getCollection("MuscleGroup")

      requireNotNull(collection) { COLLECTION_IS_NULL }

      val query =
        QueryBuilder.select(SelectResult.all())
          .from(DataSource.collection(collection))
          //          .where(Expression.all())
          .orderBy(Ordering.property("name").ascending())

      val flow =
        query
          .queryChangeFlow()
          .map { queryChange -> mapQueryChangeToMuscleGroups(queryChange) }
          .flowOn(Dispatchers.IO)

      query.execute() // <4>
      return flow // <5>
    } catch (e: Exception) {
      Log.e(e.message, e.stackTraceToString())
    }

    return flow {}
  }

  override fun getAllUtilities(): Flow<List<Utility>> {
    try {
      requireNotNull(db) { DATABASE_IS_NULL }

      val collection = db.getCollection("Utility")

      requireNotNull(collection) { COLLECTION_IS_NULL }

      val query =
        QueryBuilder.select(SelectResult.all())
          .from(DataSource.collection(collection))
          //              .where(Expression.all())
          .orderBy(Ordering.property("name").ascending())

      val flow =
        query
          .queryChangeFlow()
          .map { queryChange -> mapQueryChangeToUtilities(queryChange) }
          .flowOn(Dispatchers.IO)

      query.execute() // <4>
      return flow // <5>
    } catch (e: Exception) {
      Log.e(e.message, e.stackTraceToString())
    }

    return flow {}
  }

  override fun getAllForces(): Flow<List<Force>> {
    try {
      requireNotNull(db) { DATABASE_IS_NULL }

      val collection = db.getCollection("Force")

      requireNotNull(collection) { COLLECTION_IS_NULL }
      val query =
        QueryBuilder.select(SelectResult.all())
          .from(DataSource.collection(collection))
          //          .where(Expression.all())
          .orderBy(Ordering.property("name").ascending())

      // loop through results and add to list
      val flow =
        query
          .queryChangeFlow()
          .map { queryChange -> mapQueryChangeToForces(queryChange) }
          .flowOn(Dispatchers.IO)

      query.execute() // <4>
      return flow // <5>
    } catch (e: Exception) {
      Log.e(e.message, e.stackTraceToString())
    }

    return flow {}
  }

  override fun getAllMechanics(): Flow<List<Mechanic>> {
    try {
      requireNotNull(db) { DATABASE_IS_NULL }

      val collection = db.getCollection("Mechanic")

      requireNotNull(collection) { COLLECTION_IS_NULL }

      Mechanic
      val query =
        QueryBuilder.select(SelectResult.all())
          .from(DataSource.collection(collection))
          //          .where(Expression.all())
          .orderBy(Ordering.property("name").ascending())

      val flow =
        query
          .queryChangeFlow()
          .map { queryChange -> mapQueryChangeToMechanics(queryChange) }
          .flowOn(Dispatchers.IO)

      query.execute() // <4>
      return flow // <5>
    } catch (e: Exception) {
      Log.e(e.message, e.stackTraceToString())
    }

    return flow {}
  }

  override fun getAllMuscleRolesFunctions(): Flow<List<MuscleRoleFunction>> {
    try {
      requireNotNull(db) { DATABASE_IS_NULL }

      val collection = db.getCollection("MuscleRoleFunction")

      requireNotNull(collection) { COLLECTION_IS_NULL }

      val query =
        QueryBuilder.select(SelectResult.all())
          .from(DataSource.collection(collection))
          //          .where(Expression.all())
          .orderBy(Ordering.property("name").ascending())

      val flow =
        query
          .queryChangeFlow()
          .map { queryChange -> mapQueryChangeToMuscleRoleFunctions(queryChange) }
          .flowOn(Dispatchers.IO)

      query.execute() // <4>
      return flow // <5>
    } catch (e: Exception) {
      Log.e(e.message, e.stackTraceToString())
    }

    return flow {}
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
  // TODO Feature to clone Exercises
  override suspend fun insertExercise(exercise: Exercise) {
    try {

      val collection = db?.getCollection("Exercise")

      requireNotNull(collection) { COLLECTION_IS_NULL }

      val mutableDoc = MutableDocument("Exercise:${exercise.id}")
      // TODO Refactor
      mutableDoc.setString("lazyListItemID", newLazyListItemID().toString())
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
      mutableDoc.setString("owner_id", exercise.owner_id)
      mutableDoc.setBoolean("isPublic", exercise.isPublic)
      mutableDoc.setString("type", "Exercise")

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
              .where(Expression.property("id").equalTo(Expression.string(exerciseID.toString())))
              .orderBy(Ordering.property("name").ascending())
              .limit(Expression.intValue(1))

          val result = queryBuilder.execute().allResults().firstOrNull()?.toJSON()

          val exercise = result?.let { withUnknownKeys.decodeFromString<ExerciseDao>(it).Exercise }
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
              .where(Expression.property("id").equalTo(Expression.string(exerciseID.toString())))
              .orderBy(Ordering.property("name").ascending())
              .limit(Expression.intValue(1))

          val result = queryBuilder.execute().allResults().firstOrNull()?.toJSON()

          val exercise = result?.let { withUnknownKeys.decodeFromString<ExerciseDao>(it).Exercise }
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
      val document = MutableDocument(exercise.id.toString(), json)

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

  // TODO Test
  override fun filterExercises(
    exercise: String,
    muscleGroupIDs: List<String>,
    equipmentIDs: List<String>,
    //        movementTypes: List<String>
  ): Flow<List<Exercise>> {

    try {
      requireNotNull(db) { DATABASE_IS_NULL }

      val collection = db.getCollection("Exercise")

      requireNotNull(collection) { COLLECTION_IS_NULL }

      val value =
        when {
          exercise.isEmpty() -> "%"
          exercise.isBlank() -> "%"
          else -> "%${exercise.lowercase()}%"
        }

      //        "SELECT * FROM `Exercise` WHERE isPublic = true AND LOWER(name) LIKE '$value' AND
      // muscleGroupID IN $muscleGroupIDs AND equipmentID IN $equipmentIDs;"

      val muscleGroupInClause =
        muscleGroupIDs
          .joinToString(", ") { "'$it'" } // Join with comma and space
          .let { "[$it]" } // Wrap in parentheses

      val equipmentInClause = equipmentIDs.joinToString(", ") { "'$it'" }.let { "[$it]" }

      // Basic handling for potentially empty lists (IN () is often invalid)
      // A real implementation might need more robust logic here depending on desired behavior
      val muscleCondition = " AND muscleGroupID IN $muscleGroupInClause" // Or omit if appropriate
      val equipmentCondition = " AND equipmentID IN $equipmentInClause" // Or omit if appropriate

      Log.i("ExercisesRepositoryImpl.filterExercises()", "muscleCondition: $muscleCondition")
      Log.i("ExercisesRepositoryImpl.filterExercises()", "equipmentCondition: $equipmentCondition")

      val queryExpression =
        "SELECT *" +
                " FROM `Exercise`" +
                " WHERE isPublic = true" +
                " AND LOWER(name) LIKE '$value'" +
                muscleCondition +
                equipmentCondition
      //              " AND muscleGroupID IN $muscleGroupIDs" +
      //              " AND equipmentID IN $equipmentIDs;"

      Log.i("ExercisesRepositoryImpl.filterExercises()", "queryExpression: $queryExpression")

      val sqlQuery = db.createQuery(queryExpression)

      val flow =
        sqlQuery
          .queryChangeFlow()
          .map { queryChange -> mapQueryChangeToExercise(queryChange) }
          .flowOn(Dispatchers.IO)

      sqlQuery.execute()

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

          val exercise =
            result?.let { xercise ->
              withUnknownKeys.decodeFromString<ExerciseDao>(xercise).Exercise
            }
          return exercise?.id
        }
      }
    } catch (e: Exception) {
      Log.e(e.message, e.stackTraceToString())
    }

    return null
  }

  private fun mapQueryChangeToSet(queryChange: QueryChange): List<Set> {
    val sets = mutableListOf<Set>()
    queryChange.results?.let { results ->
      results.forEach { result ->
        val json = result.toJSON()
        val set = withUnknownKeys.decodeFromString<SetDao>(json).Set
        sets.add(set)
      }
    }
    return sets
  }

  override fun getCompletedSetsByExercise(
    exercise: Exercise
  ): Pair<List<Program>, Map<Workout, List<Set>>>? {
    try {
      //      TODO("Not yet implemented")
      requireNotNull(db) { DATABASE_IS_NULL }

      val collection = db.getCollection("CompletedSets")

      requireNotNull(collection) { COLLECTION_IS_NULL }

      //      val queryExpression = "SELECT * FROM `CompletedSets` " +
      //      "WHERE exerciseID = '${exercise.id}' " +
      //              "ORDER BY DATE_FORMAT_STR(dateCreated, 'YYYY-MM-DD') DESC, setNumber ASC"

      val sets =
        db
          .createQuery(
            "SELECT * FROM `CompletedSets` " +
                    "WHERE exerciseID = '${exercise.id}' " +
                    "ORDER BY dateCreated"
          )
          .execute()
          .allResults()
          .mapNotNull {
            try {
              withUnknownKeys.decodeFromString<CompletedSetsDao>(it.toJSON()).CompletedSets
            } catch (e: Exception) {
              Log.e(e.message, e.stackTraceToString())
              null
            }
          }

      if (sets.isEmpty()) {
        return null
      }

      val workoutIDClause =
        sets
          .map { it.workoutID.toString() }
          .distinct()
          .joinToString(",") { "'$it'" }
          .let { "[$it]" }

      val completedWorkouts =
        db
          .createQuery("SELECT * FROM `CompletedWorkouts` WHERE id IN $workoutIDClause")
          .execute()
          .allResults()
          .mapNotNull {
            try {
              withUnknownKeys.decodeFromString<CompletedWorkoutsDao>(it.toJSON()).CompletedWorkouts
            } catch (e: Exception) {
              Log.e(e.message, e.stackTraceToString())
              null
            }
          }

      val workoutSetsMap =
        sets
          .groupBy { it.workoutID }
          .map { (workoutID, sets) -> completedWorkouts.first { it.id == workoutID } to sets }
          .toMap()

      val programIDs = completedWorkouts.map { it.programID.toString() }

      val programIDsClause = if (programIDs.isEmpty()) {
        null
      } else {
        programIDs.distinct().joinToString(",") { "'$it'" }.let { "[$it]" }
      }

      val completedPrograms =
        programIDsClause?.let { it ->
          db
            .createQuery("SELECT * FROM `CompletedPrograms` WHERE id IN $it")
            .execute()
            .allResults()
            .mapNotNull {
              try {
                withUnknownKeys
                  .decodeFromString<CompletedProgramsDao>(it.toJSON())
                  .CompletedPrograms
              } catch (e: Exception) {
                Log.e(e.message, e.stackTraceToString())
                null
              }
            }
        } ?: listOf()

      return Pair(completedPrograms, workoutSetsMap)
    } catch (e: Exception) {
      Log.e(e.message, e.stackTraceToString())
    }
    return null
  }

  override fun createExercise(exercise: Exercise): Boolean {
    TODO("Not yet implemented")
  }
}
