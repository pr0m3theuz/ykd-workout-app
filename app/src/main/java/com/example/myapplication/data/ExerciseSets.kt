package com.example.myapplication.data

import android.util.Log
import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Junction
import androidx.room.PrimaryKey
import androidx.room.Relation
import com.example.myapplication.fakeULID
import com.example.myapplication.newID
import java.util.UUID
import kotlin.math.roundToInt

@Entity(
    tableName = "exerciseSets",
    primaryKeys = ["workoutID", "exerciseID"],
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
)
data class ExerciseSets(
    @PrimaryKey(autoGenerate = true) val exerciseSetsID: Long = fakeULID(),
    val id: Long = newID(), // Used for LazyColumn
    val uuid: UUID = UUID.randomUUID(),
    @Embedded val exercise: ExerciseModel,
    @Relation(parentColumn = "exerciseID", entityColumn = "setID", entity = SetModel::class)
    var sets: MutableList<SetModel>,
    @ColumnInfo(name = "workoutID") val workoutID: Long,
    @ColumnInfo(name = "exerciseID") val exerciseID: Long,
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

fun ExerciseSets.addSet() {
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
                workoutID = fakeULID() /* TODO change in workout.primaryKey*/
            )
        )
    }

    Log.i("workoutViewModel.addSet()", "${sets.size}")
}

fun ExerciseSets.removeSet(uuid: UUID) {
    sets.removeIf { it.uuid == uuid }
}

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
        sets.add(index, warmUpSets[index].copy(lazyListItemID = newID(), uuid = UUID.randomUUID()))
    }
}

fun ExerciseSets.getWorkingSetWeight(): Double? {
    return sets.firstOrNull { it.setTypeID != 2 }?.exerciseWeight
}

fun ExerciseSets.addWorkingSets(workingSets: List<SetModel>) {
    workingSets.forEach { it.lazyListItemID = newID() }

    sets.removeAll { it.setTypeID != 2 }

    sets.addAll(workingSets)
}

fun ExerciseSets.addBackOffSets(backOffSets: List<SetModel>) {
    for (index in backOffSets.indices) {
        sets.add(backOffSets[index].copy(lazyListItemID = newID(), uuid = UUID.randomUUID()))
    }
}

fun ExerciseSets.totalWorkingSetVolume(): Double {
    var totalVolume: Double = 0.0

    sets.filter { it.setTypeID != 2 }.forEach {
        totalVolume += (it.setVolume())
    }

    return totalVolume
}

fun ExerciseSets.numberOfWorkingSets(): Int {
    var numberOfWorkingSets: Int = 0

    sets.filter { it.setTypeID != 2 }.forEach { _ ->
        numberOfWorkingSets++
    }

    return numberOfWorkingSets
}

//    Returns the number of reps to be performed based on the new weight
fun ExerciseSets.equateWorkingSetVolumeByWeight(weight: Double?): Int {
    var totalVolume: Double = 0.0
    var numberOfWorkingSets: Int = 0

    sets.filter { it.setTypeID != 2 }.forEach {
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

    sets.filter { it.setTypeID != 2 }.forEach {
        totalVolume += (it.setVolume())
        numberOfWorkingSets++
    }

    val newWeight: Double = if (numberOfWorkingSets > 0 && repetitions != null && repetitions > 0) {
        totalVolume.div(repetitions).div(numberOfWorkingSets)
    } else {
        0.0
    }
    return newWeight
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
