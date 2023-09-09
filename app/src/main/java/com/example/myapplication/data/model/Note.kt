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

import com.example.myapplication.utils.fakeULID
import com.example.myapplication.utils.newID
import io.realm.kotlin.ext.backlinks
import io.realm.kotlin.query.RealmResults
import io.realm.kotlin.serializers.MutableRealmIntKSerializer
import io.realm.kotlin.serializers.RealmAnyKSerializer
import io.realm.kotlin.serializers.RealmDictionaryKSerializer
import io.realm.kotlin.serializers.RealmInstantKSerializer
import io.realm.kotlin.serializers.RealmListKSerializer
import io.realm.kotlin.serializers.RealmSetKSerializer
import io.realm.kotlin.serializers.RealmUUIDKSerializer
import io.realm.kotlin.types.RealmInstant
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.RealmUUID
import io.realm.kotlin.types.annotations.PrimaryKey
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import org.mongodb.kbson.ObjectId

// TODO Switch to use UUIDs instead of IDs
@Serializable
class ExerciseNote() : RealmObject {
    @PrimaryKey
    var _id: ObjectId = ObjectId()
    var id: Long = newID()
    var uuid: RealmUUID = RealmUUID.random()
    var note: String? = null
    var pinned: Boolean = false
    var dateCreated: RealmInstant = RealmInstant.now()
    var exercise: ObjectId? = null
    var workout: ObjectId? = null
//    var program: Program
//    var programPinned: Boolean = false
//    var mesocycle: Mesocycle
//    var mesocyclePinned: Boolean = false
//    var microcycle: Microcycle
//    var microcyclePinned: Boolean = false
}

// TODO Are the below necessary? TBD
@Serializable
class WorkoutNote() : RealmObject {
    @PrimaryKey
    var _id: ObjectId = ObjectId()
    var lazyListID: Long = newID()
    var uuid: RealmUUID = RealmUUID.random()
    var note: String? = null
    var pinned: Boolean = false
    var workout: ObjectId? = null
//    var program: Program
//    var programPinned: Boolean = false
//    var mesocycle: Mesocycle
//    var mesocyclePinned: Boolean = false
//    var microcycle: Microcycle
//    var microcyclePinned: Boolean = false
}

@Serializable
class ProgramNote() : RealmObject {
    @PrimaryKey
    var _id: ObjectId = ObjectId()
    var lazyListID: Long = newID()
    var uuid: RealmUUID = RealmUUID.random()
    var note: String? = null
    var pinned: Boolean = false
    val program: RealmResults<Program> by backlinks(Program::notes)
}

@Serializable
class MesocycleNote() : RealmObject {
    @PrimaryKey
    var _id: Long = fakeULID()
    var lazyListID: Long = newID()
    var uuid: RealmUUID = RealmUUID.random()
    var note: String? = null
    var pinned: Boolean = false
    var programPinned: Boolean = false
    var mesocycle: Microcycle? = null
}

class MicrocycleNote() : RealmObject {
    @PrimaryKey
    var _id: Long = fakeULID()
    var lazyListID: Long = newID()
    var uuid: RealmUUID = RealmUUID.random()
    var note: String? = null
    var pinned: Boolean = false
    var microcycle: Microcycle? = null

    //    var programID: Long? = null
    var programPinned: Boolean = false

    //    var mesocycleID: Long? = null
    var mesocyclePinned: Boolean = false
}
