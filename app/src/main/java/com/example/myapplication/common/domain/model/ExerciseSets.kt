@file:UseSerializers(InstantSerializer::class, UuidSerializer::class, ObjectIdSerializer::class)
@file:OptIn(ExperimentalUuidApi::class, ExperimentalTime::class)

package com.example.myapplication.common.domain.model

import android.util.Log
import com.example.myapplication.common.util.InstantSerializer
import com.example.myapplication.common.util.ObjectIdSerializer
import com.example.myapplication.common.util.UuidSerializer
import com.example.myapplication.common.util.newLazyListItemID
import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import org.bson.types.ObjectId
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

/*
 TODO:
    @Query(SELECT * FROM sets WHERE workoutID:=workoutID) return ExerciseSets
    @Query(SELECT * FROM sets WHERE exerciseID:=exerciseID) return WorkoutSets
*/

@Serializable
data class ExerciseSetsDao(val ExerciseSets: ExerciseSets)

/**
 * Represents a collection of sets for a specific exercise, linked by exercise ID. This class
 * focuses on storing set data and linking it to hierarchical structures (workout, microcycle,
 * mesocycle, program) via ObjectIds. It's a more normalized representation, requiring a separate
 * lookup to retrieve full Exercise details.
 *
 * @property id The unique identifier for this set collection (using ObjectId).
 * @property lazyListItemID An ID used for efficiently updating items in a LazyColumn (UI
 *   component).
 * @property exSetsUUID A universally unique identifier for this set collection.
 * @property order The order of this set collection within a sequence (e.g., within a workout).
 * @property exerciseID The ID of the exercise this set collection belongs to (foreign key).
 * @property sets A mutable list of [Set] objects, representing the individual sets performed.
 * @property notes A mutable list of [ExerciseNote] objects, for any notes related to the sets.
 * @property datePerformed The date and time when the exercise sets were performed (nullable,
 *   defaults to null).
 * @property totalVolumeInPounds The total volume lifted in pounds across all sets (nullable,
 *   defaults to 0.0).
 * @property totalVolumeInKilograms The total volume lifted in kilograms across all sets (nullable,
 *   defaults to 0.0).
 * @property ownerID The ID of the user who owns this set collection.
 * @property isPublic Indicates whether this set collection is publicly visible.
 * @property workoutID The ID of the workout this set collection belongs to (foreign key).
 * @property microcycleID The ID of the microcycle this set collection belongs to (nullable, foreign
 *   key).
 * @property mesocycleID The ID of the mesocycle this set collection belongs to (nullable, foreign
 *   key).
 * @property programID The ID of the program this set collection belongs to (nullable, foreign key).
 */
@ExperimentalUuidApi
@Serializable
data class ExerciseSets(
  @SerialName("id") override var id: ObjectId = ObjectId(),
  @SerialName("exSetsLazyListItemID")
  val lazyListItemID: Long = newLazyListItemID(), // Used for LazyColumn
  @Serializable(with = UuidSerializer::class) val exSetsUUID: Uuid = Uuid.random(),
  override val order: Int = 1,
  val exerciseID: ObjectId,
  var sets: MutableList<Set> = mutableListOf(),
  var description: String? = null,
  val notes: MutableList<ExerciseNote> = mutableListOf(),
  val datePerformed: Instant? = null,
  val totalVolumeInPounds: Double? = 0.0,
  val totalVolumeInKilograms: Double? = 0.0,
  override var ownerID: String,
  override var isPublic: Boolean = false,
  override val type: String = "ExerciseSets",
  var workoutID: ObjectId, // External Primary Key
  var microcycleID: ObjectId?,
  var mesocycleID: ObjectId?,
  var programID: ObjectId?,
) : WorkoutItem()

/**
 * Represents a collection of sets along with the *embedded* Exercise object itself. This class
 * provides a richer, self-contained representation, including both set data and the complete
 * exercise details. It's denormalized for easier access without requiring extra lookups.
 *
 * @property id The unique identifier for this set and exercise combination (using ObjectId).
 * @property lazyListItemID An ID used for efficiently updating items in a LazyColumn (UI
 *   component). This is mutable.
 * @property exSetsUUID A universally unique identifier for this set and exercise combination. This
 *   is mutable.
 * @property ownerID The ID of the user who owns this set and exercise combination.
 * @property isPublic Indicates whether this set and exercise combination is publicly visible.
 * @property order The order of this set and exercise combination within a sequence (e.g., within a
 *   workout). This is mutable.
 * @property exercise The full [Exercise] object, embedded directly within this class. This is
 *   mutable.
 * @property sets A mutable list of [Set] objects, representing the individual sets performed. This
 *   is mutable.
 * @property notes A mutable list of [ExerciseNote] objects, for any notes related to the sets. This
 *   is mutable.
 * @property datePerformed The date and time when the exercise sets were performed (defaults to the
 *   current time). This is mutable.
 * @property workoutID The ID of the workout this set and exercise combination belongs to. This is
 *   mutable.
 * @property microcycleID The ID of the microcycle this set and exercise belongs to (nullable). This
 *   is mutable.
 * @property mesocycleID The ID of the mesocycle this set and exercise belongs to (nullable). This
 *   is mutable.
 * @property programID The ID of the program this set and exercise belongs to (nullable). This is
 *   mutable.
 */
@OptIn(ExperimentalUuidApi::class)
@Serializable
data class ExerciseExerciseSets(
  @SerialName("id") @Serializable(ObjectIdSerializer::class) override val id: ObjectId = ObjectId(),
  override val lazyListItemID: Long = newLazyListItemID(), // Used for LazyColumn
  @Serializable(UuidSerializer::class) var exSetsUUID: Uuid = Uuid.random(),
  override val ownerID: String,
  override val isPublic: Boolean = false,
  override var order: Int = 1,
  var exercise: Exercise,
  var sets: MutableList<Set> = mutableListOf(),
  var description: String? = null,
  var notes: MutableList<ExerciseNote> = mutableListOf(),
  @Serializable(InstantSerializer::class) var datePerformed: Instant? = Clock.System.now(),
  //  @Contextual var exerciseID: ObjectId,
  @Serializable(ObjectIdSerializer::class) var workoutID: ObjectId, // TODO May be useless
  @Serializable(ObjectIdSerializer::class) var microcycleID: ObjectId?,
  @Serializable(ObjectIdSerializer::class) var mesocycleID: ObjectId?,
  @Serializable(ObjectIdSerializer::class) var programID: ObjectId?,
  override val type: String = "ExerciseExerciseSets",
) : WorkoutComponent()

/*fun ExerciseSets.addSet() {
if (sets.isNotEmpty()) {
    sets.add(
        sets.last().copy(
            lazyListItemID = newID(),
            setNumber = sets.last().setNumber?.plus(1),
            setCompleted = false
        )
    )
} else {
    sets.add(
        SetModel(
            exerciseID = exercise.exerciseID,
            workoutID = fakeULID() */
/* TODO change in workout.primaryKey*/
/*
            )
        )
    }

    Log.i("workoutViewModel.addSet()", "${sets.size}")
}

fun ExerciseSets.removeSet(uuid: RealmUUID) {
    sets.removeIf { it.uuid == uuid }
}*/

@OptIn(ExperimentalUuidApi::class)
fun ExerciseExerciseSets.updateSetWeight(uuid: Uuid, newWeight: Double?, weightUnit: String) {
  if (weightUnit == MassUnit.POUNDS.abbreviation) {
    sets.forEach {
      if (it.uuid == uuid) {
        it.weightPounds = newWeight
        it.weightKilograms = it.convertPoundToKg()
      }
    }
  } else {
    sets.forEach {
      if (it.uuid == uuid) {
        it.weightKilograms = newWeight
        it.weightPounds = it.convertKgToPound()
      }
    }
  }
}

@OptIn(ExperimentalUuidApi::class)
fun ExerciseExerciseSets.updateSetReps(uuid: Uuid, newReps: Int?) {
  sets.forEach { if (it.uuid == uuid) it.repetitions = newReps }
}

@OptIn(ExperimentalUuidApi::class)
fun ExerciseExerciseSets.updateSetRIR(uuid: Uuid, newRIR: Double?) {
  sets.forEach { if (it.uuid == uuid) it.repetitionsInReserve = newRIR }
}

@OptIn(ExperimentalUuidApi::class)
fun ExerciseExerciseSets.updateSetRPE(uuid: Uuid, newRPE: Double?) {
  sets.forEach { if (it.uuid == uuid) it.ratePerceivedEffort = newRPE }
}

@OptIn(ExperimentalUuidApi::class)
fun ExerciseExerciseSets.completeSet(uuid: Uuid, completed: Boolean) {
  sets.forEach { if (it.uuid == uuid) it.setCompleted = completed }
}

@OptIn(ExperimentalUuidApi::class)
fun ExerciseExerciseSets.updateSetType(uuid: Uuid, setType: SetType) {
  sets.forEach { if (it.uuid == uuid) it.setTypeID = setType }
}

fun ExerciseExerciseSets.addWarmUpSets(warmUpSets: List<Set>) {
  val TAG = "ExerciseExerciseSets"
  // Log initial state
  Log.d(
    TAG,
    "addWarmUpSets called. Adding ${warmUpSets.size} warm-up sets to ${this.sets.size} existing sets.",
  )

  // 1. Map the incoming warmUpSets to new Set objects with correct context IDs.
  val newWarmUpSets =
    warmUpSets.map { warmUpSet ->
      Set(
        //      setNumber = -1, // Placeholder, will be replaced
        weightPounds = warmUpSet.weightPounds,
        targetWeightPounds = warmUpSet.targetWeightPounds,
        weightKilograms = warmUpSet.weightKilograms,
        targetWeightKilograms = warmUpSet.targetWeightKilograms,
        repetitions = warmUpSet.repetitions,
        targetRepetitions = warmUpSet.targetRepetitions,
        ratePerceivedEffort = warmUpSet.ratePerceivedEffort,
        targetRatePerceivedEffort = warmUpSet.targetRatePerceivedEffort,
        repetitionsInReserve = warmUpSet.repetitionsInReserve,
        targetRepetitionsInReserve = warmUpSet.targetRepetitionsInReserve,
        setTypeID = warmUpSet.setTypeID,
        percentage1RM = warmUpSet.percentage1RM,
        targetPercentage1RM = warmUpSet.targetPercentage1RM,
        exerciseID = this@addWarmUpSets.exercise.id,
        progressionSchemeID = warmUpSet.progressionSchemeID,
        workoutID = this@addWarmUpSets.workoutID,
        exerciseSetsID = this@addWarmUpSets.id,
        microcycleID = this@addWarmUpSets.microcycleID,
        mesocycleID = this@addWarmUpSets.mesocycleID,
        programID = this@addWarmUpSets.programID,
        ownerID = this@addWarmUpSets.ownerID,
        isPublic = this@addWarmUpSets.isPublic,
      )
    }
  Log.d(TAG, "Mapped ${newWarmUpSets.size} warm-up sets with new context IDs.")

  // 2. Combine the new warm-up sets and existing sets into a single list.
  val allSets = newWarmUpSets + this.sets
  Log.d(TAG, "Combined lists. Total sets to process: ${allSets.size}.")

  // 3. Create the final list by re-numbering every set based on its new index.
  val renumberedSets =
    allSets.mapIndexed { index, set ->
      val newSetNumber = index + 1
      // Log the change for a specific set (can be verbose, but useful for deep debugging)
      // To avoid spamming logs, you might enable this only when needed.
      // Log.d(TAG, "Re-numbering set: Old number ${set.setNumber}, New number: $newSetNumber")
      set.copy(setNumber = newSetNumber)
    }

  // 4. Replace the old list with the newly created and re-numbered list.
  val previousCount = this.sets.size
  this.sets.clear()
  this.sets.addAll(renumberedSets)
}

fun ExerciseExerciseSets.replaceWarmUpSets(warmUpSets: List<Set>) {
  val TAG = "ExerciseExerciseSets"
  // Log initial state
  Log.d(TAG, "replaceWarmUpSets called. Replacing warm-up sets to ${this.sets.size} existing sets.")

  // 1. Map the incoming warmUpSets to new Set objects with correct context IDs.
  val newWarmUpSets =
    warmUpSets.map { warmUpSet ->
      Set(
        lazyListItemID = newLazyListItemID(),
        //      setNumber = -1, // Placeholder, will be replaced
        weightPounds = warmUpSet.weightPounds,
        targetWeightPounds = warmUpSet.targetWeightPounds,
        weightKilograms = warmUpSet.weightKilograms,
        targetWeightKilograms = warmUpSet.targetWeightKilograms,
        repetitions = warmUpSet.repetitions,
        targetRepetitions = warmUpSet.targetRepetitions,
        ratePerceivedEffort = warmUpSet.ratePerceivedEffort,
        targetRatePerceivedEffort = warmUpSet.targetRatePerceivedEffort,
        repetitionsInReserve = warmUpSet.repetitionsInReserve,
        targetRepetitionsInReserve = warmUpSet.targetRepetitionsInReserve,
        setTypeID = warmUpSet.setTypeID,
        percentage1RM = warmUpSet.percentage1RM,
        targetPercentage1RM = warmUpSet.targetPercentage1RM,
        exerciseID = this@replaceWarmUpSets.exercise.id,
        progressionSchemeID = warmUpSet.progressionSchemeID,
        workoutID = this@replaceWarmUpSets.workoutID,
        exerciseSetsID = this@replaceWarmUpSets.id,
        microcycleID = this@replaceWarmUpSets.microcycleID,
        mesocycleID = this@replaceWarmUpSets.mesocycleID,
        programID = this@replaceWarmUpSets.programID,
        ownerID = this@replaceWarmUpSets.ownerID,
        isPublic = this@replaceWarmUpSets.isPublic,
      )
    }
  Log.d(TAG, "replaceWarmUpSets: set.id - ${newWarmUpSets.firstOrNull()?.id}.")
  Log.d(
    TAG,
    "replaceWarmUpSets: set.targetWeightPounds - ${newWarmUpSets.firstOrNull()?.targetWeightPounds}.",
  )
  Log.d(TAG, "Mapped ${newWarmUpSets.size} warm-up sets with new context IDs.")

  // 2. Combine the new warm-up sets and existing sets into a single list.
  val allSets = newWarmUpSets + this.sets.filter { it.setTypeID != WARM_UP_SET }
  Log.d(TAG, "Combined lists. Total sets to process: ${allSets.size}.")

  // 3. Create the final list by re-numbering every set based on its new index.
  val renumberedSets =
    allSets.mapIndexed { index, set ->
      val newSetNumber = index + 1
      // Log the change for a specific set (can be verbose, but useful for deep debugging)
      // To avoid spamming logs, you might enable this only when needed.
      // Log.d(TAG, "Re-numbering set: Old number ${set.setNumber}, New number: $newSetNumber")
      set.copy(setNumber = newSetNumber)
    }

  // 4. Replace the old list with the newly created and re-numbered list.
  val previousCount = this.sets.size
  this.sets.clear()
  this.sets.addAll(renumberedSets)
}

fun ExerciseExerciseSets.replaceWorkingSets(newSets: List<Set>) {
  val TAG = "ExerciseExerciseSets"
  // Log initial state
  Log.d(
    TAG,
    "replaceWorkingSets called. Replacing warm-up sets to ${this.sets.size} existing sets.",
  )

  // 1. Map the incoming warmUpSets to new Set objects with correct context IDs.
  val newWorkingSets =
    newSets.map { workingSet ->
      Set(
        lazyListItemID = newLazyListItemID(),
        //      setNumber = -1, // Placeholder, will be replaced
        weightPounds = workingSet.weightPounds,
        targetWeightPounds = workingSet.targetWeightPounds,
        weightKilograms = workingSet.weightKilograms,
        targetWeightKilograms = workingSet.targetWeightKilograms,
        repetitions = workingSet.repetitions,
        targetRepetitions = workingSet.targetRepetitions,
        ratePerceivedEffort = workingSet.ratePerceivedEffort,
        targetRatePerceivedEffort = workingSet.targetRatePerceivedEffort,
        repetitionsInReserve = workingSet.repetitionsInReserve,
        targetRepetitionsInReserve = workingSet.targetRepetitionsInReserve,
        setTypeID = workingSet.setTypeID,
        percentage1RM = workingSet.percentage1RM,
        targetPercentage1RM = workingSet.targetPercentage1RM,
        exerciseID = this@replaceWorkingSets.exercise.id,
        progressionSchemeID = workingSet.progressionSchemeID,
        workoutID = this@replaceWorkingSets.workoutID,
        exerciseSetsID = this@replaceWorkingSets.id,
        microcycleID = this@replaceWorkingSets.microcycleID,
        mesocycleID = this@replaceWorkingSets.mesocycleID,
        programID = this@replaceWorkingSets.programID,
        ownerID = this@replaceWorkingSets.ownerID,
        isPublic = this@replaceWorkingSets.isPublic,
      )
    }
  Log.d(TAG, "replaceWorkingSets: set.id - ${newWorkingSets.firstOrNull()?.id}.")
  Log.d(
    TAG,
    "replaceWorkingSets: set.targetWeightPounds - ${newWorkingSets.firstOrNull()?.targetWeightPounds}.",
  )
  Log.d(TAG, "Mapped ${newWorkingSets.size} working sets with new context IDs.")

  // 2. Combine the new warm-up sets and existing sets into a single list.
  val allSets = this.sets.filter { it.setTypeID == WARM_UP_SET } + newWorkingSets
  Log.d(TAG, "Combined lists. Total sets to process: ${allSets.size}.")

  // 3. Create the final list by re-numbering every set based on its new index.
  val renumberedSets =
    allSets.mapIndexed { index, set ->
      val newSetNumber = index + 1
      // Log the change for a specific set (can be verbose, but useful for deep debugging)
      // To avoid spamming logs, you might enable this only when needed.
      // Log.d(TAG, "Re-numbering set: Old number ${set.setNumber}, New number: $newSetNumber")
      set.copy(setNumber = newSetNumber)
    }

  // 4. Replace the old list with the newly created and re-numbered list.
  val previousCount = this.sets.size
  this.sets.clear()
  this.sets.addAll(renumberedSets)
}

private val WARM_UP_SET = SetType.warmUpSet

fun ExerciseExerciseSets.totalWorkingSetVolume(weightUnit: String): Double {
  var totalVolume = 0.0

  sets.filter { it.setTypeID != WARM_UP_SET }.forEach { totalVolume += (it.setVolume(weightUnit)) }

  return totalVolume
}

fun ExerciseExerciseSets.calculateTotalVolume(userDefaultWeightUnit: String): Double {
  var totalVolume = 0.0
  val weightUnit = exercise.weightUnit ?: userDefaultWeightUnit

  sets.forEach { totalVolume += it.setVolume(weightUnit) }

  return totalVolume
}

fun ExerciseExerciseSets.totalVolumeInPounds(): Double {
  var totalVolume = 0.0
  sets.forEach { totalVolume += it.setVolume(MassUnit.POUNDS.abbreviation) }
  return totalVolume
}

fun ExerciseExerciseSets.totalVolumeInKilograms(): Double {
  var totalVolume = 0.0
  sets.forEach { totalVolume += it.setVolume(MassUnit.KILOGRAMS.abbreviation) }
  return totalVolume
}

fun ExerciseExerciseSets.totalRepetitions(): Int {
  var totalRepetitions = 0

  sets.forEach {
    (if (it.repetitions != null) it.repetitions else 0)?.let { it1 -> totalRepetitions += (it1) }
  }

  return totalRepetitions
}

fun ExerciseExerciseSets.averageWeight(userDefaultWeightUnit: String): Double {
  val weightUnit = exercise.weightUnit ?: userDefaultWeightUnit
  val averageWeight: Double = calculateTotalVolume(weightUnit) / totalRepetitions()

  return if (averageWeight.isNaN()) {
    0.0
  } else {
    averageWeight
  }
}

/**
 * Resets the sets of an exerciseSet by updating their IDs, lazyListItemIDs, RealmUUIDs, workoutIDs,
 * and setCompleted status. Useful for when creating a new workout from a template or previous
 * workout.
 *
 * @param workout The workout to associate the sets with.
 */
@OptIn(ExperimentalUuidApi::class)
fun ExerciseExerciseSets.resetSets(
  /*workout: Workout*/
) {
  sets.forEach { set ->
    set.id = ObjectId()
    set.lazyListItemID = newLazyListItemID()
    set.uuid = Uuid.random()
    //    set.workoutID = workout.workoutID
    //    set.workoutUUID = workout.uuid
    set.setCompleted = false
  }
}

@Serializable
data class ExercisesSets(
  val identical: Boolean,
  val numberOfIdenticalWorkouts: Int,
  val exercisesSets: List<SerializableExerciseSets>,
)

@Serializable
data class SerializableExerciseSets(
  @SerialName("id") var id: ObjectId = ObjectId(),
  var exSetsLazyListItemID: Long = newLazyListItemID(), // Used for LazyColumn
  @Contextual var exSetsUUID: Uuid = Uuid.random(),
  @Contextual val workout: ObjectId = ObjectId(),
  //  val workoutID: Long? = null // TODO May be useless
  //  val workoutUUID: Uuid? = null // TODO May be useless
  var exerciseOrder: Int,
  @Contextual var exerciseObjectID: ObjectId,
  //  var sets: List<Set> = listOf(),
  var sets: List<String> = listOf(), // List<ObjectId> = listOf(),
  var notes: List<ExerciseNote> = listOf(),
  val identicalSets: Boolean,
  val numberOfIdenticalSets: Int,
)

/*
fun ExerciseSets.getWorkingSetWeight(): Double? {
    return sets.firstOrNull { it.setTypeID != WARM_UP_SET }?.exerciseWeight
}

fun ExerciseSets.addWorkingSets(workingSets: List<SetModel>) {
    workingSets.forEach { it.lazyListItemID = newID() }

    sets.removeAll { it.setTypeID != WARM_UP_SET }

    sets.addAll(workingSets)
}

fun ExerciseSets.addBackOffSets(backOffSets: List<SetModel>) {
    for (index in backOffSets.indices) {
        sets.add(backOffSets[index].copy(lazyListItemID = newID(), Uuid = Uuid.random()))
    }
}*/

/*fun ExerciseSets.numberOfWorkingSets(): Int {
    var numberOfWorkingSets: Int = 0

    sets.filter { it.setTypeID != WARM_UP_SET }.forEach { _ ->
        numberOfWorkingSets++
    }

    return numberOfWorkingSets
}

//    Returns the number of reps to be performed based on the new weight
fun ExerciseSets.equateWorkingSetVolumeByWeight(weight: Double?): Int {
    var totalVolume: Double = 0.0
    var numberOfWorkingSets: Int = 0

    sets.filter { it.setTypeID != WARM_UP_SET }.forEach {
        totalVolume += (it.setVolume())
        numberOfWorkingSets++
    }

    val newRepetitions: Int = if (numberOfWorkingSets > 0 && weight != null && weight > 0.0) {
        totalVolume.div(weight).div(numberOfWorkingSets).roundToInt()
    } else {
        0
    }

    return newRepetitions
}

//    Returns the weight to be used based on the new repetitions
fun ExerciseSets.equateWorkingSetVolumeByRepetitions(repetitions: Int?): Double {
    var totalVolume: Double = 0.0
    var numberOfWorkingSets: Int = 0

    sets.filter { it.setTypeID != WARM_UP_SET }.forEach {
        totalVolume += (it.setVolume())
        numberOfWorkingSets++
    }

    val newWeight: Double = if (numberOfWorkingSets > 0 && repetitions != null && repetitions > 0) {
        totalVolume.div(repetitions).div(numberOfWorkingSets)
    } else {
        0.0
    }
    return newWeight
}*/
