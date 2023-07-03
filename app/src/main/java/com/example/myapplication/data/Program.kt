package com.example.myapplication.data

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Junction
import androidx.room.PrimaryKey
import androidx.room.Relation
import com.example.myapplication.fakeULID
import com.example.myapplication.newID
import java.util.Date
import java.util.UUID

data class Program(
    @PrimaryKey(autoGenerate = true) val programID: Long = fakeULID(),
    val lazyListItemID: Long = newID(),
    val uuid: UUID = UUID.randomUUID(),
    var name: String,
    var author: String? = null,
    var startDate: Date,
    var endDate: Date? = null,
    var description: String,
    var url: String,
    var tags: String, // TODO Should be a List<String>
)

@Entity(
    tableName = "mesocycle",
    primaryKeys = ["programID"],
    foreignKeys = [
        ForeignKey(
            entity = Program::class,
            parentColumns = ["programID"],
            childColumns = ["programID"],
            onDelete = ForeignKey.CASCADE
        ),
    ]
)
data class Mesocycle(
    @PrimaryKey(autoGenerate = true) val mesocycleID: Long = fakeULID(),
    val lazyListItemID: Long = newID(),
    val uuid: UUID = UUID.randomUUID(),
    var name: String,
    var notes: String? = null,
    var startDate: Date,
    var endDate: Date? = null,
    var tags: String, // TODO Should be a List<String>
    @ColumnInfo(name = "programID") val programID: Long? = null,
)

@Entity(
    tableName = "microcycle",
    primaryKeys = ["programID", "mesocycleID"],
    foreignKeys = [
        ForeignKey(
            entity = Program::class,
            parentColumns = ["programID"],
            childColumns = ["programID"],
            onDelete = ForeignKey.SET_NULL
        ),
        ForeignKey(
            entity = Mesocycle::class,
            parentColumns = ["mesocycleID"],
            childColumns = ["mesocycleID"],
            onDelete = ForeignKey.SET_NULL
        ),
    ]
)
data class Microcycle(
    @PrimaryKey(autoGenerate = true) val microcyleID: Long = fakeULID(),
    val lazyListItemID: Long = newID(),
    val uuid: UUID = UUID.randomUUID(),
    var name: String,
    var notes: String? = null,
    var startDate: Date,
    var endDate: Date? = null,
    var tags: String, // TODO Should be a List<String>
    @ColumnInfo(name = "programID") val programID: Long? = null,
    @ColumnInfo(name = "mesocycleID") val mesocycleID: Long? = null,
)

@Entity(primaryKeys = ["programID", "mesocycleID"])
data class ProgramMesoCrossRef(
    val programID: Long,
    val mesocycleID: Long,
)

data class ProgramWithMesocycles(
    @Embedded val program: Program,
    @Relation(
        parentColumn = "playlistId",
        entityColumn = "songId",
        associateBy = Junction(ProgramMesoCrossRef::class)
    )
    val mesocycles: List<Mesocycle>,
)

@Entity(primaryKeys = ["mesocycleID", "microcyleID"])
data class MesoMircocycleCrossRef(
    val mesocycleID: Long,
    val microcyleID: Long,
)

data class MesocycleWithMicrocycles(
    @Embedded val mesocycle: Mesocycle,
    @Relation(
        parentColumn = "mesocycleID",
        entityColumn = "microcyleID",
        associateBy = Junction(MesoMircocycleCrossRef::class)
    )
    val microcyles: List<Microcycle>,
)

@Entity(primaryKeys = ["microcyleID", "workoutID"])
data class MicrocycleWorkoutCrossRef(
    val microcyleID: Long,
    val workoutID: Long,
)

data class MicrocycleWithWorkouts(
    @Embedded val mesocycle: Mesocycle,
    @Relation(
        parentColumn = "microcyleID",
        entityColumn = "workoutID",
        associateBy = Junction(MicrocycleWorkoutCrossRef::class)
    )
    val workouts: List<Workout>,
)
