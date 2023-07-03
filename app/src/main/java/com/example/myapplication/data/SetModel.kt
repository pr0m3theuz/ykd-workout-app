package com.example.myapplication.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.example.myapplication.fakeULID
import com.example.myapplication.newID
import java.util.UUID

@Entity(
    tableName = "sets",
    primaryKeys = ["exerciseID", "workoutID", "setTypeID"],
    foreignKeys = [
        ForeignKey(
            entity = ExerciseModel::class,
            parentColumns = ["exerciseID"],
            childColumns = ["exerciseID"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Workout::class,
            parentColumns = ["workoutID"],
            childColumns = ["workoutID"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = SetType::class,
            parentColumns = ["setTypeID"],
            childColumns = ["setTypeID"],
            onDelete = ForeignKey.SET_DEFAULT
        ),
    ]
)
data class SetModel(
    @PrimaryKey(autoGenerate = true) val setID: Long = fakeULID(),
    var lazyListItemID: Long = newID(),
    var uuid: UUID = UUID.randomUUID(),
    var setNumber: Int? = 0,
    var exerciseWeight: Double? = null,
    var repetitions: Int? = null,
    var ratePerceivedEffort: Double? = null,
    var repetitionsInReserve: Double? = null,
    var percentage1RM: Double? = 1.0,
    var setCompleted: Boolean = false,
    @ColumnInfo(name = "setTypeID", defaultValue = "1") var setTypeID: Int = 1,
    @ColumnInfo(name = "exerciseID") var exerciseID: Long? = null,
    @ColumnInfo(name = "workoutID") var workoutID: Long? = null,
)

class Set(_setModel: SetModel) {

    private val setModel: SetModel = _setModel

    fun updateSetWeight(newWeight: Double?) {
        setModel.exerciseWeight = newWeight
    }

    fun updateSetReps(id: Long, newReps: Int?) {
        setModel.repetitions = newReps
    }

    fun updateSetRIR(newRIR: Double?) {
        setModel.repetitionsInReserve = newRIR
    }

    fun updateSetRPE(newRPE: Double?) {
        setModel.ratePerceivedEffort = newRPE
    }

    fun setComplete(setComplete: Boolean) {
        setModel.setCompleted = setComplete
    }
}

fun SetModel.setVolume(): Double {
//    Log.i("SetModel.setVolume()", "${exerciseWeight?.times(repetitions!!)}")

    return if (exerciseWeight != null && repetitions != null) {
        exerciseWeight!!.times(repetitions!!)
    } else {
        0.0
    }
}
