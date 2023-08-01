
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
        ForeignKey(
            entity = ProgressionScheme::class,
            parentColumns = ["progressionSchemeID"],
            childColumns = ["progressionSchemeID"],
            onDelete = ForeignKey.CASCADE
        ),
    ]
)
data class SetModel(
    @PrimaryKey(autoGenerate = true) var setID: Long = fakeULID(),
    var lazyListItemID: Long = newID(),
    var uuid: UUID = UUID.randomUUID(),
    var setNumber: Int? = 0,
    var exerciseWeight: Double? = null,
    var repetitions: Int? = null,
    var ratePerceivedEffort: Double? = null,
    var repetitionsInReserve: Double? = null,
    var targetWeight: Double? = null,
    var targetRepetitions: String? = null,
    var targetRatePerceivedEffort: Double? = null,
    var targetRepetitionsInReserve: Double? = null,
    var percentage1RM: Double? = 1.0,
    var setCompleted: Boolean = false,
//   TODO var exerciseOrder: Int,
    @ColumnInfo(name = "setTypeID", defaultValue = "1") var setTypeID: Int = 1,
    @ColumnInfo(name = "exerciseID") var exerciseID: Long,
    @ColumnInfo(name = "exerciseUUID") var exerciseUUID: UUID,
    @ColumnInfo(name = "workoutID") var workoutID: Long,
    @ColumnInfo(name = "workoutUUID") var workoutUUID: UUID,
    @ColumnInfo(name = "progressionSchemeID") var progressionSchemeID: Int,
    var programUUID: UUID? = null,
    var mesocycleUUID: UUID? = null,
    var microcycleUUID: UUID? = null,
)

fun SetModel.setVolume(): Double {
    return if (exerciseWeight != null && repetitions != null) {
        exerciseWeight!!.times(repetitions!!)
    } else {
        0.0
    }
}
