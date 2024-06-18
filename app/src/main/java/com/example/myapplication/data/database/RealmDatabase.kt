package com.example.myapplication.data.database

import android.util.Log
import com.example.myapplication.data.model.Equipment
import com.example.myapplication.data.model.Exercise
import com.example.myapplication.data.model.ExerciseNote
import com.example.myapplication.data.model.ExerciseSets
import com.example.myapplication.data.model.Exercises
import com.example.myapplication.data.model.ExercisesSets
import com.example.myapplication.data.model.Force
import com.example.myapplication.data.model.GymCatUser
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
import io.realm.kotlin.UpdatePolicy
import io.realm.kotlin.ext.isManaged
import io.realm.kotlin.ext.query
import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.internal.platform.assetFileAsStream
import io.realm.kotlin.internal.platform.runBlocking
import io.realm.kotlin.mongodb.App
import io.realm.kotlin.mongodb.Credentials
import io.realm.kotlin.mongodb.subscriptions
import io.realm.kotlin.mongodb.sync.SyncConfiguration
import io.realm.kotlin.mongodb.syncSession
import io.realm.kotlin.types.RealmInstant
import io.realm.kotlin.types.RealmList
import io.realm.kotlin.types.RealmUUID
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
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
            GymCatUser::class,
            // ProgressionScheme::class,)
          ),
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
              updateExisting = true,
            )
            add(realm.query<Muscle>(), "muscleStream", updateExisting = true)
            add(realm.query<MuscleGroup>(), "muscleGroupStream", updateExisting = true)
            add(realm.query<Equipment>(), "equipmentStream", updateExisting = true)
            add(realm.query<Utility>(), "utilityStream", updateExisting = true)
            add(realm.query<Force>(), "forceStream", updateExisting = true)
            add(realm.query<Mechanic>(), "mechanicStream", updateExisting = true)
            add(realm.query<GymCatUser>(), "userStream", updateExisting = true)
          }
          .build()
      }

    Realm.open(config!!)
  }

  // User & User Settings
  fun getUser(): GymCatUser? {
    return realm.query<GymCatUser>().find().firstOrNull()
  }

  // Exercise & Related Queries
  fun getAllExercises(): Flow<List<Exercise>> {
    return realm.query<Exercise>().find().asFlow().map { exercises ->
      exercises.list.sortedBy { exercise -> exercise.name }
    }
  }

  fun getAllEquipment(): List<Equipment> {
    return realm.query<Equipment>().find().toList().sortedBy { equipment -> equipment.name }
  }

  fun getAllMuscleGroups(): List<MuscleGroup> {
    return realm.query<MuscleGroup>().find().toList().sortedBy { muscleGroup -> muscleGroup.name }
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
      ?.name ?: throw Exception("Muscle Group not found")
  }

  fun getExerciseDetails(exerciseID: ObjectId): Exercise {
    return realm.query<Exercise>("_id = $0", exerciseID).first().find()
      ?: throw Exception("Exercise not found")
  }

  fun getExercise(exerciseID: ObjectId?): Exercise? {
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

  //  TODO Update Function to Update Database
  suspend fun updateWeightUnitsForExercise(exerciseID: ObjectId, weightUnit: String) {
    val exercise = getExercise(exerciseID)
    exercise?.weightUnit = weightUnit
  }

  //  TODO Update Function to Update Database
  suspend fun updateWorkingSetRestTimerForExercise(exerciseID: ObjectId, restDuration: Long) {
    val exercise = getExercise(exerciseID)
    exercise?.workingSetRestInWholeMilliseconds = restDuration
  }

  //  TODO Update Function to Update Database
  suspend fun updateWarmUpSetRestTimerForExercise(exerciseID: ObjectId, restDuration: Long) {
    val exercise = getExercise(exerciseID)
    exercise?.warmUpSetRestInWholeMilliseconds = restDuration
  }

  //  TODO Update Function to Update Database
  suspend fun enableWorkingSetRestTimerForExercise(exerciseID: ObjectId, enable: Boolean) {
    val exercise = getExercise(exerciseID)
    exercise?.workingSetRestTimerEnabled = enable
  }

  //  TODO Update Function to Update Database
  suspend fun enableWarmUpSetRestTimerForExercise(exerciseID: ObjectId, enable: Boolean) {
    val exercise = getExercise(exerciseID)
    exercise?.warmUpSetRestTimerEnabled = enable
  }

  fun filterExercises(
    exercise: String,
    muscleGroupIDs: List<String>,
    equipmentIDs: List<String>,
    //        movementTypes: List<String>
  ): Flow<List<Exercise>> {
    val muscleGroupsSize = muscleGroupIDs.size.plus(1)
    val muscleGroups = ""
    muscleGroupIDs.forEachIndexed { index, _ -> muscleGroups.plus("${index.plus(1)}, ") }

    val equipment = ""
    equipmentIDs.forEachIndexed { index, _ ->
      equipment.plus("${muscleGroupsSize.plus(index.plus(1))}, ")
    }

    Log.i("filterExercises", "exercise: $exercise")
    Log.i("filterExercises", "muscleGroups: $muscleGroupIDs")
    Log.i("filterExercises", "equipment: $equipmentIDs")

    return realm
      .query<Exercise>(
        "name CONTAINS[c] $0 AND muscleGroupID IN $1 AND equipmentID IN $2 SORT(name ASC)",
        exercise,
        muscleGroupIDs,
        equipmentIDs,
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

  fun getAllCompletedWorkouts(): Flow<List<Workout>> {
    return realm
      .query<Workout>("workoutEnded != null SORT(dateCreated DESC)")
      .find()
      .asFlow()
      .map { workouts -> workouts.list.sortedByDescending { workout -> workout.dateCreated } }
  }

  //  TODO Fix this function
  fun getWorkoutsCount(): Int {
    return realm.query<Workout>("workoutEnded != null").find().count()
  }

  fun getWorkoutByID(workoutID: ObjectId): Workout? {
    return realm.query<Workout>("_id = $0", workoutID).first().find()
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

  fun getNextWorkoutByProgramID(programID: ObjectId): Workout? {
    return realm
      .query<Workout>("programID = $0 && workoutEnded == null", programID)
      .find()
      .firstOrNull()
  }

  suspend fun insertWorkout(workout: Workout) {
    val insertedExercisesSets = insertExerciseSets(workout.exercisesSets)
    val insertedWorkoutNotes = insertWorkoutNotes(workout.notes)

    //    Log.i(
    //      "insertWorkout",
    //      "insertedExercisesSets: ${Json.encodeToString(insertedExercisesSets[0])}"
    //    )

    if (realm.query<Workout>("_id == $0", workout._id).first().find() != null) {
      realm.write {
        val _workout: Workout? = this.query<Workout>("_id == $0", workout._id).first().find()

        if (_workout != null) {
          _workout.name = workout.name
          _workout.notes = workout.notes
          _workout.dateCreated = workout.dateCreated
          _workout.workoutStarted = workout.workoutStarted
          _workout.workoutEnded = workout.workoutEnded
          _workout.workoutDuration = workout.workoutDuration
          _workout.programID = workout.programID
          _workout.microcycleID = workout.microcycleID
          _workout.exercisesSets = insertedExercisesSets

          Log.i("insertWorkout", "insertedWorkout: ${Json.encodeToString(_workout)}")
        }
      }
    } else {
      realm.write {
        //      val tempList = realmListOf<ExerciseSets>()
        //      insertedExercisesSets.forEach { tempList.add(findLatest(it) ?: return@write) }

        this.copyToRealm(
          Workout().apply {
            _id = workout._id
            name = workout.name
            workoutAuthor = workout.workoutAuthor
            notes = workout.notes
            workoutDuration = workout.workoutDuration
            workoutType = workout.workoutType
            dateCreated = workout.dateCreated
            workoutStarted = workout.workoutStarted
            workoutEnded = workout.workoutEnded
            day = workout.day
            exercisesSets = workout.exercisesSets
            programID = workout.programID
            microcycleID = workout.microcycleID
          }
        )
      }
    }
  }

  suspend fun copyWorkoutsFromTemplate(
    workouts: RealmList<Workout>,
    _microcycleID: ObjectId,
    _programID: ObjectId,
  ): RealmList<Workout> {
    val realmWorkouts = realmListOf<Workout>()
    workouts.forEach { workout ->
      val newWorkoutID = ObjectId()
      val insertedExerciseSets = copyExerciseSetsFromTemplate(workout.exercisesSets, newWorkoutID)
      realm.write {
        val newWorkout =
          this.copyToRealm(
            Workout().apply {
              _id = newWorkoutID
              uuid = workout.uuid
              name = workout.name
              workoutAuthor = workout.workoutAuthor
              notes = realmListOf()
              workoutDuration = workout.workoutDuration
              workoutType = workout.workoutType
              dateCreated = workout.dateCreated
              programID = _programID
              microcycleID = _microcycleID
              day = workout.day
              exercisesSets = insertedExerciseSets
            }
          )

        realmWorkouts.add(newWorkout)
      }
    }
    //    Log.i("copyWorkoutsFromTemplate", "workouts: ${Json.encodeToString(workouts)}")
    Log.i("copyWorkoutsFromTemplate", "workouts: ${realmWorkouts.size}")
    return realmWorkouts
  }

  suspend fun updateWorkout(workout: Workout) {
    realm.write {
      workout
      //      val latestWorkout = findLatest(workout) ?: return@write
    }
  }

  suspend fun deleteWorkout(workout: Workout) {
    workout.isManaged()
    realm.write {
      val _workout = realm.query<Workout>("workoutID = $0", workout._id).first().find()
      if (_workout != null) {
        delete(_workout)
      }
    }
  }

  fun filterWorkouts(
    workout: String,
    programIDs: List<String>,
    startDate: String,
    endDate: String,
    //        movementTypes: List<String>
  ): Flow<List<Workout>> {
    val muscleGroupsSize = programIDs.size.plus(1)
    val muscleGroups = ""
    programIDs.forEachIndexed { index, _ -> muscleGroups.plus("${index.plus(1)}, ") }

    val equipment = ""
    startDate.forEachIndexed { index, _ ->
      equipment.plus("${muscleGroupsSize.plus(index.plus(1))}, ")
    }

    Log.i("filterWorkouts", "workout: $workout")
    Log.i("filterWorkouts", "programIDs: $programIDs")
    Log.i("filterWorkouts", "dateRange: $startDate")

    return realm
      .query<Workout>(
        "name CONTAINS[c] $0 AND  programID	IN $1 AND workoutStarted BETWEEN {$2, $3} SORT(name ASC)",
        workout,
        programIDs,
        startDate,
        endDate,
      )
      .find()
      .asFlow()
      .map { it.list }
  }

  //  Program Queries
  fun getAllDistinctProgramTemplates(): Flow<List<Program>> {
    return realm.query<Program>(/*"startDate = null DISTINCT (uuid)"*/).find().asFlow()
      .map { programs ->
        programs.list.sortedByDescending { program -> program.dateCreated }
      }
  }

  fun getProgramByID(programID: ObjectId): Program? {
    return realm.query<Program>("_id = $0", programID).first().find()
  }

  fun getActivePrograms(): Flow<List<Program>> {
    return realm.query<Program>("active = true").find().asFlow().map { it.list }
  }

  fun isProgramActive(programUUID: RealmUUID): Boolean {
    return realm.query<Program>("uuid = $0 && active = true", programUUID).find().isNotEmpty()
  }

  suspend fun insertProgram(program: Program) {
    realm.write { this.copyToRealm(program) }
  }

  suspend fun copyProgramFromTemplate(program: Program) {
    val newProgramID = ObjectId()
    val insertedMesocycles = copyMesocycleFromTemplate(program.mesocycles, newProgramID)
    realm.write {
      this.copyToRealm(
        Program().apply {
          _id = newProgramID
          uuid = program.uuid
          name = program.name
          author = program.author
          dateCreated = program.dateCreated
          startDate = RealmInstant.now()
          shortDescription = program.shortDescription
          description = program.description
          uri = program.uri
          level = program.level
          programEquipment = program.programEquipment
          daysPerWeek = program.daysPerWeek
          programLength = program.programLength
          active = true
          mesocycles = insertedMesocycles
        }
      )
    }
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
    return realm.query<Mesocycle>("programID = $0 SORT(order ASC)", programID).find().asFlow().map {
      it.list
    }
  }

  suspend fun insertMesocycle(mesocycle: Mesocycle) {
    realm.write { this.copyToRealm(mesocycle) }
  }

  suspend fun copyMesocycleFromTemplate(
    mesocycles: RealmList<Mesocycle>,
    _programID: ObjectId,
  ): RealmList<Mesocycle> {

    val realmMesocycles = realmListOf<Mesocycle>()
    mesocycles.forEach { mesocycle ->
      val newMesocycleID = ObjectId()
      val insertedMicrocycles =
        copyMicrocyclesFromTemplate(mesocycle.microcycles, newMesocycleID, _programID)
      realm.write {
        val newMesocycle =
          this.copyToRealm(
            Mesocycle().apply {
              _id = newMesocycleID
              uuid = mesocycle.uuid
              name = mesocycle.name
              description = mesocycle.description
              dateCreated = mesocycle.dateCreated
              //            startDate = RealmInstant.now()
              order = mesocycle.order
              programID = _programID
              microcycles = insertedMicrocycles
            }
          )
        realmMesocycles.add(newMesocycle)
      }
    }
    Log.i("copyMesocycleFromTemplate", "mesocycles: ${realmMesocycles.size}")
    return realmMesocycles
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
      .find()
      .asFlow()
      .map { it.list }
  }

  suspend fun insertMicrocycle(microcycle: Microcycle) {
    realm.write { this.copyToRealm(microcycle) }
  }

  suspend fun copyMicrocyclesFromTemplate(
    microcycles: RealmList<Microcycle>,
    _mesocycleID: ObjectId,
    _programID: ObjectId,
  ): RealmList<Microcycle> {

    val realmMicrocycles = realmListOf<Microcycle>()
    microcycles.forEach { microcycle ->
      val newMicrocycleID = ObjectId()
      val insertedWorkouts =
        copyWorkoutsFromTemplate(microcycle.workouts, newMicrocycleID, _programID)
      realm.write {
        val newMicrocycle =
          this.copyToRealm(
            Microcycle().apply {
              _id = newMicrocycleID
              uuid = microcycle.uuid
              name = microcycle.name
              description = microcycle.description
              dateCreated = microcycle.dateCreated
              //            startDate = RealmInstant.now()
              order = microcycle.order
              programID = _programID
              mesocycleID = _mesocycleID
              workouts = insertedWorkouts
            }
          )
        realmMicrocycles.add(newMicrocycle)
      }
    }
    Log.i("copyMicrocyclesFromTemplate", "microcycles: ${realmMicrocycles.size}")
    return realmMicrocycles
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

  fun getPinnedExerciseNotes(exerciseID: ObjectId, pinned: Boolean = true): List<ExerciseNote> {
    return realm.query<ExerciseNote>("exercise = $0 AND pinned = $1", exerciseID, pinned).find()
  }

  /*  fun getPinnedExerciseNotes(
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
  }*/

  suspend fun insertExerciseNote(exerciseNote: ExerciseNote) {
    realm.write { this.copyToRealm(exerciseNote) }
  }

  suspend fun updateExerciseNote(exerciseNote: ExerciseNote) {
    realm.write { exerciseNote }
  }

  fun getPinnedWorkoutNotes(
    workoutUUID: RealmUUID,
    pinned: Boolean = true,
    //    programPinned: Boolean = false,
    //    mesocyclePinned: Boolean = false,
    //    microcyclePinned: Boolean = false,
  ): List<WorkoutNote> {
    return realm
      .query<WorkoutNote>(
        "uuid = $0 AND pinned = $1",
        workoutUUID,
        pinned,
        //        programPinned,
        //        mesocyclePinned,
        //        microcyclePinned
      )
      .find()
  }

  suspend fun insertWorkoutNote(workoutNote: WorkoutNote) {
    realm.write { this.copyToRealm(workoutNote) }
  }

  suspend fun updateWorkoutNote(workoutNote: WorkoutNote) {
    realm.write { workoutNote }
  }

  fun getPreviousSetForExercise(exerciseID: ObjectId, setNumber: Int): Set? {
    return realm
      .query<Set>(
        "exerciseID = $0 AND setNumber = $1 AND setCompleted = true SORT (dateCreated DESC)",
        exerciseID,
        setNumber,
      )
      .first()
      .find()
  }

  suspend fun importPrograms() {
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

  suspend fun importMesocycles() {
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

  suspend fun importMicrocycles() {
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

  suspend fun importWorkouts() {
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
      assetFileAsStream("database/Break-In Phase Block 2 Day 1 ExercisesSets.json")
        .bufferedReader()
        .use { it.readText() }

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

      /*realm.write {
      //        val latestExercise = findLatest(_exercise) ?: return@write
              val latestSets = mutableListOf<Set>()

              //       setIDs.forEach { set ->
              //         val latestSet = findLatest(set) ?: return@write
              //         latestSets.add(latestSet)
              //       }

              val exerciseSets = copyToRealm(ExerciseSets())
              exerciseSets.apply {
                exerciseOrder = _exerciseSets.exerciseOrder
                exerciseID = _exercise._id
                workoutID = _exerciseSets.workout
                //          sets = latestSets.toRealmList()
              }
            }*/
    }
  }

  private suspend fun insertCompletedSets(
    sets: RealmList<Set>,
    exerciseID: ObjectId?,
  ): RealmList<Set> {
    val realmSets = realmListOf<Set>()
    sets.forEach { set ->
      if (set.setCompleted) {
        realm.write {
          //          val latestExercise = findLatest(exercise ?: return@write) ?: return@write
          val _set: Set? = this.query<Set>("_id == $0", set._id).first().find()

          if (_set != null) {
            _set.setNumber = set.setNumber
            _set.weightPounds = set.weightPounds
            _set.weightKilograms = set.weightKilograms
            _set.repetitions = set.repetitions
            _set.ratePerceivedEffort = set.ratePerceivedEffort
            _set.repetitionsInReserve = set.repetitionsInReserve
            _set.targetWeight = set.targetWeight
            _set.targetRepetitions = set.targetRepetitions
            _set.targetRatePerceivedEffort = set.targetRatePerceivedEffort
            _set.targetRepetitionsInReserve = set.targetRepetitionsInReserve
            _set.percentage1RM = set.percentage1RM
            _set.setCompleted = set.setCompleted
            _set.exerciseID = exerciseID
            _set.dateCreated = set.dateCreated
          } else {
            this.copyToRealm(
              Set().apply {
                _id = set._id // updates existing ExerciseSets TODO Create UpdateSets
                // Function
                setNumber = set.setNumber
                weightPounds = set.weightPounds
                weightKilograms = set.weightKilograms
                repetitions = set.repetitions
                ratePerceivedEffort = set.ratePerceivedEffort
                repetitionsInReserve = set.repetitionsInReserve
                targetWeight = set.targetWeight
                targetRepetitions = set.targetRepetitions
                targetRatePerceivedEffort = set.targetRatePerceivedEffort
                targetRepetitionsInReserve = set.targetRepetitionsInReserve
                percentage1RM = set.percentage1RM
                setCompleted = set.setCompleted
                this.exerciseID = exerciseID
                dateCreated = set.dateCreated
              },
              updatePolicy = UpdatePolicy.ALL,
            )
          }

          realmSets.add(set)
        }
      }
    }
    return realmSets
  }

  private fun getExerciseSets(exerciseSets: ExerciseSets): ExerciseSets? {
    return realm.query<ExerciseSets>("_id == $0", exerciseSets._id).find().firstOrNull()
  }

  private suspend fun insertExerciseSets(
    exercisesSets: RealmList<ExerciseSets>
  ): RealmList<ExerciseSets> {
    val realmExerciseSets = realmListOf<ExerciseSets>()
    exercisesSets.forEach { exerciseSets ->
      val insertedSets =
        insertCompletedSets(exerciseSets.sets, exerciseID = exerciseSets.exerciseID!!)
      val insertedNotes = insertExerciseNotes(exerciseSets.notes)
      if (exerciseSets.sets.isNotEmpty()) {
        realm.write {
          val _exerciseSets: ExerciseSets? =
            this.query<ExerciseSets>("_id == $0", exerciseSets._id).first().find()
          //          val latestExercise = findLatest(exerciseSets.exerciseID ?: return@write) ?:
          // return@write

          if (_exerciseSets != null) {
            _exerciseSets.exerciseOrder = exerciseSets.exerciseOrder
            _exerciseSets.exerciseID = exerciseSets.exerciseID
            _exerciseSets.sets = insertedSets // exerciseSets.sets
            _exerciseSets.notes = insertedNotes
            _exerciseSets.datePerformed = exerciseSets.datePerformed
          } else {
            this.copyToRealm(
              ExerciseSets().apply {
                _id = exerciseSets._id // updates existing ExerciseSets
                exerciseOrder = exerciseSets.exerciseOrder
                exerciseID = exerciseSets.exerciseID
                sets = insertedSets
                notes = insertedNotes
                datePerformed = exerciseSets.datePerformed
                workoutID = exerciseSets.workoutID
              },
              updatePolicy = UpdatePolicy.ALL,
            )
          }

          Log.i("insertExerciseSets()", "isManaged: ${exerciseSets.isManaged()}")
          Log.i("insertExerciseSets()", "exerciseSets: ${Json.encodeToString(exerciseSets)}")
          realmExerciseSets.add(exerciseSets)
        }
      }
    }

    return realmExerciseSets
  }

  suspend fun copyExerciseSetsFromTemplate(
    exercisesSets: RealmList<ExerciseSets>,
    _workoutID: ObjectId,
  ): RealmList<ExerciseSets> {

    val realmExerciseSets = realmListOf<ExerciseSets>()
    exercisesSets.forEach { exerciseSets ->
      val newExerciseSetID = ObjectId()
      val insertedSets = copySetsFromTemplate(exerciseSets.sets, newExerciseSetID)
      realm.write {
        //        val latestExercise = findLatest(exerciseSets.exerciseID ?: return@write) ?:
        // return@write
        val newExerciseSet =
          this.copyToRealm(
            ExerciseSets().apply {
              _id = newExerciseSetID
              exSetsUUID = exerciseSets.exSetsUUID
              exerciseOrder = exerciseSets.exerciseOrder
              exerciseID = exerciseSets.exerciseID
              sets = insertedSets
              datePerformed = exerciseSets.datePerformed
            }
          )
        realmExerciseSets.add(newExerciseSet)
      }
    }
    Log.i("copyExerciseSetsFromTemplate()", "newExerciseSet size: ${realmExerciseSets.size}")
    return realmExerciseSets
  }

  suspend fun copySetsFromTemplate(sets: RealmList<Set>, _exerciseSetID: ObjectId): RealmList<Set> {

    val realmSets = realmListOf<Set>()
    sets.forEach { set ->
      realm.write {
        val newSet =
          this.copyToRealm(
            Set().apply {
              uuid =
                set
                  .uuid // TODO determine if this should be unique or can be used in a future
              // feature
              setNumber = set.setNumber
              weightPounds = set.weightPounds
              weightKilograms = set.weightKilograms
              repetitions = set.repetitions
              ratePerceivedEffort = set.ratePerceivedEffort
              repetitionsInReserve = set.repetitionsInReserve
              targetWeight = set.targetWeight
              targetRepetitions = set.targetRepetitions
              targetRatePerceivedEffort = set.targetRatePerceivedEffort
              targetRepetitionsInReserve = set.targetRepetitionsInReserve
              percentage1RM = set.percentage1RM
              setCompleted = set.setCompleted
              setTypeID = set.setTypeID
              progressionSchemeID = set.progressionSchemeID
              exerciseID = set.exerciseID
            }
          )
        realmSets.add(newSet)
      }
    }
    Log.i("copySetsFromTemplate()", "realmSets size: ${realmSets.size}")
    return realmSets
  }

  private suspend fun insertExerciseNotes(notes: RealmList<ExerciseNote>): RealmList<ExerciseNote> {
    val realmExerciseNotes = realmListOf<ExerciseNote>()
    notes.forEach { exerciseNote ->
      if (exerciseNote.note?.isNotBlank() == true) {
        realm.write {
          val newExerciseNote =
            this.copyToRealm(
              ExerciseNote().apply {
                _id = exerciseNote._id
                note = exerciseNote.note
                pinned = exerciseNote.pinned
                dateCreated = exerciseNote.dateCreated
                exercise = exerciseNote.exercise
                workout = exerciseNote.workout
              },
              updatePolicy = UpdatePolicy.ALL,
            )

          realmExerciseNotes.add(newExerciseNote)
        }
      }
    }
    return realmExerciseNotes
  }

  private suspend fun insertWorkoutNotes(notes: RealmList<WorkoutNote>): RealmList<WorkoutNote> {
    val realmWorkoutNotes = realmListOf<WorkoutNote>()
    notes.forEach { workoutNote ->
      if (workoutNote.note?.isNotEmpty() == true) {
        realm.write {
          val newWorkoutNote =
            copyToRealm(
              WorkoutNote().apply {
                _id = workoutNote._id
                note = workoutNote.note
                pinned = workoutNote.pinned
                workout = workoutNote.workout
              },
              updatePolicy = UpdatePolicy.ALL,
            )
          realmWorkoutNotes.add(newWorkoutNote)
        }
      }
    }
    return realmWorkoutNotes
  }

  fun getExerciseByName(exerciseName: String): Exercise {
    return realm.query<Exercise>("name CONTAINS[c] $0", exerciseName).find().first()
  }

  suspend fun importWorkoutProgramPKL(program: Program) {
    realm.write {
      this.copyToRealm(
        program
      )
    }
  }

  suspend fun importMesocyclesPKL(mesocycles: RealmList<Mesocycle>) {
    mesocycles.forEach { mesocycle ->
      realm.write {
        this.copyToRealm(
          mesocycle
        )
      }
    }
  }

  suspend fun importMicrocyclesPKL(microcycles: RealmList<Microcycle>) {
    microcycles.forEach { microcycle ->
      realm.write {
        this.copyToRealm(
          microcycle
        )
      }
    }
  }

  suspend fun importWorkoutsPKL(workouts: RealmList<Workout>) {
    workouts.forEach { workout ->
      realm.write {
        this.copyToRealm(
          workout
        )
      }
    }
  }

  suspend fun importExercisesSetsPKL(exercisesSets: RealmList<ExerciseSets>) {
    exercisesSets.forEach { exerciseSets ->
      realm.write {
        this.copyToRealm(
          exerciseSets
        )
      }
    }
  }

  suspend fun importSetsPKL(sets: RealmList<Set>) {
    sets.forEach { set ->
      realm.write {
        this.copyToRealm(
          set
        )
      }
    }
  }
}

