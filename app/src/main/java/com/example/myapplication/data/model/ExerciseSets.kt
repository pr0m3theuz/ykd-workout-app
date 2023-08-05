package com.example.myapplication.data.model

import com.example.myapplication.newID
import io.realm.kotlin.ext.backlinks
import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.query.RealmResults
import io.realm.kotlin.types.RealmList
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.RealmUUID
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.ObjectId


/*
 TODO:
    @Query(SELECT * FROM sets WHERE workoutID:=workoutID) return ExerciseSets
    @Query(SELECT * FROM sets WHERE exerciseID:=exerciseID) return WorkoutSets
*/

class ExerciseSets() : RealmObject {
  @PrimaryKey
  var exerciseSetsID: ObjectId = ObjectId()
  var exSetsLazyListItemID: Long = newID() // Used for LazyColumn
  var exSetsUUID: RealmUUID = RealmUUID.random()
  val workout: RealmResults<Workout> by backlinks(Workout::exerciseSets)

  //  val workoutID: Long? = null // TODO May be useless
//  val workoutUUID: RealmUUID? = null // TODO May be useless
  var exerciseOrder: Int = 1
  var exercise: Exercise? = null
  var sets: RealmList<Set> = realmListOf<Set>()
  var notes: RealmList<ExerciseNote> = realmListOf<ExerciseNote>()
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

fun ExerciseSets.updateSetWeight(uuid: RealmUUID, newWeight: Double?) {
  sets.forEach { if (it.uuid == uuid) it.exerciseWeight = newWeight }
}

fun ExerciseSets.updateSetReps(uuid: RealmUUID, newReps: Int?) {
  sets.forEach { if (it.uuid == uuid) it.repetitions = newReps }
}

fun ExerciseSets.updateSetRIR(uuid: RealmUUID, newRIR: Double?) {
  sets.forEach { if (it.uuid == uuid) it.repetitionsInReserve = newRIR }
}

fun ExerciseSets.updateSetRPE(uuid: RealmUUID, newRPE: Double?) {
  sets.forEach { if (it.uuid == uuid) it.ratePerceivedEffort = newRPE }
}

fun ExerciseSets.setComplete(uuid: RealmUUID, setComplete: Boolean) {
  sets.forEach { if (it.uuid == uuid) it.setCompleted = setComplete }
}

fun ExerciseSets.updateSetType(uuid: RealmUUID, setTypeID: Int) {
  sets?.forEach { if (it.uuid == uuid) it.setTypeID = setTypeID }
}

fun ExerciseSets.addWarmUpSets(warmUpSets: List<Set>) {
  for (index in warmUpSets.indices) {
    sets.add(
      index,
//      warmUpSets[index]
//      TODO: Check this
      Set().apply {
        setNumber = warmUpSets[index].setNumber
        exerciseWeight = warmUpSets[index].exerciseWeight
        repetitions = warmUpSets[index].repetitions
        ratePerceivedEffort = warmUpSets[index].ratePerceivedEffort
        repetitionsInReserve = warmUpSets[index].repetitionsInReserve
        setTypeID = warmUpSets[index].setTypeID
        percentage1RM = warmUpSets[index].percentage1RM
        progressionSchemeID = warmUpSets[index].progressionSchemeID
      }
    )

  }
}

private const val WARM_UP_SET = 2

fun ExerciseSets.totalWorkingSetVolume(): Double {
  var totalVolume = 0.0

  sets.filter { it.setTypeID != WARM_UP_SET }.forEach { totalVolume += (it.setVolume()) }

  return totalVolume
}

fun ExerciseSets.totalVolume(): Double {
  var totalVolume = 0.0

  sets.forEach { totalVolume += it.setVolume() }

  return totalVolume
}

fun ExerciseSets.totalRepetitions(): Int {
  var totalRepetitions = 0

  sets.forEach {
    (if (it.repetitions != null) it.repetitions else 0)?.let { it1 -> totalRepetitions += (it1) }
  }

  return totalRepetitions
}

fun ExerciseSets.averageWeight(): Double {
  val averageWeight: Double = totalVolume() / totalRepetitions()

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
fun ExerciseSets.resetSets(/*workout: Workout*/) {
  sets.forEach { set ->
    set.setID = ObjectId()
    set.lazyListItemID = newID()
    set.uuid = RealmUUID.random()
//    set.workoutID = workout.workoutID
//    set.workoutUUID = workout.uuid
    set.setCompleted = false
  }
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
