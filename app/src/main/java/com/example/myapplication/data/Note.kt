package com.example.myapplication.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.example.myapplication.fakeULID
import com.example.myapplication.newID
import java.util.UUID

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
    @ColumnInfo(name = "exerciseID") val exerciseID: Long,
    @ColumnInfo(name = "workoutID") val workoutID: Long,
    @ColumnInfo(name = "programID") val programID: Long? = null,
    @ColumnInfo(name = "mesocycleID") val mesocycleID: Long? = null,
    @ColumnInfo(name = "microcycleID") val microcycleID: Long? = null,
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
    val id: Long = newID(),
    val uuid: UUID = UUID.randomUUID(),
    var note: String? = null,
    var pinned: Boolean = false,
    @ColumnInfo(name = "workoutID") val workoutID: Long,
    @ColumnInfo(name = "programID") val programID: Long? = null,
    @ColumnInfo(name = "mesocycleID") val mesocycleID: Long? = null,
    @ColumnInfo(name = "microcycleID") val microcycleID: Long? = null,
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
    @ColumnInfo(name = "programID") val programID: Long? = null,
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
    @ColumnInfo(name = "programID") val programID: Long? = null,
    @ColumnInfo(name = "mesocycleID") val mesocycleID: Long? = null,
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
    @ColumnInfo(name = "programID") val programID: Long? = null,
    @ColumnInfo(name = "mesocycleID") val mesocycleID: Long? = null,
    @ColumnInfo(name = "microcycleID") val microcycleID: Long? = null,
)
