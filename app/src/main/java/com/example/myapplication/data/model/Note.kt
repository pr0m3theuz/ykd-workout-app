package com.example.myapplication.data.model

import com.example.myapplication.fakeULID
import com.example.myapplication.newID
import io.realm.kotlin.ext.backlinks
import io.realm.kotlin.query.RealmResults
import io.realm.kotlin.types.RealmInstant
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.RealmUUID
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.ObjectId

// TODO Switch to use UUIDs instead of IDs
class ExerciseNote() : RealmObject {
    @PrimaryKey
    var exerciseNoteID: ObjectId = ObjectId()
    var id: Long = newID()
    var uuid: RealmUUID = RealmUUID.random()
    var note: String? = null
    var pinned: Boolean = false
    var dateCreated: RealmInstant = RealmInstant.now()
    var exercise: Exercise? = null
    var workout: Workout? = null
//    var program: Program
//    var programPinned: Boolean = false
//    var mesocycle: Mesocycle
//    var mesocyclePinned: Boolean = false
//    var microcycle: Microcycle
//    var microcyclePinned: Boolean = false
}

// TODO Are the below necessary? TBD
class WorkoutNote() : RealmObject {
    @PrimaryKey
    var primaryKey: ObjectId = ObjectId()
    var id: Long = newID()
    var uuid: RealmUUID = RealmUUID.random()
    var note: String? = null
    var pinned: Boolean = false
    var workout: Workout? = null
//    var program: Program
//    var programPinned: Boolean = false
//    var mesocycle: Mesocycle
//    var mesocyclePinned: Boolean = false
//    var microcycle: Microcycle
//    var microcyclePinned: Boolean = false
}

class ProgramNote() : RealmObject {
    @PrimaryKey
    var primaryKey: ObjectId = ObjectId()
    var id: Long = newID()
    var uuid: RealmUUID = RealmUUID.random()
    var note: String? = null
    var pinned: Boolean = false
    val program: RealmResults<Program> by backlinks(Program::notes)
}

class MesocycleNote() : RealmObject {
    @PrimaryKey
    var primaryKey: Long = fakeULID()
    var id: Long = newID()
    var uuid: RealmUUID = RealmUUID.random()
    var note: String? = null
    var pinned: Boolean = false
    var programPinned: Boolean = false
    var mesocycle: Microcycle? = null
}

class MicrocycleNote() : RealmObject {
    @PrimaryKey
    var primaryKey: Long = fakeULID()
    var id: Long = newID()
    var uuid: RealmUUID = RealmUUID.random()
    var note: String? = null
    var pinned: Boolean = false
    var microcycle: Microcycle? = null

    //    var programID: Long? = null
    var programPinned: Boolean = false

    //    var mesocycleID: Long? = null
    var mesocyclePinned: Boolean = false
}
