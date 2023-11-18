@file:UseSerializers(
  MutableRealmIntKSerializer::class,
  RealmAnyKSerializer::class,
  RealmDictionaryKSerializer::class,
  RealmInstantKSerializer::class,
  RealmListKSerializer::class,
  RealmSetKSerializer::class,
  RealmUUIDKSerializer::class
)

package com.example.myapplication.data.model

import com.example.myapplication.utils.newID
import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.serializers.MutableRealmIntKSerializer
import io.realm.kotlin.serializers.RealmAnyKSerializer
import io.realm.kotlin.serializers.RealmDictionaryKSerializer
import io.realm.kotlin.serializers.RealmInstantKSerializer
import io.realm.kotlin.serializers.RealmListKSerializer
import io.realm.kotlin.serializers.RealmSetKSerializer
import io.realm.kotlin.serializers.RealmUUIDKSerializer
import io.realm.kotlin.types.RealmInstant
import io.realm.kotlin.types.RealmList
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.RealmUUID
import io.realm.kotlin.types.annotations.PrimaryKey
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import org.mongodb.kbson.ObjectId

/*
 TODO:
    @Query(SELECT * FROM sets WHERE workoutID:=workoutID) return ExerciseSets
    @Query(SELECT * FROM sets WHERE exerciseID:=exerciseID) return WorkoutSets
*/

@Serializable
class ExerciseSets() : RealmObject {
  @PrimaryKey
  var _id: ObjectId = ObjectId()
  var exSetsLazyListItemID: Long = newID() // Used for LazyColumn
  var exSetsUUID: RealmUUID = RealmUUID.random()
  //    val workout: RealmResults<Workout> by backlinks(Workout::exercisesSets)

  var workoutID: ObjectId? = null // TODO May be useless

  //  val workoutUUID: RealmUUID? = null // TODO May be useless
  var exerciseOrder: Int = 1
  var exerciseID: ObjectId? = null
  var sets: RealmList<Set> = realmListOf()
  var notes: RealmList<ExerciseNote> = realmListOf()
  var datePerformed: RealmInstant? = null
  var totalVolumeInPounds: Double? = 0.0
  var totalVolumeInKilograms: Double? = 0.0
}

@Serializable
class ExerciseExerciseSets() : RealmObject {
  @PrimaryKey
  var _id: ObjectId = ObjectId()
  var exSetsLazyListItemID: Long = newID() // Used for LazyColumn
  var exSetsUUID: RealmUUID = RealmUUID.random()
  //    val workout: RealmResults<Workout> by backlinks(Workout::exercisesSets)

  var workoutID: ObjectId? = null // TODO May be useless

  //  val workoutUUID: RealmUUID? = null // TODO May be useless
  var exerciseOrder: Int = 1
  var exercise: Exercise? = null
  var sets: RealmList<Set> = realmListOf()
  var notes: RealmList<ExerciseNote> = realmListOf()
  var datePerformed: RealmInstant? = null
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

fun ExerciseExerciseSets.updateSetWeight(uuid: RealmUUID, newWeight: Double?, weightUnit: String) {
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

fun ExerciseExerciseSets.updateSetReps(uuid: RealmUUID, newReps: Int?) {
  sets.forEach { if (it.uuid == uuid) it.repetitions = newReps }
}

fun ExerciseExerciseSets.updateSetRIR(uuid: RealmUUID, newRIR: Double?) {
  sets.forEach { if (it.uuid == uuid) it.repetitionsInReserve = newRIR }
}

fun ExerciseExerciseSets.updateSetRPE(uuid: RealmUUID, newRPE: Double?) {
  sets.forEach { if (it.uuid == uuid) it.ratePerceivedEffort = newRPE }
}

fun ExerciseExerciseSets.completeSet(uuid: RealmUUID, completed: Boolean) {
  sets.forEach { if (it.uuid == uuid) it.setCompleted = completed }
}

fun ExerciseExerciseSets.updateSetType(uuid: RealmUUID, setTypeID: Int) {
  sets.forEach { if (it.uuid == uuid) it.setTypeID = setTypeID }
}

fun ExerciseExerciseSets.addWarmUpSets(warmUpSets: List<Set>) {
  for (index in warmUpSets.indices) {
    sets.add(
      index,
      //      warmUpSets[index]
      //      TODO: Check this
      Set().apply {
        setNumber = warmUpSets[index].setNumber
        weightPounds = warmUpSets[index].weightPounds
        weightKilograms = warmUpSets[index].weightKilograms
        repetitions = warmUpSets[index].repetitions
        ratePerceivedEffort = warmUpSets[index].ratePerceivedEffort
        repetitionsInReserve = warmUpSets[index].repetitionsInReserve
        setTypeID = warmUpSets[index].setTypeID
        percentage1RM = warmUpSets[index].percentage1RM
        exerciseID = this@addWarmUpSets.exercise?._id
        progressionSchemeID = warmUpSets[index].progressionSchemeID
      }
    )
  }
}

private const val WARM_UP_SET = 2

fun ExerciseExerciseSets.totalWorkingSetVolume(weightUnit: String): Double {
  var totalVolume = 0.0

  sets.filter { it.setTypeID != WARM_UP_SET }.forEach { totalVolume += (it.setVolume(weightUnit)) }

  return totalVolume
}

fun ExerciseExerciseSets.calculateTotalVolume(userDefaultWeightUnit: String): Double {
  var totalVolume = 0.0
  val weightUnit = exercise?.weightUnit ?: userDefaultWeightUnit

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
  val weightUnit = exercise?.weightUnit ?: userDefaultWeightUnit
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
fun ExerciseExerciseSets.resetSets(
  /*workout: Workout*/
) {
  sets.forEach { set ->
    set._id = ObjectId()
    set.lazyListItemID = newID()
    set.uuid = RealmUUID.random()
    //    set.workoutID = workout.workoutID
    //    set.workoutUUID = workout.uuid
    set.setCompleted = false
  }
}

fun convertExerciseExerciseSetsToExerciseSets(
  exerciseExerciseSets: List<ExerciseExerciseSets>,
  userDefaultWeightUnit: String
): RealmList<ExerciseSets> {
  val exercisesSets = realmListOf<ExerciseSets>()
  exerciseExerciseSets.forEach {
    exercisesSets.add(
      ExerciseSets().apply {
        _id = it._id
        exSetsUUID
        workoutID
        exerciseOrder = it.exerciseOrder
        exerciseID = it.exercise?._id
        sets = it.sets
        notes = it.notes
        datePerformed = it.datePerformed
        totalVolumeInPounds = it.totalVolumeInPounds()
        totalVolumeInKilograms = it.totalVolumeInKilograms()
      }
    )
  }

  return exercisesSets
}

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
        sets.add(backOffSets[index].copy(lazyListItemID = newID(), RealmUUID = RealmUUID.random()))
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

@Serializable
data class ExercisesSets(
  val identical: Boolean,
  val numberOfIdenticalWorkouts: Int,
  val exercisesSets: List<SerializableExerciseSets>
)

@Serializable
data class SerializableExerciseSets(
  @PrimaryKey var _id: ObjectId = ObjectId(),
  var exSetsLazyListItemID: Long = newID(), // Used for LazyColumn
  var exSetsUUID: RealmUUID = RealmUUID.random(),
  val workout: ObjectId = ObjectId(),
  //  val workoutID: Long? = null // TODO May be useless
  //  val workoutUUID: RealmUUID? = null // TODO May be useless
  var exerciseOrder: Int,
  var exerciseObjectID: ObjectId,
  //  var sets: List<Set> = listOf(),
  var sets: List<ObjectId> = listOf(),
  var notes: List<ExerciseNote> = listOf(),
  val identicalSets: Boolean,
  val numberOfIdenticalSets: Int,
)
