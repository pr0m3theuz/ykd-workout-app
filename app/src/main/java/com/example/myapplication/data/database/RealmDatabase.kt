package com.example.myapplication.data.database

import android.util.Log
import com.example.myapplication.data.model.Equipment
import com.example.myapplication.data.model.Exercise
import com.example.myapplication.data.model.ExerciseNote
import com.example.myapplication.data.model.ExerciseSets
import com.example.myapplication.data.model.Exercises
import com.example.myapplication.data.model.ExercisesSets
import com.example.myapplication.data.model.Force
import com.example.myapplication.data.model.Mechanic
import com.example.myapplication.data.model.Mesocycle
import com.example.myapplication.data.model.MesocycleNote
import com.example.myapplication.data.model.Mesocycles
import com.example.myapplication.data.model.Microcycle
import com.example.myapplication.data.model.MicrocycleNote
import com.example.myapplication.data.model.Microcycles
import com.example.myapplication.data.model.Muscle
import com.example.myapplication.data.model.MuscleExercise
import com.example.myapplication.data.model.MuscleGroup
import com.example.myapplication.data.model.MuscleRoleFunction
import com.example.myapplication.data.model.Program
import com.example.myapplication.data.model.ProgramNote
import com.example.myapplication.data.model.Programs
import com.example.myapplication.data.model.Set
import com.example.myapplication.data.model.Utility
import com.example.myapplication.data.model.Workout
import com.example.myapplication.data.model.WorkoutNote
import com.example.myapplication.data.model.Workouts
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.isManaged
import io.realm.kotlin.ext.query
import io.realm.kotlin.internal.platform.assetFileAsStream
import io.realm.kotlin.internal.platform.runBlocking
import io.realm.kotlin.mongodb.App
import io.realm.kotlin.mongodb.Credentials
import io.realm.kotlin.mongodb.subscriptions
import io.realm.kotlin.mongodb.sync.SyncConfiguration
import io.realm.kotlin.mongodb.syncSession
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import org.json.JSONArray
import org.json.JSONObject
import org.mongodb.kbson.ObjectId

class RealmDatabase {

  /*  val realm: Realm by lazy {
      val configuration =
        RealmConfiguration.Builder(
            schema =
              setOf(
                Exercise::class,
                ExerciseSets::class,
                Set::class,
                Workout::class,
                Program::class,
                Mesocycle::class,
                Microcycle::class,
                ExerciseNote::class,
                WorkoutNote::class,
                ProgramNote::class,
                MesocycleNote::class,
                MicrocycleNote::class,
                MuscleExercise::class,
                MuscleRoleFunction::class,
                Muscle::class,
                MuscleGroup::class,
                Equipment::class,
                Utility::class,
                Force::class,
                Mechanic::class,
                //            ProgressionScheme::class,
              )
          )
  //        .initialRealmFile("./database/default.realm")
          .build()
      Realm.open(configuration)
    }*/

  val app: App = App.create("gymcats-rmmte")
  //    var credentials: Credentials

  //    CoroutineScope(Dispatchers.Main).launch { // use runBlocking sparingly -- it can delay UI
  //   interactions
  //      val user = app.login(Credentials.anonymous())
  //    }

  init {
    //    CoroutineScope(Dispatchers.Main).launch {
    //      app.login(Credentials.anonymous())
    //    }
    runBlocking { app.login(Credentials.anonymous()) }
  }

  val realm: Realm by lazy {
    val config =
      app.currentUser?.let {
        SyncConfiguration.Builder(
          user = it,
          schema =
          setOf(
            Exercise::class,
            ExerciseSets::class,
            Set::class,
            Workout::class,
            Program::class,
            Mesocycle::class,
            Microcycle::class,
            ExerciseNote::class,
            WorkoutNote::class,
            ProgramNote::class,
            MesocycleNote::class,
            MicrocycleNote::class,
            MuscleExercise::class,
            MuscleRoleFunction::class,
            Muscle::class,
            MuscleGroup::class,
            Equipment::class,
            Utility::class,
            Force::class,
            Mechanic::class,
            // ProgressionScheme::class,)
          )
        )
          .initialSubscriptions { realm ->
            add(realm.query<Program>(), "programStream", updateExisting = true)
            add(realm.query<ProgramNote>(), "programNote", updateExisting = true)
            add(realm.query<Exercise>(), "exerciseStream", updateExisting = true)
            add(realm.query<ExerciseNote>(), "exerciseNote", updateExisting = true)
            add(realm.query<Mesocycle>(), "mesocycleStream", updateExisting = true)
            add(realm.query<MesocycleNote>(), "mesocycleNote", updateExisting = true)
            add(realm.query<Microcycle>(), "microcycleStream", updateExisting = true)
            add(realm.query<MicrocycleNote>(), "microcycleNote", updateExisting = true)
            add(realm.query<Workout>(), "workoutStream", updateExisting = true)
            add(realm.query<WorkoutNote>(), "workoutNote", updateExisting = true)
            add(realm.query<ExerciseSets>(), "exerciseSetsStream", updateExisting = true)
            add(realm.query<Set>(), "setStream", updateExisting = true)
            add(realm.query<MuscleExercise>(), "muscleExerciseStream", updateExisting = true)
            add(
              realm.query<MuscleRoleFunction>(),
              "muscleRoleFunctionStream",
              updateExisting = true
            )
            add(realm.query<Muscle>(), "muscleStream", updateExisting = true)
            add(realm.query<MuscleGroup>(), "muscleGroupStream", updateExisting = true)
            add(realm.query<Equipment>(), "equipmentStream", updateExisting = true)
            add(realm.query<Utility>(), "utilityStream", updateExisting = true)
            add(realm.query<Force>(), "forceStream", updateExisting = true)
            add(realm.query<Mechanic>(), "mechanicStream", updateExisting = true)
          }
          .build()
      }

    Realm.open(config!!)
  }

  // Exercise & Related Queries
  fun getAllExercises(): Flow<List<Exercise>> {
    return realm.query<Exercise>().find().asFlow().map { exercises ->
      exercises.list.sortedBy { exercise -> exercise.name }
    }
  }

  fun getAllEquipment(): Flow<List<Equipment>> {
    return realm.query<Equipment>().find().asFlow().map { equipments ->
      equipments.list.sortedBy { equipment -> equipment.name }
    }
  }

  fun getAllMuscleGroups(): Flow<List<MuscleGroup>> {
    return realm.query<MuscleGroup>().find().asFlow().map { muscleGroups ->
      muscleGroups.list.sortedBy { muscleGroup -> muscleGroup.name }
    }
  }

  fun getAllUtilities(): Flow<List<Utility>> {
    return realm.query<Utility>().find().asFlow().map { utilities ->
      utilities.list.sortedBy { utility -> utility.name }
    }
  }

  fun getAllForces(): Flow<List<Force>> {
    return realm.query<Force>().find().asFlow().map { forces ->
      forces.list.sortedBy { force -> force.name }
    }
  }

  fun getAllMechanics(): Flow<List<Mechanic>> {
    return realm.query<Mechanic>().find().asFlow().map { mechanics ->
      mechanics.list.sortedBy { mechanic -> mechanic.name }
    }
  }

  fun getAllMuscleRolesFunctions(): Flow<List<MuscleRoleFunction>> {
    return realm.query<MuscleRoleFunction>().find().asFlow().map { functions ->
      functions.list.sortedBy { function -> function.roleFunction }
    }
  }

  fun getMuscleGroupNameByID(muscleGroupID: String): String {
    return realm
      .query<MuscleGroup>("muscleGroupID = $0 SORT(name ASC)", muscleGroupID)
      .first()
      .find()
      ?.name
      ?: throw Exception("Muscle Group not found")
  }

  fun getExerciseDetails(exerciseID: ObjectId): Exercise {
    return realm.query<Exercise>("exerciseID = $0", exerciseID).first().find()
      ?: throw Exception("Exercise not found")
  }

  suspend fun getExercise(exerciseID: ObjectId): Exercise? {
    return realm.query<Exercise>("_id = $0", exerciseID).first().find()
  }

  suspend fun insertExercise(exercise: Exercise) {
    realm.write {
      this.copyToRealm(
        exercise
        //        Exercise(
        //          name = exercise.name,
        //          equipmentID = exercise.equipmentID,
        //          muscleGroupID = exercise.muscleGroupID,
        //          utilityID = exercise.utilityID,
        //          mechanicsID = exercise.mechanicsID,
        //          force = exercise.force,
        //          functionID = exercise.functionID,
        //          intensityID = exercise.intensityID,
        //          weightUnit = exercise.weightUnit,
        //          mediaURI = exercise.mediaURI,
        //          pageURI = exercise.pageURI
        //        )
      )
    }
  }

  suspend fun updateExercise(exercise: Exercise) {
    realm.write {
      exercise
      //      this.copyToRealm(
      //        Exercise(name = exercise.name,
      //          equipmentID = exercise.equipmentID,
      //          muscleGroupID = exercise.muscleGroupID,
      //          utilityID = exercise.utilityID,
      //          mechanicsID = exercise.mechanicsID,
      //          force = exercise.force,
      //          functionID = exercise.functionID,
      //          intensityID = exercise.intensityID,
      //          weightUnit = exercise.weightUnit,
      //          mediaURI = exercise.mediaURI,
      //          pageURI = exercise.pageURI
      //        )
      //      )
    }
  }

  suspend fun updateWeightUnitsForExercise(exerciseID: ObjectId, weightUnit: String) {
    val exercise = getExercise(exerciseID)
    exercise?.weightUnit = weightUnit
  }

  suspend fun updateWorkingSetRestTimerForExercise(exerciseID: ObjectId, restDuration: Long) {
    val exercise = getExercise(exerciseID)
    exercise?.workingSetRestInWholeMilliseconds = restDuration
  }

  suspend fun updateWarmUpSetRestTimerForExercise(exerciseID: ObjectId, restDuration: Long) {
    val exercise = getExercise(exerciseID)
    exercise?.warmUpSetRestInWholeMilliseconds = restDuration
  }

  suspend fun enableWorkingSetRestTimerForExercise(exerciseID: ObjectId, enable: Boolean) {
    val exercise = getExercise(exerciseID)
    exercise?.workingSetRestTimerEnabled = enable
  }

  suspend fun enableWarmUpSetRestTimerForExercise(exerciseID: ObjectId, enable: Boolean) {
    val exercise = getExercise(exerciseID)
    exercise?.warmUpSetRestTimerEnabled = enable
  }

  //  TODO: Figure out to implement this in RealmQL
  fun filterExercises(
    exercise: String,
    muscleGroupIDs: List<String>,
    equipmentIDs: List<String>
    //        movementTypes: List<String>
  ): Flow<List<Exercise>> {
    val muscleGroupsSize = muscleGroupIDs.size.plus(1)
    val muscleGroups = ""
    muscleGroupIDs.forEachIndexed { index, _ -> muscleGroups.plus("${index.plus(1)}, ") }

    val equipment = ""
    equipmentIDs.forEachIndexed { index, _ ->
      equipment.plus("${muscleGroupsSize.plus(index.plus(1))}, ")
    }

    return realm
      .query<Exercise>(
        "name CONTAINS $0 AND muscleGroupID IN {$muscleGroups} AND equipmentID IN {$equipment} SORT(name ASC)",
        exercise,
        muscleGroupIDs,
        equipmentIDs
      )
      .find()
      .asFlow()
      .map { it.list }
  }

  // ExerciseSets Queries

  // Workout Queries

  fun getAllWorkouts(): Flow<List<Workout>> {
    return realm.query<Workout>("SORT(dateCreated DESC)").find().asFlow().map { workouts ->
      workouts.list.sortedByDescending { workout -> workout.dateCreated }
    }
  }

  suspend fun getWorkoutByID(workoutID: ObjectId): Workout? {
    return realm.query<Workout>("workoutID = $0", workoutID).first().find()
  }

  fun getWorkoutsByMicrocycleID(microcycleID: ObjectId): List<Workout> {
    return realm.query<Workout>("microcycleID = $0 SORT(day ASC)", microcycleID).find()
  }

  fun getWorkoutsByMesocycleID(mesocycleID: ObjectId): Flow<List<Workout>> {
    return realm.query<Workout>("mesocycleID = $0", mesocycleID).find().asFlow().map { it.list }
  }

  fun getWorkoutsByProgramID(programID: ObjectId): Flow<List<Workout>> {
    return realm.query<Workout>("programID = $0", programID).find().asFlow().map { it.list }
  }

  suspend fun insertWorkout(workout: Workout) {
    realm.write { this.copyToRealm(workout) }
  }

  suspend fun updateWorkout(workout: Workout) {
    realm.write { workout }
  }

  suspend fun deleteWorkout(_workout: Workout) {
    _workout.isManaged()
    realm.write {
      val workout = realm.query<Workout>("workoutID = $0", _workout._id).first().find()
      if (workout != null) {
        delete(workout)
      }
    }
  }

  //  Program Queries
  fun getAllPrograms(): Flow<List<Program>> {
    return realm.query<Program>().find().asFlow().map { programs ->
      programs.list.sortedByDescending { program -> program.dateCreated }
    }
  }

  suspend fun getProgramByID(programID: ObjectId): Program? {
    return realm.query<Program>("_id = $0", programID).first().find()
  }

  suspend fun insertProgram(program: Program) {
    realm.write { this.copyToRealm(program) }
  }

  suspend fun updateProgram(program: Program) {
    realm.write { program }
  }

  suspend fun deleteProgram(_program: Program) {
    _program.isManaged()
    realm.write {
      val program = realm.query<Program>("programID = $0", _program._id).first().find()
      if (program != null) {
        delete(program)
      }
    }
  }

  //  Mesocycle Queries
  fun getAllMesocycles(): Flow<List<Mesocycle>> {
    return realm.query<Mesocycle>().find().asFlow().map { mesocycles ->
      mesocycles.list.sortedByDescending { mesocycle -> mesocycle.dateCreated }
    }
  }

  fun getMesocycleByID(mesocycleID: ObjectId): Mesocycle? {
    return realm.query<Mesocycle>("_id = $0", mesocycleID).first().find()
  }

  fun getMesocyclesByProgramID(programID: ObjectId): Flow<List<Mesocycle>> {
    return realm.query<Mesocycle>("programID = $0", programID).find().asFlow().map { it.list }
  }

  suspend fun insertMesocycle(mesocycle: Mesocycle) {
    realm.write { this.copyToRealm(mesocycle) }
  }

  suspend fun updateMesocycle(mesocycle: Mesocycle) {
    realm.write { mesocycle }
  }

  suspend fun deleteMesocycle(_mesocycle: Mesocycle) {
    _mesocycle.isManaged()
    realm.write {
      val mesocycle = realm.query<Mesocycle>("_id = $0", _mesocycle._id).first().find()
      if (mesocycle != null) {
        delete(mesocycle)
      }
    }
  }

  //  Microcycle Queries

  fun getAllMicrocycles(): Flow<List<Microcycle>> {
    return realm.query<Microcycle>().find().asFlow().map { microcycles ->
      microcycles.list.sortedByDescending { microcycle -> microcycle.dateCreated }
    }
  }

  fun getMicrocycleByID(microcycleID: ObjectId): Microcycle? {
    return realm.query<Microcycle>("_id = $0", microcycleID).first().find()
  }

  fun getMicrocyclesByMesocycleID(mesocycleID: ObjectId): Flow<List<Microcycle>> {
    return realm.query<Microcycle>("mesocycleID = $0", mesocycleID).find().asFlow().map { it.list }
  }

  fun getMicrocycleByProgramID(programID: ObjectId): Flow<List<Microcycle>> {
    return realm
      .query<Microcycle>("programID = $0 SORT(name ASC, order ASC)", programID)
      .find().asFlow().map { it.list }
  }

  suspend fun insertMicrocycle(microcycle: Microcycle) {
    realm.write { this.copyToRealm(microcycle) }
  }

  suspend fun updateMicrocycle(microcycle: Microcycle) {
    realm.write { microcycle }
  }

  suspend fun deleteMicrocycle(_microcycle: Microcycle) {
    _microcycle.isManaged()
    realm.write {
      val microcycle = realm.query<Mesocycle>("_id = $0", _microcycle._id).first().find()
      if (microcycle != null) {
        delete(microcycle)
      }
    }
  }

  suspend fun getSetsByID(setID: ObjectId): Set? {
    return realm.query<Set>("_id = $0", setID).first().find()
  }

  //  Notes Queries
  //  TODO Get Pinned Notes, Store Seperately?
  //  fun getExerciseNotes(): Flow<List<ExerciseNote>> {
  //    return realm.query<ExerciseNote>().find().asFlow().map { it.list }
  //  }

  fun getPinnedExerciseNotes(
    exerciseID: ObjectId,
    pinned: Boolean = true,
    programPinned: Boolean = false,
    mesocyclePinned: Boolean = false,
    microcyclePinned: Boolean = false,
  ): Flow<List<ExerciseNote>> {
    return realm
      .query<ExerciseNote>(
        "exerciseID = $0 AND pinned = $1 AND programPinned = $2 AND mesocyclePinned = $3 AND microcyclePinned = $4",
        exerciseID,
        pinned,
        programPinned,
        mesocyclePinned,
        microcyclePinned
      )
      .find()
      .asFlow()
      .map { it.list }
  }

  suspend fun insertExerciseNote(exerciseNote: ExerciseNote) {
    realm.write { this.copyToRealm(exerciseNote) }
  }

  suspend fun updateExerciseNote(exerciseNote: ExerciseNote) {
    realm.write { exerciseNote }
  }

  fun getPinnedWorkoutNotes(
    workoutID: ObjectId,
    pinned: Boolean = true,
    programPinned: Boolean = false,
    mesocyclePinned: Boolean = false,
    microcyclePinned: Boolean = false,
  ): Flow<List<WorkoutNote>> {
    return realm
      .query<WorkoutNote>(
        "workoutID = $0 AND pinned = $1 AND programPinned = $2 AND mesocyclePinned = $3 AND microcyclePinned = $4",
        workoutID,
        pinned,
        programPinned,
        mesocyclePinned,
        microcyclePinned
      )
      .find()
      .asFlow()
      .map { it.list }
  }

  suspend fun insertWorkoutNote(workoutNote: WorkoutNote) {
    realm.write { this.copyToRealm(workoutNote) }
  }

  suspend fun updateWorkoutNote(workoutNote: WorkoutNote) {
    realm.write { workoutNote }
  }

  suspend fun migrateData() {
    val json =
      assetFileAsStream("database/programs_202308111151.json").bufferedReader().use {
        it.readText()
      }

    val parsedJson: JSONObject = JSONObject(json)
    val pra = JSONArray(parsedJson["programs"].toString())

    Log.i("DATABASE.migrateData()", pra[0].toString())
    Log.i("Sync Enabled", realm.syncSession.toString())
    Log.i("Realm Subscriptions", realm.subscriptions.toString())

    val programs = Json.decodeFromString<Programs>(json)

    programs.programs.forEach { program ->
      realm.write {
        this.copyToRealm(
          Program().apply {
            name = program.name
            author = program.author
            description = program.description
            shortDescription = program.shortDescription
            uri = program.uri
            level = program.level
            programEquipment = program.programEquipment
            daysPerWeek = program.daysPerWeek
            programLength = program.programLength
          }
        )
      }
    }
  }

  suspend fun migrateMesocycles() {
    val json =
      assetFileAsStream("database/mesocycles_202308111154.json").bufferedReader().use {
        it.readText()
      }

    val mesocycles = Json.decodeFromString<Mesocycles>(json)

    Log.i("DATABASE.migrateMesocycles()", mesocycles.toString())

    mesocycles.mesocycles.forEach { mesocycle ->
      realm.write {
        this.copyToRealm(
          Mesocycle().apply {
            name = mesocycle.name
            description = mesocycle.description
            order = mesocycle.order
          }
        )
      }
    }
  }

  suspend fun migrateMicrocycles() {
    val json =
      assetFileAsStream("database/microcycles_202308111153.json").bufferedReader().use {
        it.readText()
      }

    val microcycles = Json.decodeFromString<Microcycles>(json)

    Log.i("DATABASE.migrateMicrocycles()", microcycles.toString())

    microcycles.microcycles.forEach { microcycle ->
      realm.write {
        this.copyToRealm(
          Microcycle().apply {
            name = microcycle.name
            description = microcycle.description
            order = microcycle.order
            mesocycleID = microcycle.mesocycleID
            programID = microcycle.programID
          }
        )
      }
    }
  }

  suspend fun migrateWorkouts() {
    val json =
      assetFileAsStream("database/workouts_202308111151.json").bufferedReader().use {
        it.readText()
      }

    val workouts = Json.decodeFromString<Workouts>(json)

    Log.i("DATABASE.migrateWorkouts()", workouts.toString())

    workouts.workouts.forEach { workout ->
      realm.write {
        this.copyToRealm(
          Workout().apply {
            name = workout.name
            day = workout.day
            microcycleID = workout.microcycleID
          }
        )
      }
    }
  }

  suspend fun migrateExercises() {
    val json =
      assetFileAsStream("database/exercises_202308111148.json").bufferedReader().use {
        it.readText()
      }

    val exercises = Json.decodeFromString<Exercises>(json)

    Log.i("DATABASE.migrateExercises()", exercises.toString())

    exercises.exercises.forEach { exercise ->
      realm.write {
        this.copyToRealm(
          Exercise().apply {
            name = exercise.name
            exrxExerciseID = exercise.exrxExerciseID
            equipmentID = exercise.equipmentID
            primaryMuscleID = exercise.primaryMuscleID
            muscleGroupID = exercise.muscleGroupID
            utilityID = exercise.utilityID
            mechanicsID = exercise.mechanicsID
            functionID = exercise.functionID
            intensityID = exercise.intensityID
            weightUnit = exercise.weightUnit
            mediaURI = exercise.mediaURI
            pageURI = exercise.pageURI
          }
        )
      }
    }
  }

  suspend fun importExerciseSets() {
    val json =
      assetFileAsStream("database/Break-In Phase Block 1 ExercisesSets copy.json").bufferedReader()
        .use {
          it.readText()
        }

    val exercisesSets = Json.decodeFromString<ExercisesSets>(json)

    Log.i("DATABASE.importExerciseSets()", exercisesSets.toString())

    exercisesSets.exercisesSets.forEach { _exerciseSets ->
      val _exercise = getExercise(_exerciseSets.exerciseObjectID) ?: return@forEach

      val realmSets = mutableListOf<Set>()
      val identicalSets = _exerciseSets.identicalSets
      val numberOfIdenticalSets = _exerciseSets.numberOfIdenticalSets
      val _sets = _exerciseSets.sets
      /*
            val setIDs = mutableListOf<Set>()

            for (setID in _sets) {
              setIDs.add(getSetsByID(setID) ?: return)
            }
      */

      /*
      if (_sets.isNotEmpty() && identicalSets && _sets.size == 1) {
        _sets.forEach { set ->
          for (i in 1..numberOfIdenticalSets) {
            realmSets.add(
              Set().apply {
                setNumber = i
                exerciseWeight = set.exerciseWeight
                targetWeight = set.targetWeight
                targetRepetitions = set.targetRepetitions
                targetRatePerceivedEffort = set.targetRatePerceivedEffort
                targetRepetitionsInReserve = set.targetRepetitionsInReserve
                percentage1RM = set.percentage1RM
              }
            )
          }
        }
      } else if (_sets.isNotEmpty() && !identicalSets) {
        var count = 0
        _sets.forEach { set ->
          count++
          realmSets.add(
            Set().apply {
              setNumber = count
              exerciseWeight = set.exerciseWeight
              targetRepetitions = set.targetRepetitions
              percentage1RM = set.percentage1RM
            }
          )
        }
      }
      */

      realm.write {
        val latestExercise = findLatest(_exercise) ?: return@write
        val latestSets = mutableListOf<Set>()

//       setIDs.forEach { set ->
//         val latestSet = findLatest(set) ?: return@write
//         latestSets.add(latestSet)
//       }

        val exerciseSets = copyToRealm(ExerciseSets())
        exerciseSets.apply {
          exerciseOrder = _exerciseSets.exerciseOrder
          exercise = latestExercise
        }
      }
    }
  }
}
