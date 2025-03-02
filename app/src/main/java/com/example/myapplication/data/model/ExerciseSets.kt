@file:UseSerializers(
  //  MutableRealmIntKSerializer::class,
  //  RealmAnyKSerializer::class,
  //  RealmDictionaryKSerializer::class,
  //  RealmInstantKSerializer::class,
  //  RealmListKSerializer::class,
  //  RealmSetKSerializer::class,
  //  RealmUUIDKSerializer::class,
)
@file:OptIn(ExperimentalUuidApi::class)

package com.example.myapplication.data.model

import com.example.myapplication.utils.newID
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import org.bson.types.ObjectId
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

/*
 TODO:
    @Query(SELECT * FROM sets WHERE workoutID:=workoutID) return ExerciseSets
    @Query(SELECT * FROM sets WHERE exerciseID:=exerciseID) return WorkoutSets
*/

@Serializable
data class ExerciseSetsDao(val ExerciseSets: ExerciseSets)

@ExperimentalUuidApi
@Serializable
data class ExerciseSets(
  @SerialName("id") @Serializable(with = ObjectIdSerializer::class) val id: ObjectId = ObjectId(),
  val exSetsLazyListItemID: Long = newID(), // Used for LazyColumn
  @Serializable(with = UuidSerializer::class) val exSetsUUID: Uuid = Uuid.random(),
  @Serializable(with = ObjectIdSerializer::class) val workoutID: ObjectId, // External Primary Key
  @Serializable(with = ObjectIdSerializer::class) val microcycleID: ObjectId?,
  @Serializable(with = ObjectIdSerializer::class) val mesocycleID: ObjectId?,
  @Serializable(with = ObjectIdSerializer::class) val programID: ObjectId?,
  val exerciseOrder: Int = 1,
  @Serializable(with = ObjectIdSerializer::class) val exerciseID: ObjectId,
  var sets: MutableList<Set> = mutableListOf(),
  val notes: MutableList<ExerciseNote> = mutableListOf(),
  val datePerformed: Instant? = null,
  val totalVolumeInPounds: Double? = 0.0,
  val totalVolumeInKilograms: Double? = 0.0,
  val ownerID: String,
  val isPublic: Boolean = false,
)

@OptIn(ExperimentalUuidApi::class)
@Serializable
data class ExerciseExerciseSets(
  @SerialName("id") @Serializable(ObjectIdSerializer::class) val id: ObjectId = ObjectId(),
  var exSetsLazyListItemID: Long = newID(), // Used for LazyColumn
  @Serializable(UuidSerializer::class) var exSetsUUID: Uuid = Uuid.random(),
  val ownerID: String,
  val isPublic: Boolean = false,
  var exerciseOrder: Int = 1,
  var exercise: Exercise,
  var sets: MutableList<Set> = mutableListOf(),
  var notes: MutableList<ExerciseNote> = mutableListOf(),
  var datePerformed: Instant? = Clock.System.now(),
//  @Contextual var exerciseID: ObjectId,
  @Serializable(ObjectIdSerializer::class) var workoutID: ObjectId, // TODO May be useless
  @Serializable(ObjectIdSerializer::class) var microcycleID: ObjectId?,
  @Serializable(ObjectIdSerializer::class) var mesocycleID: ObjectId?,
  @Serializable(ObjectIdSerializer::class) var programID: ObjectId?,
)

@Serializable
class ListExerciseExerciseSets() {
  var list: List<ExerciseExerciseSets> = emptyList()
}

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
  for (index in warmUpSets.indices) {
    sets.add(
      index,
      //      warmUpSets[index]
      //      TODO: Check this
      Set(
        setNumber = warmUpSets[index].setNumber,
        weightPounds = warmUpSets[index].weightPounds,
        weightKilograms = warmUpSets[index].weightKilograms,
        repetitions = warmUpSets[index].repetitions,
        ratePerceivedEffort = warmUpSets[index].ratePerceivedEffort,
        repetitionsInReserve = warmUpSets[index].repetitionsInReserve,
        setTypeID = warmUpSets[index].setTypeID,
        percentage1RM = warmUpSets[index].percentage1RM,
        exerciseID = this@addWarmUpSets.exercise.id,
        progressionSchemeID = warmUpSets[index].progressionSchemeID,
        workoutID = this@addWarmUpSets.workoutID,
        exerciseSetsID = this@addWarmUpSets.id,
        ownerID = this@addWarmUpSets.ownerID.toString(),
        isPublic = this@addWarmUpSets.isPublic,
      ),
    )
  }
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
    set.lazyListItemID = newID()
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
  @SerialName("id") @Serializable(with = ObjectIdSerializer::class) var id: ObjectId = ObjectId(),
  var exSetsLazyListItemID: Long = newID(), // Used for LazyColumn
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
