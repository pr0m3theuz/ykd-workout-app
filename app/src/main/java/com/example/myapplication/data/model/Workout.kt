@file:UseSerializers(
    MutableRealmIntKSerializer::class,
    RealmAnyKSerializer::class,
    RealmDictionaryKSerializer::class,
    RealmInstantKSerializer::class,
    RealmListKSerializer::class,
    RealmSetKSerializer::class,
    RealmUUIDKSerializer::class
)

package com.example.myapplication.data.model

import com.example.myapplication.utils.newID
import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.serializers.MutableRealmIntKSerializer
import io.realm.kotlin.serializers.RealmAnyKSerializer
import io.realm.kotlin.serializers.RealmDictionaryKSerializer
import io.realm.kotlin.serializers.RealmInstantKSerializer
import io.realm.kotlin.serializers.RealmListKSerializer
import io.realm.kotlin.serializers.RealmSetKSerializer
import io.realm.kotlin.serializers.RealmUUIDKSerializer
import io.realm.kotlin.types.RealmInstant
import io.realm.kotlin.types.RealmList
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.RealmUUID
import io.realm.kotlin.types.annotations.PrimaryKey
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import org.mongodb.kbson.ObjectId
import kotlin.time.Duration

@Serializable
class Workout() : RealmObject {
    @PrimaryKey
    var _id: ObjectId = ObjectId()
    var lazyListItemID: Long = newID()
    var uuid: RealmUUID = RealmUUID.random()
    var name: String? = null // TODO make name default = User
    var workoutAuthor: String? = null
    var notes: RealmList<WorkoutNote> = realmListOf()
    var workoutDuration: Long = Duration.ZERO.inWholeMilliseconds
    var workoutType: String? = null
    var dateCreated: RealmInstant = RealmInstant.now()
    var workoutStarted: RealmInstant? = null
    var workoutEnded: RealmInstant? = null
    var day: Int? = null
    var exercisesSets: RealmList<ExerciseSets> = realmListOf()

    //    val programID: Long? = null
//    val mesocycleID: Long? = null
    var microcycleID: ObjectId? = null
}

@Serializable
data class Workouts(val workouts: List<Workout>)

//fun Workout.pinNotes(pinned: Boolean) {
//    notesPinned = pinned
//    Log.i("pinNotes()", notesPinned.toString())
//}
