package com.example.myapplication.components

import com.example.myapplication.data.model.Exercise
import com.example.myapplication.data.model.ExerciseExerciseSets
import com.example.myapplication.data.model.ExerciseNote
import com.example.myapplication.data.model.ExerciseSets
import com.example.myapplication.data.model.MassUnit
import com.example.myapplication.data.model.Set
import com.example.myapplication.data.model.Workout
import com.example.myapplication.data.model.WorkoutNote
import com.example.myapplication.data.model.calculateTotalVolume
import com.example.myapplication.data.model.completeSet
import com.example.myapplication.data.model.totalVolumeInKilograms
import com.example.myapplication.data.model.totalVolumeInPounds
import com.example.myapplication.data.model.updateSetRIR
import com.example.myapplication.data.model.updateSetRPE
import com.example.myapplication.data.model.updateSetReps
import com.example.myapplication.data.model.updateSetType
import com.example.myapplication.data.model.updateSetWeight
import com.example.myapplication.data.network.ExerciseNotesRepository
import com.example.myapplication.data.network.ExercisesRepository
import com.example.myapplication.data.network.UserRepository
import com.example.myapplication.data.network.WorkoutNotesRepository
import com.example.myapplication.data.network.WorkoutRepository
import com.example.myapplication.utils.newID
import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.ext.toRealmList
import io.realm.kotlin.mongodb.ext.customDataAsBsonDocument
import io.realm.kotlin.types.RealmInstant
import io.realm.kotlin.types.RealmList
import io.realm.kotlin.types.RealmUUID
import org.mongodb.kbson.ObjectId
import kotlin.time.Duration

class ManageWorkoutUseCase(
  private val workoutRepository: WorkoutRepository,
  //  private val setsRepository: SetsRepository,
  //  private val exerciseSetsRepository: ExerciseSetsRepository,
  private val exercisesRepository: ExercisesRepository,
  private val workoutNotesRepository: WorkoutNotesRepository,
  private val exerciseNotesRepository: ExerciseNotesRepository,
  userRepository: UserRepository,
) {
  val currentUser = userRepository.getCurrentUser()
  val userDefaultWeightUnit: String =
    userRepository.getCurrentUser().customDataAsBsonDocument()?.getString("weightUnit")?.value
      ?: MassUnit.POUNDS.abbreviation

  fun getExerciseByID(exerciseID: ObjectId?): Exercise? {
    return exercisesRepository.getExercise(exerciseID)
  }

  fun getWorkoutByID(workoutID: ObjectId): Workout? {
    // workoutRepository.getWorkoutByID(workoutID = workoutID)
    // TODO Add logic for when copying from a template (active program) vs when copying from a
    // previous workout
    return workoutRepository.getWorkoutByID(workoutID = workoutID)
  }

  fun copyWorkoutFromWorkoutTemplate(workoutTemplate: Workout): Workout {
    val newWorkout =
      Workout().apply {
        _id = workoutTemplate._id
        uuid = workoutTemplate.uuid
        name = workoutTemplate.name
        workoutAuthor = workoutTemplate.workoutAuthor
        //        notes = workoutTemplate.notes.filter { it.pinned }.toRealmList()
        workoutType = workoutTemplate.workoutType
        dateCreated = workoutTemplate.dateCreated
        workoutStarted = RealmInstant.now()
        day = workoutTemplate.day
        //        exercisesSets =
        // copyExercisesSetsFromPreviousWorkout(workoutTemplate.exercisesSets)
        microcycleID = workoutTemplate.microcycleID
        programID = workoutTemplate.programID
        owner_id = currentUser.id
        isPublic = false
      }

    return newWorkout
  }

  fun copyWorkoutFromPreviousWorkout(previousWorkout: Workout): Workout {
    val newWorkout =
      Workout().apply {
        uuid = previousWorkout.uuid
        name = previousWorkout.name
        workoutAuthor = previousWorkout.workoutAuthor
        //        notes = previousWorkout.notes.filter { it.pinned }.toRealmList()
        workoutType = previousWorkout.workoutType
        workoutStarted = RealmInstant.now()
        day = previousWorkout.day
        //        exercisesSets =
        // copyExercisesSetsFromPreviousWorkout(previousWorkout.exercisesSets)
        microcycleID = previousWorkout.microcycleID
      }
    return newWorkout
  }

  fun getPinnedWorkoutNotes(workoutUUID: RealmUUID, workoutID: ObjectId): MutableList<WorkoutNote> {
    return workoutNotesRepository.getPinnedWorkoutNotes(workoutUUID).toMutableList()
  }

  fun copyExercisesSetsFromPreviousWorkout(
    previousExercisesSets: RealmList<ExerciseSets>,
    _workoutID: ObjectId
  ): MutableList<ExerciseExerciseSets> {
    val newExercisesSets = mutableListOf<ExerciseExerciseSets>()
    previousExercisesSets.forEach { exerciseSets ->
      //      TODO get exercise by ID
      val newExerciseExerciseSets =
        ExerciseExerciseSets().apply {
          exSetsUUID = exerciseSets.exSetsUUID
          exerciseOrder = exerciseSets.exerciseOrder
          exercise = exercisesRepository.getExercise(exerciseSets.exerciseID)
          sets = copySetsFromPreviousWorkout(exerciseSets.sets)
          notes = exerciseSets.notes.filter { it.pinned }.toRealmList()
          workoutID = _workoutID
        }

      exerciseSets.exerciseID?.let {
        newExerciseExerciseSets.notes =
          exerciseNotesRepository.getPinnedExerciseNotes(it).toRealmList()
      }

      newExercisesSets.add(newExerciseExerciseSets)
    }
    return newExercisesSets.sortedBy { it.exerciseOrder }.toMutableList()
  }

  fun copySetsFromPreviousWorkout(previousSets: RealmList<Set>): RealmList<Set> {
    val newSets = realmListOf<Set>()
    val latestPreviousSets = realmListOf<Set>()
    previousSets.forEach { set ->
      val newSet =
        Set().apply {
          uuid = set.uuid
          setNumber = set.setNumber
          weightPounds = set.weightPounds
          weightKilograms = set.weightKilograms
          repetitions = set.repetitions
          ratePerceivedEffort = set.ratePerceivedEffort
          repetitionsInReserve = set.repetitionsInReserve
          targetWeightPounds = set.targetWeightPounds
          targetRepetitions = set.targetRepetitions
          targetRatePerceivedEffort = set.targetRatePerceivedEffort
          targetRepetitionsInReserve = set.targetRepetitionsInReserve
          percentage1RM = set.percentage1RM
          setTypeID = set.setTypeID
          progressionSchemeID = set.progressionSchemeID
          owner_id = currentUser.id
        }
      newSets.add(newSet)
      if (set.exerciseID != null && set.setNumber != null) {
        val previousSet =
          workoutRepository.getPreviousSetForExercise(set.exerciseID!!, set.setNumber!!)
        previousSet?.let { latestPreviousSets.add(it) }
      }
    }
    return newSets
  }

  fun loadExercisesSetsFromWorkout(
    previousExercisesSets: RealmList<ExerciseSets>,
  ): MutableList<ExerciseExerciseSets> {
    val newExercisesSets = mutableListOf<ExerciseExerciseSets>()
    previousExercisesSets.forEach { exerciseSets ->
      val newExerciseExerciseSets =
        ExerciseExerciseSets().apply {
          _id = exerciseSets._id
          exSetsUUID = exerciseSets.exSetsUUID
          workoutID = exerciseSets.workoutID
          exerciseOrder = exerciseSets.exerciseOrder
          exercise = exercisesRepository.getExercise(exerciseSets.exerciseID)
          sets = loadSetsFromWorkout(exerciseSets.sets)
          notes = exerciseSets.notes
          datePerformed = exerciseSets.datePerformed
        }
      newExercisesSets.add(newExerciseExerciseSets)
    }
    return newExercisesSets.sortedBy { it.exerciseOrder }.toMutableList()
  }

  fun loadSetsFromWorkout(previousSets: RealmList<Set>): RealmList<Set> {
    val newSets = realmListOf<Set>()
    val latestPreviousSets = realmListOf<Set>()
    previousSets.forEach { set ->
      val newSet =
        Set().apply {
          _id = set._id
          uuid = set.uuid
          setNumber = set.setNumber
          weightPounds = set.weightPounds
          weightKilograms = set.weightKilograms
          repetitions = set.repetitions
          ratePerceivedEffort = set.ratePerceivedEffort
          repetitionsInReserve = set.repetitionsInReserve
          targetWeightPounds = set.targetWeightPounds
          targetRepetitions = set.targetRepetitions
          targetRatePerceivedEffort = set.targetRatePerceivedEffort
          targetRepetitionsInReserve = set.targetRepetitionsInReserve
          percentage1RM = set.percentage1RM
          setCompleted = set.setCompleted
          setTypeID = set.setTypeID
          progressionSchemeID = set.progressionSchemeID
          owner_id = currentUser.id
          isPublic = false
        }
      newSets.add(newSet)
      if (set.exerciseID != null && set.setNumber != null) {
        val previousSet =
          workoutRepository.getPreviousSetForExercise(set.exerciseID!!, set.setNumber!!)
        previousSet?.let { latestPreviousSets.add(it) }
      }
    }
    return newSets
  }

  fun populatePreviousSetField(exerciseID: ObjectId, setNumber: Int): Set? {
    return workoutRepository.getPreviousSetForExercise(exerciseID, setNumber)
  }

  fun addWorkoutNote(
    workoutNotes: MutableList<WorkoutNote>,
    workoutID: ObjectId,
    _workoutUUID: RealmUUID
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
    workoutNotes: MutableList<WorkoutNote>
  ): List<WorkoutNote> {
    return workoutNotes.filter { it != workoutNote }
  }

  fun setWorkoutNote(
    _workoutNotes: MutableList<WorkoutNote>,
    workoutNote: WorkoutNote,
    note: String
  ): List<WorkoutNote> {
    val index = _workoutNotes.indexOf(workoutNote)
    _workoutNotes[index].note = note
    return _workoutNotes
  }

  fun pinWorkoutNote(
    _workoutNotes: MutableList<WorkoutNote>,
    workoutNote: WorkoutNote,
    pinned: Boolean
  ): List<WorkoutNote> {
    val index = _workoutNotes.indexOf(workoutNote)
    _workoutNotes[index].pinned = pinned
    return _workoutNotes
  }

  fun updateWorkoutNotes(
    _workoutNotes: MutableList<WorkoutNote>,
    note: WorkoutNote
  ): List<WorkoutNote> {
    _workoutNotes.first { it == note }.lazyListID = newID()
    return _workoutNotes
  }

  suspend fun saveWorkout(
    workout: Workout,
    workoutNotes: List<WorkoutNote>,
    exercisesSets: List<ExerciseExerciseSets>,
  ) {
    //    TODO Implement this
    workout.notes = workoutNotes.toRealmList()

    //    TODO Only Add Completed Sets, Implement Check ond Remove Uncompleted Sets
    //    removeUncompletedSets(exercisesSets.toMutableList())

    workout.exercisesSets =
      convertExerciseExerciseSetsToExerciseSets(
        exercisesSets.toMutableList(),
        userDefaultWeightUnit
      )

    workout.calculateTotalVolume(userDefaultWeightUnit = userDefaultWeightUnit)
//    workout.calculateTotalVolumeInPounds()
//    workout.calculateTotalVolumeInKilograms()

    workoutRepository.insertWorkout(workout)
  }

  private fun convertExerciseExerciseSetsToExerciseSets(
    exerciseExerciseSets: List<ExerciseExerciseSets>,
    userDefaultWeightUnit: String,
  ): RealmList<ExerciseSets> {
    val exercisesSets = realmListOf<ExerciseSets>()
    exerciseExerciseSets.forEach { _exerciseSets ->
      exercisesSets.add(
        ExerciseSets().apply {
          _id = _exerciseSets._id
          exSetsUUID
          workoutID
          exerciseOrder = _exerciseSets.exerciseOrder
          exerciseID = _exerciseSets.exercise?._id
          sets = _exerciseSets.sets
          notes = _exerciseSets.notes
          datePerformed = _exerciseSets.datePerformed
          totalVolumeInPounds = _exerciseSets.totalVolumeInPounds()
          totalVolumeInKilograms = _exerciseSets.totalVolumeInKilograms()
          isPublic = false
          owner_id = currentUser.id
        }
      )
    }

    return exercisesSets
  }

  fun getCompletedWorkout(workoutID: ObjectId): Workout? {
    return workoutRepository.getWorkoutByID(workoutID)
  }

  fun getWorkoutsCount(): Int {
    return workoutRepository.getWorkoutsCount()
  }

  fun checkAllSetsComplete(exerciseExerciseSets: MutableList<ExerciseExerciseSets>): Boolean {
    var countSets: Int = 0
    var countSetsCompleted: Int = 0

    exerciseExerciseSets.forEach { exerciseSets ->
      exerciseSets.sets.forEach { set ->
        countSets++
        if (set.setCompleted) {
          countSetsCompleted++
        }
      }
    }

    return countSetsCompleted == countSets && countSets > 0
  }

  fun removeUncompletedSets(
    exerciseExerciseSets: MutableList<ExerciseExerciseSets>
  ): List<ExerciseExerciseSets> {
    //    TODO Doesn't Work, FIX
    exerciseExerciseSets.forEach { it.sets.removeIf { set -> !set.setCompleted } }
    exerciseExerciseSets.filter { it.sets.isNotEmpty() }
    return exerciseExerciseSets.toList()
  }

  fun replaceExerciseInExerciseSets(
    exercisesSets: MutableList<ExerciseExerciseSets>,
    exerciseSets: ExerciseExerciseSets,
    newExercise: Exercise
  ): List<ExerciseExerciseSets> {
    val index = exercisesSets.indexOfFirst { it.exSetsUUID == exerciseSets.exSetsUUID }
    exerciseSets.exercise = newExercise
    for (set in exerciseSets.sets) {
      set.exerciseID = newExercise._id
    }
    exerciseSets.exSetsLazyListItemID = newID()

    exercisesSets[index] = exerciseSets

    return exercisesSets
  }

  fun addMultipleEmptyExerciseSets(
    exerciseSets: MutableList<ExerciseExerciseSets>,
    exercisesSelectedToBeAddedToWorkout: List<Exercise>,
    _workoutID: ObjectId
  ): MutableList<ExerciseExerciseSets> {
    exercisesSelectedToBeAddedToWorkout.forEach { _exercise ->
      val pinnedNotes = exerciseNotesRepository.getPinnedExerciseNotes(_exercise._id)
      exerciseSets.add(
        ExerciseExerciseSets().apply {
          exerciseOrder =
            if (exerciseSets.isEmpty()) 1 else exerciseSets.last().exerciseOrder.plus(1)
          exercise = _exercise
          notes = pinnedNotes.toRealmList()
          datePerformed = RealmInstant.now()
          workoutID = _workoutID
        }
      )
    }
    return exerciseSets
  }

  fun removeExerciseSets(
    exerciseExerciseSets: MutableList<ExerciseExerciseSets>,
    exerciseSets: ExerciseExerciseSets
  ): List<ExerciseExerciseSets> {
    return exerciseExerciseSets.filter { it != exerciseSets }
  }

  fun addSet(exerciseSets: ExerciseExerciseSets): List<Set> {
    val temporarySets: List<Set> =
      if (exerciseSets.sets.isNotEmpty()) {
        exerciseSets.sets +
                Set().apply {
                  exerciseID = exerciseSets.exercise?._id
                  setNumber = exerciseSets.sets.last().setNumber?.plus(1)
                  weightPounds = exerciseSets.sets.last().weightPounds
                  weightKilograms = exerciseSets.sets.last().weightKilograms
                  repetitions = exerciseSets.sets.last().repetitions
                  ratePerceivedEffort = exerciseSets.sets.last().ratePerceivedEffort
                  repetitionsInReserve = exerciseSets.sets.last().repetitionsInReserve
                  setTypeID = exerciseSets.sets.last().setTypeID
                  percentage1RM = exerciseSets.sets.last().percentage1RM
                  isPublic = false
                  owner_id = currentUser.id
                  targetWeightPounds = exerciseSets.sets.last().targetWeightPounds
                  targetWeightKilograms = exerciseSets.sets.last().targetWeightKilograms
                  targetRepetitions = exerciseSets.sets.last().targetRepetitions
                  targetRepetitionsInReserve = exerciseSets.sets.last().targetRepetitionsInReserve
                  targetRatePerceivedEffort = exerciseSets.sets.last().targetRatePerceivedEffort
                  progressionSchemeID = exerciseSets.sets.last().progressionSchemeID

                }
      } else {
        listOf(
          Set().apply {
            exerciseID = exerciseSets.exercise?._id
            setNumber = 0
            isPublic = false
            progressionSchemeID = "" // Random().nextInt() // TODO Replace with
            // progressionScheme.progressionSchemeID
            owner_id = currentUser.id
            isPublic = false
          }
        )
      }
    return temporarySets
    //    updateExercisesSetsData(exerciseSets, temporarySets)
  }

  fun removeSet(exerciseSets: ExerciseExerciseSets, set: Set): List<Set> {
    return exerciseSets.sets.minusElement(set)

    //    updateExercisesSetsData(exerciseSets, temporarySets)
  }

  fun updateExercisesSetsData(
    exerciseSets: ExerciseExerciseSets,
    temporarySets: List<Set>,
  ) {
    exerciseSets.sets = temporarySets.toRealmList()

    //    updateExerciseExerciseSetsData(exerciseSets)
  }

  fun addExerciseNote(exerciseSets: ExerciseExerciseSets, workoutID: ObjectId): List<ExerciseNote> {
    exerciseSets.notes.add(
      ExerciseNote().apply {
        exercise = exerciseSets.exercise?._id
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
    exerciseNote: ExerciseNote
  ): List<ExerciseNote> {
    return exerciseSets.notes.minusElement(exerciseNote)

    //    updateExercisesNotesData(exerciseSets, exerciseSets.notes)
    //    Log.i("removeExerciseNote", "removeExerciseNote: ${exerciseSets.notes.size}")
  }

  fun pinExerciseNote(
    exerciseSets: ExerciseExerciseSets,
    exerciseNote: ExerciseNote,
    pinned: Boolean
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
    exerciseSets.notes = temporaryNotes.toRealmList()

    //    updateExerciseExerciseSetsData(exerciseSets)
  }

  fun updateExerciseExerciseSetsData(
    exerciseExerciseSets: MutableList<ExerciseExerciseSets>,
    exerciseSets: ExerciseExerciseSets
  ): List<ExerciseExerciseSets> {
    val index = exerciseExerciseSets.indexOfFirst { it.exSetsUUID == exerciseSets.exSetsUUID }

    exerciseSets.exSetsLazyListItemID = newID()

    exerciseExerciseSets[index] =
      ExerciseExerciseSets().apply {
        exSetsUUID = exerciseSets.exSetsUUID
        exerciseOrder = exerciseSets.exerciseOrder
        exercise = exerciseSets.exercise
        sets = exerciseSets.sets
        notes = exerciseSets.notes
      }
    // _allSets.value = allSets().toList()
    return exerciseExerciseSets.toList()
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
    setUUID: RealmUUID,
    newWeight: Double?
  ): ExerciseExerciseSets {
    val weightUnit = exerciseSets.exercise?.weightUnit ?: userDefaultWeightUnit
    exerciseSets.updateSetWeight(uuid = setUUID, newWeight = newWeight, weightUnit = weightUnit)
    return exerciseSets
    //    updateExerciseExerciseSetsData(exerciseSets)
  }

  fun updateSetReps(
    exerciseSets: ExerciseExerciseSets,
    setUUID: RealmUUID,
    newReps: Int?
  ): ExerciseExerciseSets {
    exerciseSets.updateSetReps(uuid = setUUID, newReps = newReps)
    return exerciseSets
    //    updateExerciseExerciseSetsData(exerciseSets)
  }

  fun updateSetRIR(
    exerciseSets: ExerciseExerciseSets,
    setUUID: RealmUUID,
    newRIR: Double?
  ): ExerciseExerciseSets {
    exerciseSets.updateSetRIR(uuid = setUUID, newRIR = newRIR)
    return exerciseSets
    //    updateExerciseExerciseSetsData(exerciseSets)
  }

  fun updateSetRPE(
    exerciseSets: ExerciseExerciseSets,
    setUUID: RealmUUID,
    newRPE: Double?
  ): ExerciseExerciseSets {
    exerciseSets.updateSetRPE(uuid = setUUID, newRPE = newRPE)
    return exerciseSets
    //    updateExerciseExerciseSetsData(exerciseSets)
  }

  fun setComplete(
    exerciseSets: ExerciseExerciseSets,
    setUUID: RealmUUID,
    setComplete: Boolean
  ): ExerciseExerciseSets {
    exerciseSets.completeSet(setUUID, setComplete)
    return exerciseSets
    //    updateExerciseExerciseSetsData(exerciseSets)
  }

  fun updateSetType(
    exerciseSets: ExerciseExerciseSets,
    setUUID: RealmUUID,
    setTypeID: Int
  ): ExerciseExerciseSets {
    exerciseSets.updateSetType(uuid = setUUID, setTypeID = setTypeID)
    return exerciseSets
    //    updateExerciseExerciseSetsData(exerciseSets)
  }

  fun getWeightUnit(exerciseSets: ExerciseExerciseSets): String {
    return if (exerciseSets.exercise?.weightUnit != null) {
      exerciseSets.exercise?.weightUnit ?: MassUnit.POUNDS.abbreviation
    } else {
      userDefaultWeightUnit
    }
  }
}

class ManageExercisesUseCase(
  private val exercisesRepository: ExercisesRepository,
) {
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

  fun getExerciseByName(exerciseName: String): ObjectId {
    return exercisesRepository.getExerciseByName(exerciseName)._id
  }
}


