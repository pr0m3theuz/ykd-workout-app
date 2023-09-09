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
import io.realm.kotlin.ext.backlinks
import io.realm.kotlin.query.RealmResults
import io.realm.kotlin.serializers.MutableRealmIntKSerializer
import io.realm.kotlin.serializers.RealmAnyKSerializer
import io.realm.kotlin.serializers.RealmDictionaryKSerializer
import io.realm.kotlin.serializers.RealmInstantKSerializer
import io.realm.kotlin.serializers.RealmListKSerializer
import io.realm.kotlin.serializers.RealmSetKSerializer
import io.realm.kotlin.serializers.RealmUUIDKSerializer
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.RealmUUID
import io.realm.kotlin.types.annotations.PrimaryKey
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import org.mongodb.kbson.ObjectId

@Serializable
class Set() : RealmObject {
    @PrimaryKey
    var _id: ObjectId = ObjectId()
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

    // var targetPercentage1RM: Double? = null
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

@Serializable
data class Sets(
    val sets: List<Set>
)