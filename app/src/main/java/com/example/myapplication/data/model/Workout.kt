package com.example.myapplication.data.model

import com.example.myapplication.newID
import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.types.RealmInstant
import io.realm.kotlin.types.RealmList
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.RealmUUID
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.ObjectId
import kotlin.time.Duration

class Workout() : RealmObject {
    @PrimaryKey
    var workoutID: ObjectId = ObjectId()
    var lazyListItemID: Long = newID()
    var uuid: RealmUUID = RealmUUID.random()
    var name: String? = null
    var workoutAuthor: String? = null

    //    TODO Remove. Deprecated.
    var notes: RealmList<WorkoutNote> = realmListOf()

    //    var pinnedNotes: String? = null
//    var notesPinned: Boolean = false
//
    var workoutDuration: Long = Duration.ZERO.inWholeMilliseconds
    var workoutType: String? = null
    var dateCreated: RealmInstant = RealmInstant.now()
    var workoutStarted: RealmInstant = RealmInstant.now()
    var workoutEnded: RealmInstant? = null
    var day: Int? = null
    var exerciseSets: RealmList<ExerciseSets> = realmListOf<ExerciseSets>()

    //    val programID: Long? = null
//    val mesocycleID: Long? = null
    var microcycle: Microcycle? = null
}

//fun Workout.pinNotes(pinned: Boolean) {
//    notesPinned = pinned
//    Log.i("pinNotes()", notesPinned.toString())
//}
