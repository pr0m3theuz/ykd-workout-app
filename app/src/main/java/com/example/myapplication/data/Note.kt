package com.example.myapplication.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.example.myapplication.fakeULID
import com.example.myapplication.newID
import java.time.LocalDateTime
import java.util.UUID

// TODO Switch to use UUIDs instead of IDs
@Entity(
    tableName = "exerciseNotes",
    foreignKeys = [
        ForeignKey(
            entity = Workout::class,
            parentColumns = ["workoutID"],
            childColumns = ["workoutID"],
            onDelete = ForeignKey.SET_NULL
        ),
        ForeignKey(
            entity = ExerciseModel::class,
            parentColumns = ["exerciseID"],
            childColumns = ["exerciseID"],
            onDelete = ForeignKey.CASCADE
        ),
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
        ForeignKey(
            entity = Microcycle::class,
            parentColumns = ["microcycleID"],
            childColumns = ["microcycleID"],
            onDelete = ForeignKey.SET_NULL
        ),
    ]
)
data class ExerciseNote(
    @PrimaryKey(autoGenerate = true) val exerciseNotePrimaryKey: Long = fakeULID(),
    val id: Long = newID(),
    val uuid: UUID = UUID.randomUUID(),
    var note: String? = null,
    var pinned: Boolean = false,
    var dateCreated: LocalDateTime = LocalDateTime.now(),
    @ColumnInfo(name = "exerciseID") val exerciseID: Long,
    @ColumnInfo(name = "workoutID") var workoutID: Long? = null,
    @ColumnInfo(name = "programID") var programID: Long? = null,
    var programPinned: Boolean = false,
    @ColumnInfo(name = "mesocycleID") var mesocycleID: Long? = null,
    var mesocyclePinned: Boolean = false,
    @ColumnInfo(name = "microcycleID") var microcycleID: Long? = null,
    var microcyclePinned: Boolean = false,
)

// TODO Are the below necessary? TBD
@Entity(
    tableName = "workoutNotes",
    foreignKeys = [
        ForeignKey(
            entity = Workout::class,
            parentColumns = ["workoutID"],
            childColumns = ["workoutID"],
            onDelete = ForeignKey.CASCADE
        ),
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
        ForeignKey(
            entity = Microcycle::class,
            parentColumns = ["microcycleID"],
            childColumns = ["microcycleID"],
            onDelete = ForeignKey.SET_NULL
        ),
    ]
)
data class WorkoutNote(
    @PrimaryKey(autoGenerate = true) val primaryKey: Long = fakeULID(),
    var id: Long = newID(),
    val uuid: UUID = UUID.randomUUID(),
    var note: String? = null,
    var pinned: Boolean = false,
    @ColumnInfo(name = "workoutID") val workoutID: Long,
    @ColumnInfo(name = "programID") var programID: Long? = null,
    var programPinned: Boolean = false,
    @ColumnInfo(name = "mesocycleID") var mesocycleID: Long? = null,
    var mesocyclePinned: Boolean = false,
    @ColumnInfo(name = "microcycleID") var microcycleID: Long? = null,
    var microcyclePinned: Boolean = false,
)

@Entity(
    tableName = "programNotes",
    foreignKeys = [
        ForeignKey(
            entity = Program::class,
            parentColumns = ["programID"],
            childColumns = ["programID"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class ProgramNote(
    @PrimaryKey(autoGenerate = true) val primaryKey: Long = fakeULID(),
    val id: Long = newID(),
    val uuid: UUID = UUID.randomUUID(),
    var note: String? = null,
    var pinned: Boolean = false,
    @ColumnInfo(name = "programID") var programID: Long? = null,
)

@Entity(
    tableName = "mesocycleNotes",
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
            onDelete = ForeignKey.CASCADE
        ),
    ]
)
data class MesocycleNote(
    @PrimaryKey(autoGenerate = true) val primaryKey: Long = fakeULID(),
    val id: Long = newID(),
    val uuid: UUID = UUID.randomUUID(),
    var note: String? = null,
    var pinned: Boolean = false,
    @ColumnInfo(name = "programID") var programID: Long? = null,
    var programPinned: Boolean = false,
    @ColumnInfo(name = "mesocycleID") var mesocycleID: Long? = null,
)

@Entity(
    tableName = "microcycleNotes",
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
        ForeignKey(
            entity = Microcycle::class,
            parentColumns = ["microcycleID"],
            childColumns = ["microcycleID"],
            onDelete = ForeignKey.CASCADE
        ),
    ]
)
data class MicrocycleNote(
    @PrimaryKey(autoGenerate = true) val primaryKey: Long = fakeULID(),
    val id: Long = newID(),
    val uuid: UUID = UUID.randomUUID(),
    var note: String? = null,
    var pinned: Boolean = false,
    @ColumnInfo(name = "microcycleID") var microcycleID: Long? = null,
    @ColumnInfo(name = "programID") var programID: Long? = null,
    var programPinned: Boolean = false,
    @ColumnInfo(name = "mesocycleID") var mesocycleID: Long? = null,
    var mesocyclePinned: Boolean = false,
)
