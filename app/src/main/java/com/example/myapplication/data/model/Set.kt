
package com.example.myapplication.data.model


import com.example.myapplication.newID
import io.realm.kotlin.ext.backlinks
import io.realm.kotlin.query.RealmResults
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.RealmUUID
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.ObjectId


class Set() : RealmObject {
    @PrimaryKey
    var setID: ObjectId = ObjectId()
    var lazyListItemID: Long = newID()
    var uuid: RealmUUID = RealmUUID.random()
    var setNumber: Int? = 0
    var exerciseWeight: Double? = null
    var repetitions: Int? = null
    var ratePerceivedEffort: Double? = null
    var repetitionsInReserve: Double? = null
    var targetWeight: Double? = null
    var targetRepetitions: String? = null
    var targetRatePerceivedEffort: Double? = null
    var targetRepetitionsInReserve: Double? = null
    var percentage1RM: Double? = 1.0
    var setCompleted: Boolean = false

    //    var exerciseOrder: Int = 1
    var setTypeID: Int = 1
    val exerciseSet: RealmResults<ExerciseSets> by backlinks(ExerciseSets::sets) // TODO Use backlinks for ExerciseSets or not
    var progressionSchemeID: Int = 0
//    var programUUID: RealmUUID? = null
//    var mesocycleUUID: RealmUUID? = null
//    var microcycleUUID: RealmUUID? = null
}

fun Set.setVolume(): Double {
    return if (exerciseWeight != null && repetitions != null) {
        exerciseWeight!!.times(repetitions!!)
    } else {
        0.0
    }
}
