package com.example.myapplication.data

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Junction
import androidx.room.PrimaryKey
import androidx.room.Relation
import com.example.myapplication.fakeULID
import com.example.myapplication.newID
import java.util.UUID

/*@Entity(
    tableName = "exerciseSets",
    foreignKeys = [
        ForeignKey(
            entity = Workout::class,
            parentColumns = ["workoutID"],
            childColumns = ["workoutID"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ExerciseModel::class,
            parentColumns = ["exerciseID"],
            childColumns = ["exerciseID"],
            onDelete = ForeignKey.CASCADE
        ),
    ]
)*/
/*
 TODO:
    @Query(SELECT * FROM sets WHERE workoutID:=workoutID) return ExerciseSets
    @Query(SELECT * FROM sets WHERE exerciseID:=exerciseID) return WorkoutSets
*/

data class ExerciseSets(
    @PrimaryKey(autoGenerate = true) val exerciseSetsID: Long = fakeULID(),
    var exSetsLazyListItemID: Long = newID(), // Used for LazyColumn
    val exSetsUUID: UUID = UUID.randomUUID(),
    @ColumnInfo(name = "workoutID") val workoutID: Long, // TODO May be useless
    @ColumnInfo(name = "workoutUUID") val workoutUUID: UUID, // TODO May be useless
    @Embedded val exercise: ExerciseModel,
    @Relation(parentColumn = "exerciseID", entityColumn = "setID", entity = SetModel::class)
    val sets: MutableList<SetModel>,
    val notes: MutableList<ExerciseNote>,
)

// TODO to use for exercise history
data class TestExerciseSets(
    @Embedded val exercise: ExerciseModel,
    @Relation(parentColumn = "exerciseID", entityColumn = "exerciseID", entity = SetModel::class)
//    @Relation(parentColumn = "exerciseID", entityColumn = "setID", entity = SetModel::class)
    val sets: MutableList<SetModel>,
)

data class WorkoutSets(
    @Embedded val workout: Workout,
    @Relation(parentColumn = "workoutID", entityColumn = "setID", entity = SetModel::class)
    var sets: MutableList<SetModel>,
)

@Entity(primaryKeys = ["exerciseID", "setID"/*, "exerciseNotePrimaryKey"*/])
data class ExerciseSetCrossRef(
    val exerciseID: Long,
    val setID: Long,
)

data class ExerciseWithSets(
    @Embedded val exercise: ExerciseModel,
    @Relation(
        parentColumn = "exerciseID",
        entityColumn = "setId",
        associateBy = Junction(ExerciseSetCrossRef::class)
    )
    val sets: List<SetModel>,
)

data class SetsWithExercise(
    @Embedded val set: SetModel,
    @Relation(
        parentColumn = "setID",
        entityColumn = "exerciseID",
        associateBy = Junction(ExerciseSetCrossRef::class)
    )
    val exercise: ExerciseModel,
)

data class WorkoutWithSetsAndExercise(
    @Embedded val workout: Workout,
    @Relation(
        entity = SetModel::class,
        parentColumn = "workoutID",
        entityColumn = "workoutID",
    )
    val sets: List<SetsWithExercise>,
)

@Entity(primaryKeys = ["workoutID", "setID"/*, "exerciseNotePrimaryKey"*/])
data class WorkoutSetCrossRef(
    val workoutID: Long,
    val setID: Long,

    )

data class WorkoutWithSets(
    @Embedded val workout: Workout,
    @Relation(
        parentColumn = "workoutID",
        entityColumn = "setId",
        associateBy = Junction(WorkoutSetCrossRef::class)
    )
    val sets: List<SetModel>,
)


data class SetsWithWorkout(
    @Embedded val set: SetModel,
    @Relation(
        parentColumn = "setID",
        entityColumn = "workoutID",
        associateBy = Junction(WorkoutSetCrossRef::class)
    )
    val workout: List<Workout>,
)

data class ExercisesWithSetsAndWorkout(
    @Embedded val exercise: ExerciseModel,
    @Relation(
        entity = SetModel::class,
        parentColumn = "exerciseID",
        entityColumn = "exerciseID",
    )
    val sets: MutableList<SetsWithWorkout>,
)

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
                workoutID = fakeULID() *//* TODO change in workout.primaryKey*//*
            )
        )
    }

    Log.i("workoutViewModel.addSet()", "${sets.size}")
}

fun ExerciseSets.removeSet(uuid: UUID) {
    sets.removeIf { it.uuid == uuid }
}*/

fun ExerciseSets.updateSetWeight(uuid: UUID, newWeight: Double?) {
    sets.forEach { if (it.uuid == uuid) it.exerciseWeight = newWeight }
}

fun ExerciseSets.updateSetReps(uuid: UUID, newReps: Int?) {
    sets.forEach { if (it.uuid == uuid) it.repetitions = newReps }
}

fun ExerciseSets.updateSetRIR(uuid: UUID, newRIR: Double?) {
    sets.forEach { if (it.uuid == uuid) it.repetitionsInReserve = newRIR }
}

fun ExerciseSets.updateSetRPE(uuid: UUID, newRPE: Double?) {
    sets.forEach { if (it.uuid == uuid) it.ratePerceivedEffort = newRPE }
}

fun ExerciseSets.setComplete(uuid: UUID, setComplete: Boolean) {
    sets.forEach { if (it.uuid == uuid) it.setCompleted = setComplete }
}

fun ExerciseSets.updateSetType(uuid: UUID, setTypeID: Int) {
    sets.forEach { if (it.uuid == uuid) it.setTypeID = setTypeID }
}

fun ExerciseSets.addWarmUpSets(warmUpSets: List<SetModel>) {
    for (index in warmUpSets.indices) {
        sets.add(
            index,
            warmUpSets[index].copy(
                lazyListItemID = newID(),
                uuid = UUID.randomUUID(),
                exerciseID = exercise.exerciseID
            )
        )
    }
}

private const val WARM_UP_SET = 2

fun ExerciseSets.totalWorkingSetVolume(): Double {
    var totalVolume = 0.0

    sets.filter { it.setTypeID != WARM_UP_SET }.forEach {
        totalVolume += (it.setVolume())
    }

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
        (if (it.repetitions != null) it.repetitions else 0)?.let { it1 ->
            totalRepetitions += (it1)
        }
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
 * Resets the sets of an exerciseSet by updating their IDs, lazyListItemIDs, UUIDs, workoutIDs, and setCompleted status.
 * Useful for when creating a new workout from a template or previous workout.
 * @param workout The workout to associate the sets with.
 */
fun ExerciseSets.resetSets(workout: Workout) {
    sets.forEach { set ->
        set.setID = newID();
        set.lazyListItemID = newID();
        set.uuid = UUID.randomUUID();
        set.workoutID = workout.workoutID;
        set.workoutUUID = workout.uuid
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
        sets.add(backOffSets[index].copy(lazyListItemID = newID(), uuid = UUID.randomUUID()))
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
