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
import java.time.LocalDateTime
import java.util.UUID


// TODO Add a woy to differentiate between templates and completed/current/active
@Entity(tableName = "programs")
data class Program(
    @PrimaryKey(autoGenerate = true) val programID: Long = fakeULID(),
    val lazyListItemID: Long = newID(),
    val uuid: UUID = UUID.randomUUID(),
    var name: String,
    var author: String? = null,
    var dateCreated: LocalDateTime = LocalDateTime.now(),
    var startDate: LocalDateTime? = null,
    var endDate: LocalDateTime? = null,
    var description: String? = null,
    var uri: String? = null,
//    var tags: String, // TODO Should be a List<String>
)

@Entity(
    tableName = "mesocycles",
    foreignKeys = [
        ForeignKey(
            entity = Program::class,
            parentColumns = ["programID"],
            childColumns = ["programID"],
            onDelete = ForeignKey.CASCADE
        ),
    ],
//    indices = [Index("programID", unique = false)]
)
data class Mesocycle(
    @PrimaryKey(autoGenerate = true) val mesocycleID: Long = fakeULID(),
    val lazyListItemID: Long = newID(),
    val uuid: UUID = UUID.randomUUID(),
    var name: String,
    var description: String? = null,
    var dateCreated: LocalDateTime = LocalDateTime.now(),
    var startDate: LocalDateTime? = null,
    var endDate: LocalDateTime? = null,
    var order: Int,
//    var tags: String, // TODO Should be a List<String>
    @ColumnInfo(name = "programID") val programID: Long? = null,
)

@Entity(
    tableName = "microcycles",
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
    @PrimaryKey(autoGenerate = true) val microcycleID: Long = fakeULID(),
    val lazyListItemID: Long = newID(),
    val uuid: UUID = UUID.randomUUID(),
    var name: String,
    var description: String? = null,
    var dateCreated: LocalDateTime = LocalDateTime.now(),
    var startDate: LocalDateTime? = null,
    var endDate: LocalDateTime? = null,
    var order: Int,
//    var tags: String, // TODO Should be a List<String>
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

@Entity(primaryKeys = ["mesocycleID", "microcycleID"])
data class MesoMircocycleCrossRef(
    val mesocycleID: Long,
    val microcycleID: Long,
)

data class MesocycleWithMicrocycles(
    @Embedded val mesocycle: Mesocycle,
    @Relation(
        parentColumn = "mesocycleID",
        entityColumn = "microcycleID",
        associateBy = Junction(MesoMircocycleCrossRef::class)
    )
    val microcyles: List<Microcycle>,
)

@Entity(primaryKeys = ["microcycleID", "workoutID"])
data class MicrocycleWorkoutCrossRef(
    val microcycleID: Long,
    val workoutID: Long,
)

data class MicrocycleWithWorkouts(
    @Embedded val mesocycle: Mesocycle,
    @Relation(
        parentColumn = "microcycleID",
        entityColumn = "workoutID",
        associateBy = Junction(MicrocycleWorkoutCrossRef::class)
    )
    val workouts: List<Workout>,
)

// TODO Create a data class to track progress in a program
//  i.e. the workouts, microcycles, and mesocycles/blocks completed