package com.example.myapplication.data

import androidx.room.ColumnInfo
import androidx.room.PrimaryKey
import com.example.myapplication.fakeULID
import com.example.myapplication.newID
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.RealmUUID
import java.time.LocalDateTime

// TODO Switch to use UUIDs instead of IDs
open class ExerciseNote(
    @PrimaryKey(autoGenerate = true) val exerciseNotePrimaryKey: Long = fakeULID(),
    val id: Long = newID(),
    val uuid: RealmUUID = RealmUUID.random(),
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
) : RealmObject {}

// TODO Are the below necessary? TBD
open class WorkoutNote(
    @PrimaryKey(autoGenerate = true) val primaryKey: Long = fakeULID(),
    var id: Long = newID(),
    val uuid: RealmUUID = RealmUUID.random(),
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

open class ProgramNote(
    @PrimaryKey(autoGenerate = true) val primaryKey: Long = fakeULID(),
    val id: Long = newID(),
    val uuid: RealmUUID = RealmUUID.random(),
    var note: String? = null,
    var pinned: Boolean = false,
    @ColumnInfo(name = "programID") var programID: Long? = null,
) : RealmObject {}

open class MesocycleNote(
    @PrimaryKey(autoGenerate = true) val primaryKey: Long = fakeULID(),
    val id: Long = newID(),
    val uuid: RealmUUID = RealmUUID.random(),
    var note: String? = null,
    var pinned: Boolean = false,
    @ColumnInfo(name = "programID") var programID: Long? = null,
    var programPinned: Boolean = false,
    @ColumnInfo(name = "mesocycleID") var mesocycleID: Long? = null,
) : RealmObject {}

open class MicrocycleNote(
    @PrimaryKey(autoGenerate = true) val primaryKey: Long = fakeULID(),
    val id: Long = newID(),
    val uuid: RealmUUID = RealmUUID.random(),
    var note: String? = null,
    var pinned: Boolean = false,
    @ColumnInfo(name = "microcycleID") var microcycleID: Long? = null,
    @ColumnInfo(name = "programID") var programID: Long? = null,
    var programPinned: Boolean = false,
    @ColumnInfo(name = "mesocycleID") var mesocycleID: Long? = null,
    var mesocyclePinned: Boolean = false,
) : RealmObject {}
