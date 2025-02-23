@file:OptIn(ExperimentalUuidApi::class, ExperimentalUuidApi::class)

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
import kotlinx.datetime.Clock
import org.bson.types.ObjectId
import kotlin.time.Duration
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class ManageWorkoutUseCase(
  private val workoutRepository: WorkoutRepository,
  //  private val setsRepository: SetsRepository,
  //  private val exerciseSetsRepository: ExerciseSetsRepository,
  private val exercisesRepository: ExercisesRepository,
  private val workoutNotesRepository: WorkoutNotesRepository,
  private val exerciseNotesRepository: ExerciseNotesRepository,
  userRepository: UserRepository,
) {

  // TODO rewrite
  //  val currentUser = userRepository.getCurrentUser()
  //  val userDefaultWeightUnit: String =
  //    userRepository.getCurrentUser().customDataAsBsonDocument()?.getString("weightUnit")?.value
  //      ?: MassUnit.POUNDS.abbreviation

  private val tempOwnerId = ObjectId().toString()
  val userDefaultWeightUnit: String = MassUnit.POUNDS.abbreviation

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
      Workout(
        id = workoutTemplate.id,
        uuid = workoutTemplate.uuid,
        name = workoutTemplate.name,
        workoutAuthor = workoutTemplate.workoutAuthor,
        //        notes = workoutTemplate.notes.filter { it.pinned }.toMutableList(),
        workoutType = workoutTemplate.workoutType,
        dateCreated = workoutTemplate.dateCreated,
        workoutStarted = Clock.System.now(),
        day = workoutTemplate.day,
        //        exercisesSets =,
        // copyExercisesSetsFromPreviousWorkout(workoutTemplate.exercisesSets),
        microcycleID = workoutTemplate.microcycleID,
        programID = workoutTemplate.programID,
        ownerID = tempOwnerId, // TODO Replace currentUser.id
        isPublic = false,
      )

    return newWorkout
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
        //        exercisesSets =,
        // copyExercisesSetsFromPreviousWorkout(previousWorkout.exercisesSets),
        microcycleID = previousWorkout.microcycleID,
      )
    return newWorkout
  }

  fun getPinnedWorkoutNotes(workoutUUID: Uuid, workoutID: ObjectId): MutableList<WorkoutNote> {
    return workoutNotesRepository.getPinnedWorkoutNotes(workoutUUID).toMutableList()
  }

  fun copyExercisesSetsFromPreviousWorkout(
    previousExercisesSets: List<ExerciseSets>,
    _workoutID: ObjectId,
  ): MutableList<ExerciseExerciseSets> {
    val newExercisesSets = mutableListOf<ExerciseExerciseSets>()
    previousExercisesSets.forEach { exerciseSets ->
      // TODO get exercise by ID
      val exerciseSetsID = ObjectId()

      val exercise = exercisesRepository.getExercise(exerciseSets.exerciseID)
      requireNotNull(exercise) { "Exercise not found." }

      val newExerciseExerciseSets =
        ExerciseExerciseSets(
          id = exerciseSetsID,
          exerciseOrder = exerciseSets.exerciseOrder,
          exercise = exercise,
          sets = copySetsFromPreviousWorkout(exerciseSets.sets, exerciseSetsID, _workoutID),
          notes = exerciseSets.notes.filter { it.pinned }.toMutableList(),
          workoutID = _workoutID,
//          exerciseID = exerciseSets.exerciseID,
          microcycleID = exerciseSets.microcycleID,
          mesocycleID = exerciseSets.mesocycleID,
          programID = exerciseSets.programID,
          isPublic = false,
          ownerID = exerciseSets.ownerID, // TODO Replace currentUser.id
        )

      exerciseSets.exerciseID.let {
        newExerciseExerciseSets.notes =
          exerciseNotesRepository.getPinnedExerciseNotes(it).toMutableList()
      }

      newExercisesSets.add(newExerciseExerciseSets)
    }
    return newExercisesSets.sortedBy { it.exerciseOrder }.toMutableList()
  }

  fun copySetsFromPreviousWorkout(
    previousSets: List<Set>,
    currentExerciseSetsID: ObjectId,
    currentWorkoutID: ObjectId,
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
          targetRepetitions = set.targetRepetitions,
          targetRatePerceivedEffort = set.targetRatePerceivedEffort,
          targetRepetitionsInReserve = set.targetRepetitionsInReserve,
          percentage1RM = set.percentage1RM,
          setTypeID = set.setTypeID,
          progressionSchemeID = set.progressionSchemeID,
          exerciseSetsID = currentExerciseSetsID.toString(),
          workoutID = currentWorkoutID.toString(),
          exerciseID = set.exerciseID,
          ownerID = tempOwnerId, // TODO Replace currentUser.id
        )
      newSets.add(newSet)
      if (set.setNumber != null) {
        val previousSet =
          workoutRepository.getPreviousSetForExercise(set.exerciseID!!, set.setNumber!!)
        previousSet?.let { latestPreviousSets.add(it) }
      }
    }
    return newSets
  }

  fun loadExercisesSetsFromWorkout(
    previousExercisesSets: List<ExerciseSets>
  ): MutableList<ExerciseExerciseSets> {
    val newExercisesSets = mutableListOf<ExerciseExerciseSets>()
    previousExercisesSets.forEach { exerciseSets ->
      val exercise = exercisesRepository.getExercise(exerciseSets.exerciseID)

      requireNotNull(exercise) { "Exercise not found." }

      val newExerciseExerciseSets =
        ExerciseExerciseSets(
          id = exerciseSets.id,
          exSetsUUID = exerciseSets.exSetsUUID,
          workoutID = exerciseSets.workoutID,
          exerciseOrder = exerciseSets.exerciseOrder,
          //          exerciseID = exerciseSets.exerciseID,
          exercise = exercise,
          sets = loadSetsFromWorkout(exerciseSets.sets),
          notes = exerciseSets.notes,
          datePerformed = exerciseSets.datePerformed,
          microcycleID = exerciseSets.microcycleID,
          mesocycleID = exerciseSets.mesocycleID,
          programID = exerciseSets.programID,
          ownerID = exerciseSets.ownerID,
        )

      newExercisesSets.add(newExerciseExerciseSets)
    }
    return newExercisesSets.sortedBy { it.exerciseOrder }.toMutableList()
  }

  fun loadSetsFromWorkout(previousSets: List<Set>): MutableList<Set> {
    val newSets = mutableListOf<Set>()
    val latestPreviousSets = mutableListOf<Set>()
    previousSets.forEach { set ->
      val newSet =
        Set(
          id = set.id,
          uuid = set.uuid,
          setNumber = set.setNumber,
          weightPounds = set.weightPounds,
          weightKilograms = set.weightKilograms,
          repetitions = set.repetitions,
          ratePerceivedEffort = set.ratePerceivedEffort,
          repetitionsInReserve = set.repetitionsInReserve,
          targetWeightPounds = set.targetWeightPounds,
          targetRepetitions = set.targetRepetitions,
          targetRatePerceivedEffort = set.targetRatePerceivedEffort,
          targetRepetitionsInReserve = set.targetRepetitionsInReserve,
          percentage1RM = set.percentage1RM,
          setCompleted = set.setCompleted,
          setTypeID = set.setTypeID,
          progressionSchemeID = set.progressionSchemeID,
          workoutID = set.workoutID,
          exerciseSetsID = set.exerciseSetsID,
          exerciseID = set.exerciseID,
          ownerID = tempOwnerId, // TODO Replace currentUser.id
          isPublic = false,
        )
      newSets.add(newSet)
      if (set.setNumber != null) {
        val previousSet =
          workoutRepository.getPreviousSetForExercise(set.exerciseID, set.setNumber!!)
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
    _workoutNotes.first { it == note }.lazyListID = newID()
    return _workoutNotes
  }

  suspend fun saveWorkout(
    workout: Workout,
    workoutNotes: List<WorkoutNote>,
    exercisesSets: List<ExerciseExerciseSets>,
  ) {
    //    TODO Implement this
    workout.notes = workoutNotes.toMutableList()

    //    TODO Only Add Completed Sets, Implement Check ond Remove Uncompleted Sets
    //    removeUncompletedSets(exercisesSets.toMutableList())

    workout.exercisesSets =
      convertExerciseExerciseSetsToExerciseSets(
        exercisesSets.toMutableList(),
        userDefaultWeightUnit,
      )

    workout.calculateTotalVolume(userDefaultWeightUnit = userDefaultWeightUnit)
    //    workout.calculateTotalVolumeInPounds()
    //    workout.calculateTotalVolumeInKilograms()

    workoutRepository.insertWorkout(workout)
  }

  private fun convertExerciseExerciseSetsToExerciseSets(
    exerciseExerciseSets: List<ExerciseExerciseSets>,
    userDefaultWeightUnit: String,
  ): List<ExerciseSets> {
    val exercisesSets = mutableListOf<ExerciseSets>()
    exerciseExerciseSets.forEach { _exerciseSets ->
      exercisesSets.add(
        ExerciseSets(
          id = _exerciseSets.id,
          exSetsUUID = _exerciseSets.exSetsUUID,
          workoutID = _exerciseSets.workoutID,
          exerciseOrder = _exerciseSets.exerciseOrder,
          exerciseID = _exerciseSets.exercise.id,
          sets = _exerciseSets.sets,
          notes = _exerciseSets.notes,
          datePerformed = _exerciseSets.datePerformed,
          totalVolumeInPounds = _exerciseSets.totalVolumeInPounds(),
          totalVolumeInKilograms = _exerciseSets.totalVolumeInKilograms(),
          isPublic = false,
          microcycleID = _exerciseSets.microcycleID,
          mesocycleID = _exerciseSets.mesocycleID,
          programID = _exerciseSets.programID,
          ownerID = tempOwnerId, // TODO Replace currentUser.id
        )
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
    newExercise: Exercise,
  ): List<ExerciseExerciseSets> {
    val index = exercisesSets.indexOfFirst { it.exSetsUUID == exerciseSets.exSetsUUID }
    exerciseSets.exercise = newExercise
    for (set in exerciseSets.sets) {
      set.exerciseID = newExercise.id
    }
    exerciseSets.exSetsLazyListItemID = newID()

    exercisesSets[index] = exerciseSets

    return exercisesSets
  }

  fun addMultipleEmptyExerciseSets(
    exerciseSets: MutableList<ExerciseExerciseSets>,
    exercisesSelectedToBeAddedToWorkout: List<Exercise>,
    _workoutID: ObjectId,
  ): MutableList<ExerciseExerciseSets> {
    exercisesSelectedToBeAddedToWorkout.forEach { _exercise ->
      val pinnedNotes = exerciseNotesRepository.getPinnedExerciseNotes(_exercise.id)
      exerciseSets.add(
        ExerciseExerciseSets(
          exerciseOrder =
          if (exerciseSets.isEmpty()) 1 else exerciseSets.last().exerciseOrder.plus(1),
          exercise = _exercise,
          notes = pinnedNotes.toMutableList(),
          datePerformed = Clock.System.now(),
          workoutID = _workoutID,
          microcycleID = null, // TODO get details from workout
          mesocycleID = null, // TODO get details from workout
          programID = null, // TODO get details from workout
          ownerID = tempOwnerId, // TODO Replace currentUser.id
          isPublic = false,
        )
      )
    }
    return exerciseSets
  }

  fun removeExerciseSets(
    exerciseExerciseSets: MutableList<ExerciseExerciseSets>,
    exerciseSets: ExerciseExerciseSets,
  ): List<ExerciseExerciseSets> {
    return exerciseExerciseSets.filter { it != exerciseSets }
  }

  fun addSet(exerciseSets: ExerciseExerciseSets): List<Set> {
    val temporarySets: List<Set> =
      if (exerciseSets.sets.isNotEmpty()) {
        exerciseSets.sets +
                Set(
                  exerciseID = exerciseSets.exercise.id,
                  setNumber = exerciseSets.sets.last().setNumber?.plus(1),
                  weightPounds = exerciseSets.sets.last().weightPounds,
                  weightKilograms = exerciseSets.sets.last().weightKilograms,
                  repetitions = exerciseSets.sets.last().repetitions,
                  ratePerceivedEffort = exerciseSets.sets.last().ratePerceivedEffort,
                  repetitionsInReserve = exerciseSets.sets.last().repetitionsInReserve,
                  setTypeID = exerciseSets.sets.last().setTypeID,
                  percentage1RM = exerciseSets.sets.last().percentage1RM,
                  isPublic = false,
                  ownerID = tempOwnerId, // TODO Replace currentUser.id
                  targetWeightPounds = exerciseSets.sets.last().targetWeightPounds,
                  targetWeightKilograms = exerciseSets.sets.last().targetWeightKilograms,
                  targetRepetitions = exerciseSets.sets.last().targetRepetitions,
                  targetRepetitionsInReserve = exerciseSets.sets.last().targetRepetitionsInReserve,
                  targetRatePerceivedEffort = exerciseSets.sets.last().targetRatePerceivedEffort,
                  progressionSchemeID = exerciseSets.sets.last().progressionSchemeID,
                  workoutID = exerciseSets.sets.last().workoutID,
                  exerciseSetsID = exerciseSets.sets.last().exerciseSetsID,
                )
      } else {
        listOf(
          Set(
            exerciseID = exerciseSets.exercise.id,
            workoutID = exerciseSets.workoutID.toString(),
            exerciseSetsID = exerciseSets.id.toString(),
            setNumber = 0,
            isPublic = false,
            progressionSchemeID = "", // Random().nextInt() // TODO Replace with,
            // progressionScheme.progressionSchemeID,
            ownerID = tempOwnerId, // TODO Replace currentUser.id,
          )
        )
      }
    return temporarySets
    //    updateExercisesSetsData(exerciseSets, temporarySets)
  }

  fun removeSet(exerciseSets: ExerciseExerciseSets, set: Set): List<Set> {
    return exerciseSets.sets.minusElement(set)

    //    updateExercisesSetsData(exerciseSets, temporarySets)
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

  fun updateExerciseExerciseSetsData(
    exerciseExerciseSets: MutableList<ExerciseExerciseSets>,
    exerciseSets: ExerciseExerciseSets,
  ): List<ExerciseExerciseSets> {
    val index = exerciseExerciseSets.indexOfFirst { it.exSetsUUID == exerciseSets.exSetsUUID }

    exerciseSets.exSetsLazyListItemID = newID()

    exerciseExerciseSets[index] =
      ExerciseExerciseSets(
        exSetsUUID = exerciseSets.exSetsUUID,
        exerciseOrder = exerciseSets.exerciseOrder,
        exercise = exerciseSets.exercise,
        sets = exerciseSets.sets,
        notes = exerciseSets.notes,
        workoutID = exerciseSets.workoutID,
        //        exerciseID = exerciseSets.exerciseID,
        microcycleID = exerciseSets.microcycleID,
        mesocycleID = exerciseSets.mesocycleID,
        programID = exerciseSets.programID,
        ownerID = exerciseSets.ownerID,
      )
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
    setUUID: Uuid,
    newWeight: Double?,
  ): ExerciseExerciseSets {
    val weightUnit = exerciseSets.exercise?.weightUnit ?: userDefaultWeightUnit
    exerciseSets.updateSetWeight(uuid = setUUID, newWeight = newWeight, weightUnit = weightUnit)
    return exerciseSets
    //    updateExerciseExerciseSetsData(exerciseSets)
  }

  fun updateSetReps(
    exerciseSets: ExerciseExerciseSets,
    setUUID: Uuid,
    newReps: Int?,
  ): ExerciseExerciseSets {
    exerciseSets.updateSetReps(uuid = setUUID, newReps = newReps)
    return exerciseSets
    //    updateExerciseExerciseSetsData(exerciseSets)
  }

  fun updateSetRIR(
    exerciseSets: ExerciseExerciseSets,
    setUUID: Uuid,
    newRIR: Double?,
  ): ExerciseExerciseSets {
    exerciseSets.updateSetRIR(uuid = setUUID, newRIR = newRIR)
    return exerciseSets
    //    updateExerciseExerciseSetsData(exerciseSets)
  }

  fun updateSetRPE(
    exerciseSets: ExerciseExerciseSets,
    setUUID: Uuid,
    newRPE: Double?,
  ): ExerciseExerciseSets {
    exerciseSets.updateSetRPE(uuid = setUUID, newRPE = newRPE)
    return exerciseSets
    //    updateExerciseExerciseSetsData(exerciseSets)
  }

  fun setComplete(
    exerciseSets: ExerciseExerciseSets,
    setUUID: Uuid,
    setComplete: Boolean,
  ): ExerciseExerciseSets {
    exerciseSets.completeSet(setUUID, setComplete)
    return exerciseSets
    //    updateExerciseExerciseSetsData(exerciseSets)
  }

  fun updateSetType(
    exerciseSets: ExerciseExerciseSets,
    setUUID: Uuid,
    setTypeID: Int,
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
