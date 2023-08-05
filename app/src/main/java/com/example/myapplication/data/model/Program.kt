package com.example.myapplication.data.model

import com.example.myapplication.newID
import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.types.RealmInstant
import io.realm.kotlin.types.RealmList
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.RealmUUID
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.ObjectId

// TODO Add a woy to differentiate between templates and completed/current/active
// TODO Create a data class to track progress in a program
//  i.e. the workouts, microcycles, and mesocycles/blocks completed


class Program() : RealmObject {
    @PrimaryKey
    var programID: ObjectId = ObjectId()
    var lazyListItemID: Long = newID()
    var uuid: RealmUUID = RealmUUID.random()
    var name: String? = null
    var author: String? = null
    var dateCreated: RealmInstant = RealmInstant.now()
    var startDate: RealmInstant? = null
    var endDate: RealmInstant? = null
    var shortDescription: String? = null
    var description: String? = null
    var uri: String? = null
    var level: String? = null
    var programEquipment: String? = null
    var daysPerWeek: String? = null
    var programLength: String? = null
    var notes: RealmList<ProgramNote> = realmListOf()
//    var tags: String // TODO Should be a RealmList<String>
}

class Mesocycle() : RealmObject {
    @PrimaryKey
    var mesocycleID: ObjectId = ObjectId()
    var lazyListItemID: Long = newID()
    var uuid: RealmUUID = RealmUUID.random()
    var name: String? = null
    var description: String? = null
    var dateCreated: RealmInstant = RealmInstant.now()
    var startDate: RealmInstant? = null
    var endDate: RealmInstant? = null
    var order: Int = 0

    //    var tags: String // TODO Should be a List<String>
    var programID: Long? = null
}


class Microcycle() : RealmObject {
    @PrimaryKey
    var microcycleID: ObjectId = ObjectId()
    var lazyListItemID: Long = newID()
    var uuid: RealmUUID = RealmUUID.random()
    var name: String? = null
    var description: String? = null
    var dateCreated: RealmInstant = RealmInstant.now()
    var startDate: RealmInstant? = null
    var endDate: RealmInstant? = null
    var order: Int = 0

    //    var tags: String // TODO Should be a List<String>
    var programID: Long? = null
    var mesocycleID: Long? = null
}