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
    var targetWeight: Double? = null,
    var targetRepetitions: Int? = null,
    var targetRatePerceivedEffort: Double? = null,
    var targetRepetitionsInReserve: Double? = null,
    var percentage1RM: Double? = 1.0,
    var setCompleted: Boolean = false,
    @ColumnInfo(name = "setTypeID", defaultValue = "1") var setTypeID: Int = 1,
    @ColumnInfo(name = "exerciseID") var exerciseID: Long,
    @ColumnInfo(name = "workoutID") var workoutID: Long,
    var programID: Long? = null,
    var mesocycleID: Long? = null,
    var microcycleID: Long? = null,
)

fun SetModel.setVolume(): Double {
//    Log.i("SetModel.setVolume()", "${exerciseWeight?.times(repetitions!!)}")

    return if (exerciseWeight != null && repetitions != null) {
        exerciseWeight!!.times(repetitions!!)
    } else {
        0.0
    }
}
