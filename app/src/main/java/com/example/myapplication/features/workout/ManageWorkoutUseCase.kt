@file:OptIn(ExperimentalUuidApi::class, ExperimentalUuidApi::class, ExperimentalTime::class)

package com.example.myapplication.features.workout

import android.util.Log
import com.example.myapplication.common.domain.model.Exercise
import com.example.myapplication.common.domain.model.ExerciseExerciseSets
import com.example.myapplication.common.domain.model.ExerciseNote
import com.example.myapplication.common.domain.model.ExerciseSets
import com.example.myapplication.common.domain.model.MassUnit
import com.example.myapplication.common.domain.model.MuscleFractionalSets
import com.example.myapplication.common.domain.model.Set
import com.example.myapplication.common.domain.model.SetType
import com.example.myapplication.common.domain.model.Superset
import com.example.myapplication.common.domain.model.SupersetExercisesSets
import com.example.myapplication.common.domain.model.UserProfileManager
import com.example.myapplication.common.domain.model.Workout
import com.example.myapplication.common.domain.model.WorkoutComponent
import com.example.myapplication.common.domain.model.WorkoutItem
import com.example.myapplication.common.domain.model.WorkoutNote
import com.example.myapplication.common.domain.model.calculateTotalVolume
import com.example.myapplication.common.domain.model.completeSet
import com.example.myapplication.common.domain.model.totalVolumeInKilograms
import com.example.myapplication.common.domain.model.totalVolumeInPounds
import com.example.myapplication.common.domain.model.updateSetRIR
import com.example.myapplication.common.domain.model.updateSetRPE
import com.example.myapplication.common.domain.model.updateSetReps
import com.example.myapplication.common.domain.model.updateSetType
import com.example.myapplication.common.domain.model.updateSetWeight
import com.example.myapplication.common.infrastructure.persistence.CompletedWorkoutRepository
import com.example.myapplication.common.infrastructure.persistence.ExerciseNotesRepository
import com.example.myapplication.common.infrastructure.persistence.ExercisesRepository
import com.example.myapplication.common.infrastructure.persistence.ModernizedLoadRIRRepository
import com.example.myapplication.common.infrastructure.persistence.UserRepository
import com.example.myapplication.common.infrastructure.persistence.WorkoutNotesRepository
import com.example.myapplication.common.infrastructure.persistence.WorkoutRepository
import com.example.myapplication.common.util.ModernizedLoadRIR
import com.example.myapplication.common.util.OneRepMaxEstimateFormulas
import com.example.myapplication.common.util.RepCapacity
import com.example.myapplication.common.util.estimateTargetOneRepMaxModernized
import com.example.myapplication.common.util.newLazyListItemID
import org.bson.types.ObjectId
import kotlin.math.min
import kotlin.time.Clock
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime
import kotlin.time.toDuration
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class ManageWorkoutUseCase(
  private val workoutRepository: WorkoutRepository,
  //  private val setsRepository: SetsRepository,
  //  private val exerciseSetsRepository: ExerciseSetsRepository,
  private val exercisesRepository: ExercisesRepository,
  private val workoutNotesRepository: WorkoutNotesRepository,
  private val exerciseNotesRepository: ExerciseNotesRepository,
  private val completedWorkoutRepository: CompletedWorkoutRepository,
  private val userRepository: UserRepository,
  private val userProfileManager: UserProfileManager,
  private val modernizedLoadRIRRepository: ModernizedLoadRIRRepository,
) {

  // TODO rewrite
  //  val currentUser = userRepository.getCurrentUser()
  //  val userDefaultWeightUnit: String =
  //    userRepository.getCurrentUser().customDataAsBsonDocument()?.getString("weightUnit")?.value
  //      ?: MassUnit.POUNDS.abbreviation

  private val userID = userProfileManager.userProfile.value.id.toHexString() ?: "TemporaryUserID"
  val userDefaultWeightUnit: String = userProfileManager.userProfile.value.defaultWeightUnit.abbreviation

  fun getExerciseByID(exerciseID: ObjectId?): Exercise? {
    return exercisesRepository.getExercise(exerciseID)
  }

  fun getWorkoutByID(workoutID: ObjectId): Workout? {
    // workoutRepository.getWorkoutByID(workoutID = workoutID)
    // TODO Add logic for when copying from a template (active program) vs when copying from a
    // previous workout
    return workoutRepository.getWorkoutByID(workoutID = workoutID)
  }

  fun getCompletedWorkoutByID(workoutID: ObjectId): Workout? {
    // workoutRepository.getWorkoutByID(workoutID = workoutID)
    // TODO Add logic for when copying from a template (active program) vs when copying from a
    // previous workout
    return completedWorkoutRepository.getCompletedWorkoutByID(workoutID = workoutID)
  }

  fun copyWorkoutFromWorkoutTemplate(workoutTemplate: Workout): Workout {
    return workoutTemplate.copy(
      id = ObjectId(),
      notes = workoutTemplate.notes, // .filter { it.pinned }.toMutableList(),
      dateCreated = Clock.System.now(),
      workoutStarted = Clock.System.now(),
      ownerID = userID,
      isPublic = false,
      signature = workoutTemplate.signature,
      hashedSignature = workoutTemplate.hashedSignature,
    )
  }

  fun copyWorkoutFromPreviousWorkout(previousWorkout: Workout): Workout {
    val newWorkout =
      Workout(
        uuid = previousWorkout.uuid,
        name = previousWorkout.name,
        workoutAuthor = previousWorkout.workoutAuthor,
        //        notes = previousWorkout.notes.filter { it.pinned }.toMutableList(),
        workoutType = previousWorkout.workoutType,
        workoutStarted = Clock.System.now(),
        day = previousWorkout.day,
        // workoutItems =
        // createNewExerciseExerciseSetsFromPreviousWorkout(previousWorkout.workoutItems),
        //        exercisesSets =,
        // copyExercisesSetsFromPreviousWorkout(previousWorkout.exercisesSets),
        microcycleID = previousWorkout.microcycleID,
        mesocycleID = previousWorkout.mesocycleID,
        programID = previousWorkout.programID,
        ownerID = userID, // TODO Replace currentUser.id
        isPublic = false,
        signature = previousWorkout.signature,
        hashedSignature = previousWorkout.hashedSignature,
      )
    return newWorkout
  }

  fun getPinnedWorkoutNotes(workoutUUID: Uuid, workoutID: ObjectId): MutableList<WorkoutNote> {
    return workoutNotesRepository.getPinnedWorkoutNotes(workoutUUID).toMutableList()
  }

  fun createNewWorkoutComponentsFromPreviousWorkout(
    previousWorkoutItems: List<WorkoutItem>,
    workoutID: ObjectId,
  ): List<WorkoutComponent> {
    val workoutComponents =
      previousWorkoutItems.map { workoutItem ->
        when (workoutItem) {
          is ExerciseSets -> {
            createNewExerciseExerciseSetsFromPreviousWorkout(workoutItem, workoutID)
          }

          is Superset -> {
            SupersetExercisesSets(
              //            name = workoutItem.name,
              exercisesSets =
                workoutItem.exercisesSets.map { exerciseSets ->
                  createNewExerciseExerciseSetsFromPreviousWorkout(
                    previousExerciseSets = exerciseSets,
                    workoutID = workoutID,
                  )
                },
              restBetweenExercises = workoutItem.restBetweenExercises,
              enableRestBetweenExercises = workoutItem.enableRestBetweenExercises,
              enableRestAfterSuperset = workoutItem.enableRestAfterSuperset,
              restAfterSuperset = workoutItem.restAfterSuperset,
              supersetNotes = workoutItem.supersetNotes.filter { it.pinned }.toMutableList(),
              order = workoutItem.order,
              ownerID = userID,
              isPublic = false,
              workoutID = workoutID,
              microcycleID = workoutItem.microcycleID,
              mesocycleID = workoutItem.mesocycleID,
              programID = workoutItem.programID,
            )
          }
        }
      }

    val TAG = "ManageWorkoutUseCase.createNewWorkoutComponentsFromPreviousWorkout(): "
    val allSets = workoutComponents.flatMap { component ->
      when (component) {
        is ExerciseExerciseSets -> component.sets
        is SupersetExercisesSets -> component.exercisesSets.flatMap { it.sets }
      }
    }
// 
    Log.d(TAG, "allSets have the same workoutID ($workoutID): ${allSets.all { set -> set.workoutID == workoutID }}")

    require(allSets.all { set -> set.workoutID == workoutID }) {
      "$TAG All sets workoutIDs does not match Workout.id!"
    }

    return workoutComponents
  }

  private fun createNewExerciseExerciseSetsFromPreviousWorkout(
    previousExerciseSets: ExerciseSets,
    workoutID: ObjectId,
  ): ExerciseExerciseSets {
    val exercise = exercisesRepository.getExercise(previousExerciseSets.exerciseID)
    requireNotNull(exercise) { "Exercise not found." }

    val exerciseSetsID = ObjectId()
//     Log.d("createNewExerciseExerciseSetsFromPreviousWorkout", "exerciseSetsID: $exerciseSetsID")

    return ExerciseExerciseSets(
      id = exerciseSetsID,
      order = previousExerciseSets.order,
      exercise = exercise,
      sets =
        createNewSetsFromPreviousWorkout(
          previousExerciseSets.sets,
          exerciseSetsID,
          workoutID,
          exerciseID = exercise.id,
        ),
      description = previousExerciseSets.description,
      notes = previousExerciseSets.notes, // .filter { it.pinned }.toMutableList(),
      workoutID = workoutID,
      microcycleID = previousExerciseSets.microcycleID,
      mesocycleID = previousExerciseSets.mesocycleID,
      programID = previousExerciseSets.programID,
      isPublic = false,
      ownerID = userID, // TODO Replace currentUser.id
    )
  }

  private fun createNewSetsFromPreviousWorkout(
    previousSets: List<Set>,
    currentExerciseSetsID: ObjectId,
    currentWorkoutID: ObjectId,
    exerciseID: ObjectId,
  ): MutableList<Set> {
    val newSets = mutableListOf<Set>()
    val latestPreviousSets = mutableListOf<Set>()
    previousSets.forEach { set ->
      val newSet =
        Set(
          uuid = set.uuid,
          setNumber = set.setNumber,
          weightPounds = set.weightPounds,
          weightKilograms = set.weightKilograms,
          repetitions = set.repetitions,
          ratePerceivedEffort = set.ratePerceivedEffort,
          repetitionsInReserve = set.repetitionsInReserve,
          targetWeightPounds = set.targetWeightPounds,
          targetWeightKilograms = set.targetWeightKilograms,
          targetRepetitions = set.targetRepetitions,
          targetMinimumRepetitions = set.targetMinimumRepetitions,
          targetMaximumRepetitions = set.targetMaximumRepetitions,
          targetRatePerceivedEffort = set.targetRatePerceivedEffort,
          targetRepetitionsInReserve = set.targetRepetitionsInReserve,
          targetPercentage1RM = set.targetPercentage1RM,
          percentage1RM = set.percentage1RM,
          setTypeID = set.setTypeID,
          progressionSchemeID = set.progressionSchemeID,
          exerciseSetsID = currentExerciseSetsID,
          workoutID = currentWorkoutID,
          exerciseID = exerciseID,
          ownerID = userID, // TODO Replace currentUser.id
        )
//       Log.d("createNewSetsFromPreviousWorkout", "set.workoutID (${newSet.workoutID}) is the same as currentWorkoutID ($currentWorkoutID): ${newSet.workoutID == currentWorkoutID}")
      newSets.add(newSet)
      //      Log.i(
      //        "createNewSetsFromPreviousWorkout",
      //        "setNumber: ${set.setNumber}, set.exerciseID: ${set.exerciseID}"
      //      )

      if (set.setNumber != null) {
        set.exerciseID?.let { exerciseID ->
          val previousSet =
            completedWorkoutRepository.getPreviousSetForExercise(
              exerciseID,
              set.setNumber!!,
              set.setTypeID
            )
          previousSet?.let { latestPreviousSets.add(it) }
        }
      }
    }
    return newSets
  }

  fun loadWorkoutItemsFromWorkout(previousWorkoutItems: List<WorkoutItem>): List<WorkoutComponent> {
    return previousWorkoutItems.map { previousWorkoutItem ->
      when (previousWorkoutItem) {
        is ExerciseSets -> {
          loadExerciseSetsFromWorkout(previousWorkoutItem)
        }

        is Superset -> {
          SupersetExercisesSets(
            id = previousWorkoutItem.id,
            uuid = previousWorkoutItem.uuid,
            //            name = previousWorkoutItem.name,
            exercisesSets =
              previousWorkoutItem.exercisesSets.map { exerciseSets ->
                loadExerciseSetsFromWorkout(exerciseSets)
              },
            restBetweenExercises = previousWorkoutItem.restBetweenExercises,
            enableRestBetweenExercises = previousWorkoutItem.enableRestBetweenExercises,
            enableRestAfterSuperset = previousWorkoutItem.enableRestAfterSuperset,
            restAfterSuperset = previousWorkoutItem.restAfterSuperset,
            supersetNotes = previousWorkoutItem.supersetNotes.filter { it.pinned }.toMutableList(),
            order = previousWorkoutItem.order,
            ownerID = previousWorkoutItem.ownerID,
            isPublic = previousWorkoutItem.isPublic,
            workoutID = previousWorkoutItem.workoutID,
            microcycleID = previousWorkoutItem.microcycleID,
            mesocycleID = previousWorkoutItem.mesocycleID,
            programID = previousWorkoutItem.programID,
          )
        }
      }
    }
  }

  private fun loadExerciseSetsFromWorkout(
    previousExerciseSets: ExerciseSets
  ): ExerciseExerciseSets {
    val exercise = exercisesRepository.getExercise(previousExerciseSets.exerciseID)
    requireNotNull(exercise) { "Exercise not found." }

/*
    // TODO Create a wrapper class for previous exercise sets to be stored in WorkoutViewModel
//    TODO Check if results make sense, try grouping by Date and WorkoutID sort by setNumber and filter by SetType
    val previousSets = getPreviousSetsForExercise(exercise.id)
    val warmUpSets = previousSets.filter { it.setTypeID == SetType.warmUpSet }.zip(previousExerciseSets.sets.filter { it.setTypeID == SetType.warmUpSet })
    val workingSets = previousSets.filter { it.setTypeID != SetType.warmUpSet }.zip(previousExerciseSets.sets.filter { it.setTypeID != SetType.warmUpSet })
*/

    return ExerciseExerciseSets(
      id = previousExerciseSets.id,
      exSetsUUID = previousExerciseSets.exSetsUUID,
      workoutID = previousExerciseSets.workoutID,
      order = previousExerciseSets.order,
      exercise = exercise,
      description = previousExerciseSets.description,
      sets = previousExerciseSets.sets,
      notes = previousExerciseSets.notes,
      datePerformed = previousExerciseSets.datePerformed,
      microcycleID = previousExerciseSets.microcycleID,
      mesocycleID = previousExerciseSets.mesocycleID,
      programID = previousExerciseSets.programID,
      ownerID = previousExerciseSets.ownerID,
      isPublic = previousExerciseSets.isPublic,
    )
  }

  /**
   * Creates deep copies of a list of sets, resetting specific fields for use in a new context
   * (e.g., starting a new workout). Assigns a temporary owner ID (TODO: fix) and sets visibility to
   * private. Resets completion status.
   *
   * @param previousSets The list of Set objects to copy from a previous workout structure.
   * @return A new immutable list of copied Set objects ready for a new workout.
   */
  private fun loadSetsFromWorkout(previousSets: List<Set>): List<Set> {

    return previousSets.map { set ->
      // Use the data class .copy() method for conciseness and safety
      set.copy(
        // --- Fields to Reset/Change for a New Instance ---

        // Decide if you need new unique identifiers for the database/runtime
        // id = ObjectId(), // Generate new primary key if storing as a new distinct row
        // uuid = Uuid.random(), // Generate new UUID if needing unique instance tracking

        // Reset status fields for a new workout
        // setCompleted = false, // [cite: 90] A new workout's sets aren't completed yet

        // Assign context-specific fields
        ownerID = userID, // TODO: Replace with actual current user ID logic [cite: 91]
        isPublic = false, // Assuming new sets default to private [cite: 91]

        // --- Fields typically kept from the previous set ---
        // setNumber, weightPounds, weightKilograms, repetitions, RPE, RIR,
        // targetWeightPounds, targetRepetitions, targetRPE, targetRIR,
        // percentage1RM, setTypeID, progressionSchemeID,
        // workoutID, exerciseSetsID, exerciseID (validated above)
        // are all implicitly copied by .copy() unless overridden above.
      )
    }
  }

  /**
   * Retrieves the most recent historical performance for a specific set number of a given exercise.
   * Delegates the call to the workout repository.
   *
   * @param exerciseID The ID of the exercise.
   * @param setNumber The specific set number (e.g., 1st working set).
   * @return The historical Set object if found and no error occurs, null otherwise.
   */
  fun getPreviousSetForExercise(exerciseID: ObjectId, setNumber: Int, setType: SetType): Set? {
    return try {
      // Delegate the actual data fetching to the repository
      // TODO Add SetType to getPreviousSetForExercise
      completedWorkoutRepository.getPreviousSetForExercise(exerciseID, setNumber, setType)
    } catch (e: Exception) {
      // Optional: Log the error or handle specific exceptions
      Log.e(
        "ManageWorkoutUseCase",
        "Error fetching previous set for exercise $exerciseID, set $setNumber",
        e,
      )
      // Return null if any exception occurs during repository access
      null
    }
  }

  fun getPreviousSetsForExercise(exerciseID: ObjectId): List<Set> {
    return try {
      // Delegate the actual data fetching to the repository
      // TODO Add SetType to getPreviousSetForExercise
      completedWorkoutRepository.getPreviousSetsForExercise(exerciseID)
    } catch (e: Exception) {
      // Optional: Log the error or handle specific exceptions
      Log.e(
        "ManageWorkoutUseCase",
        "Error fetching previous set for exercise $exerciseID",
        e,
      )
      // Return null if any exception occurs during repository access
      emptyList<Set>()
    }
  }


  fun addWorkoutNote(
    workoutNotes: MutableList<WorkoutNote>,
    workoutID: ObjectId,
    _workoutUUID: Uuid,
  ): MutableList<WorkoutNote> {
    workoutNotes.add(
      WorkoutNote().apply {
        workout = workoutID
        workoutUUID = _workoutUUID
      }
    )
    return workoutNotes
  }

  fun removeWorkoutNote(
    workoutNote: WorkoutNote,
    workoutNotes: MutableList<WorkoutNote>,
  ): List<WorkoutNote> {
    return workoutNotes.filter { it != workoutNote }
  }

  fun setWorkoutNote(
    _workoutNotes: MutableList<WorkoutNote>,
    workoutNote: WorkoutNote,
    note: String,
  ): List<WorkoutNote> {
    val index = _workoutNotes.indexOf(workoutNote)
    _workoutNotes[index].note = note
    return _workoutNotes
  }

  fun pinWorkoutNote(
    _workoutNotes: MutableList<WorkoutNote>,
    workoutNote: WorkoutNote,
    pinned: Boolean,
  ): List<WorkoutNote> {
    val index = _workoutNotes.indexOf(workoutNote)
    _workoutNotes[index].pinned = pinned
    return _workoutNotes
  }

  fun updateWorkoutNotes(
    _workoutNotes: MutableList<WorkoutNote>,
    note: WorkoutNote,
  ): List<WorkoutNote> {
    _workoutNotes.first { it == note }.lazyListID = newLazyListItemID()
    return _workoutNotes
  }

  suspend fun saveWorkout(
    workout: Workout,
    workoutNotes: List<WorkoutNote>,
    workoutComponents: List<WorkoutComponent>,
  ) {
    val TAG = "ManageWorkoutUseCase: "
    val allSets =
      workoutComponents.flatMap { component ->
        when (component) {
          is ExerciseExerciseSets -> component.sets
          is SupersetExercisesSets -> component.exercisesSets.flatMap { it.sets }
        }
      }

    require(allSets.all { set -> set.workoutID == workout.id }) {
      "$TAG All sets workoutIDs does not match Workout.id!"
    }

    val allSuperSets = workoutComponents.filterIsInstance<SupersetExercisesSets>()

    require(allSuperSets.all { superset -> superset.workoutID == workout.id }) {
      "$TAG All Supersets workoutIDs does not match Workout.id!"
    }

    require(
      workoutComponents.all { component ->
        when (component) {
          is ExerciseExerciseSets -> component.workoutID == workout.id
          is SupersetExercisesSets -> component.exercisesSets.all { it.workoutID == workout.id }
        }
      }
    ) {
      "$TAG All ExerciseExerciseSets workoutIDs does not match Workout.id!"
    }

    //    TODO Implement this
    workout.notes = workoutNotes.toMutableList()

    workout.isPublic = false
    workout.isCompleted = true

    //    TODO Only Add Completed Sets, Implement Check ond Remove Uncompleted Sets
    //    removeUncompletedSets(exercisesSets.toMutableList())

    modernizedLoadRIRItems = modernizedLoadRIRRepository.getAllModernizedLoadRIR()

    workoutComponents.forEach { workoutItem ->
      when (workoutItem) {
        is ExerciseExerciseSets -> {
          workoutItem.sets.forEach { set ->
            set.estimated1RMPounds =
              calculateEstimated1RMPounds(set, workoutItem.exercise.repCapacity)
            set.estimated1RMKilograms =
              calculateEstimated1RMKilograms(set, workoutItem.exercise.repCapacity)
          }
        }

        is SupersetExercisesSets -> {
          workoutItem.exercisesSets.forEach { exerciseSets ->
            exerciseSets.sets.forEach { set ->
              set.estimated1RMPounds =
                calculateEstimated1RMPounds(set, exerciseSets.exercise.repCapacity)
              set.estimated1RMKilograms =
                calculateEstimated1RMKilograms(set, exerciseSets.exercise.repCapacity)
            }
          }
        }
      }
    }

    workout.workoutItems =
      convertWorkoutComponentsToWorkoutItems(
        workoutComponents
        //        userDefaultWeightUnit,
      )

    workout.calculateTotalVolume(userDefaultWeightUnit = userDefaultWeightUnit)

    //    workout.calculateTotalVolumeInPounds()
    //    workout.calculateTotalVolumeInKilograms()

    // TODO Check if new estimate 1RM record has been set, update exercise
    //  if RIR/RPE is null, skip
    // TODO Check if rep capacity has changed on sets taken to failure, prompt user
    //  if they want to update the rep capacity for that exercise.
    //  Sets need to be SetType.Failure and rir = 0 or rpe = 10.

    completedWorkoutRepository.upsertCompletedWorkout(workout)
  }

  private var modernizedLoadRIRItems: List<ModernizedLoadRIR>? = null

  fun getRelativeLoadPercentage(
    repCapacity: RepCapacity,
    rpe: Double,
    rir: Double?,
    repsPerformed: Int,
  ): Double? {
    var resultList =
      modernizedLoadRIRItems
        ?.filter { it.repCapacity == repCapacity }
        ?.filter { it.repsPerformed == repsPerformed }

    resultList = resultList?.filter { it.rir == rir }?.filter { it.rpe == rpe }

    val result = resultList?.firstOrNull()?.relativeLoadPercent

    return result
  }

  private fun calculateEstimated1RMPounds(set: Set, repCapacity: RepCapacity): Double? {
    return if (
      set.ratePerceivedEffort != null || set.repetitionsInReserve != null && set.repetitions != null
    ) {
      estimateTargetOneRepMaxModernized(
        set.weightPounds ?: 0.0,
        set.repetitions ?: 0,
        targetReps = 1,
        set.repetitionsInReserve ?: 10.0.minus(set.ratePerceivedEffort!!),
        set.ratePerceivedEffort ?: 10.0.minus(set.repetitionsInReserve!!),
        repCapacity = repCapacity,
        getRelativeLoadPercentage = ::getRelativeLoadPercentage,
      )
    } else if (set.repetitions != null) {
      OneRepMaxEstimateFormulas.calculateOneRepMax(
        set.weightPounds ?: 0.0,
        set.repetitions!!,
        OneRepMaxEstimateFormulas.Formula.Brzycki,
      )
    } else {
      null
    }
  }

  private fun calculateEstimated1RMKilograms(set: Set, repCapacity: RepCapacity): Double? {
    return if (
      set.ratePerceivedEffort != null || set.repetitionsInReserve != null && set.repetitions != null
    ) {
      estimateTargetOneRepMaxModernized(
        set.weightKilograms ?: 0.0,
        set.repetitions ?: 0,
        targetReps = 1,
        set.repetitionsInReserve ?: 10.0.minus(set.ratePerceivedEffort!!),
        set.ratePerceivedEffort ?: 10.0.minus(set.repetitionsInReserve!!),
        repCapacity = repCapacity,
        getRelativeLoadPercentage = ::getRelativeLoadPercentage,
      )
    } else if (set.repetitions != null) {
      OneRepMaxEstimateFormulas.calculateOneRepMax(
        set.weightKilograms ?: 0.0,
        set.repetitions!!,
        OneRepMaxEstimateFormulas.Formula.Brzycki,
      )
    } else {
      null
    }
  }

  private fun convertExerciseExerciseSetsToExerciseSets(
    exerciseExerciseSets: List<ExerciseExerciseSets>
    //    userDefaultWeightUnit: String,
  ): List<ExerciseSets> {
    val exercisesSets = mutableListOf<ExerciseSets>()
    exerciseExerciseSets.forEach { _exerciseSets ->
      exercisesSets.add(
        ExerciseSets(
          id = _exerciseSets.id,
          exSetsUUID = _exerciseSets.exSetsUUID,
          order = _exerciseSets.order,
          exerciseID = _exerciseSets.exercise.id,
          sets = _exerciseSets.sets,
          description = _exerciseSets.description,
          notes = _exerciseSets.notes,
          datePerformed = _exerciseSets.datePerformed,
          totalVolumeInPounds = _exerciseSets.totalVolumeInPounds(),
          totalVolumeInKilograms = _exerciseSets.totalVolumeInKilograms(),
          workoutID = _exerciseSets.workoutID,
          microcycleID = _exerciseSets.microcycleID,
          mesocycleID = _exerciseSets.mesocycleID,
          programID = _exerciseSets.programID,
          ownerID = userID, // TODO Replace currentUser.id
          isPublic = false,
        )
      )
    }

    return exercisesSets
  }

  fun convertWorkoutComponentsToWorkoutItems(
    workoutComponents: List<WorkoutComponent>
    //    userDefaultWeightUnit: String,
  ): List<WorkoutItem> {
    val workoutItems = mutableListOf<WorkoutItem>()
    workoutComponents.forEach { it ->
      when (it) {
        is ExerciseExerciseSets -> {
          workoutItems.add(
            ExerciseSets(
              id = it.id,
              exSetsUUID = it.exSetsUUID,
              workoutID = it.workoutID,
              order = it.order,
              exerciseID = it.exercise.id,
              sets = it.sets,
              description = it.description,
              notes = it.notes,
              datePerformed = it.datePerformed,
              totalVolumeInPounds = it.totalVolumeInPounds(),
              totalVolumeInKilograms = it.totalVolumeInKilograms(),
              isPublic = false,
              microcycleID = it.microcycleID,
              mesocycleID = it.mesocycleID,
              programID = it.programID,
              ownerID = userID, // TODO Replace currentUser.id
            )
          )
        }

        is SupersetExercisesSets -> {
          workoutItems.add(
            Superset(
              id = it.id,
              uuid = it.uuid,
              //              name = it.name,
              exercisesSets =
                convertExerciseExerciseSetsToExerciseSets(
                  it.exercisesSets
                  //                userDefaultWeightUnit
                ),
              restBetweenExercises = it.restBetweenExercises,
              enableRestBetweenExercises = it.enableRestBetweenExercises,
              enableRestAfterSuperset = it.enableRestAfterSuperset,
              restAfterSuperset = it.restAfterSuperset,
              supersetNotes = it.supersetNotes,
              order = it.order,
              workoutID = it.workoutID,
              microcycleID = it.microcycleID,
              mesocycleID = it.mesocycleID,
              programID = it.programID,
              ownerID = it.ownerID,
              isPublic = it.isPublic,
            )
          )
        }
      }
    }

    return workoutItems
  }

  fun getCompletedWorkout(workoutID: ObjectId): Workout? {
    // TODO Change to CompletedWorkoutRepository
    return completedWorkoutRepository.getCompletedWorkoutByID(workoutID)
  }

  fun getWorkoutsCount(): Int {
    return completedWorkoutRepository.getCompletedWorkoutsCount()
  }

  fun checkAllSetsComplete(workoutComponents: List<WorkoutComponent>): Pair<Int, Int> {
    var countSets: Int = 0
    var countSetsCompleted: Int = 0

    workoutComponents.forEach { workoutComponent ->
      when (workoutComponent) {
        is ExerciseExerciseSets -> {
          workoutComponent.sets.forEach { set ->
            countSets++
            if (set.setCompleted) {
              countSetsCompleted++
            }
          }
        }

        is SupersetExercisesSets -> {
          workoutComponent.exercisesSets.forEach { exerciseSets ->
            exerciseSets.sets.forEach { set ->
              countSets++
              if (set.setCompleted) {
                countSetsCompleted++
              }
            }
          }
        }
      }
    }
    return Pair(countSetsCompleted, countSets)
  }

  /**
   * Creates a new list of WorkoutComponents containing only the completed sets.
   * - Removes incomplete sets within each component.
   * - Removes ExerciseExerciseSets within a SupersetExercisesSets if they become empty.
   * - Removes the entire WorkoutComponent if it becomes empty after removing incomplete sets.
   *
   * @param workoutComponents The original list of workout components.
   * @return A new list of workout components with incomplete sets removed.
   */
  fun removeUncompletedSets(
    workoutComponents: MutableList<WorkoutComponent>
  ): List<WorkoutComponent> {
    return workoutComponents.mapNotNull { component ->
      when (component) {
        // Handle Superset components
        is SupersetExercisesSets -> {
          // Process each ExerciseExerciseSets within the superset
          val updatedExercisesSets =
            component.exercisesSets.mapNotNull { exerciseSets ->
              // Filter to keep only completed sets within this ExerciseExerciseSets
              val completedSets = exerciseSets.sets.filter { set -> set.setCompleted }
              // If sets remain, create a modified copy of ExerciseExerciseSets
              if (completedSets.isNotEmpty()) {
                exerciseSets.copy(sets = completedSets.toMutableList())
              } else {
                // If no sets remain, discard this ExerciseExerciseSets by returning null
                null
              }
            }
          // If ExerciseExerciseSets remain within the superset, create a modified copy of the
          // Superset
          if (updatedExercisesSets.isNotEmpty()) {
            component.copy(exercisesSets = updatedExercisesSets)
          } else {
            // If no ExerciseExerciseSets remain, discard the entire Superset by returning null
            null
          }
        }

        is ExerciseExerciseSets -> {
          val completedSets = component.sets.filter { set -> set.setCompleted }
          if (completedSets.isNotEmpty()) {
            component.copy(sets = completedSets.toMutableList())
          } else {
            null // Discard this component if it becomes empty
          }
        }
      }
    }
  }

  /**
   * Replaces a specific ExerciseExerciseSets within a list of WorkoutComponents with a new
   * Exercise.
   *
   * It finds the target ExerciseExerciseSets within a SupersetExercisesSets component using its
   * exSetsUUID, updates it immutably with the new Exercise details (including updating set exercise
   * IDs and generating a new lazy list ID), and returns a new list of WorkoutComponents reflecting
   * the change.
   *
   * @param workoutComponents The original immutable list of workout components.
   * @param targetExerciseSets The specific ExerciseExerciseSets instance to find and replace.
   * @param newExercise The new Exercise to associate with the replaced ExerciseExerciseSets.
   * @return A new list of WorkoutComponents with the specified ExerciseExerciseSets updated, or the
   *   original list if the target was not found.
   */
  fun replaceExerciseInExerciseSets(
    workoutComponents: List<WorkoutComponent>,
    targetExerciseSets: ExerciseExerciseSets,
    newExercise: Exercise,
  ): List<WorkoutComponent> {
    // Use map to iterate through components and create a new list
    return workoutComponents.map { component ->
      when (component) {
        is SupersetExercisesSets -> {
          // Try to find the index of the target ExerciseExerciseSets within this Superset
          val targetIndex =
            component.exercisesSets.indexOfFirst { it.exSetsUUID == targetExerciseSets.exSetsUUID }

          // If the target ExerciseExerciseSets was found in this Superset
          if (targetIndex != -1) {
            // Get the original instance to copy from
            val originalExerciseSets = component.exercisesSets[targetIndex]

            // Create new Set instances with the updated exerciseID
            val updatedSets =
              originalExerciseSets.sets.map { set -> set.copy(exerciseID = newExercise.id) }

            // Create the new ExerciseExerciseSets instance using copy for immutability
            val newExerciseSetsInstance =
              originalExerciseSets.copy(
                exercise = newExercise, // Assign the new Exercise
                sets = updatedSets.toMutableList(), // Assign the list of updated Sets
                lazyListItemID = newLazyListItemID(), // Generate a new ID for UI purposes
                // Note: exSetsUUID and id (ObjectId) remain the same as the original
              )

            // Create a new list of ExerciseExerciseSets for the Superset
            val updatedSupersetExercisesList =
              component.exercisesSets.toMutableList().apply {
                this[targetIndex] = newExerciseSetsInstance // Replace item at the index
              }

            // Return a copy of the Superset component with the updated list
            component.copy(
              lazyListItemID = newLazyListItemID(),
              exercisesSets = updatedSupersetExercisesList.toList(),
            )
          } else {
            // Target not found in this Superset, return the component unmodified
            component
          }
        }

        is ExerciseExerciseSets -> {
          // Try to find the target ExerciseExerciseSets within list of WorkoutComponents
          if (component.exSetsUUID == targetExerciseSets.exSetsUUID) {
            targetExerciseSets.exercise = newExercise

            // Create new Set instances with the updated exerciseID
            val updatedSets = component.sets.map { set -> set.copy(exerciseID = newExercise.id) }

            // Create the new ExerciseExerciseSets instance using copy for immutability
            val newExerciseSets =
              component.copy(
                exercise = newExercise, // Assign the new Exercise
                sets = updatedSets.toMutableList(), // Assign the list of updated Sets
                lazyListItemID = newLazyListItemID(), // Generate a new ID for UI purposes
                // Note: exSetsUUID and id (ObjectId) remain the same as the original
              )

            newExerciseSets
          } else {
            // Not the target ExerciseExerciseSets, return the component unmodified
            component
          }
        }
      }
    }
  }

  fun addMultipleEmptyExerciseSets(
    exerciseSets: MutableList<WorkoutComponent>,
    exercisesSelectedToBeAddedToWorkout: List<Exercise>,
    _workoutID: ObjectId,
  ): MutableList<WorkoutComponent> {
    exercisesSelectedToBeAddedToWorkout.forEach { _exercise ->
      val pinnedNotes = exerciseNotesRepository.getPinnedExerciseNotes(_exercise.id)
      exerciseSets.add(
        ExerciseExerciseSets(
          order = if (exerciseSets.isEmpty()) 1 else exerciseSets.size.plus(1),
          exercise = _exercise,
          notes = pinnedNotes.toMutableList(),
          datePerformed = Clock.System.now(),
          workoutID = _workoutID,
          microcycleID = null, // TODO get details from workout
          mesocycleID = null, // TODO get details from workout
          programID = null, // TODO get details from workout
          ownerID = userID, // TODO Replace currentUser.id
          isPublic = false,
        )
      )
    }
    return exerciseSets
  }

  fun removeExerciseSets(
    exerciseExerciseSets: MutableList<WorkoutComponent>,
    exerciseSets: ExerciseExerciseSets,
  ): List<WorkoutComponent> {
    return exerciseExerciseSets.filter { it != exerciseSets }
  }

  // TODO Debug Causes Crash
  /**
   * Creates a new list of sets by adding a new set based on the state of the provided
   * ExerciseExerciseSets. If exerciseSets.sets is empty, adds a default first set (setNumber 0). If
   * exerciseSets.sets is not empty, adds a copy of the last set, incrementing the set number and
   * resetting completion status.
   *
   * @param exerciseSets The context providing the current sets and exercise/workout IDs.
   * @return A new immutable list containing the original sets plus the newly added one.
   */
  fun addSet(exerciseSets: ExerciseExerciseSets): List<Set> {
    val currentSets = exerciseSets.sets

    return if (currentSets.isNotEmpty()) {
      // --- Case: Add based on the last set ---
      val lastSet = currentSets.last()

      // Create the new set by copying the last one and overriding specific fields
      val newSet =
        lastSet.copy(
          // Generate new unique identifiers if required for each distinct set instance
          id = ObjectId(),
          uuid = Uuid.random(),
          lazyListItemID = newLazyListItemID(),

          // Calculate next set number, defaulting to 0 if the last one was null (adjust if null is
          // invalid)
          setNumber = lastSet.setNumber?.plus(1) ?: 0,
          setCompleted = false, // New sets start as not completed

          // Assign ownership/metadata
          ownerID = userID, // TODO: Replace currentUser.id
          isPublic = false, // Assuming default

          // All other fields (weight, reps, targets, exerciseID, workoutID etc.) are copied from
          // lastSet
        )
      // Return a new list combining the old list and the new set
      currentSets.add(newSet)
      currentSets.mapIndexed { i, set ->
        // Create a new object with the updated order
        set.copy(setNumber = i + 1)
      }
    } else {
      // --- Case: Add the very first set ---
      // Return a new list containing only the first default set
      listOf(
        Set(
          // Generate unique identifiers if required
          // id = ObjectId(),
          // uuid = Uuid.random(),

          // Link to parent objects
          exerciseID = exerciseSets.exercise.id,
          workoutID = exerciseSets.workoutID,
          exerciseSetsID = exerciseSets.id,

          // Initial set details
          setNumber = 1,
          setCompleted = false,
          isPublic = false,
          progressionSchemeID = null, // TODO: Replace with actual logic if available
          ownerID = userID, // TODO: Replace currentUser.id

          // Initialize performance fields to null/defaults as appropriate
          // weightPounds = null,
          // weightKilograms = null,
          // repetitions = null,
          // ratePerceivedEffort = null,
          // repetitionsInReserve = null
          // ... initialize target fields etc. if needed
        )
      )
    }
  }

  fun addSetAtIndex(exerciseSets: ExerciseExerciseSets, index: Int): List<Set> {
    val currentSets = exerciseSets.sets

    return if (currentSets.isNotEmpty()) {
      // --- Case: Add based on the set call this function ---
      val setToCopy = currentSets[index]
      val setIndex = min(index.plus(1), currentSets.size)

      // Create the new set by copying the last one and overriding specific fields
      val newSet =
        setToCopy.copy(
          // Generate new unique identifiers if required for each distinct set instance
          id = ObjectId(),
          uuid = Uuid.random(),
          lazyListItemID = newLazyListItemID(),
          setCompleted = false, // New sets start as not completed

          // Assign ownership/metadata
          ownerID = userID, // TODO: Replace currentUser.id
          isPublic = false, // Assuming default

          // All other fields (weight, reps, targets, exerciseID, workoutID etc.) are copied from
          // lastSet
        )

      currentSets.add(setIndex, newSet)

      // Return a new list and Re-assign order property for all sets in the new list
      currentSets.mapIndexed { i, set ->
        // Create a new object with the updated order
        set.copy(setNumber = i + 1)
      }
    } else {
      // --- Case: Add the very first set ---
      // Return a new list containing only the first default set
      listOf(
        Set(
          // Generate unique identifiers if required
          // id = ObjectId(),
          // uuid = Uuid.random(),

          // Link to parent objects
          exerciseID = exerciseSets.exercise.id,
          workoutID = exerciseSets.workoutID,
          exerciseSetsID = exerciseSets.id,

          // Initial set details
          setNumber = 1,
          setCompleted = false,
          isPublic = false,
          progressionSchemeID = null, // TODO: Replace with actual logic if available
          ownerID = userID, // TODO: Replace currentUser.id

          // Initialize performance fields to null/defaults as appropriate
          // weightPounds = null,
          // weightKilograms = null,
          // repetitions = null,
          // ratePerceivedEffort = null,
          // repetitionsInReserve = null
          // ... initialize target fields etc. if needed
        )
      )
    }
  }
  
  /**
   * Adds a new set to a specific ExerciseExerciseSets instance nested within a specific
   * SupersetExercisesSets. This function operates immutably, returning a new list of
   * WorkoutComponents.
   *
   * @param workoutComponent The current immutable list of all workout components.
   * @param supersetUuid The UUID identifying the target SupersetExercisesSets component.
   * @param targetExSetsUuid The UUID identifying the target ExerciseExerciseSets within the
   *   superset.
   * @return A new list of WorkoutComponents with the added set, or the original list if the target
   *   superset or exercise set is not found.
   */
  @OptIn(ExperimentalUuidApi::class) // If Uuid requires it
  fun addSetToExerciseExerciseSet(
    workoutComponent: List<WorkoutComponent>, // Input: immutable list
    supersetUuid: Uuid?, // ID of the Superset component
    targetExSetsUuid: Uuid, // ID of the ExerciseExerciseSets within the Superset
  ): List<WorkoutComponent> { // Return: new immutable list

    // Use map to create a new list based on transformations
    return workoutComponent.map { component ->
      // Find the target Superset component by its UUID
      when (component) {
        is ExerciseExerciseSets if component.exSetsUUID == targetExSetsUuid -> {
          val newSetsList = addSet(component).toMutableList()

          component.copy(
            lazyListItemID = newLazyListItemID(),
            sets = newSetsList)
        }

        is SupersetExercisesSets if component.uuid == supersetUuid -> {
          var supersetModified = false // Flag to track if modification occurred

          // Map the inner exercisesSets list to potentially update the target
          val updatedInnerExercisesSets =
            component.exercisesSets.map { innerExSets ->
              // Find the target ExerciseExerciseSets within the Superset by its UUID
              if (innerExSets.exSetsUUID == targetExSetsUuid) {
                supersetModified = true // Mark that we found and will modify the target

                // *** Reuse the logic from your existing addSet function ***
                // This generates a new list with the added set
                val newSetsList =
                  addSet(innerExSets) // Assuming addSet(ExerciseExerciseSets) returns List<Set>

                // Return a copy of the innerExSets with the new list of sets
                innerExSets.copy(
                  lazyListItemID = newLazyListItemID(),
                  sets = newSetsList.toMutableList()) // Update the sets
              } else {
                // Not the target ExerciseExerciseSets, return it unmodified
                innerExSets
              }
            }

          // If we modified something inside, return a copy of the Superset with the updated list
          if (supersetModified) {
            component.copy(exercisesSets = updatedInnerExercisesSets)
          } else {
            // Target ExerciseExerciseSets wasn't found in this Superset, return Superset unmodified
            component
          }
        }

        else -> {
          // Not the target Superset component, return it unmodified
          component
        }
      }
    }
  }

  @OptIn(ExperimentalUuidApi::class) // If Uuid requires it
  fun addSetAtIndexToExerciseExerciseSet(
    workoutComponent: List<WorkoutComponent>, // Input: immutable list
    supersetUuid: Uuid?, // ID of the Superset component
    targetExSetsUuid: Uuid, // ID of the ExerciseExerciseSets within the Superset
    index: Int, // Index to insert the new set
  ): List<WorkoutComponent> { // Return: new immutable list

    // Use map to create a new list based on transformations
    return workoutComponent.map { component ->
      // Find the target Superset component by its UUID
      when (component) {
        is ExerciseExerciseSets if component.exSetsUUID == targetExSetsUuid -> {
          component.copy(
            lazyListItemID = newLazyListItemID(),
            sets = addSetAtIndex(component, index).toMutableList()
          )
        }

        is SupersetExercisesSets if component.uuid == supersetUuid -> {
          var supersetModified = false // Flag to track if modification occurred

          // Map the inner exercisesSets list to potentially update the target
          val updatedInnerExercisesSets =
            component.exercisesSets.map { innerExSets ->
              // Find the target ExerciseExerciseSets within the Superset by its UUID
              if (innerExSets.exSetsUUID == targetExSetsUuid) {
                supersetModified = true // Mark that we found and will modify the target

                // *** Reuse the logic from your existing addSet function ***
                // This generates a new list with the added set
                val newSetsList =
                  addSetAtIndex(
                    innerExSets,
                    index,
                  ) // Assuming addSet(ExerciseExerciseSets) returns List<Set>

                // Return a copy of the innerExSets with the new list of sets
                innerExSets.copy(
                  lazyListItemID = newLazyListItemID(),
                  sets = newSetsList.toMutableList()
                ) // Update the sets

              } else {
                // Not the target ExerciseExerciseSets, return it unmodified
                innerExSets
              }
            }

          // If we modified something inside, return a copy of the Superset with the updated list
          if (supersetModified) {
            component.copy(exercisesSets = updatedInnerExercisesSets)
          } else {
            // Target ExerciseExerciseSets wasn't found in this Superset, return Superset unmodified
            component
          }
        }

        else -> {
          // Not the target Superset component, return it unmodified
          component
        }
      }
    }
  }

  fun removeSet(exerciseSets: ExerciseExerciseSets, set: Set): List<Set> {
    return exerciseSets.sets.minusElement(set).mapIndexed { index, set ->
      set.setNumber = index + 1
      set
    }
  }

  fun updateExercisesSetsData(exerciseSets: ExerciseExerciseSets, temporarySets: List<Set>) {
    exerciseSets.sets = temporarySets.toMutableList()

    //    updateExerciseExerciseSetsData(exerciseSets)
  }

  fun addExerciseNote(exerciseSets: ExerciseExerciseSets, workoutID: ObjectId): List<ExerciseNote> {
    exerciseSets.notes.add(
      ExerciseNote().apply {
        exercise = exerciseSets.exercise.id
        workout = workoutID
        isPublic = false
      }
    )
    return exerciseSets.notes
    //    Log.i("addExerciseNote", "$exerciseSets.notes")

    //    updateExercisesNotesData(exerciseSets, exerciseSets.notes)
  }

  fun removeExerciseNote(
    exerciseSets: ExerciseExerciseSets,
    exerciseNote: ExerciseNote,
  ): List<ExerciseNote> {
    return exerciseSets.notes.minusElement(exerciseNote)

    //    updateExercisesNotesData(exerciseSets, exerciseSets.notes)
    //    Log.i("removeExerciseNote", "removeExerciseNote: ${exerciseSets.notes.size}")
  }

  fun pinExerciseNote(
    exerciseSets: ExerciseExerciseSets,
    exerciseNote: ExerciseNote,
    pinned: Boolean,
  ): List<ExerciseNote> {
    val index = exerciseSets.notes.indexOf(exerciseNote)

    exerciseSets.notes[index].pinned = pinned
    return exerciseSets.notes
    //    updateExercisesNotesData(exerciseSets, exerciseSets.notes)
  }

  fun updateExercisesNotesData(
    exerciseSets: ExerciseExerciseSets,
    temporaryNotes: List<ExerciseNote>,
  ) {
    exerciseSets.notes = temporaryNotes.toMutableList()

    //    updateExerciseExerciseSetsData(exerciseSets)
  }

  /**
   * Creates a new list of WorkoutComponents with an updated exSetsLazyListItemID for a specific
   * target ExerciseExerciseSets. Useful for forcing recomposition in a LazyColumn when an item's
   * data changes.
   *
   * @param workoutComponents The original immutable list of workout components.
   * @param targetExerciseSets The ExerciseExerciseSets instance whose corresponding component needs
   *   its ID refreshed.
   * @return A new list of WorkoutComponents with updated exSetsLazyListItemID for the target.
   */
  fun refreshWorkoutComponentLazyListItemID(
    workoutComponents: List<WorkoutComponent>,
    targetExerciseExerciseSets: ExerciseExerciseSets,
  ): List<WorkoutComponent> {
    return workoutComponents.map { component ->
      when (component) {
        // Case 1: The component itself IS the target ExerciseExerciseSets
        is ExerciseExerciseSets -> {
          if (component.exSetsUUID == targetExerciseExerciseSets.exSetsUUID) {
            // Return a copy with a new lazy ID
            targetExerciseExerciseSets.copy(
              lazyListItemID = newLazyListItemID(),
              sets =
                targetExerciseExerciseSets.sets
                  .map { set -> set.copy(lazyListItemID = newLazyListItemID()) }
                  .toMutableList(),
            )
          } else {
            // Not the target, return unmodified
            component
          }
        }
        // Case 2: The component is a Superset potentially containing the target
        is SupersetExercisesSets -> {
          val targetIndex =
            component.exercisesSets.indexOfFirst {
              it.exSetsUUID == targetExerciseExerciseSets.exSetsUUID
            }

          // If the target ExerciseExerciseSets was found in this Superset
          if (targetIndex != -1) {
            // Create a new list of ExerciseExerciseSets for the Superset
            val updatedSupersetExercisesList =
              component.exercisesSets.toMutableList().apply {
                // Replace item at the index
                this[targetIndex] =
                  targetExerciseExerciseSets.copy(
                    lazyListItemID = newLazyListItemID(),
                    sets =
                      targetExerciseExerciseSets.sets
                        .map { set -> set.copy(lazyListItemID = newLazyListItemID()) }
                        .toMutableList(),
                  )
              }
            // Return a copy of the Superset with the updated inner list
            component.copy(
              //              lazyListItemID = newLazyListItemID(),
              exercisesSets = updatedSupersetExercisesList
            )
          } else {
            // Target not in this Superset, return unmodified
            component
          }
        }
      }
    }
  }

  fun allSets(): MutableList<Set> {
    val setsList = mutableListOf<Set>()
    //    _exercisesSetsData.forEach { exerciseSet ->
    //      exerciseSet.sets.forEach { set -> setsList.add(set) }
    //    }

    return setsList
  }

  fun updateSetWeight(
    exerciseSets: ExerciseExerciseSets,
    setUUID: Uuid,
    newWeight: Double?,
  ): WorkoutComponent {
    val weightUnit = exerciseSets.exercise?.weightUnit ?: userDefaultWeightUnit
    exerciseSets.updateSetWeight(uuid = setUUID, newWeight = newWeight, weightUnit = weightUnit)
    return exerciseSets
    //    updateExerciseExerciseSetsData(exerciseSets)
  }

  fun updateSetReps(
    exerciseSets: ExerciseExerciseSets,
    setUUID: Uuid,
    newReps: Int?,
  ): WorkoutComponent {
    exerciseSets.updateSetReps(uuid = setUUID, newReps = newReps)
    return exerciseSets
    //    updateExerciseExerciseSetsData(exerciseSets)
  }

  fun updateSetRIR(
    exerciseSets: ExerciseExerciseSets,
    setUUID: Uuid,
    newRIR: Double?,
  ): WorkoutComponent {
    exerciseSets.updateSetRIR(uuid = setUUID, newRIR = newRIR)
    return exerciseSets
    //    updateExerciseExerciseSetsData(exerciseSets)
  }

  fun updateSetRPE(
    exerciseSets: ExerciseExerciseSets,
    setUUID: Uuid,
    newRPE: Double?,
  ): WorkoutComponent {
    exerciseSets.updateSetRPE(uuid = setUUID, newRPE = newRPE)
    return exerciseSets
    //    updateExerciseExerciseSetsData(exerciseSets)
  }

  fun setComplete(
    exerciseSets: ExerciseExerciseSets,
    setUUID: Uuid,
    setComplete: Boolean,
  ): WorkoutComponent {
    exerciseSets.completeSet(setUUID, setComplete)
    return exerciseSets
    //    updateExerciseExerciseSetsData(exerciseSets)
  }

  fun updateSetType(
    exerciseSets: ExerciseExerciseSets,
    setUUID: Uuid,
    setType: SetType,
  ): WorkoutComponent {
    exerciseSets.updateSetType(uuid = setUUID, setType = setType)
    return exerciseSets
    //    updateExerciseExerciseSetsData(exerciseSets)
  }

  fun getWeightUnit(exerciseSets: ExerciseExerciseSets): String {
    return if (exerciseSets.exercise.weightUnit != null) {
      exerciseSets.exercise.weightUnit ?: MassUnit.POUNDS.abbreviation
    } else {
      userDefaultWeightUnit
    }
  }

  fun createSupersetFromExercise(
    workoutComponents: MutableList<WorkoutComponent>,
    exercises: List<Exercise>,
    workout: Workout,
  ): MutableList<WorkoutComponent> {

    val exercisesSetsList = mutableListOf<ExerciseExerciseSets>()

    exercises.forEachIndexed { index, exercise ->
      val pinnedNotes = exerciseNotesRepository.getPinnedExerciseNotes(exercise.id)

      exercisesSetsList.add(
        ExerciseExerciseSets(
          order = index.plus(1),
          exercise = exercise,
          notes = pinnedNotes.toMutableList(),
          datePerformed = Clock.System.now(),
          workoutID = workout.id,
          microcycleID = workout.microcycleID, // TODO get details from workout
          mesocycleID = workout.mesocycleID, // TODO get details from workout
          programID = workout.programID, // TODO get details from workout
          ownerID = userID, // TODO Replace currentUser.id
          isPublic = false,
        )
      )
    }

    // TODO Get Superset Notes when no name, Use UUID?

    workoutComponents.add(
      SupersetExercisesSets(
        //        name = TODO(),
        exercisesSets = exercisesSetsList.toList(),
        restBetweenExercises = 0.toDuration(DurationUnit.SECONDS).inWholeMilliseconds,
        enableRestBetweenExercises = false,
        enableRestAfterSuperset = true,
        restAfterSuperset = 1.toDuration(DurationUnit.MINUTES).inWholeMilliseconds,
        order = workoutComponents.size.plus(1),
        workoutID = workout.id,
        microcycleID = workout.microcycleID, // TODO get details from workout
        mesocycleID = workout.mesocycleID, // TODO get details from workout
        programID = workout.programID, // TODO get details from workout
      )
    )

    return workoutComponents
  }

  fun createSupersetFromExercisesSets(
    workoutComponents: MutableList<WorkoutComponent>,
    exercisesSets: List<ExerciseExerciseSets>,
    workout: Workout,
  ): List<WorkoutComponent> {
    // TODO Get Superset Notes when no name, Use UUID?

    workoutComponents.add(
      SupersetExercisesSets(
        //        name = TODO(),
        exercisesSets = exercisesSets.sortedBy { it.order },
        restBetweenExercises = 0.toDuration(DurationUnit.SECONDS).inWholeMilliseconds,
        enableRestBetweenExercises = false,
        enableRestAfterSuperset = true,
        restAfterSuperset = 1.toDuration(DurationUnit.MINUTES).inWholeMilliseconds,
        order = exercisesSets.first().order,
        workoutID = workout.id,
        microcycleID = workout.microcycleID, // TODO get details from workout
        mesocycleID = workout.mesocycleID, // TODO get details from workout
        programID = workout.programID, // TODO get details from workout
      )
    )

    // Remove duplicated ExerciseExerciseSets
    workoutComponents.removeAll { exercisesSets.contains(it) }

    return workoutComponents.sortedBy { it.order }
  }


  suspend fun getWorkoutTotalSetsPerMuscle(
    workoutID: ObjectId
  ): List<MuscleFractionalSets> {
    return completedWorkoutRepository.getWorkoutTotalSetsPerMuscle(workoutID)
  }
}

class ManageExercisesUseCase(private val exercisesRepository: ExercisesRepository) {
  suspend fun updateExerciseWeightUnit(exercise: Exercise?, weightUnit: String?) {
    exercise?.weightUnit = weightUnit

    if (exercise != null) {
      exercisesRepository.updateExercise(exercise)
    }
  }

  suspend fun updateExerciseAutoRestTimer(tabRowState: Int, exercise: Exercise, time: Duration) {
    if (tabRowState == 1) {
      exercise.warmUpSetRestInWholeMilliseconds = time.inWholeMilliseconds
    } else if (tabRowState == 0) {
      exercise.workingSetRestInWholeMilliseconds = time.inWholeMilliseconds
    }
    exercisesRepository.updateExercise(exercise)
  }

  suspend fun enableExerciseAutoRestTimer(tabRowState: Int, exercise: Exercise, enable: Boolean) {
    if (tabRowState == 1) {
      exercise.warmUpSetRestTimerEnabled = enable
    } else if (tabRowState == 0) {
      exercise.workingSetRestTimerEnabled = enable
    }
    exercisesRepository.updateExercise(exercise)
  }

  fun getExerciseByName(exerciseName: String): ObjectId? {
    return exercisesRepository.getExerciseByName(exerciseName)
  }
}
