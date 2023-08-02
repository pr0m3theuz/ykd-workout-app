package com.example.myapplication.data

import androidx.room.ColumnInfo
import androidx.room.PrimaryKey
import com.example.myapplication.fakeULID
import com.example.myapplication.newID
import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.types.RealmList
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.RealmUUID
import java.time.LocalDateTime
import kotlin.time.Duration

open class Workout(
    @PrimaryKey(autoGenerate = true) val workoutID: Long = fakeULID(),
    val lazyListItemID: Long = newID(),
    val uuid: RealmUUID = RealmUUID.random(),
    var name: String,
    var workoutAuthor: String? = null,
//    TODO Remove. Deprecated.
//    var notes: String? = null,
//    var pinnedNotes: String? = null,
//    var notesPinned: Boolean = false,
//
    var workoutDuration: Long = Duration.ZERO.inWholeMilliseconds,
    var workoutType: String? = null,
    var dateCreated: LocalDateTime = LocalDateTime.now(),
    val workoutStarted: LocalDateTime = LocalDateTime.now(),
    var workoutEnded: LocalDateTime? = null,
    var day: Int? = null,
    var exerciseSets: RealmList<ExerciseSets>? = realmListOf(),
    @ColumnInfo(name = "programID") val programID: Long? = null,
    @ColumnInfo(name = "mesocycleID") val mesocycleID: Long? = null,
    @ColumnInfo(name = "microcycleID") val microcycleID: Long? = null,
) : RealmObject {}

//fun Workout.pinNotes(pinned: Boolean) {
//    notesPinned = pinned
//    Log.i("pinNotes()", notesPinned.toString())
//}
